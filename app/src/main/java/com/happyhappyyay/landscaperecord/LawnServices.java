package com.happyhappyyay.landscaperecord;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class LawnServices extends Fragment{
    private List<CheckBox> checkBoxes;
    private EditText otherText;


    public LawnServices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lawn, container, false);
        checkBoxes = new ArrayList<>();
        CheckBox sprayGrass = view.findViewById(R.id.lawn_services_spray_grass);
        checkBoxes.add(sprayGrass);
        CheckBox sprayLandscape = view.findViewById(R.id.lawn_services_spray_landscape);
        checkBoxes.add(sprayLandscape);
        CheckBox cut = view.findViewById(R.id.lawn_services_cut);
        checkBoxes.add(cut);
        CheckBox prune = view.findViewById(R.id.lawn_services_prune);
        checkBoxes.add(prune);
        CheckBox fertilize = view.findViewById(R.id.lawn_services_fertilize);
        checkBoxes.add(fertilize);
        CheckBox rake = view.findViewById(R.id.lawn_services_rake);
        checkBoxes.add(rake);
        CheckBox pullWeeds = view.findViewById(R.id.lawn_services_pull_weeds);
        checkBoxes.add(pullWeeds);
        CheckBox cleanup = view.findViewById(R.id.lawn_services_clean_up);
        checkBoxes.add(cleanup);
        CheckBox aerate = view.findViewById(R.id.lawn_services_aerate);
        checkBoxes.add(aerate);
        CheckBox removeLeaves = view.findViewById(R.id.lawn_services_remove_leaves);
        checkBoxes.add(removeLeaves);
        CheckBox other = view.findViewById(R.id.lawn_services_other);
        checkBoxes.add(other);
        otherText = view.findViewById(R.id.lawn_services_other_text);
        setRetainInstance(true);
        return view;
    }

    public String markedCheckBoxes() {
        StringBuilder servicesStringBuilder = new StringBuilder();
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {
                String checkBoxText = c.getText().toString().toLowerCase();
                if(checkBoxText.equals("other")) {
                    String otherString = otherText.getText().toString();
                    if (!otherString.isEmpty()) {
                        otherString += Util.DELIMITER;
                        servicesStringBuilder.append(otherString);
                    }
                }
                else {
                    String serviceString = c.getText().toString() + Util.DELIMITER;
                    servicesStringBuilder.append(serviceString);
                }
            }
        }
        return servicesStringBuilder.toString();
    }
}
