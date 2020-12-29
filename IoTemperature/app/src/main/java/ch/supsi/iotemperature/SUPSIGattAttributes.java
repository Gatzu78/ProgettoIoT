package ch.supsi.iotemperature;

import java.util.HashMap;

public class SUPSIGattAttributes {
    public static final String KEY_DEVICE_NAME = "DEVICE_NAME";
    public static final String KEY_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private static HashMap<String, String> attributes = new HashMap();

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static String MI_BAND2_BASIC_SERVICE = "0000fee0-0000-1000-8000-00805f9b34fb";
    public static String CURRENT_TIME_CHAR = "00002a2b-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_NAME_CHAR = "00002a00-0000-1000-8000-00805f9b34fb";

    public static String LED_SERVICE = "f0001110-0451-4000-b000-000000000000";
    public static String LED0_CHARACTERISTIC = "f0001111-0451-4000-b000-000000000000";
    public static String LED1_CHARACTERISTIC = "f0001112-0451-4000-b000-000000000000";

    static {
        // MI_BAND2_BASIC_SERVICE
        attributes.put(MI_BAND2_BASIC_SERVICE, "MI Band Basic Service");
        attributes.put(CURRENT_TIME_CHAR, "Current Time Characteristic");
        attributes.put(DEVICE_NAME_CHAR, "Device Name Characteristic");

        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service Service");
        attributes.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Level Characteristic");
        attributes.put("00002a1a-0000-1000-8000-00805f9b34fb", "Battery Power State Characteristic");
        attributes.put("00002a1b-0000-1000-8000-00805f9b34fb", "Battery Level State Characteristic");

        // SUPSI BLE
        attributes.put(LED_SERVICE, "SUPSI LED Service");
        attributes.put(LED0_CHARACTERISTIC, "LED0 Characteristic");
        attributes.put(LED1_CHARACTERISTIC, "LED1 Characteristic");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
