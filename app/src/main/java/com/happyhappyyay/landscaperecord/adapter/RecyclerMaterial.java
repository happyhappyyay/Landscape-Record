package com.happyhappyyay.landscaperecord.adapter;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.pojo.Material;

import java.util.ArrayList;
import java.util.List;

public class RecyclerMaterial extends RecyclerView.Adapter {
    private List<Material> materials;

    public RecyclerMaterial(List<Material> materials) {
        this.materials = materials;
    }

    public RecyclerMaterial() {
        materials = new ArrayList<>();
    }

    public void addMaterial(Material material) {
        int position = materials.size();
        materials.add(material);
        notifyItemInserted(position);
    }

    private void removeAt(int position) {
        materials.remove(position);
        notifyItemRemoved(position);
    }

    public List<Material> getMaterials(){
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.landscaping_material_item, parent, false);
        return new ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);

    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView typeText;
        private TextView priceText;
        private TextView additionText;
        private TextView quantityText;
        private FloatingActionButton deleteButton;


        private ListViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.landscaping_material_item_material_name);
            typeText = view.findViewById(R.id.landscaping_material_item_material_type);
            priceText = view.findViewById(R.id.landscaping_material_item__material_price);
            additionText = view.findViewById(R.id.landscaping_material_item_add_remove);
            quantityText = view.findViewById(R.id.landscaping_material_item_material_amount);
            deleteButton = view.findViewById(R.id.landscaping_material_item_delete);
        }

        public void bindView(final int position) {

            final Material material = materials.get(position);
            String quantity = Double.toString(material.getQuantity()) + material.getMeasurement();
            String price = Double.toString(material.getPrice());
            nameText.setText(material.getName());
            typeText.setText(material.getType());
            priceText.setText(price);
            quantityText.setText(quantity);
            additionText.setText(material.isAdd() ? "Add" : "Remove");
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeAt(position);
                }
            });
        }
    }
}
