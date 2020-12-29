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

public class DashboardViewModel extends ViewModel {
    private final static String TAG = DashboardViewModel.class.getSimpleName();

    private MutableLiveData<String> mDeviceAddress;
    private MutableLiveData<String> mDeviceName;
    private final MutableLiveData<Boolean> mConnected;
    private final MutableLiveData<String> mConnectionState;
    private final MutableLiveData<List<String>> mData;

    public DashboardViewModel() {
        Log.i(TAG, "**** NEW DashboardViewModel");

        mData = new MutableLiveData<>(new ArrayList<>());
        mConnected = new MutableLiveData<>(false);
        mConnectionState = new MutableLiveData<>();
        mDeviceAddress = new MutableLiveData<>();
        mDeviceName = new MutableLiveData<>();
    }

    public LiveData<List<String>> getData() { return mData; }
    public LiveData<String> getConnectionState() {
        return mConnectionState;
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
            mConnectionState.setValue("CONNECTED");
            mConnected.setValue(true);
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            mConnectionState.setValue("DISCONNECTED");
            mConnected.setValue(false);
        } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
        }
        }
    };
}