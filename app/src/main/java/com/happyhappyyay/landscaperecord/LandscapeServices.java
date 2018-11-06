package com.happyhappyyay.landscaperecord;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class LandscapeServices extends Fragment {
    private RecyclerMaterialAdapter adapter;
    private List<Material> materials = new ArrayList<>();
    private List<CheckBox> checkBoxes;
    private EditText materialName, materialQuantity, materialPrice, dumpAmount, otherText;
    private Spinner materialTypeSpinner, materialMeasurementSpinner, dumpSpinner, dumpTypeSpinner;
    private int recyclerPosition = 0;

    public LandscapeServices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landscaping, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.land_services_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerMaterialAdapter();
        recyclerView.setAdapter(adapter);
        checkBoxes = new ArrayList<>();
        CheckBox drain = view.findViewById(R.id.land_services_drainage);
        checkBoxes.add(drain);
        CheckBox till = view.findViewById(R.id.land_services_till);
        checkBoxes.add(till);
        CheckBox edging = view.findViewById(R.id.land_services_edging);
        checkBoxes.add(edging);
        CheckBox dump = view.findViewById(R.id.land_services_dump);
        checkBoxes.add(dump);
        CheckBox other = view.findViewById(R.id.land_services_other);
        checkBoxes.add(other);
        materialName = view.findViewById(R.id.land_services_material_name_text);
        materialQuantity = view.findViewById(R.id.land_services_material_quantity_text);
        materialPrice = view.findViewById(R.id.land_services_material_price_text);
        materialTypeSpinner = view.findViewById(R.id.land_services_type_spinner);
        materialMeasurementSpinner = view.findViewById(R.id.land_services_measure_spinner);
        dumpAmount = view.findViewById(R.id.land_services_dump_amount);
        dumpSpinner = view.findViewById(R.id.land_services_dump_spinner_measurement);
        otherText = view.findViewById(R.id.land_services_other_text);
        dumpTypeSpinner = view.findViewById(R.id.land_services_dump_type_spinner);
        FloatingActionButton deleteButton = view.findViewById(R.id.land_services_delete_button);
        deleteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMaterial();
                    }
                }
        );

        Button addButton = view.findViewById(R.id.land_services_add_material_button);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Material material = createMaterial(true);
                        if (checkMaterialAddition()) {
                            adapter.addMaterial(material, recyclerPosition);
                            recyclerPosition += 1;
                            materials.add(material);
                            resetMaterialInformation();
                        }
                    }
                }
        );

        Button removeButton = view.findViewById(R.id.land_services_remove_material_button);
        removeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Material material = createMaterial(false);
                        if (checkMaterialAddition()) {
                            adapter.addMaterial(material, recyclerPosition);
                            recyclerPosition += 1;
                            materials.add(material);
                            resetMaterialInformation();
                        }
                    }
                }
        );
        setRetainInstance(true);
        return view;
    }

    private void resetMaterialInformation() {
        materialMeasurementSpinner.setSelection(0);
        materialTypeSpinner.setSelection(0);
        materialName.setText("");
        materialQuantity.setText("");
        materialPrice.setText("");
    }

    public void deleteMaterial() {
        List<Material> materialsToRemove;
        if (materials != null) {
            materialsToRemove = adapter.getSelectedMaterials();
            for (int i = 0; i < materialsToRemove.size(); i++) {
                for (int j = 0; j < materials.size(); j++) {
                    if (materialsToRemove.get(i).equals(materials.get(j))) {
                        materials.remove(j);
                        adapter.removeAt(j);
                        recyclerPosition -= 1;
                    }
                }
            }
        }
    }

    private boolean checkMaterialAddition() {
        String materialSpinnerText = materialTypeSpinner.getSelectedItem().toString().toLowerCase();
        String other = MaterialType.OTHER.toString().toLowerCase();
        return (!(materialName.toString().isEmpty() &
                materialSpinnerText.equals(other))) && !materialQuantity.toString().isEmpty();

    }

    private Material createMaterial(boolean addMaterial) {
        String name = materialName.getText().toString();
        double quantity;
        try {
            quantity = Double.parseDouble(materialQuantity.getText().toString());
        }
        catch(Exception e){
            e.printStackTrace();
            quantity = 1;
        }
        String measurement = materialMeasurementSpinner.getSelectedItem().toString();
        String type = materialTypeSpinner.getSelectedItem().toString();
        if (!type.toLowerCase().equals("Drain Tile")) {
            type = type.toUpperCase();
        } else {
            type = "DRAIN_TILE";
        }

        if (name.equals("")) {
            if (!type.isEmpty()) {
                name = type;
            }
        }

        Material material = new Material(name, MaterialType.valueOf(type), addMaterial);
        material.setQuantity(quantity);
        material.setMeasurement(measurement);

        if (!materialPrice.getText().toString().isEmpty()) {
            double price = Double.parseDouble(materialPrice.getText().toString());
            material.setMaterialPrice(price);
        }


        return material;
    }

    public String markedCheckBoxes() {
        StringBuilder servicesStringBuilder = new StringBuilder();

        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {
                String checkBoxText = c.getText().toString().toLowerCase();
                switch(checkBoxText) {
                    case "other":
                        String otherString = otherText.getText().toString() + Util.DELIMITER;
                        if (!otherString.isEmpty()) {
                            servicesStringBuilder.append(otherString);
                        }
                        break;
                    case "dump":
                        String dumpString = dumpAmount.getText().toString();
                            String dumpAmount = "Dump " + dumpString + dumpTypeSpinner.getSelectedItem().toString()
                                    + ": " + dumpString + dumpSpinner.getSelectedItem().toString() +
                                    Util.DELIMITER;
                            servicesStringBuilder.append(dumpAmount);
                        break;
                    default:
                        String serviceString = c.getText().toString() + Util.DELIMITER;
                        servicesStringBuilder.append(serviceString);
                        break;
                }
            }
        }
        return servicesStringBuilder.toString();
    }

    public List<Material> getMaterials() {
        return materials;
    }
}
