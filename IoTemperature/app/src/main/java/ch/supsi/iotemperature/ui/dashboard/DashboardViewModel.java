package ch.supsi.iotemperature.ui.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ch.supsi.iotemperature.BluetoothLeService;
import ch.supsi.iotemperature.SUPSIGattAttributes;

public class DashboardViewModel extends ViewModel {
    private final static String TAG = DashboardViewModel.class.getSimpleName();
    private static final int NUM_OF_TEMP_VALUES = 10;

    private final MutableLiveData<String> mDeviceAddress;
    private final MutableLiveData<String> mDeviceName;
    private final MutableLiveData<Integer> mConnectionStatus;
    private final MutableLiveData<Boolean> mLED1On;
    private final MutableLiveData<List<String>> mLog;
    private final MutableLiveData<Integer> mSamplingValue;
    private final MutableLiveData<List<Integer>> mTemperatures;

    public DashboardViewModel() {
        Log.i(TAG, "**** NEW DashboardViewModel");

        mLED1On = new MutableLiveData<>(false);
        mLog = new MutableLiveData<>(new ArrayList<>());
        mConnectionStatus = new MutableLiveData<>(BluetoothLeService.STATE_CONNECTING);
        mDeviceAddress = new MutableLiveData<>("-");
        mDeviceName = new MutableLiveData<>("-");
        mSamplingValue = new MutableLiveData<>(0);
        mTemperatures = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<List<String>> getLog() { return mLog; }
    public LiveData<List<Integer>> getTemperatures() { return mTemperatures; }

    public LiveData<Integer> getSamplingValue() {
        return mSamplingValue;
    }
    public LiveData<Boolean> isLED1On() {
        return mLED1On;
    }
    public LiveData<Integer> getConnStatus() {
        return mConnectionStatus;
    }
    public LiveData<String> getDeviceName() {
        return mDeviceName;
    }
    public LiveData<String> getDeviceAddress() {
        return mDeviceAddress;
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mGattUpdateReceiver);
    }
    public void registerReceiver(Context context) {
        Log.i(TAG, "**** registerReceiver");
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
        mLog.getValue().add(0, data);
        mLog.setValue(mLog.getValue());
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

        if (BluetoothLeService.ACTION_GATT_CONNECTING.equals(action)) {
            mConnectionStatus.setValue(BluetoothLeService.STATE_CONNECTING);
        } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            mConnectionStatus.setValue(BluetoothLeService.STATE_CONNECTED);
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            mConnectionStatus.setValue(BluetoothLeService.STATE_DISCONNECTED);
        } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            processData(intent);
        }
        }
    };

    private void processData(Intent intent) {
        switch (intent.getStringExtra(BluetoothLeService.EXTRA_CHARACTERISTIC)) {
            case SUPSIGattAttributes.BUTTON0_CHARACTERISTIC:
                int value = intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0);
                displayData(String.format("Button0: %d", value));
                break;
            case SUPSIGattAttributes.SAMPLING_CHARACTERISTIC:
                int sampling = intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0);
                mSamplingValue.setValue(sampling);
                displayData(String.format("Sampling: %d", sampling));
                break;
            case SUPSIGattAttributes.LED1_CHARACTERISTIC:
                int led1Status = intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0);
                mLED1On.setValue(led1Status == 1);
                displayData(String.format("LED1: %d", led1Status));
                break;
            case SUPSIGattAttributes.TEMPERATURE_CHARACTERISTIC:
                Integer temperature = intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0);
                updateTemperatureCollection(temperature);
                displayData(String.format("Temperature: %d", temperature));
                break;
            default:
                String extra = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                displayData(extra);
                break;
        }
    }

    private void updateTemperatureCollection(int temperature) {
        List<Integer> values = mTemperatures.getValue();
        values.add(temperature);
        while (values.size() > NUM_OF_TEMP_VALUES)
            values.remove(0);
        mTemperatures.setValue(values);
    }

    public void setSampling(int value) {
        if(mSamplingValue.getValue() != value)
            mSamplingValue.setValue(value);
    }

    public void setDeviceName(String deviceName) {
        mDeviceName.setValue(deviceName);
    }

    public void setDeviceAddress(String deviceAddress) {
        mDeviceAddress.setValue(deviceAddress);
    }
}