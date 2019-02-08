package com.happyhappyyay.landscaperecord.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.utility.ExistingService;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;


public class LawnServices extends Fragment {
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
        if(ExistingService.getExistingService().getServices() != null) {

        }
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
        String existingServices = ExistingService.getExistingService().getServices();
        if (existingServices != null && !existingServices.isEmpty()) {
            ExistingService.getExistingService().setServices(updateCheckBoxesAndExistingService(ExistingService.getExistingService().getServices()));
        }
        setRetainInstance(true);
        return view;
    }

    private String updateCheckBoxesAndExistingService(String existingServices) {
        final String SPACE = " ";
        final String TYPE = "Lawn";
        int lengthOfServiceString = existingServices.length();
        for (CheckBox c : checkBoxes) {
            String checkBoxText = c.getText().toString().toLowerCase();
            if (existingServices.toLowerCase().contains(checkBoxText)) {
                int startIndex = existingServices.toLowerCase().indexOf(checkBoxText);
                int endIndex = lengthOfServiceString;
                if(startIndex > 0) {
                    if (existingServices.substring(startIndex - 1, startIndex).equals(Util.DELIMITER)) {
                        switch (checkBoxText) {
                            case "other:":
                                int tempStartIndex = startIndex - TYPE.length() - SPACE.length();
                                while (!existingServices.substring(tempStartIndex, startIndex - SPACE.length()).equals(TYPE) & startIndex != -1) {
                                    startIndex = existingServices.indexOf(c.getText().toString(), startIndex + checkBoxText.length());
                                    tempStartIndex = startIndex - TYPE.length() - SPACE.length();
                                }
                                if (startIndex != -1) {
                                    int otherTextStartIndex = startIndex +
                                            checkBoxText.length() + SPACE.length();
                                    startIndex = tempStartIndex;

                                    for (int i = otherTextStartIndex; i < lengthOfServiceString; i++) {
                                        if (Util.DELIMITER.equals(existingServices.substring(i, i + 1))) {
                                            endIndex = i;
                                            break;
                                        }
                                    }

                                    otherText.setText(existingServices.substring(otherTextStartIndex, endIndex));
                                }
                                break;
                            default:
                                endIndex = c.getText().toString().length() + Util.DELIMITER.length() + startIndex;
                                if (endIndex > lengthOfServiceString) {
                                    endIndex = endIndex - Util.DELIMITER.length();
                                    break;
                                }
                        }
                    }
                    if (startIndex != -1) {
                        String existingServicesPreService = existingServices.substring(0, startIndex);
                        String existingServicesPostService = existingServices.substring(endIndex);
                        existingServices = existingServicesPreService + existingServicesPostService;
                        c.setChecked(true);
                    }
                }
            }
            else {
                c.setChecked(false);
            }
        }
        return existingServices;
    }

    public List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public void setCheckBoxes(List<CheckBox> checkBoxes) {
        this.checkBoxes = checkBoxes;
    }

    public String markedCheckBoxes() {
        final String SPACE = " ";
        final String TYPE = "Lawn";
        StringBuilder servicesStringBuilder = new StringBuilder();
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {
                String checkBoxText = c.getText().toString();
                if(checkBoxText.toLowerCase().equals("other")) {
                    String otherString = otherText.getText().toString();
                    if (!otherString.isEmpty()) {
                        otherString = TYPE + SPACE + checkBoxText + ": " + otherString + Util.DELIMITER;
                        servicesStringBuilder.append(otherString);
                    }
                }
                else {
                    String serviceString = checkBoxText + Util.DELIMITER;
                    servicesStringBuilder.append(serviceString);
                }
            }
        }
        return servicesStringBuilder.toString();
    }
}
