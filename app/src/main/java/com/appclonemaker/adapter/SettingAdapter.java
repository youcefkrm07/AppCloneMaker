package com.appclonemaker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appclonemaker.R;
import com.appclonemaker.model.Category;
import com.appclonemaker.model.Setting;

import java.util.ArrayList;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingViewHolder> {
    
    private List<Setting> settings;
    private String currentCategoryName;
    private CategoryAdapter.OnSettingClickListener listener;
    
    public SettingAdapter(CategoryAdapter.OnSettingClickListener listener) {
        this.settings = new ArrayList<>();
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_setting, parent, false);
        return new SettingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        Setting setting = settings.get(position);
        holder.bind(setting, currentCategoryName);
    }
    
    @Override
    public int getItemCount() {
        return settings.size();
    }
    
    public void updateSettings(List<Setting> newSettings, String categoryName) {
        this.settings.clear();
        this.settings.addAll(newSettings);
        this.currentCategoryName = categoryName;
        notifyDataSetChanged();
    }
    
    class SettingViewHolder extends RecyclerView.ViewHolder {
        private TextView settingName;
        private TextView settingValue;
        private View itemContainer;
        
        public SettingViewHolder(@NonNull View itemView) {
            super(itemView);
            settingName = itemView.findViewById(R.id.setting_name);
            settingValue = itemView.findViewById(R.id.setting_value);
            itemContainer = itemView.findViewById(R.id.setting_container);
        }
        
        public void bind(Setting setting, String categoryName) {
            settingName.setText(formatLabel(setting.getName()));
            settingValue.setText(setting.getDisplayValue());
            settingValue.setTextColor(getValueColor(setting));
            
            // Set click listener
            itemContainer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSettingClick(setting, categoryName);
                }
            });
            
            // Apply setting type specific styling
            applySettingTypeStyling(setting);
        }
        
        private void applySettingTypeStyling(Setting setting) {
            switch (setting.getType()) {
                case BOOLEAN:
                    // Add boolean-specific styling
                    break;
                case CUSTOM:
                    // Add custom editor styling
                    break;
                case COMPOUND:
                    // Add compound setting styling
                    break;
                default:
                    // Default styling
                    break;
            }
        }
        
        private int getValueColor(Setting setting) {
            switch (setting.getType()) {
                case BOOLEAN:
                    return setting.isEnabled() ? 
                           itemView.getContext().getColor(R.color.success_green) : 
                           itemView.getContext().getColor(R.color.error_red);
                case CUSTOM:
                case COMPOUND:
                    return itemView.getContext().getColor(R.color.primary_blue);
                default:
                    return itemView.getContext().getColor(R.color.text_secondary);
            }
        }
    }
    
    private String formatLabel(String key) {
        return key.replaceAll("([A-Z])", " $1")
                 .replaceAll("^.", str -> str.toUpperCase())
                 .replace("Web View", "WebView");
    }
}