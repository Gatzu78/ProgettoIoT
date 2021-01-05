package ch.supsi.iotemperature;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_BT_PERMISSIONS = 0;
    private static final int REQUEST_BT_ENABLE = 1;

    private boolean mServiceBound = false;
    private BluetoothLeService mBluetoothLeService;

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
        mServiceBound = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBluetooth();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void checkBluetooth() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getApplicationContext(), R.string.ble_not_supported, Toast.LENGTH_LONG).show();
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        this.requestPermissions(
                new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                REQUEST_BT_PERMISSIONS);

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (adapter == null || !adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
        }
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
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "**** BLE Service disconnected");
            mBluetoothLeService = null;
            mServiceBound = false;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    //                        result of read or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                checkGattServices(mBluetoothLeService.getSupportedGattServices());
                asyncReadLED1();
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

                final int charaProp = characteristic.getProperties();
                boolean canRead = (charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0;
                boolean canWrite = (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
                boolean canNotify = (charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0;
                Log.d(TAG, String.format("**** Characteristics\t R=%s W=%s N=%s \t[%s]",
                        canRead, canWrite, canNotify,
                        SUPSIGattAttributes.lookup(uuid.toString(), uuid.toString())));
            }
        }
    }

    public void connect(String deviceAddress) {
        mBluetoothLeService.connect(deviceAddress);
    }

    public void asyncReadTemperature() {
        mBluetoothLeService.asyncReadTemperature();
    }
    public void asyncReadSampling() {
        mBluetoothLeService.asyncReadSampling();
    }
    public void asyncReadLED1() {
        mBluetoothLeService.asyncReadLED1();
    }

    public void toggleLED1() {
        mBluetoothLeService.toggletLED1();
    }

    public void writeSampling(int value) {
        mBluetoothLeService.writeSampling(value);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        return intentFilter;
    }

}