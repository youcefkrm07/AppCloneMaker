package com.appclonemaker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.appclonemaker.R;
import com.appclonemaker.model.Setting;
import com.appclonemaker.utils.DeviceProfileManager;
import com.appclonemaker.utils.PathPicker;

import java.util.ArrayList;
import java.util.List;

public class EditorDialogFragment extends DialogFragment {
    
    private static final String ARG_SETTING = "setting";
    private static final String ARG_CATEGORY = "category";
    
    private Setting setting;
    private String categoryName;
    private OnSettingSavedListener listener;
    private LinearLayout editorContainer;
    private TextView headerTitle;
    private Button cancelButton;
    private Button saveButton;
    
    // Views for different input types
    private EditText textInput;
    private CheckBox checkboxInput;
    private RadioGroup radioGroupInput;
    private TextView dropdownInput;
    private EditText arrayTextarea;
    
    public interface OnSettingSavedListener {
        void onSettingSaved(Setting setting);
    }
    
    public static EditorDialogFragment newInstance(Setting setting, String categoryName) {
        EditorDialogFragment fragment = new EditorDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SETTING, setting);
        args.putString(ARG_CATEGORY, categoryName);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setting = (Setting) getArguments().getSerializable(ARG_SETTING);
            categoryName = getArguments().getString(ARG_CATEGORY);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return createDialogView(inflater, container);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        setupEditor();
        setupListeners();
    }
    
    private View createDialogView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.dialog_floating_editor, container, false);
        
        // Make dialog full width and responsive
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }
        
        return view;
    }
    
    private void setupViews(View view) {
        headerTitle = view.findViewById(R.id.editor_header);
        editorContainer = view.findViewById(R.id.editor_body);
        cancelButton = view.findViewById(R.id.editor_cancel_btn);
        saveButton = view.findViewById(R.id.editor_save_btn);
        
        headerTitle.setText(formatLabel(setting.getName()));
    }
    
    private void setupEditor() {
        editorContainer.removeAllViews();
        
        if (setting.hasCustomEditor()) {
            setupCustomEditor();
        } else {
            setupSimpleEditor();
        }
    }
    
    private void setupSimpleEditor() {
        switch (setting.getType()) {
            case BOOLEAN:
                setupBooleanEditor();
                break;
            case STRING:
                setupStringEditor();
                break;
            case NUMBER:
                setupNumberEditor();
                break;
            case DROPDOWN:
                setupDropdownEditor();
                break;
            case TEXT:
                setupTextEditor();
                break;
            case ARRAY:
                setupArrayEditor();
                break;
            default:
                setupDefaultEditor();
                break;
        }
    }
    
    private void setupBooleanEditor() {
        checkboxInput = new CheckBox(getContext());
        checkboxInput.setText(formatLabel(setting.getName()));
        checkboxInput.setChecked(setting.isEnabled());
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 16, 0, 16);
        checkboxInput.setLayoutParams(params);
        
        editorContainer.addView(checkboxInput);
    }
    
    private void setupStringEditor() {
        // Add label
        TextView label = new TextView(getContext());
        label.setText(formatLabel(setting.getName()));
        label.setTextColor(Color.BLACK);
        label.setTextSize(16);
        editorContainer.addView(label);
        
        // Add input field
        textInput = new EditText(getContext());
        textInput.setText(setting.getValue() != null ? setting.getValue().toString() : "");
        textInput.setHint("Enter value...");
        editorContainer.addView(textInput);
    }
    
    private void setupNumberEditor() {
        // Similar to string editor but with number input type
        TextView label = new TextView(getContext());
        label.setText(formatLabel(setting.getName()));
        label.setTextColor(Color.BLACK);
        label.setTextSize(16);
        editorContainer.addView(label);
        
        textInput = new EditText(getContext());
        textInput.setText(setting.getValue() != null ? setting.getValue().toString() : "");
        textInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        textInput.setHint("Enter number...");
        editorContainer.addView(textInput);
    }
    
    private void setupDropdownEditor() {
        // Create a clickable text view that opens a list dialog
        dropdownInput = new TextView(getContext());
        dropdownInput.setText(formatLabel(setting.getName()));
        dropdownInput.setTextColor(Color.BLACK);
        dropdownInput.setTextSize(16);
        dropdownInput.setPadding(16, 16, 16, 16);
        dropdownInput.setBackgroundResource(R.drawable.spinner_background);
        dropdownInput.setOnClickListener(v -> showDropdownDialog());
        editorContainer.addView(dropdownInput);
        
        // Set current value
        updateDropdownDisplay();
    }
    
    private void setupTextEditor() {
        // Similar to string editor but with multiline support
        TextView label = new TextView(getContext());
        label.setText(formatLabel(setting.getName()));
        label.setTextColor(Color.BLACK);
        label.setTextSize(16);
        editorContainer.addView(label);
        
        arrayTextarea = new EditText(getContext());
        arrayTextarea.setText(setting.getValue() != null ? setting.getValue().toString() : "");
        arrayTextarea.setHint("Enter text...");
        arrayTextarea.setLines(5);
        arrayTextarea.setMaxLines(10);
        editorContainer.addView(arrayTextarea);
    }
    
    private void setupArrayEditor() {
        // For array settings, show a textarea where each line is an array item
        TextView label = new TextView(getContext());
        label.setText(formatLabel(setting.getName()));
        label.setTextColor(Color.BLACK);
        label.setTextSize(16);
        editorContainer.addView(label);
        
        arrayTextarea = new EditText(getContext());
        if (setting.getValue() instanceof List) {
            List<?> listValue = (List<?>) setting.getValue();
            StringBuilder sb = new StringBuilder();
            for (Object item : listValue) {
                sb.append(item.toString()).append("\n");
            }
            arrayTextarea.setText(sb.toString().trim());
        }
        arrayTextarea.setHint("Enter one item per line...");
        arrayTextarea.setLines(5);
        arrayTextarea.setMaxLines(10);
        editorContainer.addView(arrayTextarea);
    }
    
    private void setupDefaultEditor() {
        // Fallback for unknown types
        TextView label = new TextView(getContext());
        label.setText("Unsupported setting type: " + setting.getType());
        label.setTextColor(Color.RED);
        editorContainer.addView(label);
    }
    
    private void setupCustomEditor() {
        // Handle custom editors for complex settings
        if (setting.getKey().equals("bundleAppData")) {
            setupBundleAppDataEditor();
        } else if (setting.getKey().equals("deleteOnExit")) {
            setupDeleteOnExitEditor();
        } else if (setting.getKey().equals("spoofLocation")) {
            setupLocationSpoofEditor();
        } else if (setting.getKey().equals("buildsProps")) {
            setupBuildPropsEditor();
        } else if (setting.getKey().startsWith("webView")) {
            setupWebViewEditor();
        } else {
            setupSimpleEditor(); // Fallback to simple editor
        }
    }
    
    private void setupBundleAppDataEditor() {
        // Custom editor for bundle app data settings
        TextView title = new TextView(getContext());
        title.setText("Bundle App Data");
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 16);
        editorContainer.addView(title);
        
        // Path selection
        TextView pathLabel = new TextView(getContext());
        pathLabel.setText("App Data Path:");
        pathLabel.setTextSize(14);
        pathLabel.setTextColor(Color.GRAY);
        editorContainer.addView(pathLabel);
        
        Button selectPathBtn = new Button(getContext());
        selectPathBtn.setText("📁 Select Path");
        selectPathBtn.setOnClickListener(v -> selectAppDataPath());
        editorContainer.addView(selectPathBtn);
        
        // Password field
        TextView passwordLabel = new TextView(getContext());
        passwordLabel.setText("Password:");
        passwordLabel.setTextSize(14);
        passwordLabel.setTextColor(Color.GRAY);
        passwordLabel.setPadding(0, 16, 0, 0);
        editorContainer.addView(passwordLabel);
        
        textInput = new EditText(getContext());
        textInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        textInput.setHint("Enter password...");
        editorContainer.addView(textInput);
    }
    
    private void setupDeleteOnExitEditor() {
        // Custom editor for delete on exit settings
        TextView title = new TextView(getContext());
        title.setText("Delete Files & Directories on Exit");
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 16);
        editorContainer.addView(title);
        
        // Enable toggle
        checkboxInput = new CheckBox(getContext());
        checkboxInput.setText("Enable deletion on exit");
        checkboxInput.setChecked(setting.isEnabled());
        editorContainer.addView(checkboxInput);
        
        // Path selection
        TextView pathLabel = new TextView(getContext());
        pathLabel.setText("Files & directories:");
        pathLabel.setTextSize(14);
        pathLabel.setTextColor(Color.GRAY);
        pathLabel.setPadding(0, 16, 0, 8);
        editorContainer.addView(pathLabel);
        
        Button selectPathBtn = new Button(getContext());
        selectPathBtn.setText("➕ Select Path");
        selectPathBtn.setOnClickListener(v -> selectDeletePath());
        editorContainer.addView(selectPathBtn);
        
        // Secure deletion option
        checkboxInput = new CheckBox(getContext());
        checkboxInput.setText("Delete files securely");
        editorContainer.addView(checkboxInput);
    }
    
    private void setupLocationSpoofEditor() {
        // Custom editor for location spoofing with map integration
        TextView title = new TextView(getContext());
        title.setText("Spoof Location");
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 16);
        editorContainer.addView(title);
        
        // Random location button
        Button randomBtn = new Button(getContext());
        randomBtn.setText("🎲 Random Location");
        randomBtn.setOnClickListener(v -> generateRandomLocation());
        editorContainer.addView(randomBtn);
        
        // Latitude input
        TextView latLabel = new TextView(getContext());
        latLabel.setText("Latitude:");
        editorContainer.addView(latLabel);
        
        textInput = new EditText(getContext());
        textInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        textInput.setHint("0.0");
        editorContainer.addView(textInput);
        
        // Longitude input
        TextView lonLabel = new TextView(getContext());
        lonLabel.setText("Longitude:");
        editorContainer.addView(lonLabel);
        
        EditText lonInput = new EditText(getContext());
        lonInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        lonInput.setHint("0.0");
        editorContainer.addView(lonInput);
    }
    
    private void setupBuildPropsEditor() {
        // Custom editor for build properties with device profile selection
        TextView title = new TextView(getContext());
        title.setText("Build Properties");
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 16);
        editorContainer.addView(title);
        
        // Device profile selector
        Button selectProfileBtn = new Button(getContext());
        selectProfileBtn.setText("📱 Select Device Profile");
        selectProfileBtn.setOnClickListener(v -> selectDeviceProfile());
        editorContainer.addView(selectProfileBtn);
        
        // Custom property inputs
        createPropertyInput("Device Name", "buildPropsDeviceName");
        createPropertyInput("Manufacturer", "buildPropsManufacturer");
        createPropertyInput("Model", "buildPropsModel");
        createPropertyInput("Brand", "buildPropsBrand");
    }
    
    private void setupWebViewEditor() {
        // Custom editors for WebView related settings
        TextView title = new TextView(getContext());
        title.setText("WebView Settings");
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 16);
        editorContainer.addView(title);
        
        // URL filter editor
        setupUrlFilterEditor();
    }
    
    private void setupUrlFilterEditor() {
        // Editor for URL filtering settings
        TextView filterLabel = new TextView(getContext());
        filterLabel.setText("URL Filter Expression:");
        editorContainer.addView(filterLabel);
        
        textInput = new EditText(getContext());
        textInput.setHint("Enter regular expression...");
        editorContainer.addView(textInput);
        
        // Block on match option
        checkboxInput = new CheckBox(getContext());
        checkboxInput.setText("Block if matching");
        editorContainer.addView(checkboxInput);
    }
    
    private void createPropertyInput(String label, String key) {
        TextView propertyLabel = new TextView(getContext());
        propertyLabel.setText(label + ":");
        propertyLabel.setTextSize(14);
        propertyLabel.setTextColor(Color.GRAY);
        propertyLabel.setPadding(0, 8, 0, 0);
        editorContainer.addView(propertyLabel);
        
        EditText propertyInput = new EditText(getContext());
        propertyInput.setTag(key);
        propertyInput.setHint("Enter " + label.toLowerCase() + "...");
        editorContainer.addView(propertyInput);
    }
    
    private void setupListeners() {
        cancelButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> saveChanges());
    }
    
    private void saveChanges() {
        // Extract values from editor controls and update setting
        if (setting.hasCustomEditor()) {
            saveCustomEditorValues();
        } else {
            saveSimpleEditorValues();
        }
        
        if (listener != null) {
            listener.onSettingSaved(setting);
        }
        
        dismiss();
    }
    
    private void saveSimpleEditorValues() {
        switch (setting.getType()) {
            case BOOLEAN:
                if (checkboxInput != null) {
                    setting.setEnabled(checkboxInput.isChecked());
                }
                break;
            case STRING:
            case NUMBER:
                if (textInput != null) {
                    String value = textInput.getText().toString();
                    if (setting.getType() == Setting.SettingType.NUMBER) {
                        try {
                            setting.setValue(Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            setting.setValue(0.0);
                        }
                    } else {
                        setting.setValue(value);
                    }
                }
                break;
            case TEXT:
            case ARRAY:
                if (arrayTextarea != null) {
                    String value = arrayTextarea.getText().toString();
                    if (setting.getType() == Setting.SettingType.ARRAY) {
                        // Parse array from textarea
                        List<String> items = new ArrayList<>();
                        String[] lines = value.split("\n");
                        for (String line : lines) {
                            String trimmed = line.trim();
                            if (!trimmed.isEmpty()) {
                                items.add(trimmed);
                            }
                        }
                        setting.setValue(items);
                    } else {
                        setting.setValue(value);
                    }
                }
                break;
            case DROPDOWN:
                // Value is already set when dropdown is selected
                break;
        }
    }
    
    private void saveCustomEditorValues() {
        // Save values from custom editors
        if (setting.getKey().equals("bundleAppData")) {
            if (textInput != null) {
                // Save password or other bundle app data properties
                setting.setValue(textInput.getText().toString());
            }
        } else if (setting.getKey().equals("deleteOnExit")) {
            if (checkboxInput != null) {
                setting.setEnabled(checkboxInput.isChecked());
            }
        } else if (setting.getKey().equals("spoofLocation")) {
            // Save latitude and longitude
            if (textInput != null) {
                setting.setValue(textInput.getText().toString());
            }
        }
        // Add more custom editor saving logic as needed
    }
    
    private void showDropdownDialog() {
        // Show a simple dialog with radio buttons for dropdown selection
        if (setting.getOptions() == null || setting.getOptions().isEmpty()) {
            return;
        }
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
                new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle(formatLabel(setting.getName()));
        
        String[] options = setting.getOptions().toArray(new String[0]);
        builder.setSingleChoiceItems(options, getSelectedIndex(), (dialog, which) -> {
            setting.setValue(setting.getOptions().get(which));
            updateDropdownDisplay();
            dialog.dismiss();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private int getSelectedIndex() {
        if (setting.getValue() == null) return 0;
        String currentValue = setting.getValue().toString();
        return setting.getOptions().indexOf(currentValue);
    }
    
    private void updateDropdownDisplay() {
        if (dropdownInput != null && setting.getValue() != null) {
            dropdownInput.setText(setting.getValue().toString());
            dropdownInput.setPadding(16, 16, 16, 16);
        }
    }
    
    private void selectAppDataPath() {
        PathPicker.selectPath(getContext(), new PathPicker.PathSelectionListener() {
            @Override
            public void onPathSelected(String path) {
                // Handle selected path for app data bundle
                showToast("Selected app data path: " + path);
            }
        });
    }
    
    private void selectDeletePath() {
        PathPicker.selectPath(getContext(), new PathPicker.PathSelectionListener() {
            @Override
            public void onPathSelected(String path) {
                // Handle selected path for deletion
                showToast("Selected delete path: " + path);
            }
        });
    }
    
    private void generateRandomLocation() {
        double latitude = -90 + Math.random() * 180;
        double longitude = -180 + Math.random() * 360;
        
        if (textInput != null) {
            textInput.setText(String.format("%.6f", latitude));
        }
        // You would also update longitude input here
    }
    
    private void selectDeviceProfile() {
        DeviceProfileManager.getInstance().showDeviceProfileDialog(getContext(), 
                new DeviceProfileManager.DeviceProfileListener() {
                    @Override
                    public void onProfileSelected(String profileName) {
                        showToast("Selected device profile: " + profileName);
                        // Apply profile properties to build props
                    }
                });
    }
    
    private String formatLabel(String key) {
        return key.replaceAll("([A-Z])", " $1")
                 .replaceAll("^.", str -> str.toUpperCase())
                 .replace("Web View", "WebView");
    }
    
    private void showToast(String message) {
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    public void setOnSettingSavedListener(OnSettingSavedListener listener) {
        this.listener = listener;
    }
}