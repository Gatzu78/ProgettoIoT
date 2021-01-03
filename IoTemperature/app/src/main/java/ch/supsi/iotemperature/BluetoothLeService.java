/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.supsi.iotemperature;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private int mLED1Value = 0;
    private int mSamplingValue;

    private BluetoothGattCharacteristic mTemperatureCharacteristic;
    private BluetoothGattCharacteristic mLED1Characteristic;
    private BluetoothGattCharacteristic mSamplingCharacteristic;
    private BluetoothGattCharacteristic mCurrentTimeChar;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTING =
            "ch.supsi.iotemperature.ACTION_GATT_CONNECTING";
    public final static String ACTION_GATT_CONNECTED =
            "ch.supsi.iotemperature.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "ch.supsi.iotemperature.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "ch.supsi.iotemperature.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "ch.supsi.iotemperature.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "ch.supsi.iotemperature.EXTRA_DATA";
    public final static String EXTRA_CHARACTERISTIC =
            "ch.supsi.iotemperature.EXTRA_CHARACTERISTIC";


    // MI BAND SERVICE
    public final static UUID UUID_MI_BAND_BASIC_SERVICE =
            UUID.fromString(SUPSIGattAttributes.MI_BAND_BASIC_SERVICE);
    public final static UUID UUID_CURRENT_TIME_CHAR =
            UUID.fromString(SUPSIGattAttributes.CURRENT_TIME_CHAR);

    public final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString(SUPSIGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);


    // SUPSI SERVICE
    public final static UUID UUID_TEMPERATURE_SERVICE =
            UUID.fromString(SUPSIGattAttributes.TEMPERATURE_SERVICE);
    public final static UUID UUID_SAMPLING_CHARACTERISTIC =
            UUID.fromString(SUPSIGattAttributes.SAMPLING_CHARACTERISTIC);
    public final static UUID UUID_TEMPERATURE_CHARACTERISTIC =
            UUID.fromString(SUPSIGattAttributes.TEMPERATURE_CHARACTERISTIC);
    public final static UUID UUID_LED_SERVICE =
            UUID.fromString(SUPSIGattAttributes.LED_SERVICE);
    public final static UUID UUID_LED1_CHARACTERISTIC =
            UUID.fromString(SUPSIGattAttributes.LED1_CHARACTERISTIC);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(ACTION_GATT_CONNECTED);
                Log.i(TAG, "**** Connected to GATT server.");

                // Attempts to discover services after successful connection.
                Log.i(TAG, "**** Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "**** Disconnected from GATT server.");
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService svc;

                //TODO REMOVE
                svc = mBluetoothGatt.getService(UUID_MI_BAND_BASIC_SERVICE);
                if(svc != null) {
                    mCurrentTimeChar = svc.getCharacteristic(UUID_CURRENT_TIME_CHAR);
                    mCurrentTimeChar.setWriteType(WRITE_TYPE_DEFAULT);
                    setCharacteristicNotification(mCurrentTimeChar, true);
                }

                // TEMPERATURE SERVICE
                svc = mBluetoothGatt.getService(UUID_TEMPERATURE_SERVICE);
                if(svc != null) {
                    mTemperatureCharacteristic = svc.getCharacteristic(UUID_TEMPERATURE_CHARACTERISTIC);
                    if(mTemperatureCharacteristic == null) {
                        Log.e(TAG, "**** TEMP CHAR not found");
                    } else {
                        // ENABLE NOTIFICATION
                        setCharacteristicNotification(mTemperatureCharacteristic, true);
                    }

                    mSamplingCharacteristic = svc.getCharacteristic(UUID_SAMPLING_CHARACTERISTIC);
                    if(mSamplingCharacteristic == null) {
                        Log.e(TAG, "**** SAMPLING CHAR not found");
                    }
                }

                // LED SERVICE
                svc = mBluetoothGatt.getService(UUID_LED_SERVICE);
                if(svc != null) {
                    mLED1Characteristic = svc.getCharacteristic(UUID_LED1_CHARACTERISTIC);
                    if(mLED1Characteristic == null) {
                        Log.e(TAG, "**** LED1 CHAR not found");
                    } else {
                        asyncReadLED1();
                    }
                }
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, mConnectionState);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        // Create an Intent based on action and broadcast to listener
        // pass the data through putExtra with key EXTRA_DATA
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_CHARACTERISTIC, characteristic.getUuid().toString());

        int format = getCharacteristicFormat(characteristic);
        int shift = format == BluetoothGattCharacteristic.FORMAT_UINT8 ? 8 : 16;
        Log.d(TAG, String.format("**** Characteristics %d bit format", shift));

        // TODO SUPSI: Choose the proper characteristic
        switch (characteristic.getUuid().toString()) {
            // MI BAND - TODO REMOTE
            case SUPSIGattAttributes.CURRENT_TIME_CHAR:
                parseCurrentTimeCharacteristic(action, characteristic, intent, format, shift);
                break;
            // SUPSI
            case SUPSIGattAttributes.TEMPERATURE_CHARACTERISTIC:
                parseTemperatureCharacteristic(action, characteristic, intent, format, shift);
                break;
            case SUPSIGattAttributes.SAMPLING_CHARACTERISTIC:
                parseSamplingCharacteristic(action, characteristic, intent, format, shift);
                break;
            case SUPSIGattAttributes.LED1_CHARACTERISTIC:
                parseLED1Characteristic(action, characteristic, intent, format, shift);
                break;
            default:
                parseUnknownCharacteristic(action, characteristic, intent);
                break;
        }
        sendBroadcast(intent);
    }

    private void parseTemperatureCharacteristic(String action, BluetoothGattCharacteristic characteristic, Intent intent, int format, int shift) {
        int temperatureValue = characteristic.getIntValue(format, 0);
        Log.d(TAG, String.format("*** CURRENT SAMPLING [%s] Action [%s] Extra [%d]",
                characteristic.getUuid(), action, temperatureValue));
        intent.putExtra(EXTRA_DATA, temperatureValue);
    }

    private void parseSamplingCharacteristic(String action, BluetoothGattCharacteristic characteristic, Intent intent, int format, int shift) {
        mSamplingValue = characteristic.getIntValue(format, 0);
        Log.d(TAG, String.format("*** CURRENT SAMPLING [%s] Action [%s] Extra [%d]",
                characteristic.getUuid(), action, mSamplingValue));
        intent.putExtra(EXTRA_DATA, mSamplingValue);
    }

    private void parseLED1Characteristic(String action, BluetoothGattCharacteristic characteristic, Intent intent, int format, int shift) {
        mLED1Value = characteristic.getIntValue(format, 0);
        Log.d(TAG, String.format("*** CURRENT LED1 [%s] Action [%s] Extra [%d]",
                characteristic.getUuid(), action, mLED1Value));
        intent.putExtra(EXTRA_DATA, mLED1Value);
    }

    private void parseCurrentTimeCharacteristic(String action, BluetoothGattCharacteristic characteristic, Intent intent, int format, int shift) {
        final byte[] data = characteristic.getValue();
        int yearLow = characteristic.getIntValue(format, 0);
        int yearHi = characteristic.getIntValue(format, 1);
        int year = (yearHi << shift) + yearLow;

        LocalDateTime extra = LocalDateTime.of(year, data[2], data[3], data[4], data[5], data[6]);
        Log.d(TAG, String.format("*** CURRENT TIME CHAR [%s] Action [%s] Extra [%s]",
                characteristic.getUuid(), action, extra));
        intent.putExtra(EXTRA_DATA, extra);
    }

    private void parseUnknownCharacteristic(String action, BluetoothGattCharacteristic characteristic, Intent intent) {
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));

            String extra =  new String(data) + "\n" + stringBuilder.toString();
            Log.d(TAG, String.format("*** Characteristic [%s] Action [%s] Extra [%s]",
                    characteristic.getUuid(), action, extra));
            intent.putExtra(EXTRA_DATA, extra);
        }
    }

    private int getCharacteristicFormat(BluetoothGattCharacteristic characteristic) {
        int flag = characteristic.getProperties();
        return ((flag & 0x01) != 0) ?
                BluetoothGattCharacteristic.FORMAT_UINT16 :
                BluetoothGattCharacteristic.FORMAT_UINT8;
    }

    public void asyncReadCurrentTime() {
        readCharacteristic(mCurrentTimeChar);
    }

    public void asyncReadSampling() {
        readCharacteristic(mSamplingCharacteristic);
    }

    public void writeSampling(int value) {
        mSamplingCharacteristic.setValue(value, BluetoothGattCharacteristic.FORMAT_UINT8, 0 );
        writeCharacteristic(mSamplingCharacteristic);
    }

    public void asyncReadTemperature() {
        readCharacteristic(mTemperatureCharacteristic);
    }

    public void asyncReadLED1() {
        readCharacteristic(mLED1Characteristic);
    }

    public void toggletLED1() {
        mLED1Value = mLED1Value ^ 1;
        mLED1Characteristic.setValue(mLED1Value, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        writeCharacteristic(mLED1Characteristic);
    }


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                broadcastUpdate(ACTION_GATT_CONNECTING);
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if (characteristic == null) {
            Log.w(TAG, "Characteristic is null");
            return;
        }

        mBluetoothGatt.readCharacteristic(characteristic);
    }

    private void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if (characteristic == null) {
            Log.w(TAG, "Characteristic is null");
            return;
        }

        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
//        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                UUID_CLIENT_CHARACTERISTIC_CONFIG);
//        if(descriptor != null) {
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        } else
//            Log.w(TAG, "**** SetCharNotification - No Client Descriptor for " +
//                    characteristic.getUuid());
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}
