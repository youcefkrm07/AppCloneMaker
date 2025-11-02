package com.appclonemaker.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceProfileManager {
    
    private static DeviceProfileManager instance;
    private List<DeviceProfile> deviceProfiles;
    
    public interface DeviceProfileListener {
        void onProfileSelected(String profileName);
        void onProfilePropertiesLoaded(Map<String, String> properties);
    }
    
    public static class DeviceProfile {
        private String displayName;
        private Map<String, String> properties;
        
        public DeviceProfile(String displayName) {
            this.displayName = displayName;
            this.properties = new HashMap<>();
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public Map<String, String> getProperties() {
            return properties;
        }
        
        public void setProperty(String key, String value) {
            properties.put(key, value);
        }
        
        public String getProperty(String key) {
            return properties.get(key);
        }
    }
    
    private DeviceProfileManager() {
        deviceProfiles = new ArrayList<>();
        initializeDeviceProfiles();
    }
    
    public static DeviceProfileManager getInstance() {
        if (instance == null) {
            instance = new DeviceProfileManager();
        }
        return instance;
    }
    
    private void initializeDeviceProfiles() {
        // Sample device profiles based on the HTML data
        addDeviceProfile("Google Pixel 8 Pro", createPixel8ProProps());
        addDeviceProfile("Google Pixel 7a", createPixel7aProps());
        addDeviceProfile("Samsung Galaxy S24 Ultra", createS24UltraProps());
        addDeviceProfile("Samsung Galaxy A54 5G", createA54Props());
        addDeviceProfile("OnePlus 12", createOnePlus12Props());
        addDeviceProfile("OnePlus Nord 3 5G", createNord3Props());
        addDeviceProfile("Xiaomi 14 Ultra", createXiaomi14Props());
        addDeviceProfile("Redmi Note 13 Pro 5G", createRedmiNote13Props());
        addDeviceProfile("Asus ROG Phone 7 series", createROGPhone7Props());
        addDeviceProfile("Asus Zenfone 10", createZenfone10Props());
    }
    
    private void addDeviceProfile(String displayName, Map<String, String> properties) {
        DeviceProfile profile = new DeviceProfile(displayName);
        profile.getProperties().putAll(properties);
        deviceProfiles.add(profile);
    }
    
    private Map<String, String> createPixel8ProProps() {
        Map<String, String> props = new HashMap<>();
        props.put("buildPropsDeviceName", "husky");
        props.put("buildPropsManufacturer", "Google");
        props.put("buildPropsBrand", "google");
        props.put("buildPropsModel", "Pixel 8 Pro");
        props.put("buildPropsProduct", "husky");
        props.put("buildPropsDevice", "husky");
        props.put("buildPropsBoard", "husky");
        props.put("buildPropsRadio", "");
        props.put("buildPropsHardware", "husky");
        return props;
    }
    
    private Map<String, String> createPixel7aProps() {
        Map<String, String> props = new HashMap<>();
        props.put("buildPropsDeviceName", "lynx");
        props.put("buildPropsManufacturer", "Google");
        props.put("buildPropsBrand", "google");
        props.put("buildPropsModel", "Pixel 7a");
        props.put("buildPropsProduct", "lynx");
        props.put("buildPropsDevice", "lynx");
        props.put("buildPropsBoard", "lynx");
        props.put("buildPropsRadio", "");
        props.put("buildPropsHardware", "lynx");
        return props;
    }
    
    private Map<String, String> createS24UltraProps() {
        Map<String, String> props = new HashMap<>();
        props.put("buildPropsDeviceName", "e3q");
        props.put("buildPropsManufacturer", "Samsung");
        props.put("buildPropsBrand", "samsung");
        props.put("buildPropsModel", "SM-S928B");
        props.put("buildPropsProduct", "e3q");
        props.put("buildPropsDevice", "e3q");
        props.put("buildPropsBoard", "e3q");
        props.put("buildPropsRadio", "");
        props.put("buildPropsHardware", "e3q");
        return props;
    }
    
    private Map<String, String> createA54Props() {
        Map<String, String> props = new HashMap<>();
        props.put("buildPropsDeviceName", "a54x");
        props.put("buildPropsManufacturer", "Samsung");
        props.put("buildPropsBrand", "samsung");
        props.put("buildPropsModel", "SM-A546B");
        props.put("buildPropsProduct", "a54x");
        props.put("buildPropsDevice", "a54x");
        props.put("buildPropsBoard", "a54x");
        props.put("buildPropsRadio", "");
        props.put("buildPropsHardware", "a54x");
        return props;
    }
    
    private Map<String, String> createOnePlus12Props() {
        Map<String, String> props = new HashMap<>();
        props.put("buildPropsDeviceName", "OP5929L1");
        props.put("buildPropsManufacturer", "OnePlus");
        props.put("buildPropsBrand", "OnePlus");
        props.put("buildPropsModel", "CPH2449");
        props.put("buildPropsProduct", "OP5929L1");
        props.put("buildPropsDevice", "OP5929L1");
        props.put("buildPropsBoard", "OP5929L1");
        props.put("buildPropsRadio", "");
        props.put("buildPropsHardware", "OP5929L1");
        return props;
    }
    
    private Map<String, String> createNord3Props() {
        Map<String, String> props = new HashMap<>();
        props.put("buildPropsDeviceName", "OP556FL1");
        props.put("buildPropsManufacturer", "OnePlus");
        props.put("buildPropsBrand", "OnePlus");
        props.put("buildPropsModel", "CPH2383");
        props.put("buildPropsProduct", "OP556FL1");
        props.put("buildPropsDevice", "OP556FL1");
        props.put("buildPropsBoard", "OP556FL1");
        props.put("buildPropsRadio", "");
        props.put("buildPropsHardware", "OP556FL1");
        return props;
    }
    
    private Map<String, String> createXiaomi14Props() {
        Map<String, String> props = new HashMap<>();
        props.put("buildPropsDeviceName", "aurora");
        props.put("buildPropsManufacturer", "Xiaomi");
        props.put("buildPropsBrand", "Xiaomi");
        props.put("buildPropsModel", "23127PN0CC");
        props.put("buildPropsProduct", "aurora");
        props.put("buildPropsDevice", "aurora");
        props.put("buildPropsBoard", "aurora");
        props.put("buildPropsRadio", "");
        props.put("buildPropsHardware", "aurora");
        return props;
    }
    
    private Map<String, String> createRedmiNote13Props() {
        Map<String, String> props = new HashMap<>();
        props.put("buildPropsDeviceName", "garnet");
        props.put("buildPropsManufacturer", "Redmi");
        props.put("buildPropsBrand", "Redmi");
        props.put("buildPropsModel", "2312DRA50G");
        props.put("buildPropsProduct", "garnet");
        props.put("buildPropsDevice", "garnet");
        props.put("buildPropsBoard", "garnet");
        props.put("buildPropsRadio", "");
        props.put("buildPropsHardware", "garnet");
        return props;
    }
    
    private Map<String, String> createROGPhone7Props() {
        Map<String, String> props = new HashMap<>();
        props.put("buildPropsDeviceName", "ASUS_AI2205");
        props.put("buildPropsManufacturer", "Asus");
        props.put("buildPropsBrand", "asus");
        props.put("buildPropsModel", "ASUS_AI2205");
        props.put("buildPropsProduct", "ASUS_AI2205");
        props.put("buildPropsDevice", "ASUS_AI2205");
        props.put("buildPropsBoard", "ASUS_AI2205");
        props.put("buildPropsRadio", "");
        props.put("buildPropsHardware", "ASUS_AI2205");
        return props;
    }
    
    private Map<String, String> createZenfone10Props() {
        Map<String, String> props = new HashMap<>();
        props.put("buildPropsDeviceName", "ASUS_AI2302");
        props.put("buildPropsManufacturer", "Asus");
        props.put("buildPropsBrand", "asus");
        props.put("buildPropsModel", "ASUS_AI2302");
        props.put("buildPropsProduct", "ASUS_AI2302");
        props.put("buildPropsDevice", "ASUS_AI2302");
        props.put("buildPropsBoard", "ASUS_AI2302");
        props.put("buildPropsRadio", "");
        props.put("buildPropsHardware", "ASUS_AI2302");
        return props;
    }
    
    public List<DeviceProfile> getDeviceProfiles() {
        return new ArrayList<>(deviceProfiles);
    }
    
    public DeviceProfile getDeviceProfile(String displayName) {
        for (DeviceProfile profile : deviceProfiles) {
            if (profile.getDisplayName().equals(displayName)) {
                return profile;
            }
        }
        return null;
    }
    
    public void showDeviceProfileDialog(Context context, DeviceProfileListener listener) {
        if (deviceProfiles.isEmpty()) {
            return;
        }
        
        String[] profileNames = new String[deviceProfiles.size()];
        for (int i = 0; i < deviceProfiles.size(); i++) {
            profileNames[i] = deviceProfiles.get(i).getDisplayName();
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Device Profile");
        builder.setItems(profileNames, (dialog, which) -> {
            DeviceProfile selectedProfile = deviceProfiles.get(which);
            if (listener != null) {
                listener.onProfileSelected(selectedProfile.getDisplayName());
                listener.onProfilePropertiesLoaded(selectedProfile.getProperties());
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    public void showProfileDetailsDialog(Context context, DeviceProfile profile) {
        if (profile == null) return;
        
        StringBuilder details = new StringBuilder();
        details.append("Device Profile: ").append(profile.getDisplayName()).append("\n\n");
        
        for (Map.Entry<String, String> entry : profile.getProperties().entrySet()) {
            details.append(formatPropertyName(entry.getKey()))
                   .append(": ")
                   .append(entry.getValue())
                   .append("\n");
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Profile Details");
        builder.setMessage(details.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private String formatPropertyName(String key) {
        return key.replace("buildProps", "")
                 .replaceAll("([A-Z])", " $1")
                 .replaceAll("^.", str -> str.toUpperCase())
                 .trim();
    }
    
    public void addCustomDeviceProfile(String displayName, Map<String, String> properties) {
        DeviceProfile profile = new DeviceProfile(displayName);
        profile.getProperties().putAll(properties);
        deviceProfiles.add(profile);
    }
    
    public void removeDeviceProfile(String displayName) {
        deviceProfiles.removeIf(profile -> profile.getDisplayName().equals(displayName));
    }
    
    public Map<String, String> getRandomDeviceProperties() {
        if (deviceProfiles.isEmpty()) {
            return new HashMap<>();
        }
        
        int randomIndex = (int) (Math.random() * deviceProfiles.size());
        DeviceProfile randomProfile = deviceProfiles.get(randomIndex);
        
        Map<String, String> properties = new HashMap<>(randomProfile.getProperties());
        
        // Add some randomization if needed
        String originalModel = properties.get("buildPropsModel");
        if (originalModel != null) {
            String randomizedModel = originalModel + "_" + System.currentTimeMillis() % 1000;
            properties.put("buildPropsModel", randomizedModel);
        }
        
        return properties;
    }
    
    public List<String> getSupportedSdkVersions(String deviceName) {
        // Return supported SDK versions for specific devices
        Map<String, List<String>> sdkVersions = new HashMap<>();
        
        // Sample SDK version mappings
        sdkVersions.put("Pixel 8 Pro", java.util.Arrays.asList("34", "35"));
        sdkVersions.put("Pixel 7a", java.util.Arrays.asList("33", "34"));
        sdkVersions.put("Galaxy S24 Ultra", java.util.Arrays.asList("34"));
        sdkVersions.put("Galaxy A54 5G", java.util.Arrays.asList("33", "34"));
        sdkVersions.put("OnePlus 12", java.util.Arrays.asList("34"));
        sdkVersions.put("OnePlus Nord 3 5G", java.util.Arrays.asList("33", "34"));
        sdkVersions.put("Xiaomi 14 Ultra", java.util.Arrays.asList("34"));
        sdkVersions.put("Redmi Note 13 Pro 5G", java.util.Arrays.asList("33", "34"));
        sdkVersions.put("Asus ROG Phone 7 series", java.util.Arrays.asList("34"));
        sdkVersions.put("Asus Zenfone 10", java.util.Arrays.asList("34"));
        
        return sdkVersions.getOrDefault(deviceName, java.util.Arrays.asList("28", "29", "30", "31", "32", "33", "34"));
    }
}