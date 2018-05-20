package com.example.kingdenis.landscaperecord;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class LandscapeServices extends Fragment implements FragmentListener {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Material> materials = new ArrayList<>();
    private FragmentListener callBack;
    private CheckBox drain, till, edging, dump, other;
    private List<CheckBox> checkBoxes;
    private Button deleteButton, addButton, removeButton;
    private EditText materialName, materialQuantity, materialPrice;
    private Spinner materialTypeSpinner, materialMeasurementSpinner;
    private boolean pause;
    private int recyclerPosition = 0;

    private final ServiceType SERVICE_TYPE = ServiceType.LANDSCAPING_SERVICES;

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
        recyclerView = (RecyclerView) view.findViewById(R.id.land_services_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
        checkBoxes = new ArrayList<>();
        drain = (CheckBox) view.findViewById(R.id.land_services_drainage);
        checkBoxes.add(drain);
        till = (CheckBox) view.findViewById(R.id.land_services_till);
        checkBoxes.add(till);
        edging = (CheckBox) view.findViewById(R.id.land_services_edging);
        checkBoxes.add(edging);
        dump = (CheckBox) view.findViewById(R.id.land_services_dump);
        checkBoxes.add(dump);
        other = (CheckBox) view.findViewById(R.id.land_services_other);
        checkBoxes.add(other);
        materialName = (EditText) view.findViewById(R.id.land_services_material_name_text);
        materialQuantity = (EditText) view.findViewById(R.id.land_services_material_quantity_text);
        materialPrice = (EditText) view.findViewById(R.id.land_services_material_price_text);
        materialTypeSpinner = (Spinner) view.findViewById(R.id.land_services_type_spinner);
        materialMeasurementSpinner = (Spinner) view.findViewById(R.id.land_services_measure_spinner);
        deleteButton = (Button) view.findViewById(R.id.land_services_delete_button);
        deleteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMaterial();
                    }
                }
        );

        addButton = (Button) view.findViewById(R.id.land_services_add_material_button);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Material material = createMaterial(true);
                        if(checkMaterialAddition()) {
                            adapter.addMaterial(material, recyclerPosition);
                            recyclerPosition += 1;
                            materials.add(material);
                        }
                    }
                }
        );

        removeButton = (Button) view.findViewById(R.id.land_services_remove_material_button);
        removeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Material material = createMaterial(false);
                        if(checkMaterialAddition()) {
                            adapter.addMaterial(material, recyclerPosition);
                            recyclerPosition  += 1;
                            materials.add(material);
                        }
                    }
                }
        );
        setRetainInstance(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    public void deleteMaterial() {
        List<Material> materialsToRemove = new ArrayList<>();
        if(materials != null) {
            materialsToRemove = adapter.getSelectedMaterials();
            for (int i = 0; i < materialsToRemove.size(); i++) {
                for (int j = 0; j < materials.size(); j++){
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
        if ((materialName.toString().isEmpty() &
                materialSpinnerText.equals(other)) || materialQuantity.toString().isEmpty()) {
            return false;
        }
        else {
            return true;
        }

    }

    private Material createMaterial(boolean addMaterial) {
        String name = materialName.getText().toString();
        double quantity = Double.parseDouble(materialQuantity.getText().toString());
        String measurement = materialMeasurementSpinner.getSelectedItem().toString();
        String type = materialTypeSpinner.getSelectedItem().toString();
        if (!type.toLowerCase().equals("Drain Tile")) {
            type = type.toUpperCase();
        }
        else {
            type = "DRAIN_TILE";
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
        for(CheckBox c: checkBoxes) {
            if(c.isChecked()) {
                services += c.getText().toString() + " ";
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
