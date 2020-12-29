package ch.supsi.iotemperature.ui.dashboard;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.supsi.iotemperature.BluetoothLeService;

import static android.content.Context.BIND_AUTO_CREATE;
import static ch.supsi.iotemperature.BluetoothLeService.UUID_CURRENT_TIME_CHAR;

public class DashboardViewModel extends ViewModel {
    private final static String TAG = DashboardViewModel.class.getSimpleName();

    private MutableLiveData<String> mDeviceAddress;
    private MutableLiveData<String> mDeviceName;
    private final MutableLiveData<Boolean> mConnected;
    private final MutableLiveData<String> mConnectionState;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
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

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                return;
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress.getValue());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "**** BLE Service disconnected");
            mBluetoothLeService = null;
        }
    };

    public void connect(Context context, String deviceAddress, String deviceName) {
        mDeviceAddress.setValue(deviceAddress);
        mDeviceName.setValue(deviceName);
        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        context.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
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
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                checkGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void checkGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;

        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : gattCharacteristics) {
                UUID uuid = characteristic.getUuid();
                Log.d(TAG, String.format("**** Char [%s]", uuid));
                if(!uuid.equals(UUID_CURRENT_TIME_CHAR))
                    continue;

                final int charaProp = characteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                    if (mNotifyCharacteristic != null) {
                        mBluetoothLeService.setCharacteristicNotification(
                                mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }
                    mBluetoothLeService.readCharacteristic(characteristic);
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(
                            characteristic, true);
                }
                return;
            }
        }
    }

    private void displayData(String data) {
        mData.getValue().add(data);
        mData.setValue(mData.getValue());
    }

}