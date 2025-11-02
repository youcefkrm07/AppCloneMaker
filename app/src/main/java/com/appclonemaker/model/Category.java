package com.appclonemaker.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private List<Setting> settings;
    private boolean isExpanded;
    
    public Category(String name) {
        this.name = name;
        this.settings = new ArrayList<>();
        this.isExpanded = false;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Setting> getSettings() {
        return settings;
    }
    
    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }
    
    public void addSetting(Setting setting) {
        this.settings.add(setting);
    }
    
    public boolean isExpanded() {
        return isExpanded;
    }
    
    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
    
    public void toggleExpanded() {
        isExpanded = !isExpanded;
    }
}