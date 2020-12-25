package com.happyhappyyay.landscaperecord.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.FragmentExchange;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;


public class LawnServices extends Fragment {
    private List<CheckBox> checkBoxes;
    private FragmentExchange mListener;

    public LawnServices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        String existingServices = mListener.getServices();
        if (existingServices != null && !existingServices.isEmpty()) {
            updateCheckBoxesAndExistingService();
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

    private void updateCheckBoxesAndExistingService() {
        String existingServices = mListener.getServices();
        int lengthOfServiceString = existingServices.length();
        for (CheckBox c : checkBoxes) {
            String checkBoxText = c.getText().toString().toLowerCase();
            if (existingServices.toLowerCase().contains(checkBoxText)) {
                int startIndex = existingServices.toLowerCase().indexOf(checkBoxText);
                int endIndex = c.getText().toString().length() + Util.DELIMITER.length() + startIndex;
                if(endIndex > lengthOfServiceString) {
                    endIndex = endIndex - Util.DELIMITER.length();
                }
                if(startIndex != -1) {
                    String existingServicesPreService = existingServices.substring(0, startIndex);
                    String existingServicesPostService = existingServices.substring(endIndex);
                    existingServices = existingServicesPreService + existingServicesPostService;
                    c.setChecked(true);
                }
            }
            else {
                c.setChecked(false);
            }
        }
        mListener.setServices(existingServices);
    }

    public void markedCheckBoxes() {
        StringBuilder servicesStringBuilder = new StringBuilder();
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {
                String checkBoxText = c.getText().toString();
                String serviceString = checkBoxText + Util.DELIMITER;
                servicesStringBuilder.append(serviceString);
            }
        }
        mListener.appendServices(servicesStringBuilder.toString());
    }
}
