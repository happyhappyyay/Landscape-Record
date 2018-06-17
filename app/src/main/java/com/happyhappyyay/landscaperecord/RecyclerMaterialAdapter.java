package com.happyhappyyay.landscaperecord;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerMaterialAdapter extends RecyclerView.Adapter {
    private static final String TAG = "selected";
    protected List<Material> materials;
    protected List<Material> selectedMaterials;
    private AppDatabase db;
    private int selectedPos = RecyclerView.NO_POSITION;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public RecyclerMaterialAdapter() {
        selectedMaterials = new ArrayList<>();
        materials = new ArrayList<>();
    }

    public List<Material> getSelectedMaterials() {
        return selectedMaterials;
    }

    public void addMaterial(Material material, int position) {
        materials.add(material);
        notifyItemInserted(position);
    }

    public void removeAt(int position) {
        selectedItems.clear();
        materials.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        Log.d(TAG, "onBindViewHolder: " + materials.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_item, parent, false);
        return new ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setSelected(selectedItems.get(position, false));
        ((ListViewHolder) holder).bindView(position);

    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView name;
        public TextView type;
        public TextView price;
        public TextView addition;
        public TextView quantity;
        public LinearLayout linearLayout;


        public ListViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.material_item_name);
            type = (TextView) view.findViewById(R.id.material_item_type);
            price = (TextView) view.findViewById(R.id.material_item_price);
            addition = (TextView) view.findViewById(R.id.material_item_add_remove_text);
            quantity = (TextView) view.findViewById(R.id.material_item_quantity);
            linearLayout = (LinearLayout) view.findViewById(R.id.material_item_layout);
            view.setOnClickListener(this);
        }

        public void bindView(int position) {
            Material material = materials.get(position);
            name.setText(material.getMaterialName());
            type.setText(material.getMaterialType().toString());
            price.setText(Double.toString(material.getMaterialPrice()));
            quantity.setText(Double.toString(material.getQuantity()) + material.getMeasurement());
            addition.setText(material.isAddMaterial() ? "Add" : "Remove");
        }

        @Override
        public void onClick(View view) {

            if (selectedItems.get(getAdapterPosition(), false)) {
                selectedItems.delete(getAdapterPosition());
                linearLayout.setSelected(false);
                selectedMaterials.remove(materials.get(getAdapterPosition()));
            } else {
                selectedItems.put(getAdapterPosition(), true);
                linearLayout.setSelected(true);
                selectedPos = getLayoutPosition();
                notifyItemChanged(selectedPos);
                selectedMaterials.add(materials.get(getAdapterPosition()));
            }
        }
    }
}
