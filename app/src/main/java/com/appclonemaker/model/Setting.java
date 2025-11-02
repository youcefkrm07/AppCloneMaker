package com.appclonemaker.model;

import java.util.List;

public class Setting {
    private String key;
    private String name;
    private Object value;
    private SettingType type;
    private boolean isEnabled;
    private List<String> options; // For dropdown options
    private String description;
    private boolean isParentSetting; // For settings that have child settings
    private List<Setting> childSettings;
    
    public enum SettingType {
        BOOLEAN,
        STRING,
        NUMBER,
        ARRAY,
        TEXT,
        DROPDOWN,
        CUSTOM, // For complex settings like device profiles, path selectors, etc.
        COMPOUND // For settings with multiple related fields
    }
    
    public Setting(String key, String name, SettingType type, Object value) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.value = value;
        this.isEnabled = true;
        this.isParentSetting = false;
    }
    
    // Getters and Setters
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public SettingType getType() {
        return type;
    }
    
    public void setType(SettingType type) {
        this.type = type;
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }
    
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public void setOptions(List<String> options) {
        this.options = options;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isParentSetting() {
        return isParentSetting;
    }
    
    public void setParentSetting(boolean parentSetting) {
        isParentSetting = parentSetting;
    }
    
    public List<Setting> getChildSettings() {
        return childSettings;
    }
    
    public void setChildSettings(List<Setting> childSettings) {
        this.childSettings = childSettings;
    }
    
    // Helper methods
    public String getDisplayValue() {
        if (value == null) {
            return "Not set";
        }
        
        switch (type) {
            case BOOLEAN:
                return (Boolean) value ? "Enabled" : "Disabled";
            case STRING:
                String stringValue = (String) value;
                return stringValue.length() > 20 ? stringValue.substring(0, 17) + "..." : stringValue;
            case NUMBER:
                return value.toString();
            case ARRAY:
                if (value instanceof List) {
                    List<?> listValue = (List<?>) value;
                    if (listValue.isEmpty()) return "[0 items]";
                    return listValue.size() + " item(s)";
                }
                return "Array";
            case DROPDOWN:
                return value.toString();
            case TEXT:
                String textValue = value.toString();
                return textValue.length() > 20 ? textValue.substring(0, 17) + "..." : textValue;
            case CUSTOM:
                return value.toString();
            case COMPOUND:
                return "Complex Setting";
            default:
                return value.toString();
        }
    }
    
    public boolean hasCustomEditor() {
        return type == SettingType.CUSTOM || type == SettingType.COMPOUND || 
               key.equals("webViewUrlDataFilterList") ||
               key.equals("overrideSharedPreferences") ||
               key.equals("customBuildProps") ||
               key.equals("webViewCookies") ||
               key.equals("bundleAppData") ||
               key.equals("deleteOnExit");
    }
}