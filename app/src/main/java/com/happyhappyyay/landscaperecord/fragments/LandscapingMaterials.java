package com.happyhappyyay.landscaperecord.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.adapter.RecyclerMaterial;
import com.happyhappyyay.landscaperecord.interfaces.FragmentExchange;
import com.happyhappyyay.landscaperecord.pojo.Material;
import com.happyhappyyay.landscaperecord.utility.LandscapingMaterialsViewModel;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;

public class LandscapingMaterials extends Fragment {

    private LandscapingMaterialsViewModel mViewModel;
    private RecyclerMaterial adapter;
    private EditText materialNameEText, materialQuantityEText, materialPriceEText;
    private Spinner materialMeasurementSpinner, materialTypeSpinner;
    private FragmentExchange mListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landscaping_materials, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.landscaping_materials_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if(mViewModel != null) {
            adapter = new RecyclerMaterial(mViewModel.getMaterials());
        }
        else {
            adapter = new RecyclerMaterial();
        }
        recyclerView.setAdapter(adapter);
        materialNameEText = view.findViewById(R.id.landscaping_materials_material_name_text);
        materialPriceEText = view.findViewById(R.id.landscaping_materials_material_price_text);
        materialQuantityEText = view.findViewById(R.id.landscaping_materials_material_quantity_text);
        materialMeasurementSpinner = view.findViewById(R.id.landscaping_materials_measure_spinner);
        materialTypeSpinner = view.findViewById(R.id.landscaping_materials_type_spinner);
        Button addMaterialButton = view.findViewById(R.id.landscaping_materials_add_material_button);
        addMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkMaterialAddition()) {
                    adapter.addMaterial(createMaterial(true));
                    resetMaterialInformation();
                }


            }
        });
        Button removeMaterialButton = view.findViewById(R.id.landscaping_materials_remove_material_button);
        removeMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkMaterialAddition()) {
                    adapter.addMaterial(createMaterial(false));
                    resetMaterialInformation();
                }
            }
        });
        List<Material> materials = mListener.getMaterials();
        if (materials != null) {
            adapter.setMaterials(materials);
            mListener.setMaterials(new ArrayList<Material>());
        }
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentExchange) {
            mListener = (FragmentExchange) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InteractionListener");
        }
    }

    private void resetMaterialInformation() {
        materialMeasurementSpinner.setSelection(0);
        materialTypeSpinner.setSelection(0);
        materialNameEText.setText("");
        materialQuantityEText.setText("");
        materialPriceEText.setText("");
    }

    private boolean checkMaterialAddition() {
        String materialSpinnerText = materialTypeSpinner.getSelectedItem().toString().toLowerCase();
        String other = "other";
        return (!(materialNameEText.getText().toString().isEmpty() &
                materialSpinnerText.equals(other))) && !materialQuantityEText.getText().toString().isEmpty();
    }

    private Material createMaterial(boolean addMaterial) {
        String name = materialNameEText.getText().toString();
        double quantity;
        try {
            quantity = Double.parseDouble(materialQuantityEText.getText().toString());
        }
        catch(Exception e){
            e.printStackTrace();
            quantity = 1;
        }
        String measurement = materialMeasurementSpinner.getSelectedItem().toString();
        String type = materialTypeSpinner.getSelectedItem().toString();
        Material material = new Material(name, type, addMaterial);
        material.setQuantity(quantity);
        material.setMeasurement(measurement);

        if (!materialPriceEText.getText().toString().isEmpty()) {
            double price = Double.parseDouble(materialPriceEText.getText().toString());
            material.setPrice(price);
        }

        return material;
    }

    public void retrieveMaterials() {
        StringBuilder servicesStringBuilder = new StringBuilder();
        List<Material> materials = adapter.getMaterials();
        for(int i = 0; i < materials.size(); i++) {
            if(i == 0) {
                servicesStringBuilder.append(System.getProperty("line.separator"));
            }
            String materialString = materials.get(i).toString() + Util.DELIMITER + System.getProperty("line.separator");
            servicesStringBuilder.append(materialString);
        }
        mListener.setMaterials(adapter.getMaterials());
        mListener.appendServices(servicesStringBuilder.toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LandscapingMaterialsViewModel.class);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mViewModel.setMaterials(adapter.getMaterials());
        super.onSaveInstanceState(outState);
    }

}
