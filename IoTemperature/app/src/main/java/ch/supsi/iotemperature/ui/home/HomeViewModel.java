package ch.supsi.iotemperature.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {
    private final BluetoothAdapter myBluetooth;
    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<BluetoothDevice>> iotDevices;

    public MutableLiveData<Boolean> getRefreshEnabled() {
        return refreshEnabled;
    }

    private final MutableLiveData<Boolean> refreshEnabled;

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(isSupsiDevice(device)) {
                    iotDevices.getValue().add(device);
                    iotDevices.setValue(iotDevices.getValue());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.e("bluetoothReceiver", "ACTION_DISCOVERY_STARTED");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.e("bluetoothReceiver", "ACTION_DISCOVERY_FINISHED");
                context.unregisterReceiver(bluetoothReceiver);
                refreshEnabled.setValue(true);
            }
        }
    };

    private boolean isSupsiDevice(BluetoothDevice device) {
        return device.getName() != null &&
                !device.getName().isEmpty() &&
                device.getName().startsWith("Mi ");
//        return device.getName() != null &&
//                !device.getName().isEmpty()
//                device.getName().startsWith("SUPSI IoT") &&
//                device.getAddress().startsWith("80:6f:b0");
    }


    public HomeViewModel() {
        refreshEnabled = new MutableLiveData<>(true);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        iotDevices = new MutableLiveData<>(new ArrayList<>());
        mText = new MutableLiveData<>();
        mText.setValue("SUPSI IoT devices");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<List<BluetoothDevice>> getIoTDevices() { return iotDevices; }

    public void refreshDevices(Context context) {
        if(myBluetooth == null) {
            mText.setValue("Bluetooth not available on this device");
        } else {
            refreshEnabled.setValue(false);
            // Register for broadcasts when a device is discovered.
            IntentFilter bluetoothFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(bluetoothReceiver, bluetoothFilter);

            myBluetooth.startDiscovery();
        }
    }

    public void pair(Context context, BluetoothDevice device) {
        device.createBond();
    }
}