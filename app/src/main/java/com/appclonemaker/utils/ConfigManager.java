package com.appclonemaker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.appclonemaker.model.Category;
import com.appclonemaker.model.Setting;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfigManager {
    
    private Context context;
    private SharedPreferences prefs;
    private Gson gson;
    
    private static final String PREFS_NAME = "AppCloneMakerPrefs";
    private static final String KEY_CONFIG = "current_config";
    private static final String KEY_PACKAGE_NAME = "package_name";
    private static final String KEY_SPLIT_COUNT = "split_count";
    
    public ConfigManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }
    
    public boolean saveConfiguration() {
        try {
            // Get current configuration from the app
            Map<String, Object> config = buildConfigurationMap();
            
            // Save to SharedPreferences
            String configJson = gson.toJson(config);
            prefs.edit().putString(KEY_CONFIG, configJson).apply();
            
            // Save to file as backup
            saveToFile(config);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String exportConfiguration(List<Category> categories) {
        Map<String, Object> config = buildConfigurationMap(categories);
        return gson.toJson(config, Map.class);
    }
    
    public void setPackageName(String packageName) {
        prefs.edit().putString(KEY_PACKAGE_NAME, packageName).apply();
    }
    
    public String getPackageName() {
        return prefs.getString(KEY_PACKAGE_NAME, "");
    }
    
    public void setSplitCount(int count) {
        prefs.edit().putInt(KEY_SPLIT_COUNT, count).apply();
    }
    
    public int getSplitCount() {
        return prefs.getInt(KEY_SPLIT_COUNT, 101);
    }
    
    private Map<String, Object> buildConfigurationMap() {
        // This would typically get the current state from the UI
        // For now, return a sample configuration
        Map<String, Object> config = new HashMap<>();
        
        // Add various settings based on the HTML structure
        config.put("changeAndroidId", true);
        config.put("changeImei", false);
        config.put("changeAndroidSerial", false);
        config.put("changeWifiMacAddress", false);
        config.put("changeBluetoothMacAddress", false);
        config.put("changeImsi", false);
        config.put("changeGoogleAdvertisingId", false);
        config.put("changeGoogleServiceFrameworkId", false);
        config.put("changeFacebookAttributionId", false);
        config.put("changeAppSetId", false);
        config.put("changeOpenId", false);
        config.put("changeAmazonAdvertisingId", false);
        config.put("changeHuaweiAdvertisingId", false);
        config.put("changeLocale", "en_US");
        config.put("changeEthernetMacAddress", false);
        
        // Add more settings as needed...
        
        return config;
    }
    
    private Map<String, Object> buildConfigurationMap(List<Category> categories) {
        Map<String, Object> config = new HashMap<>();
        
        for (Category category : categories) {
            for (Setting setting : category.getSettings()) {
                processSettingForConfig(config, setting);
            }
        }
        
        return config;
    }
    
    private void processSettingForConfig(Map<String, Object> config, Setting setting) {
        String key = setting.getKey();
        Object value = setting.getValue();
        
        // Handle special cases based on setting type and key
        if (setting.hasCustomEditor()) {
            handleCustomSetting(config, setting);
        } else if (setting.getType() == Setting.SettingType.BOOLEAN) {
            config.put(key, setting.isEnabled());
        } else if (setting.getType() == Setting.SettingType.STRING) {
            config.put(key, value != null ? value.toString() : "");
        } else if (setting.getType() == Setting.SettingType.NUMBER) {
            config.put(key, value != null ? value : 0);
        } else if (setting.getType() == Setting.SettingType.ARRAY) {
            config.put(key, value);
        } else if (setting.getType() == Setting.SettingType.DROPDOWN) {
            config.put(key, value);
        }
    }
    
    private void handleCustomSetting(Map<String, Object> config, Setting setting) {
        String key = setting.getKey();
        Object value = setting.getValue();
        
        switch (key) {
            case "bundleAppData":
                config.put("bundleAppData", setting.isEnabled());
                if (setting.isEnabled()) {
                    config.put("bundleAppDataPath", getValueFromSetting("bundleAppDataPath", setting));
                    config.put("bundleAppDataPassword", getValueFromSetting("bundleAppDataPassword", setting));
                }
                break;
                
            case "deleteOnExit":
                config.put("deleteOnExit", setting.isEnabled());
                if (setting.isEnabled()) {
                    config.put("deleteFilesDirectoriesOnExit", getValueFromSetting("deleteFilesDirectoriesOnExit", setting));
                    config.put("securelyDeleteFilesDirectoriesOnExit", getValueFromSetting("securelyDeleteFilesDirectoriesOnExit", setting));
                }
                break;
                
            case "spoofLocation":
                config.put("spoofLocation", setting.isEnabled());
                if (setting.isEnabled()) {
                    config.put("spoofLocationLatitude", getValueFromSetting("spoofLocationLatitude", setting));
                    config.put("spoofLocationLongitude", getValueFromSetting("spoofLocationLongitude", setting));
                }
                break;
                
            case "buildsProps":
                config.put("buildsProps", setting.isEnabled());
                if (setting.isEnabled()) {
                    config.put("buildPropsDeviceName", getValueFromSetting("buildPropsDeviceName", setting));
                    config.put("buildPropsManufacturer", getValueFromSetting("buildPropsManufacturer", setting));
                    config.put("buildPropsBrand", getValueFromSetting("buildPropsBrand", setting));
                    config.put("buildPropsModel", getValueFromSetting("buildPropsModel", setting));
                }
                break;
                
            case "webViewUrlDataFilterList":
                config.put("webViewUrlDataFilterList", value);
                break;
                
            case "overrideSharedPreferences":
                config.put("overrideSharedPreferences", value);
                break;
                
            case "customBuildProps":
                config.put("customBuildProps", value);
                break;
                
            case "webViewCookies":
                config.put("webViewCookies", value);
                break;
                
            case "hostsBlocker":
                config.put("hostsBlocker", setting.isEnabled());
                break;
                
            default:
                // Handle other custom settings
                config.put(key, value);
                break;
        }
    }
    
    private Object getValueFromSetting(String childKey, Setting parentSetting) {
        // This would look up the child setting value
        // For now, return a default value
        return "";
    }
    
    private void saveToFile(Map<String, Object> config) {
        try {
            File configDir = new File(context.getExternalFilesDir(null), "configs");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            String packageName = getPackageName();
            if (packageName.isEmpty()) {
                packageName = "unknown_package";
            }
            
            File configFile = new File(configDir, packageName + "_cloneSettings.json");
            
            try (FileWriter writer = new FileWriter(configFile)) {
                String json = gson.toJson(config, Map.class);
                // Apply Unicode escaping like in the original
                json = applyUnicodeEscaping(json);
                writer.write(json);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String applyUnicodeEscaping(String json) {
        // Apply Unicode escaping for special characters
        return json.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    public Map<String, Object> loadConfiguration() {
        String configJson = prefs.getString(KEY_CONFIG, "");
        if (configJson.isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            return gson.fromJson(configJson, type);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    public void generateRandomValue(Setting setting) {
        String key = setting.getKey();
        String randomValue = "";
        
        switch (key) {
            case "changeAndroidId":
                randomValue = generateHex(16);
                break;
            case "changeImei":
                randomValue = generateLuhnCheckedImei();
                break;
            case "changeAndroidSerial":
                randomValue = Math.random() > 0.5 ? generateAlphanum(12) : generateHex(16);
                break;
            case "changeWifiMacAddress":
            case "changeBluetoothMacAddress":
            case "changeEthernetMacAddress":
                randomValue = generateMacAddress();
                break;
            case "changeGoogleAdvertisingId":
            case "changeFacebookAttributionId":
            case "changeAppSetId":
            case "changeOpenId":
            case "changeAmazonAdvertisingId":
            case "changeHuaweiAdvertisingId":
                randomValue = generateUuidV4();
                break;
            case "changeGoogleServiceFrameworkId":
                randomValue = generateHex(16);
                break;
            default:
                randomValue = "CUSTOM_" + UUID.randomUUID().toString().substring(0, 8);
                break;
        }
        
        setting.setValue(randomValue);
    }
    
    private String generateHex(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(Integer.toHexString((int)(Math.random() * 16)));
        }
        return sb.toString();
    }
    
    private String generateAlphanum(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int)(Math.random() * chars.length())));
        }
        return sb.toString();
    }
    
    private String generateUuidV4() {
        return UUID.randomUUID().toString();
    }
    
    private String generateMacAddress() {
        String[] ouiList = {"00:05:69", "00:1A:11", "00:E0:4C", "3C:5A:B4", "40:B8:9A", "BC:F5:AC", "D8:80:39"};
        String oui = ouiList[(int)(Math.random() * ouiList.length)];
        String host = String.format("%02X:%02X:%02X", 
                (int)(Math.random() * 256), 
                (int)(Math.random() * 256), 
                (int)(Math.random() * 256));
        return (oui + ":" + host).toUpperCase();
    }
    
    private String generateLuhnCheckedImei() {
        StringBuilder imeiBase = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            imeiBase.append((int)(Math.random() * 10));
        }
        
        // Calculate Luhn check digit
        int sum = 0;
        for (int i = 0; i < imeiBase.length(); i++) {
            int digit = Character.getNumericValue(imeiBase.charAt(i));
            if (i % 2 == 1) { // Every second digit
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            sum += digit;
        }
        
        int checkDigit = (10 - (sum % 10)) % 10;
        return imeiBase.append(checkDigit).toString();
    }
    
    public String generateRealisticLocation() {
        // Generate realistic coordinates on land masses
        double[][] landmasses = {
            // America (North & South)
            {-56, 72, -168, -34},
            // Europe  
            {36, 71, -25, 45},
            // Asia
            {-11, 82, 25, 180},
            // Australia
            {-44, -10, 112, 154}
        };
        
        int areaIndex = (int)(Math.random() * landmasses.length);
        double[] area = landmasses[areaIndex];
        
        double latMin = area[0], latMax = area[1];
        double lonMin = area[2], lonMax = area[3];
        
        double latitude = latMin + Math.random() * (latMax - latMin);
        double longitude = lonMin + Math.random() * (lonMax - lonMin);
        
        return String.format("%.6f,%.6f", latitude, longitude);
    }
}