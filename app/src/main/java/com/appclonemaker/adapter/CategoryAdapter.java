package com.appclonemaker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appclonemaker.R;
import com.appclonemaker.model.Category;
import com.appclonemaker.model.Setting;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    
    private List<Category> categories;
    private OnSettingClickListener listener;
    private SettingAdapter settingAdapter;
    
    public interface OnSettingClickListener {
        void onSettingClick(Setting setting, String categoryName);
    }
    
    public CategoryAdapter(OnSettingClickListener listener) {
        this.categories = new ArrayList<>();
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }
    
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    public void updateCategories(List<Category> newCategories) {
        this.categories.clear();
        this.categories.addAll(newCategories);
        notifyDataSetChanged();
    }
    
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;
        private ImageView expandIcon;
        private RecyclerView settingsRecycler;
        private View categoryHeader;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            expandIcon = itemView.findViewById(R.id.expand_icon);
            settingsRecycler = itemView.findViewById(R.id.settings_recycler);
            categoryHeader = itemView.findViewById(R.id.category_header);
            
            // Setup RecyclerView for settings
            settingsRecycler.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            settingAdapter = new SettingAdapter(listener);
            settingsRecycler.setAdapter(settingAdapter);
            
            // Set initial visibility
            settingsRecycler.setVisibility(View.GONE);
        }
        
        public void bind(Category category) {
            categoryName.setText(formatLabel(category.getName()));
            
            // Setup click listener for header
            categoryHeader.setOnClickListener(v -> {
                category.toggleExpanded();
                updateExpansionState(category);
            });
            
            // Update expansion state
            updateExpansionState(category);
            
            // Update settings adapter
            settingAdapter.updateSettings(category.getSettings(), category.getName());
        }
        
        private void updateExpansionState(Category category) {
            if (category.isExpanded()) {
                expandIcon.setRotation(180);
                settingsRecycler.setVisibility(View.VISIBLE);
            } else {
                expandIcon.setRotation(0);
                settingsRecycler.setVisibility(View.GONE);
            }
        }
    }
    
    private String formatLabel(String key) {
        return key.replaceAll("([A-Z])", " $1")
                 .replaceAll("^.", str -> str.toUpperCase())
                 .replace("Web View", "WebView");
    }
}