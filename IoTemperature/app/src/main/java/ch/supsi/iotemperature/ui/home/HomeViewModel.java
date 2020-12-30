package ch.supsi.iotemperature.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.supsi.iotemperature.BluetoothLeService;

public class HomeViewModel extends ViewModel {
    private final static String TAG = HomeViewModel.class.getSimpleName();

    private final BluetoothAdapter mBluetooth = BluetoothAdapter.getDefaultAdapter();
    private final BluetoothLeScanner mBleScanner = mBluetooth.getBluetoothLeScanner();

    private final MutableLiveData<Boolean> mScanning;
    private final MutableLiveData<String> mText;
    private final MutableLiveData<Set<BluetoothDevice>> mIotDevices;

    private Handler handler = new Handler();

    // Stops scanning after 20 seconds.
    private static final long SCAN_PERIOD = 20000;
    private static final String MATCH_PATTERN_NAME = "SUPSI"; //"SUPSI IoT"
    private static final String MATCH_PATTERN_ADDR = ""; //"80:6f:b0"

    public HomeViewModel() {
        mScanning = new MutableLiveData<>(false);
        mIotDevices = new MutableLiveData<>(new HashSet<>());
        mText = new MutableLiveData<>();
        mText.setValue("SUPSI IoT devices");
    }

    // Device scan callback.
    private ScanCallback bleScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    BluetoothDevice device = result.getDevice();
                    if(isSUPSIDevice(device)) {
                        mIotDevices.getValue().add(device);
                        mIotDevices.setValue(mIotDevices.getValue());
                    }
                }
            };

    public void scanBleDevice() {
        if (!mScanning.getValue()) {
            mIotDevices.getValue().clear();
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(() -> {
                mScanning.setValue(false);
                stopScan();
            }, SCAN_PERIOD);

            mScanning.setValue(true);
            mBleScanner.startScan(bleScanCallback);
        } else {
            mScanning.setValue(false);
            stopScan();
        }
    }

    public void stopScan() {
        mBleScanner.stopScan(bleScanCallback);
    }

    private boolean isSUPSIDevice(BluetoothDevice device) {
        return device.getName() != null &&
                !device.getName().isEmpty() &&
                device.getAddress().startsWith(MATCH_PATTERN_ADDR) &&
                device.getName().startsWith(MATCH_PATTERN_NAME);
    }

    public MutableLiveData<Boolean> getIsScanning() {
        return mScanning;
    }
    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Set<BluetoothDevice>> getIoTDevices() { return mIotDevices; }
}