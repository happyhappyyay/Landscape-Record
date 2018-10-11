package com.happyhappyyay.landscaperecord;

import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class LandscapeServices extends Fragment implements FragmentListener {
    private final ServiceType SERVICE_TYPE = ServiceType.LANDSCAPING_SERVICES;
    private RecyclerView recyclerView;
    private RecyclerMaterialAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Material> materials = new ArrayList<>();
    private FragmentListener callBack;
    private CheckBox drain, till, edging, dump, other;
    private List<CheckBox> checkBoxes;
    private Button addButton, removeButton;
    private FloatingActionButton deleteButton;
    private EditText materialName, materialQuantity, materialPrice, dumpText, otherText;
    private Spinner materialTypeSpinner, materialMeasurementSpinner, dumpSpinner, dumpTypeSpinner;
    private boolean pause;
    private int recyclerPosition = 0;

    public LandscapeServices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landscaping, container, false);
        recyclerView = view.findViewById(R.id.land_services_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerMaterialAdapter();
        recyclerView.setAdapter(adapter);
        checkBoxes = new ArrayList<>();
        drain = view.findViewById(R.id.land_services_drainage);
        checkBoxes.add(drain);
        till = view.findViewById(R.id.land_services_till);
        checkBoxes.add(till);
        edging = view.findViewById(R.id.land_services_edging);
        checkBoxes.add(edging);
        dump = view.findViewById(R.id.land_services_dump);
        checkBoxes.add(dump);
        other = view.findViewById(R.id.land_services_other);
        checkBoxes.add(other);
        materialName = view.findViewById(R.id.land_services_material_name_text);
        materialQuantity = view.findViewById(R.id.land_services_material_quantity_text);
        materialPrice = view.findViewById(R.id.land_services_material_price_text);
        materialTypeSpinner = view.findViewById(R.id.land_services_type_spinner);
        materialMeasurementSpinner = view.findViewById(R.id.land_services_measure_spinner);
        dumpText = view.findViewById(R.id.land_services_dump_text);
        dumpSpinner = view.findViewById(R.id.land_services_dump_spinner);
        otherText = view.findViewById(R.id.land_services_other_text);
        dumpTypeSpinner = view.findViewById(R.id.land_services_dump_type_spinner);
        deleteButton = view.findViewById(R.id.land_services_delete_button);
        deleteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMaterial();
                    }
                }
        );

        addButton = view.findViewById(R.id.land_services_add_material_button);
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

        removeButton = view.findViewById(R.id.land_services_remove_material_button);
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
        List<Material> materialsToRemove = new ArrayList<>();
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

        if (name == "") {
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

    public void setFragmentListener(FragmentListener listener) {
        callBack = listener;
    }

    public String markedCheckBoxes() {
        String services = "";
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {

                if (c.getText().toString().toLowerCase().equals("other")) {
                    String otherString = otherText.getText().toString();
                    if (!otherString.isEmpty()) {
                        services += "Other " + otherString + "#*#";
                    }
                } else if (c.getText().toString().toLowerCase().equals("dump")) {
                    String dumpString = dumpText.getText().toString();
                    if (!dumpString.isEmpty()) {
                        services += "Dump " + dumpTypeSpinner.getSelectedItem().toString() + ": " +
                                dumpString + dumpSpinner.getSelectedItem().toString() + "#*#";
                    }
                } else {
                    services += c.getText().toString() + "#*#";
                }
            }
        }
        return services;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    @Override
    public void checkBoxData(String string) {
    }

    @Override
    public void pausedFragment(ServiceType serviceType) {

    }
}
