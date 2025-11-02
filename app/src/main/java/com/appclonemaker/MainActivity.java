package com.appclonemaker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.appclonemaker.adapter.CategoryAdapter;
import com.appclonemaker.model.Category;
import com.appclonemaker.model.Setting;
import com.appclonemaker.utils.ConfigManager;
import com.appclonemaker.utils.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnSettingClickListener {
    
    private EditText searchInput;
    private RecyclerView categoriesRecycler;
    private CategoryAdapter categoryAdapter;
    private FloatingActionButton saveFab;
    private List<Category> categories;
    private ConfigManager configManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSearch();
        setupFab();
        loadConfiguration();
    }
    
    private void initViews() {
        searchInput = findViewById(R.id.search_input);
        categoriesRecycler = findViewById(R.id.categories_recycler);
        saveFab = findViewById(R.id.save_fab);
        configManager = new ConfigManager(this);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AppCloner Maker - Floating Editor");
    }
    
    private void setupRecyclerView() {
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(this, this);
        categoriesRecycler.setAdapter(categoryAdapter);
    }
    
    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCategories(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupFab() {
        saveFab.setOnClickListener(v -> {
            if (configManager.saveConfiguration()) {
                Toast.makeText(this, "✅ Configuration saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "❌ Failed to save configuration", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadConfiguration() {
        // Load default configuration or from saved state
        categories = JsonParser.parseConfiguration();
        categoryAdapter.updateCategories(categories);
    }
    
    private void filterCategories(String query) {
        if (query.trim().isEmpty()) {
            categoryAdapter.updateCategories(categories);
        } else {
            List<Category> filteredCategories = new ArrayList<>();
            for (Category category : categories) {
                Category filteredCategory = new Category(category.getName());
                for (Setting setting : category.getSettings()) {
                    if (setting.getName().toLowerCase().contains(query.toLowerCase())) {
                        filteredCategory.addSetting(setting);
                    }
                }
                if (!filteredCategory.getSettings().isEmpty()) {
                    filteredCategories.add(filteredCategory);
                }
            }
            categoryAdapter.updateCategories(filteredCategories);
        }
    }
    
    @Override
    public void onSettingClick(Setting setting, String categoryName) {
        // Open floating editor dialog
        EditorDialogFragment dialog = EditorDialogFragment.newInstance(setting, categoryName);
        dialog.setOnSettingSavedListener(new EditorDialogFragment.OnSettingSavedListener() {
            @Override
            public void onSettingSaved(Setting savedSetting) {
                // Update the setting in our data
                updateSettingInCategory(categoryName, savedSetting);
                categoryAdapter.notifyDataSetChanged();
            }
        });
        dialog.show(getSupportFragmentManager(), "EditorDialog");
    }
    
    private void updateSettingInCategory(String categoryName, Setting setting) {
        for (Category category : categories) {
            if (category.getName().equals(categoryName)) {
                for (int i = 0; i < category.getSettings().size(); i++) {
                    if (category.getSettings().get(i).getKey().equals(setting.getKey())) {
                        category.getSettings().get(i).setValue(setting.getValue());
                        break;
                    }
                }
                break;
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_load) {
            loadConfiguration();
            return true;
        } else if (id == R.id.action_export) {
            exportConfiguration();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void exportConfiguration() {
        // Export configuration as JSON
        String jsonConfig = configManager.exportConfiguration(categories);
        // Share or save the JSON file
        Toast.makeText(this, "Configuration exported", Toast.LENGTH_SHORT).show();
    }
}