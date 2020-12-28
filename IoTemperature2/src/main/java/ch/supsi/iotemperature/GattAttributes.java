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

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_SERVICE ="0000180d-0000-1000-8000-00805f9b34fb";
    public static String HEART_RATE_MEASUREMENT_CHAR = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String HEART_RATE_CONTROLPOINT_CHAR = "00002a39-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static String LED_SERVICE = "F0001110-0451-4000-B000-000000000000";
    public static String LED0_CHARACTERISTIC = "F0001111-0451-4000-B000-000000000000";
    public static String LED1_CHARACTERISTIC = "F0001112-0451-4000-B000-000000000000";

    static {
        // SUPSI BLE
        attributes.put(LED_SERVICE, "SUPSI LED Service");
        attributes.put(LED0_CHARACTERISTIC, "LED0 Characteristic");
        attributes.put(LED1_CHARACTERISTIC, "LED1 Characteristic");

        // Sample Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access Service");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute Service");
        attributes.put("00001802-0000-1000-8000-00805f9b34fb", "Immediate Alert Service");
        attributes.put("00001803-0000-1000-8000-00805f9b34fb", "Link Loss Service");
        attributes.put("00001804-0000-1000-8000-00805f9b34fb", "Tx Power Service");
        attributes.put("00001805-0000-1000-8000-00805f9b34fb", "Current Time Service Service");
        attributes.put("00001806-0000-1000-8000-00805f9b34fb", "Reference Time Update Service Service");
        attributes.put("00001807-0000-1000-8000-00805f9b34fb", "Next DST Change Service Service");
        attributes.put("00001808-0000-1000-8000-00805f9b34fb", "Glucose Service");
        attributes.put("00001809-0000-1000-8000-00805f9b34fb", "Health Thermometer Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(HEART_RATE_SERVICE, "Heart Rate Service");
        attributes.put("0000180e-0000-1000-8000-00805f9b34fb", "Phone Alert Status Service Service");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service Service");
        attributes.put("00001810-0000-1000-8000-00805f9b34fb", "Blood Pressure Service");
        attributes.put("00001811-0000-1000-8000-00805f9b34fb", "Alert Notification Service Service");
        attributes.put("00001812-0000-1000-8000-00805f9b34fb", "Human Interface Device Service");
        attributes.put("00001813-0000-1000-8000-00805f9b34fb", "Scan Parameters Service");
        attributes.put("00001814-0000-1000-8000-00805f9b34fb", "Running Speed and Cadence Service");
        attributes.put("00001815-0000-1000-8000-00805f9b34fb", "Automation IO Service");
        attributes.put("00001816-0000-1000-8000-00805f9b34fb", "Cycling Speed and Cadence Service");
        attributes.put("00001818-0000-1000-8000-00805f9b34fb", "Cycling Power Service");
        attributes.put("00001819-0000-1000-8000-00805f9b34fb", "Location and Navigation Service");
        attributes.put("0000181a-0000-1000-8000-00805f9b34fb", "Environmental Sensing Service");
        attributes.put("0000181b-0000-1000-8000-00805f9b34fb", "Body Composition Service");
        attributes.put("0000181c-0000-1000-8000-00805f9b34fb", "User Data Service");
        attributes.put("0000181d-0000-1000-8000-00805f9b34fb", "Weight Scale Service");
        attributes.put("0000181e-0000-1000-8000-00805f9b34fb", "Bond Management Service Service");
        attributes.put("0000181f-0000-1000-8000-00805f9b34fb", "Continuous Glucose Monitoring Service");
        attributes.put("00001820-0000-1000-8000-00805f9b34fb", "Internet Protocol Support Service Service");
        attributes.put("00001821-0000-1000-8000-00805f9b34fb", "Indoor Positioning Service");
        attributes.put("00001822-0000-1000-8000-00805f9b34fb", "Pulse Oximeter Service Service");
        attributes.put("00001823-0000-1000-8000-00805f9b34fb", "HTTP Proxy Service");
        attributes.put("00001824-0000-1000-8000-00805f9b34fb", "Transport Discovery Service");
        attributes.put("00001825-0000-1000-8000-00805f9b34fb", "Object Transfer Service Service");
        attributes.put("00001826-0000-1000-8000-00805f9b34fb", "Fitness Machine Service");
        attributes.put("00001827-0000-1000-8000-00805f9b34fb", "Mesh Provisioning Service Service");
        attributes.put("00001828-0000-1000-8000-00805f9b34fb", "Mesh Proxy Service Service");
        attributes.put("00001829-0000-1000-8000-00805f9b34fb", "Reconnection Configuration Service");

        // Characteristics
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name Characteristic");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance Characteristic");
        attributes.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Privacy Flag Characteristic");
        attributes.put("00002a03-0000-1000-8000-00805f9b34fb", "Reconnection Address Characteristic");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters Characteristic");
        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed Characteristic");
        attributes.put("00002a06-0000-1000-8000-00805f9b34fb", "Alert Level Characteristic");
        attributes.put("00002a07-0000-1000-8000-00805f9b34fb", "Tx Power Level Characteristic");
        attributes.put("00002a08-0000-1000-8000-00805f9b34fb", "Date Time Characteristic");
        attributes.put("00002a09-0000-1000-8000-00805f9b34fb", "Day of Week Characteristic");
        attributes.put("00002a0a-0000-1000-8000-00805f9b34fb", "Day Date Time Characteristic");
        attributes.put("00002a0b-0000-1000-8000-00805f9b34fb", "Exact Time 100 Characteristic");
        attributes.put("00002a0c-0000-1000-8000-00805f9b34fb", "Exact Time 256 Characteristic");
        attributes.put("00002a0d-0000-1000-8000-00805f9b34fb", "DST Offset Characteristic");
        attributes.put("00002a0e-0000-1000-8000-00805f9b34fb", "Time Zone Characteristic");
        attributes.put("00002a0f-0000-1000-8000-00805f9b34fb", "Local Time Information Characteristic");
        attributes.put("00002a10-0000-1000-8000-00805f9b34fb", "Secondary Time Zone Characteristic");
        attributes.put("00002a11-0000-1000-8000-00805f9b34fb", "Time with DST Characteristic");
        attributes.put("00002a12-0000-1000-8000-00805f9b34fb", "Time Accuracy Characteristic");
        attributes.put("00002a13-0000-1000-8000-00805f9b34fb", "Time Source Characteristic");
        attributes.put("00002a14-0000-1000-8000-00805f9b34fb", "Reference Time Information Characteristic");
        attributes.put("00002a15-0000-1000-8000-00805f9b34fb", "Time Broadcast Characteristic");
        attributes.put("00002a16-0000-1000-8000-00805f9b34fb", "Time Update Control Point Characteristic");
        attributes.put("00002a17-0000-1000-8000-00805f9b34fb", "Time Update State Characteristic");
        attributes.put("00002a18-0000-1000-8000-00805f9b34fb", "Glucose Measurement Characteristic");
        attributes.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Level Characteristic");
        attributes.put("00002a1a-0000-1000-8000-00805f9b34fb", "Battery Power State Characteristic");
        attributes.put("00002a1b-0000-1000-8000-00805f9b34fb", "Battery Level State Characteristic");
        attributes.put("00002a1c-0000-1000-8000-00805f9b34fb", "Temperature Measurement Characteristic");
        attributes.put("00002a1d-0000-1000-8000-00805f9b34fb", "Temperature Type Characteristic");
        attributes.put("00002a1e-0000-1000-8000-00805f9b34fb", "Intermediate Temperature Characteristic");
        attributes.put("00002a1f-0000-1000-8000-00805f9b34fb", "Temperature Celsius Characteristic");
        attributes.put("00002a20-0000-1000-8000-00805f9b34fb", "Temperature Fahrenheit Characteristic");
        attributes.put("00002a21-0000-1000-8000-00805f9b34fb", "Measurement Interval Characteristic");
        attributes.put("00002a22-0000-1000-8000-00805f9b34fb", "Boot Keyboard Input Report Characteristic");
        attributes.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID Characteristic");
        attributes.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String Characteristic");
        attributes.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String Characteristic");
        attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String Characteristic");
        attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String Characteristic");
        attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String Characteristic");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String Characteristic");
        attributes.put("00002a2a-0000-1000-8000-00805f9b34fb", "IEEE 11073-20601 Regulatory Certification Data List Characteristic");
        attributes.put("00002a2b-0000-1000-8000-00805f9b34fb", "Current Time Characteristic");
        attributes.put("00002a2c-0000-1000-8000-00805f9b34fb", "Magnetic Declination Characteristic");
        attributes.put("00002a2f-0000-1000-8000-00805f9b34fb", "Position 2D Characteristic");
        attributes.put("00002a30-0000-1000-8000-00805f9b34fb", "Position 3D Characteristic");
        attributes.put("00002a31-0000-1000-8000-00805f9b34fb", "Scan Refresh Characteristic");
        attributes.put("00002a32-0000-1000-8000-00805f9b34fb", "Boot Keyboard Output Report Characteristic");
        attributes.put("00002a33-0000-1000-8000-00805f9b34fb", "Boot Mouse Input Report Characteristic");
        attributes.put("00002a34-0000-1000-8000-00805f9b34fb", "Glucose Measurement Context Characteristic");
        attributes.put("00002a35-0000-1000-8000-00805f9b34fb", "Blood Pressure Measurement Characteristic");
        attributes.put("00002a36-0000-1000-8000-00805f9b34fb", "Intermediate Cuff Pressure Characteristic");
        attributes.put(HEART_RATE_MEASUREMENT_CHAR, "Heart Rate Measurement Characteristic");
        attributes.put("00002a38-0000-1000-8000-00805f9b34fb", "Body Sensor Location Characteristic");
        attributes.put(HEART_RATE_CONTROLPOINT_CHAR, "Heart Rate Control Point Characteristic");
        attributes.put("00002a3a-0000-1000-8000-00805f9b34fb", "Removable Characteristic");
        attributes.put("00002a3b-0000-1000-8000-00805f9b34fb", "Service Required Characteristic");
        attributes.put("00002a3c-0000-1000-8000-00805f9b34fb", "Scientific Temperature Celsius Characteristic");
        attributes.put("00002a3d-0000-1000-8000-00805f9b34fb", "String Characteristic");
        attributes.put("00002a3e-0000-1000-8000-00805f9b34fb", "Network Availability Characteristic");
        attributes.put("00002a3f-0000-1000-8000-00805f9b34fb", "Alert Status Characteristic");
        attributes.put("00002a40-0000-1000-8000-00805f9b34fb", "Ringer Control point Characteristic");
        attributes.put("00002a41-0000-1000-8000-00805f9b34fb", "Ringer Setting Characteristic");
        attributes.put("00002a42-0000-1000-8000-00805f9b34fb", "Alert Category ID Bit Mask Characteristic");
        attributes.put("00002a43-0000-1000-8000-00805f9b34fb", "Alert Category ID Characteristic");
        attributes.put("00002a44-0000-1000-8000-00805f9b34fb", "Alert Notification Control Point Characteristic");
        attributes.put("00002a45-0000-1000-8000-00805f9b34fb", "Unread Alert Status Characteristic");
        attributes.put("00002a46-0000-1000-8000-00805f9b34fb", "New Alert Characteristic");
        attributes.put("00002a47-0000-1000-8000-00805f9b34fb", "Supported New Alert Category Characteristic");
        attributes.put("00002a48-0000-1000-8000-00805f9b34fb", "Supported Unread Alert Category Characteristic");
        attributes.put("00002a49-0000-1000-8000-00805f9b34fb", "Blood Pressure Feature Characteristic");
        attributes.put("00002a4a-0000-1000-8000-00805f9b34fb", "HID Information Characteristic");
        attributes.put("00002a4b-0000-1000-8000-00805f9b34fb", "Report Map Characteristic");
        attributes.put("00002a4c-0000-1000-8000-00805f9b34fb", "HID Control Point Characteristic");
        attributes.put("00002a4d-0000-1000-8000-00805f9b34fb", "Report Characteristic");
        attributes.put("00002a4e-0000-1000-8000-00805f9b34fb", "Protocol Mode Characteristic");
        attributes.put("00002a4f-0000-1000-8000-00805f9b34fb", "Scan Interval Window Characteristic");
        attributes.put("00002a50-0000-1000-8000-00805f9b34fb", "PnP ID Characteristic");
        attributes.put("00002a51-0000-1000-8000-00805f9b34fb", "Glucose Feature Characteristic");
        attributes.put("00002a52-0000-1000-8000-00805f9b34fb", "Record Access Control Point Characteristic");
        attributes.put("00002a53-0000-1000-8000-00805f9b34fb", "RSC Measurement Characteristic");
        attributes.put("00002a54-0000-1000-8000-00805f9b34fb", "RSC Feature Characteristic");
        attributes.put("00002a55-0000-1000-8000-00805f9b34fb", "SC Control Point Characteristic");
        attributes.put("00002a56-0000-1000-8000-00805f9b34fb", "Digital Characteristic");
        attributes.put("00002a57-0000-1000-8000-00805f9b34fb", "Digital Output Characteristic");
        attributes.put("00002a58-0000-1000-8000-00805f9b34fb", "Analog Characteristic");
        attributes.put("00002a59-0000-1000-8000-00805f9b34fb", "Analog Output Characteristic");
        attributes.put("00002a5a-0000-1000-8000-00805f9b34fb", "Aggregate Characteristic");
        attributes.put("00002a5b-0000-1000-8000-00805f9b34fb", "CSC Measurement Characteristic");
        attributes.put("00002a5c-0000-1000-8000-00805f9b34fb", "CSC Feature Characteristic");
        attributes.put("00002a5d-0000-1000-8000-00805f9b34fb", "Sensor Location Characteristic");
        attributes.put("00002a5e-0000-1000-8000-00805f9b34fb", "PLX Spot-Check Measurement Characteristic");
        attributes.put("00002a5f-0000-1000-8000-00805f9b34fb", "PLX Continuous Measurement Characteristic Characteristic");
        attributes.put("00002a60-0000-1000-8000-00805f9b34fb", "PLX Features Characteristic");
        attributes.put("00002a62-0000-1000-8000-00805f9b34fb", "Pulse Oximetry Control Point Characteristic");
        attributes.put("00002a63-0000-1000-8000-00805f9b34fb", "Cycling Power Measurement Characteristic");
        attributes.put("00002a64-0000-1000-8000-00805f9b34fb", "Cycling Power Vector Characteristic");
        attributes.put("00002a65-0000-1000-8000-00805f9b34fb", "Cycling Power Feature Characteristic");
        attributes.put("00002a66-0000-1000-8000-00805f9b34fb", "Cycling Power Control Point Characteristic");
        attributes.put("00002a67-0000-1000-8000-00805f9b34fb", "Location and Speed Characteristic Characteristic");
        attributes.put("00002a68-0000-1000-8000-00805f9b34fb", "Navigation Characteristic");
        attributes.put("00002a69-0000-1000-8000-00805f9b34fb", "Position Quality Characteristic");
        attributes.put("00002a6a-0000-1000-8000-00805f9b34fb", "LN Feature Characteristic");
        attributes.put("00002a6b-0000-1000-8000-00805f9b34fb", "LN Control Point Characteristic");
        attributes.put("00002a6c-0000-1000-8000-00805f9b34fb", "Elevation Characteristic");
        attributes.put("00002a6d-0000-1000-8000-00805f9b34fb", "Pressure Characteristic");
        attributes.put("00002a6e-0000-1000-8000-00805f9b34fb", "Temperature Characteristic");
        attributes.put("00002a6f-0000-1000-8000-00805f9b34fb", "Humidity Characteristic");
        attributes.put("00002a70-0000-1000-8000-00805f9b34fb", "True Wind Speed Characteristic");
        attributes.put("00002a71-0000-1000-8000-00805f9b34fb", "True Wind Direction Characteristic");
        attributes.put("00002a72-0000-1000-8000-00805f9b34fb", "Apparent Wind Speed Characteristic");
        attributes.put("00002a73-0000-1000-8000-00805f9b34fb", "Apparent Wind Direction Characteristic");
        attributes.put("00002a74-0000-1000-8000-00805f9b34fb", "Gust Factor Characteristic");
        attributes.put("00002a75-0000-1000-8000-00805f9b34fb", "Pollen Concentration Characteristic");
        attributes.put("00002a76-0000-1000-8000-00805f9b34fb", "UV Index Characteristic");
        attributes.put("00002a77-0000-1000-8000-00805f9b34fb", "Irradiance Characteristic");
        attributes.put("00002a78-0000-1000-8000-00805f9b34fb", "Rainfall Characteristic");
        attributes.put("00002a79-0000-1000-8000-00805f9b34fb", "Wind Chill Characteristic");
        attributes.put("00002a7a-0000-1000-8000-00805f9b34fb", "Heat Index Characteristic");
        attributes.put("00002a7b-0000-1000-8000-00805f9b34fb", "Dew Point Characteristic");
        attributes.put("00002a7d-0000-1000-8000-00805f9b34fb", "Descriptor Value Changed Characteristic");
        attributes.put("00002a7e-0000-1000-8000-00805f9b34fb", "Aerobic Heart Rate Lower Limit Characteristic");
        attributes.put("00002a7f-0000-1000-8000-00805f9b34fb", "Aerobic Threshold Characteristic");
        attributes.put("00002a80-0000-1000-8000-00805f9b34fb", "Age Characteristic");
        attributes.put("00002a81-0000-1000-8000-00805f9b34fb", "Anaerobic Heart Rate Lower Limit Characteristic");
        attributes.put("00002a82-0000-1000-8000-00805f9b34fb", "Anaerobic Heart Rate Upper Limit Characteristic");
        attributes.put("00002a83-0000-1000-8000-00805f9b34fb", "Anaerobic Threshold Characteristic");
        attributes.put("00002a84-0000-1000-8000-00805f9b34fb", "Aerobic Heart Rate Upper Limit Characteristic");
        attributes.put("00002a85-0000-1000-8000-00805f9b34fb", "Date of Birth Characteristic");
        attributes.put("00002a86-0000-1000-8000-00805f9b34fb", "Date of Threshold Assessment Characteristic");
        attributes.put("00002a87-0000-1000-8000-00805f9b34fb", "Email Address Characteristic");
        attributes.put("00002a88-0000-1000-8000-00805f9b34fb", "Fat Burn Heart Rate Lower Limit Characteristic");
        attributes.put("00002a89-0000-1000-8000-00805f9b34fb", "Fat Burn Heart Rate Upper Limit Characteristic");
        attributes.put("00002a8a-0000-1000-8000-00805f9b34fb", "First Name Characteristic");
        attributes.put("00002a8b-0000-1000-8000-00805f9b34fb", "Five Zone Heart Rate Limits Characteristic");
        attributes.put("00002a8c-0000-1000-8000-00805f9b34fb", "Gender Characteristic");
        attributes.put("00002a8d-0000-1000-8000-00805f9b34fb", "Heart Rate Max Characteristic");
        attributes.put("00002a8e-0000-1000-8000-00805f9b34fb", "Height Characteristic");
        attributes.put("00002a8f-0000-1000-8000-00805f9b34fb", "Hip Circumference Characteristic");
        attributes.put("00002a90-0000-1000-8000-00805f9b34fb", "Last Name Characteristic");
        attributes.put("00002a91-0000-1000-8000-00805f9b34fb", "Maximum Recommended Heart Rate Characteristic");
        attributes.put("00002a92-0000-1000-8000-00805f9b34fb", "Resting Heart Rate Characteristic");
        attributes.put("00002a93-0000-1000-8000-00805f9b34fb", "Sport Type for Aerobic and Anaerobic Thresholds Characteristic");
        attributes.put("00002a94-0000-1000-8000-00805f9b34fb", "Three Zone Heart Rate Limits Characteristic");
        attributes.put("00002a95-0000-1000-8000-00805f9b34fb", "Two Zone Heart Rate Limit Characteristic");
        attributes.put("00002a96-0000-1000-8000-00805f9b34fb", "VO2 Max Characteristic");
        attributes.put("00002a97-0000-1000-8000-00805f9b34fb", "Waist Circumference Characteristic");
        attributes.put("00002a98-0000-1000-8000-00805f9b34fb", "Weight Characteristic");
        attributes.put("00002a99-0000-1000-8000-00805f9b34fb", "Database Change Increment Characteristic");
        attributes.put("00002a9a-0000-1000-8000-00805f9b34fb", "User Index Characteristic");
        attributes.put("00002a9b-0000-1000-8000-00805f9b34fb", "Body Composition Feature Characteristic");
        attributes.put("00002a9c-0000-1000-8000-00805f9b34fb", "Body Composition Measurement Characteristic");
        attributes.put("00002a9d-0000-1000-8000-00805f9b34fb", "Weight Measurement Characteristic");
        attributes.put("00002a9e-0000-1000-8000-00805f9b34fb", "Weight Scale Feature Characteristic");
        attributes.put("00002a9f-0000-1000-8000-00805f9b34fb", "User Control Point Characteristic");
        attributes.put("00002aa0-0000-1000-8000-00805f9b34fb", "Magnetic Flux Density - 2D Characteristic");
        attributes.put("00002aa1-0000-1000-8000-00805f9b34fb", "Magnetic Flux Density - 3D Characteristic");
        attributes.put("00002aa2-0000-1000-8000-00805f9b34fb", "Language Characteristic");
        attributes.put("00002aa3-0000-1000-8000-00805f9b34fb", "Barometric Pressure Trend Characteristic");
        attributes.put("00002aa4-0000-1000-8000-00805f9b34fb", "Bond Management Control Point Characteristic");
        attributes.put("00002aa5-0000-1000-8000-00805f9b34fb", "Bond Management Features Characteristic");
        attributes.put("00002aa6-0000-1000-8000-00805f9b34fb", "Central Address Resolution Characteristic");
        attributes.put("00002aa7-0000-1000-8000-00805f9b34fb", "CGM Measurement Characteristic");
        attributes.put("00002aa8-0000-1000-8000-00805f9b34fb", "CGM Feature Characteristic");
        attributes.put("00002aa9-0000-1000-8000-00805f9b34fb", "CGM Status Characteristic");
        attributes.put("00002aaa-0000-1000-8000-00805f9b34fb", "CGM Session Start Time Characteristic");
        attributes.put("00002aab-0000-1000-8000-00805f9b34fb", "CGM Session Run Time Characteristic");
        attributes.put("00002aac-0000-1000-8000-00805f9b34fb", "CGM Specific Ops Control Point Characteristic");
        attributes.put("00002aad-0000-1000-8000-00805f9b34fb", "Indoor Positioning Configuration Characteristic");
        attributes.put("00002aae-0000-1000-8000-00805f9b34fb", "Latitude Characteristic");
        attributes.put("00002aaf-0000-1000-8000-00805f9b34fb", "Longitude Characteristic");
        attributes.put("00002ab0-0000-1000-8000-00805f9b34fb", "Local North Coordinate Characteristic");
        attributes.put("00002ab1-0000-1000-8000-00805f9b34fb", "Local East Coordinate Characteristic");
        attributes.put("00002ab2-0000-1000-8000-00805f9b34fb", "Floor Number Characteristic");
        attributes.put("00002ab3-0000-1000-8000-00805f9b34fb", "Altitude Characteristic");
        attributes.put("00002ab4-0000-1000-8000-00805f9b34fb", "Uncertainty Characteristic");
        attributes.put("00002ab5-0000-1000-8000-00805f9b34fb", "Location Name Characteristic");
        attributes.put("00002ab6-0000-1000-8000-00805f9b34fb", "URI Characteristic");
        attributes.put("00002ab7-0000-1000-8000-00805f9b34fb", "HTTP Headers Characteristic");
        attributes.put("00002ab8-0000-1000-8000-00805f9b34fb", "HTTP Status Code Characteristic");
        attributes.put("00002ab9-0000-1000-8000-00805f9b34fb", "HTTP Entity Body Characteristic");
        attributes.put("00002aba-0000-1000-8000-00805f9b34fb", "HTTP Control Point Characteristic");
        attributes.put("00002abb-0000-1000-8000-00805f9b34fb", "HTTPS Security Characteristic");
        attributes.put("00002abc-0000-1000-8000-00805f9b34fb", "TDS Control Point Characteristic");
        attributes.put("00002abd-0000-1000-8000-00805f9b34fb", "OTS Feature Characteristic");
        attributes.put("00002abe-0000-1000-8000-00805f9b34fb", "Object Name Characteristic");
        attributes.put("00002abf-0000-1000-8000-00805f9b34fb", "Object Type Characteristic");
        attributes.put("00002ac0-0000-1000-8000-00805f9b34fb", "Object Size Characteristic");
        attributes.put("00002ac1-0000-1000-8000-00805f9b34fb", "Object First-Created Characteristic");
        attributes.put("00002ac2-0000-1000-8000-00805f9b34fb", "Object Last-Modified Characteristic");
        attributes.put("00002ac3-0000-1000-8000-00805f9b34fb", "Object ID Characteristic");
        attributes.put("00002ac4-0000-1000-8000-00805f9b34fb", "Object Properties Characteristic");
        attributes.put("00002ac5-0000-1000-8000-00805f9b34fb", "Object Action Control Point Characteristic");
        attributes.put("00002ac6-0000-1000-8000-00805f9b34fb", "Object List Control Point Characteristic");
        attributes.put("00002ac7-0000-1000-8000-00805f9b34fb", "Object List Filter Characteristic");
        attributes.put("00002ac8-0000-1000-8000-00805f9b34fb", "Object Changed Characteristic");
        attributes.put("00002ac9-0000-1000-8000-00805f9b34fb", "Resolvable Private Address Only Characteristic");
        attributes.put("00002acc-0000-1000-8000-00805f9b34fb", "Fitness Machine Feature Characteristic");
        attributes.put("00002acd-0000-1000-8000-00805f9b34fb", "Treadmill Data Characteristic");
        attributes.put("00002ace-0000-1000-8000-00805f9b34fb", "Cross Trainer Data Characteristic");
        attributes.put("00002acf-0000-1000-8000-00805f9b34fb", "Step Climber Data Characteristic");
        attributes.put("00002ad0-0000-1000-8000-00805f9b34fb", "Stair Climber Data Characteristic");
        attributes.put("00002ad1-0000-1000-8000-00805f9b34fb", "Rower Data Characteristic");
        attributes.put("00002ad2-0000-1000-8000-00805f9b34fb", "Indoor Bike Data Characteristic");
        attributes.put("00002ad3-0000-1000-8000-00805f9b34fb", "Training Status Characteristic");
        attributes.put("00002ad4-0000-1000-8000-00805f9b34fb", "Supported Speed Range Characteristic");
        attributes.put("00002ad5-0000-1000-8000-00805f9b34fb", "Supported Inclination Range Characteristic");
        attributes.put("00002ad6-0000-1000-8000-00805f9b34fb", "Supported Resistance Level Range Characteristic");
        attributes.put("00002ad7-0000-1000-8000-00805f9b34fb", "Supported Heart Rate Range Characteristic");
        attributes.put("00002ad8-0000-1000-8000-00805f9b34fb", "Supported Power Range Characteristic");
        attributes.put("00002ad9-0000-1000-8000-00805f9b34fb", "Fitness Machine Control Point Characteristic");
        attributes.put("00002ada-0000-1000-8000-00805f9b34fb", "Fitness Machine Status Characteristic");
        attributes.put("00002aed-0000-1000-8000-00805f9b34fb", "Date UTC Characteristic");
        attributes.put("00002b1d-0000-1000-8000-00805f9b34fb", "RC Feature Characteristic");
        attributes.put("00002b1e-0000-1000-8000-00805f9b34fb", "RC Settings Characteristic");
        attributes.put("00002b1f-0000-1000-8000-00805f9b34fb", "Reconnection Configuration Control Point Characteristic");
    }

    public static String lookup(String uuid) {
        return lookup(uuid, "--");
    }

    public static String lookup(UUID uuid) {
        return lookup(uuid.toString(), "--");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
