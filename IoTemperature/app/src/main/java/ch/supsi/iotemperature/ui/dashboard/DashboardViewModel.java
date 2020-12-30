package ch.supsi.iotemperature.ui.dashboard;

import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.supsi.iotemperature.BluetoothLeService;
import ch.supsi.iotemperature.SUPSIGattAttributes;

public class DashboardViewModel extends ViewModel {
    private final static String TAG = DashboardViewModel.class.getSimpleName();

    private MutableLiveData<String> mDeviceAddress;
    private MutableLiveData<String> mDeviceName;
    private final MutableLiveData<Boolean> mConnected;
    private final MutableLiveData<Boolean> mLED1On;
    private final MutableLiveData<List<String>> mData;
    private final MutableLiveData<Integer> mSamplingValue;

    public DashboardViewModel() {
        Log.i(TAG, "**** NEW DashboardViewModel");

        mLED1On = new MutableLiveData<>(false);
        mData = new MutableLiveData<>(new ArrayList<>());
        mConnected = new MutableLiveData<>(false);
        mDeviceAddress = new MutableLiveData<>("-");
        mDeviceName = new MutableLiveData<>("-");
        mSamplingValue = new MutableLiveData<>(2);
    }

    public LiveData<List<String>> getData() { return mData; }
    public LiveData<Integer> getSamplingValue() {
        return mSamplingValue;
    }
    public LiveData<Boolean> isLED1On() {
        return mLED1On;
    }
    public LiveData<Boolean> isConnected() {
        return mConnected;
    }
    public LiveData<String> getDeviceName() {
        return mDeviceName;
    }
    public LiveData<String> getDeviceAddress() {
        return mDeviceAddress;
    }

    public void connect(Context context, String deviceAddress, String deviceName) {
        Log.i(TAG, "**** Connect");

        mDeviceAddress.setValue(deviceAddress);
        mDeviceName.setValue(deviceName);
        mConnected.setValue(false);
        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void displayData(String data) {
        mData.getValue().add(data);
        mData.setValue(mData.getValue());
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            mConnected.setValue(true);
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            mConnected.setValue(false);
        } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            processData(intent);
        }
        }
    };

    private void processData(Intent intent) {
        switch (intent.getStringExtra(BluetoothLeService.EXTRA_CHARACTERISTIC)) {
            case SUPSIGattAttributes.CURRENT_TIME_CHAR:
                LocalDateTime time = (LocalDateTime)intent.getSerializableExtra(BluetoothLeService.EXTRA_DATA);
                displayData(time.toString());
                break;
            case SUPSIGattAttributes.SAMPLING_CHARACTERISTIC:
                int sampling = intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0);
                mSamplingValue.setValue(sampling);
                break;
            case SUPSIGattAttributes.LED1_CHARACTERISTIC:
                int led1Status = intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0);
                mLED1On.setValue(led1Status == 1);
                break;
            default:
                String extra = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                displayData(extra);
                break;
        }
    }
}