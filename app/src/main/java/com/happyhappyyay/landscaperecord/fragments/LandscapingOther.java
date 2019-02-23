package com.happyhappyyay.landscaperecord.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.FragmentExchange;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;


public class LandscapingOther extends Fragment {
    private List<CheckBox> checkBoxes;
    private Spinner dumpTypeSpinner, dumpMeasurementSpinner;
    private EditText dumpQuantity;
    private FragmentExchange mListener;

    public LandscapingOther() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landscaping_other, container, false);
        checkBoxes = new ArrayList<>();
        CheckBox drainTile = view.findViewById(R.id.landscaping_other_drainage);
        checkBoxes.add(drainTile);
        CheckBox till = view.findViewById(R.id.landscaping_other_till);
        checkBoxes.add(till);
        CheckBox edging = view.findViewById(R.id.landscaping_other_edging);
        checkBoxes.add(edging);
        CheckBox dump = view.findViewById(R.id.landscaping_other_dump);
        checkBoxes.add(dump);
        dumpTypeSpinner = view.findViewById(R.id.landscaping_other_dump_type_spinner);
        dumpMeasurementSpinner = view.findViewById(R.id.landscaping_other_dump_spinner_measurement);
        dumpQuantity = view.findViewById(R.id.landscaping_other_dump_amount);
        String existingServices = mListener.getServices();
        if (existingServices != null && !existingServices.isEmpty()) {
            updateCheckBoxesAndExistingService();
        }
        return view;
    }

    private void updateCheckBoxesAndExistingService() {
        String existingServices = mListener.getServices();
        final String SPACE = " ";
        int lengthOfServiceString = existingServices.length();
        for (CheckBox c : checkBoxes) {
             String checkBoxText = c.getText().toString().toLowerCase();
            if (existingServices.toLowerCase().contains(checkBoxText)) {
                int startIndex = existingServices.toLowerCase().indexOf(checkBoxText);
                int endIndex = lengthOfServiceString;
                switch (checkBoxText) {
                    case "dump":
                        for(int i = startIndex; i < lengthOfServiceString; i++ ) {
                            if(Util.DELIMITER.equals(existingServices.substring(i, i+1))) {
                                endIndex = i;
                                break;
                            }
                        }
                        String dumpPhraseStringEnd = existingServices.substring(startIndex + checkBoxText.length() + SPACE.length(),endIndex);
                        int quantityEndIndex = 0;
                        for(int i = 0; i < dumpPhraseStringEnd.length(); i++) {
                            if(Character.isLetter(dumpPhraseStringEnd.charAt(i))) {
                                quantityEndIndex = i;
                                break;
                            }
                        }
                        if(quantityEndIndex > 0) {
                            dumpQuantity.setText(dumpPhraseStringEnd.substring(0, quantityEndIndex));
                        }
                        int measurementEndIndex = 0;
                        String dumpStringAfterQuantity = dumpPhraseStringEnd.substring(quantityEndIndex);
                        for(int i = 0; i < dumpStringAfterQuantity.length(); i++) {
                            if(dumpStringAfterQuantity.substring(i, i+1).equals(SPACE)){
                                measurementEndIndex = i;
                                break;
                            }
                        }
                        for(int i = 0; i < dumpMeasurementSpinner.getAdapter().getCount(); i++)
                        if (dumpMeasurementSpinner.getAdapter().getItem(i).toString().equals(dumpStringAfterQuantity.substring(0,measurementEndIndex))) {
                            dumpMeasurementSpinner.setSelection(i);
                        }
                        String dumpStringAfterMeasurement = dumpStringAfterQuantity.substring(measurementEndIndex + SPACE.length());
                        for(int i = 0; i < dumpTypeSpinner.getAdapter().getCount(); i++)
                            if (dumpTypeSpinner.getAdapter().getItem(i).toString().equals(dumpStringAfterMeasurement)) {
                                dumpTypeSpinner.setSelection(i);
                            }

                        break;
                    default:
                        endIndex = checkBoxText.length() + Util.DELIMITER.length() + startIndex;
                        if(endIndex > lengthOfServiceString) {
                            endIndex = endIndex - Util.DELIMITER.length();
                            break;
                        }
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

    public void markedCheckBoxes() {
        StringBuilder servicesStringBuilder = new StringBuilder();
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {
                String checkBoxText = c.getText().toString();
                switch (checkBoxText.toLowerCase()) {
                    case "dump":
                        String dumpString = dumpQuantity.getText().toString();
                        String dumpAmount = "Dump " + dumpString + dumpMeasurementSpinner.getSelectedItem().toString() +
                                " " + dumpTypeSpinner.getSelectedItem().toString() + Util.DELIMITER;
                        servicesStringBuilder.append(dumpAmount);
                        break;
                    default:
                        String serviceString = checkBoxText + Util.DELIMITER;
                        servicesStringBuilder.append(serviceString);
                        break;
                }
            }
        }
        mListener.appendServices(servicesStringBuilder.toString());
    }
}
