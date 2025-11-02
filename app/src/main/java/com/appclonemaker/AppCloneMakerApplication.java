package com.appclonemaker;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.appclonemaker.utils.DeviceProfileManager;
import com.appclonemaker.utils.JsonParser;

import java.util.Locale;

public class AppCloneMakerApplication extends Application {
    
    private static final String NOTIFICATION_CHANNEL_ID = "appclone_maker_channel";
    private static final String NOTIFICATION_CHANNEL_NAME = "AppCloner Maker";
    private static final String NOTIFICATION_CHANNEL_DESC = "Background processing notifications";
    
    private static AppCloneMakerApplication instance;
    private SharedPreferences preferences;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        initializeApplication();
    }
    
    private void initializeApplication() {
        // Initialize shared preferences
        preferences = getSharedPreferences("AppCloneMakerPrefs", MODE_PRIVATE);
        
        // Create notification channel for Android Oreo and above
        createNotificationChannel();
        
        // Initialize managers and utilities
        initializeManagers();
        
        // Set default locale for consistent formatting
        setDefaultLocale();
        
        // Initialize logging
        initializeLogging();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    private void initializeManagers() {
        // Initialize device profile manager
        DeviceProfileManager.getInstance();
        
        // Initialize JSON parser with sample data
        JsonParser.parseConfiguration();
    }
    
    private void setDefaultLocale() {
        // Set default locale to ensure consistent behavior across devices
        Locale.setDefault(Locale.US);
        
        android.content.res.Configuration config = getResources().getConfiguration();
        config.setLocale(Locale.US);
        config.setLayoutDirection(Locale.US);
        
        createConfigurationContext(config);
    }
    
    private void initializeLogging() {
        // Initialize debug logging if debug build
        if (isDebugBuild()) {
            android.util.Log.d("AppCloneMaker", "Debug mode enabled");
        }
    }
    
    public boolean isDebugBuild() {
        return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
    
    public static AppCloneMakerApplication getInstance() {
        return instance;
    }
    
    public SharedPreferences getPreferences() {
        return preferences;
    }
    
    public Context getApplicationContext() {
        return getApplicationContext();
    }
    
    // Application level constants
    public static class Constants {
        public static final String PACKAGE_NAME_KEY = "package_name";
        public static final String SPLIT_COUNT_KEY = "split_count";
        public static final String CONFIG_JSON_KEY = "config_json";
        public static final String LAST_SAVE_TIME_KEY = "last_save_time";
        public static final String FIRST_RUN_KEY = "first_run";
        public static final String ENABLE_ANALYTICS_KEY = "enable_analytics";
        public static final String THEME_PREFERENCE_KEY = "theme_preference";
        
        // File system constants
        public static final String CONFIG_DIRECTORY = "configs";
        public static final String BACKUP_DIRECTORY = "backups";
        public static final String TEMP_DIRECTORY = "temp";
        public static final String LOG_DIRECTORY = "logs";
        
        // Configuration defaults
        public static final int DEFAULT_SPLIT_COUNT = 101;
        public static final String DEFAULT_LOCALE = "en_US";
        public static final long DEFAULT_SAVE_INTERVAL = 30000; // 30 seconds
        
        // Feature flags
        public static final boolean ENABLE_DEVICE_PROFILES = true;
        public static final boolean ENABLE_LOCATION_SPOOFING = true;
        public static final boolean ENABLE_WEBVIEW_MONITORING = true;
        public static final boolean ENABLE_AUTO_SAVE = true;
        
        // API endpoints (for future network features)
        public static final String BASE_API_URL = "https://api.appclonemaker.com";
        public static final String DEVICE_PROFILES_URL = "https://devices.appclonemaker.com";
        public static final String UPDATE_CHECK_URL = "https://updates.appclonemaker.com/check";
        
        // Timeouts and limits
        public static final int CONFIG_LOAD_TIMEOUT = 10000; // 10 seconds
        public static final int MAX_CONFIG_SIZE = 1024 * 1024; // 1MB
        public static final int MAX_RECENT_CONFIGS = 10;
        
        // Error codes
        public static final int ERROR_CONFIG_PARSE = 1001;
        public static final int ERROR_FILE_ACCESS = 1002;
        public static final int ERROR_NETWORK = 1003;
        public static final int ERROR_VALIDATION = 1004;
        
        // Success codes
        public static final int SUCCESS_CONFIG_SAVE = 2001;
        public static final int SUCCESS_CONFIG_LOAD = 2002;
        public static final int SUCCESS_CONFIG_EXPORT = 2003;
        public static final int SUCCESS_DEVICE_PROFILE_LOAD = 2004;
    }
    
    // Utility methods for application state
    public boolean isFirstRun() {
        return preferences.getBoolean(Constants.FIRST_RUN_KEY, true);
    }
    
    public void setFirstRun(boolean firstRun) {
        preferences.edit().putBoolean(Constants.FIRST_RUN_KEY, firstRun).apply();
    }
    
    public String getPackageName() {
        return preferences.getString(Constants.PACKAGE_NAME_KEY, "");
    }
    
    public void setPackageName(String packageName) {
        preferences.edit().putString(Constants.PACKAGE_NAME_KEY, packageName).apply();
    }
    
    public int getSplitCount() {
        return preferences.getInt(Constants.SPLIT_COUNT_KEY, Constants.DEFAULT_SPLIT_COUNT);
    }
    
    public void setSplitCount(int count) {
        preferences.edit().putInt(Constants.SPLIT_COUNT_KEY, count).apply();
    }
    
    public long getLastSaveTime() {
        return preferences.getLong(Constants.LAST_SAVE_TIME_KEY, 0);
    }
    
    public void updateLastSaveTime() {
        preferences.edit().putLong(Constants.LAST_SAVE_TIME_KEY, System.currentTimeMillis()).apply();
    }
    
    public boolean isAutoSaveEnabled() {
        return preferences.getBoolean("auto_save_enabled", Constants.ENABLE_AUTO_SAVE);
    }
    
    public void setAutoSaveEnabled(boolean enabled) {
        preferences.edit().putBoolean("auto_save_enabled", enabled).apply();
    }
    
    // Debug and logging utilities
    public void logError(String tag, String message, Throwable throwable) {
        if (isDebugBuild()) {
            android.util.Log.e(tag, message, throwable);
        }
        // In production, you might want to send errors to a crash reporting service
    }
    
    public void logInfo(String tag, String message) {
        if (isDebugBuild()) {
            android.util.Log.i(tag, message);
        }
    }
    
    public void logDebug(String tag, String message) {
        if (isDebugBuild()) {
            android.util.Log.d(tag, message);
        }
    }
    
    // Memory and performance utilities
    public long getAvailableMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }
    
    public int getMemoryUsagePercent() {
        long used = getAvailableMemory();
        long max = getMaxMemory();
        return (int) ((used * 100) / max);
    }
    
    // Application lifecycle helpers
    public void onAppForeground() {
        // Called when app comes to foreground
        logDebug("AppLifecycle", "App came to foreground");
    }
    
    public void onAppBackground() {
        // Called when app goes to background
        logDebug("AppLifecycle", "App went to background");
        
        // Auto-save if enabled
        if (isAutoSaveEnabled()) {
            // Trigger auto-save in background thread
            new Thread(() -> {
                // Auto-save logic would go here
            }).start();
        }
    }
}