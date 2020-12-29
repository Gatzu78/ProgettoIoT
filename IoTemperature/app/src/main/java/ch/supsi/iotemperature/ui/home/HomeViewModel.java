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
import java.util.List;

import ch.supsi.iotemperature.BluetoothLeService;

public class HomeViewModel extends ViewModel {
    private final static String TAG = HomeViewModel.class.getSimpleName();

    private final BluetoothAdapter mBluetooth = BluetoothAdapter.getDefaultAdapter();
    private final BluetoothLeScanner mBleScanner = mBluetooth.getBluetoothLeScanner();

    private final MutableLiveData<Boolean> mScanning;
    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<BluetoothDevice>> mIotDevices;

    private Handler handler = new Handler();
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public HomeViewModel() {
        mScanning = new MutableLiveData<>(false);
        mIotDevices = new MutableLiveData<>(new ArrayList<>());
        mText = new MutableLiveData<>();
        mText.setValue("SUPSI IoT devices");
    }

    // Device scan callback.
    private ScanCallback bleScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    BluetoothDevice device = result.getDevice();
                    if(isSupsiDevice(device)) {
                        mIotDevices.getValue().add(device);
                        mIotDevices.setValue(mIotDevices.getValue());
                    }
                }
            };

    private void scanBleDevice() {
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

    private boolean isSupsiDevice(BluetoothDevice device) {
        return device.getName() != null &&
                !device.getName().isEmpty() &&
                device.getName().startsWith("Mi ");
//        return device.getName() != null &&
//                !device.getName().isEmpty()
//                device.getName().startsWith("SUPSI IoT") &&
//                device.getAddress().startsWith("80:6f:b0");
    }


    public MutableLiveData<Boolean> getIsScanning() {
        return mScanning;
    }
    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<BluetoothDevice>> getIoTDevices() { return mIotDevices; }

    public void refreshDevices(Context context) {
        scanBleDevice();
    }

    // Demonstrates how to iterate through the supported GATT
    // Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the
    // ExpandableListView on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "unkown_service";
        String unknownCharaString = "unknown_characteristic";

        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<>();
        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            Log.i(TAG, uuid);
//            currentServiceData.put(
//                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
//            currentServiceData.put(LIST_UUID, uuid);
//            gattServiceData.add(currentServiceData);
//
//            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<>();
//            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
//            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();
//            // Loops through available Characteristics.
//            for (BluetoothGattCharacteristic gattCharacteristic :
//                    gattCharacteristics) {
//                charas.add(gattCharacteristic);
//                HashMap<String, String> currentCharaData =
//                        new HashMap<>();
//                uuid = gattCharacteristic.getUuid().toString();
//                currentCharaData.put(LIST_NAME,
//                                SampleGattAttributes.lookup(uuid, unknownCharaString));
//                currentCharaData.put(LIST_UUID, uuid);
//                gattCharacteristicGroupData.add(currentCharaData);
//            }
//            mGattCharacteristics.add(charas);
//            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }
}