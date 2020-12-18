package ch.supsi.iotemperature.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {
    private final BluetoothAdapter myBluetooth;
    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<BluetoothDevice>> iotDevices;

    public HomeViewModel() {
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        iotDevices = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("SUPSI IoT devices");
        refreshPairedDevices();
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<List<BluetoothDevice>> getIoTDevices() { return iotDevices; }

    public void refreshPairedDevices() {
        if(myBluetooth == null) {
            mText.setValue("Bluetooth not available on this device");
        } else {
            //myBluetooth.getBluetoothLeScanner()
            List<BluetoothDevice> list = myBluetooth.getBondedDevices()
                    .stream()
//                    .filter(d -> d.getAddress().startsWith("80:6f:b0"))
//                    .filter(d -> d.getName().startsWith("SUPSI IoT"))
                    .collect(Collectors.toList());
            iotDevices.setValue(list);
        }
    }
}