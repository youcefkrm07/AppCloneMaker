package com.appclonemaker.utils;

import com.appclonemaker.model.Category;
import com.appclonemaker.model.Setting;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
    
    private static final Gson gson = new Gson();
    
    // Sample configuration data based on the HTML structure
    private static final String SAMPLE_CONFIG_JSON = """
    {
        "deviceInformation": {
            "changeAndroidId": true,
            "changeImei": false,
            "changeAndroidSerial": false,
            "changeWifiMacAddress": false,
            "changeBluetoothMacAddress": false,
            "changeImsi": false,
            "changeGoogleAdvertisingId": false,
            "changeGoogleServiceFrameworkId": false,
            "changeFacebookAttributionId": false,
            "changeAppSetId": false,
            "changeOpenId": false,
            "changeAmazonAdvertisingId": false,
            "changeHuaweiAdvertisingId": false,
            "changeLocale": "en_US",
            "changeEthernetMacAddress": false
        },
        "locationPrivacy": {
            "spoofLocation": false,
            "spoofLocationLatitude": "",
            "spoofLocationLongitude": "",
            "spoofRandomLocation": false,
            "spoofLocationUseIpLocation": false,
            "spoofLocationApi": "",
            "spoofLocationCalculateBearing": false,
            "spoofLocationCompatibilityMode": false,
            "spoofLocationInterval": 0,
            "spoofLocationShareLocationReceiver": false,
            "spoofLocationShowSpoofLocationNotification": false,
            "spoofLocationSimulatePositionalUncertainty": false,
            "favoriteLocationsShowDistance": false
        },
        "buildProperties": {
            "buildsProps": false,
            "buildPropsDeviceName": "",
            "buildPropsManufacturer": "",
            "buildPropsBrand": "",
            "buildPropsModel": "",
            "buildPropsProduct": "",
            "buildPropsDevice": "",
            "buildPropsBoard": "",
            "buildPropsRadio": "",
            "buildPropsHardware": "",
            "randomizeBuildProps": false,
            "filterDevicesDatabase": false,
            "devicesDatabaseFilters": [],
            "devicesDatabaseUseAndroidVersion": false,
            "devicesDatabaseSdkVersions": {},
            "randomizeBuildPropsDeviceNamePrefix": ""
        },
        "appData": {
            "bundleAppData": false,
            "bundleAppDataPath": "",
            "bundleAppDataPassword": "",
            "bundleAppDataEncryptCertificate": false,
            "restoreAppDataOnEveryStart": false,
            "deleteOnExit": false,
            "deleteFilesDirectoriesOnExit": [],
            "securelyDeleteFilesDirectoriesOnExit": false,
            "bundleFilesDirectories": [],
            "bundleInternalFilesDirectories": []
        },
        "webViewPrivacy": {
            "webViewPrivacyOptions": false,
            "webViewDisableWebRtc": false,
            "webViewDisableWebGl": false,
            "webViewDisableAudioContext": false,
            "webViewUrlDataMonitor": false,
            "webViewUrlDataMonitorAutoCopy": false,
            "webViewUrlDataMonitorAutoOpen": false,
            "webViewUrlDataMonitorFilter": "DISABLED",
            "webViewUrlDataMonitorFilterStrings": [],
            "webViewUrlDataMonitorRegularExpression": "",
            "webViewUrlDataMonitorShowJavaScriptUrls": false,
            "webViewUrlDataMonitorShowOverrideUrlLoading": false,
            "webViewUrlDataMonitorUrlDecode": false,
            "webViewUrlDataFilterList": [],
            "showWebViewSourceCode": false,
            "showWebViewIFrameSourceCode": false,
            "showWebViewSourceCodeFilter": "DISABLED",
            "showWebViewSourceCodeFilterStrings": [],
            "showWebViewSourceCodeRegularExpression": "",
            "webViewCookies": false,
            "webViewOverrideUrlLoadingList": [],
            "webViewUrlDataMonitorRegularExpression": ""
        },
        "systemSettings": {
            "skipDialogsStrings": [],
            "skipDialogsStacktraceStrings": [],
            "skipDialogsMonitorStacktraces": false,
            "overrideSharedPreferences": [],
            "overrideSharedPreferencesEnablePlaceholders": false,
            "customBuildProps": [],
            "customBuildPropsFile": "",
            "customBuildPropsFileEnablePlaceholders": false,
            "hostsBlocker": false,
            "hostsBlockerBlockByDefault": false,
            "hostsBlockerShowNotification": false,
            "hostsBlockerUseFile": false,
            "hostsBlockerFileContent": "",
            "hostsBlockerAllowAllOtherHosts": false,
            "addPermissions": [],
            "addProviders": [],
            "addReceivers": [],
            "addServices": [],
            "addActivities": [],
            "stringsProperties": {},
            "serialFormat": ""
        },
        "installTime": {
            "changeInstallUpdateTime": false,
            "customInstallUpdateTime": 0,
            "randomizeUserCreationTime": false,
            "relativeInstallUpdateTime": false,
            "relativeInstallUpdateTimeUnit": "days"
        },
        "network": {
            "dnsOverHttps": false,
            "dnsOverHttpsCustomUrl": "",
            "dnsOverHttpsSilent": false
        },
        "extra": {
            "addSnow": false,
            "snow": "",
            "pictureInPictureSupport": false,
            "pictureInPicture": ""
        }
    }
    """;
    
    public static List<Category> parseConfiguration() {
        try {
            Type mapType = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
            Map<String, Map<String, Object>> configData = gson.fromJson(SAMPLE_CONFIG_JSON, mapType);
            return parseConfigData(configData);
        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultCategories();
        }
    }
    
    public static List<Category> parseConfigData(Map<String, Map<String, Object>> configData) {
        List<Category> categories = new ArrayList<>();
        
        for (Map.Entry<String, Map<String, Object>> categoryEntry : configData.entrySet()) {
            String categoryName = categoryEntry.getKey();
            Map<String, Object> categoryData = categoryEntry.getValue();
            
            Category category = new Category(categoryName);
            
            for (Map.Entry<String, Object> settingEntry : categoryData.entrySet()) {
                String settingKey = settingEntry.getKey();
                Object settingValue = settingEntry.getValue();
                
                Setting setting = createSettingFromData(settingKey, settingValue);
                if (setting != null) {
                    category.addSetting(setting);
                }
            }
            
            if (!category.getSettings().isEmpty()) {
                categories.add(category);
            }
        }
        
        return categories;
    }
    
    private static Setting createSettingFromData(String key, Object value) {
        Setting.SettingType type = determineSettingType(key, value);
        String name = formatSettingName(key);
        
        Setting setting = new Setting(key, name, type, value);
        
        // Set specific properties based on key
        setupSettingProperties(setting, key, value);
        
        return setting;
    }
    
    private static Setting.SettingType determineSettingType(String key, Object value) {
        // Check for specific custom editors first
        if (isCustomEditorKey(key)) {
            return Setting.SettingType.CUSTOM;
        }
        
        // Determine type based on value and key patterns
        if (value instanceof Boolean) {
            return Setting.SettingType.BOOLEAN;
        } else if (value instanceof Number) {
            return Setting.SettingType.NUMBER;
        } else if (value instanceof String) {
            String stringValue = (String) value;
            
            // Check if it's a known dropdown option
            if (isDropdownKey(key)) {
                return Setting.SettingType.DROPDOWN;
            }
            
            // Check for long text fields
            if (key.contains("Expression") || key.contains("Content") || key.equals("customBuildPropsFile")) {
                return Setting.SettingType.TEXT;
            }
            
            return Setting.SettingType.STRING;
        } else if (value instanceof List) {
            return Setting.SettingType.ARRAY;
        } else if (value instanceof Map) {
            return Setting.SettingType.COMPOUND;
        }
        
        return Setting.SettingType.STRING; // Default fallback
    }
    
    private static boolean isCustomEditorKey(String key) {
        String[] customKeys = {
            "bundleAppData",
            "deleteOnExit", 
            "spoofLocation",
            "buildsProps",
            "webViewUrlDataFilterList",
            "overrideSharedPreferences",
            "customBuildProps",
            "webViewCookies",
            "hostsBlocker",
            "webViewOverrideUrlLoadingList",
            "skipDialogsStrings",
            "bundleFilesDirectories",
            "bundleInternalFilesDirectories"
        };
        
        for (String customKey : customKeys) {
            if (key.equals(customKey)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean isDropdownKey(String key) {
        String[] dropdownKeys = {
            "changeLocale",
            "spoofLocationApi",
            "spoofLocationCompatibilityMode",
            "relativeInstallUpdateTimeUnit",
            "dnsOverHttpsSilent",
            "webViewUrlDataMonitorFilter",
            "showWebViewSourceCodeFilter",
            "addSnow",
            "pictureInPicture"
        };
        
        for (String dropdownKey : dropdownKeys) {
            if (key.equals(dropdownKey)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static void setupSettingProperties(Setting setting, String key, Object value) {
        switch (key) {
            case "changeLocale":
                setting.setOptions(getLocaleOptions());
                break;
                
            case "spoofLocationApi":
                setting.setOptions(getLocationApiOptions());
                break;
                
            case "spoofLocationCompatibilityMode":
                setting.setOptions(getCompatibilityModeOptions());
                break;
                
            case "relativeInstallUpdateTimeUnit":
                setting.setOptions(getTimeUnitOptions());
                break;
                
            case "webViewUrlDataMonitorFilter":
            case "showWebViewSourceCodeFilter":
                setting.setOptions(getFilterOptions());
                break;
                
            case "addSnow":
                setting.setOptions(getSnowOptions());
                break;
                
            case "pictureInPicture":
                setting.setOptions(getPictureInPictureOptions());
                break;
        }
        
        // Mark as parent setting if it has related child settings
        if (isParentSetting(key)) {
            setting.setParentSetting(true);
        }
        
        // Add descriptions for certain settings
        setting.setDescription(getSettingDescription(key));
    }
    
    private static List<String> getLocaleOptions() {
        List<String> options = new ArrayList<>();
        options.add("en_US");
        options.add("en_GB");
        options.add("en_CA");
        options.add("en_AU");
        options.add("de_DE");
        options.add("fr_FR");
        options.add("es_ES");
        options.add("it_IT");
        options.add("pt_BR");
        options.add("zh_CN");
        options.add("zh_TW");
        options.add("ja_JP");
        options.add("ko_KR");
        options.add("ru_RU");
        options.add("CUSTOM");
        return options;
    }
    
    private static List<String> getLocationApiOptions() {
        List<String> options = new ArrayList<>();
        options.add("");
        options.add("Google");
        options.add("OpenStreetMap");
        options.add("CUSTOM");
        return options;
    }
    
    private static List<String> getCompatibilityModeOptions() {
        List<String> options = new ArrayList<>();
        options.add("PERMISSIVE");
        options.add("RESTRICTIVE");
        return options;
    }
    
    private static List<String> getTimeUnitOptions() {
        List<String> options = new ArrayList<>();
        options.add("days");
        options.add("weeks");
        options.add("months");
        return options;
    }
    
    private static List<String> getFilterOptions() {
        List<String> options = new ArrayList<>();
        options.add("DISABLED");
        options.add("INCLUDE");
        options.add("EXCLUDE");
        return options;
    }
    
    private static List<String> getSnowOptions() {
        List<String> options = new ArrayList<>();
        options.add("");
        options.add("flake");
        options.add("heart");
        options.add("star");
        options.add("custom");
        return options;
    }
    
    private static List<String> getPictureInPictureOptions() {
        List<String> options = new ArrayList<>();
        options.add("");
        options.add("generic");
        options.add("youtube");
        options.add("custom");
        return options;
    }
    
    private static boolean isParentSetting(String key) {
        String[] parentKeys = {
            "spoofLocation",
            "buildsProps",
            "bundleAppData",
            "deleteOnExit",
            "randomizeBuildProps",
            "changeInstallUpdateTime",
            "webViewPrivacyOptions",
            "webViewUrlDataMonitor",
            "showWebViewSourceCode",
            "hostsBlocker"
        };
        
        for (String parentKey : parentKeys) {
            if (key.equals(parentKey)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static String getSettingDescription(String key) {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("changeAndroidId", "Generate a new random Android ID");
        descriptions.put("changeImei", "Generate a new random IMEI number");
        descriptions.put("changeAndroidSerial", "Generate a new random Android serial number");
        descriptions.put("changeWifiMacAddress", "Generate a new random WiFi MAC address");
        descriptions.put("changeBluetoothMacAddress", "Generate a new random Bluetooth MAC address");
        descriptions.put("changeImsi", "Generate a new random IMSI number");
        descriptions.put("spoofLocation", "Fake GPS location coordinates");
        descriptions.put("buildsProps", "Modify Android build properties");
        descriptions.put("bundleAppData", "Include app data in the clone");
        descriptions.put("deleteOnExit", "Delete files and directories when app exits");
        descriptions.put("webViewPrivacyOptions", "Configure WebView privacy settings");
        descriptions.put("webViewUrlDataMonitor", "Monitor and modify WebView URL data");
        descriptions.put("showWebViewSourceCode", "Display WebView source code");
        descriptions.put("hostsBlocker", "Block specific hosts/domains");
        
        return descriptions.getOrDefault(key, "");
    }
    
    private static String formatSettingName(String key) {
        // Convert camelCase to readable format
        String formatted = key.replaceAll("([A-Z])", " $1")
                             .replaceAll("^.", str -> str.toUpperCase())
                             .replace("Web View", "WebView")
                             .replace("Dns Over Https", "DNS over HTTPS")
                             .replace("Imei", "IMEI")
                             .replace("Imsi", "IMSI")
                             .replace("Mac Address", "MAC Address");
        
        return formatted.trim();
    }
    
    private static List<Category> getDefaultCategories() {
        // Return basic categories if parsing fails
        List<Category> categories = new ArrayList<>();
        
        // Device Information Category
        Category deviceInfo = new Category("deviceInformation");
        deviceInfo.addSetting(new Setting("changeAndroidId", "Change Android ID", Setting.SettingType.BOOLEAN, true));
        deviceInfo.addSetting(new Setting("changeImei", "Change IMEI", Setting.SettingType.BOOLEAN, false));
        categories.add(deviceInfo);
        
        // Location Privacy Category
        Category locationPrivacy = new Category("locationPrivacy");
        locationPrivacy.addSetting(new Setting("spoofLocation", "Spoof Location", Setting.SettingType.CUSTOM, false));
        categories.add(locationPrivacy);
        
        return categories;
    }
    
    public static Map<String, Object> parseJsonConfig(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            return gson.fromJson(json, mapType);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    public static String toJsonConfig(Map<String, Object> config) {
        return gson.toJson(config);
    }
}