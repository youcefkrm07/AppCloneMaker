package com.appclonemaker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PathPicker {
    
    public interface PathSelectionListener {
        void onPathSelected(String path);
    }
    
    public static void selectPath(Context context, PathSelectionListener listener) {
        // For simplicity, we'll use a basic file picker approach
        // In a real implementation, you might want to use a more sophisticated file picker
        
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        
        // Start activity for result (this would need to be handled in the calling activity)
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            ActivityResultLauncher<Intent> launcher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        String path = getRealPathFromUri(context, uri);
                        if (path != null && listener != null) {
                            listener.onPathSelected(path);
                        }
                    }
                }
            );
            launcher.launch(intent);
        } else {
            // Fallback: show a simple input dialog
            showSimplePathDialog(context, listener);
        }
    }
    
    private static void showSimplePathDialog(Context context, PathSelectionListener listener) {
        // Simple fallback - in a real app you'd want a proper dialog
        androidx.appcompat.app.AlertDialog.Builder builder = 
                new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Enter Path");
        builder.setMessage("Enter the file or directory path:");
        
        final android.widget.EditText input = new android.widget.EditText(context);
        input.setHint("/storage/emulated/0/");
        builder.setView(input);
        
        builder.setPositiveButton("OK", (dialog, which) -> {
            String path = input.getText().toString();
            if (!path.isEmpty() && listener != null) {
                listener.onPathSelected(path);
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private static String getRealPathFromUri(Context context, Uri uri) {
        // This is a simplified version - in a real app you'd implement proper URI resolution
        try {
            String path = uri.getPath();
            if (path != null) {
                // Clean up the path
                return path.replace("/tree/", "").replace(":", "/");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void selectMultiplePaths(Context context, PathSelectionListener listener) {
        // For selecting multiple files/directories
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            ActivityResultLauncher<Intent> launcher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri uri = data.getClipData().getItemAt(i).getUri();
                                String path = getRealPathFromUri(context, uri);
                                if (path != null && listener != null) {
                                    listener.onPathSelected(path);
                                }
                            }
                        } else if (data.getData() != null) {
                            Uri uri = data.getData();
                            String path = getRealPathFromUri(context, uri);
                            if (path != null && listener != null) {
                                listener.onPathSelected(path);
                            }
                        }
                    }
                }
            );
            launcher.launch(intent);
        } else {
            showSimplePathDialog(context, listener);
        }
    }
    
    public static boolean isValidPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        
        // Check if path starts with valid Android paths
        return path.startsWith("/storage/") || 
               path.startsWith("/sdcard/") || 
               path.startsWith("/data/") ||
               path.startsWith("/system/") ||
               path.startsWith("/proc/") ||
               path.contains("/Android/") ||
               path.contains("/Documents/");
    }
    
    public static File ensureDirectoryExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                return directory;
            }
        }
        return directory;
    }
    
    public static boolean copyFile(File source, File destination) {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(destination)) {
            
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String getAppDataDirectory(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }
    
    public static String getCacheDirectory(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }
    
    public static String getDocumentsDirectory(Context context) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!documentsDir.exists()) {
            documentsDir.mkdirs();
        }
        return documentsDir.getAbsolutePath();
    }
    
    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
    
    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            return directory.delete();
        }
        return false;
    }
    
    public static String getFileName(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < path.length() - 1) {
            return path.substring(lastSlash + 1);
        }
        
        return path;
    }
    
    public static String getParentDirectory(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash > 0) {
            return path.substring(0, lastSlash);
        }
        
        return "";
    }
    
    public static boolean isDirectory(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }
    
    public static boolean isFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }
    
    public static long getFileSize(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.length();
        }
        return 0;
    }
    
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}