package com.happyhappyyay.landscaperecord;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class SnowServices extends Fragment {
    private List<CheckBox> checkBoxes;
    private EditText otherText, saltText;
    private Spinner saltSpinner;

    public SnowServices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_snow, container, false);
        checkBoxes = new ArrayList<>();
        CheckBox plow = view.findViewById(R.id.snow_services_plow);
        checkBoxes.add(plow);
        CheckBox shovel = view.findViewById(R.id.snow_services_shovel);
        checkBoxes.add(shovel);
        CheckBox snowBlow = view.findViewById(R.id.snow_services_snow_blow);
        checkBoxes.add(snowBlow);
        CheckBox salt = view.findViewById(R.id.snow_services_salt);
        checkBoxes.add(salt);
        CheckBox other = view.findViewById(R.id.snow_services_other);
        checkBoxes.add(other);
        otherText = view.findViewById(R.id.snow_services_other_text);
        saltText = view.findViewById(R.id.snow_services_salt_text);
        saltSpinner = view.findViewById(R.id.snow_services_salt_spinner);
        setRetainInstance(true);
        return view;
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
                    case "salt":
                        String saltString = saltText.getText().toString();
                        if (!saltString.isEmpty()) {
                            String saltAmount = "Salt " + saltString + saltSpinner.getSelectedItem().toString() + Util.DELIMITER;
                            servicesStringBuilder.append(saltAmount);
                        } else {
                            String salt = c.getText().toString() + Util.DELIMITER;
                            servicesStringBuilder.append(salt);
                        }
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
}
