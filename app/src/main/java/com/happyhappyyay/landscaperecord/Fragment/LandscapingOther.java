package com.happyhappyyay.landscaperecord.Fragment;

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
import com.happyhappyyay.landscaperecord.Utility.ExistingService;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.ArrayList;
import java.util.List;


public class LandscapingOther extends Fragment {
    private List<CheckBox> checkBoxes;
    private Spinner dumpTypeSpinner, dumpMeasurementSpinner;
    private EditText dumpQuantity, otherText;

    public LandscapingOther() {
        // Required empty public constructor
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
        CheckBox other = view.findViewById(R.id.landscaping_other_other);
        checkBoxes.add(other);
        dumpTypeSpinner = view.findViewById(R.id.landscaping_other_dump_type_spinner);
        dumpMeasurementSpinner = view.findViewById(R.id.landscaping_other_dump_spinner_measurement);
        dumpQuantity = view.findViewById(R.id.landscaping_other_dump_amount);
        otherText = view.findViewById(R.id.landscaping_other_other_text);
        String existingServices = ExistingService.getExistingService().getServices();
        if (existingServices != null && !existingServices.isEmpty()) {
            ExistingService.getExistingService().setServices(updateCheckBoxesAndExistingService(ExistingService.getExistingService().getServices()));
        }
        return view;
    }

    private String updateCheckBoxesAndExistingService(String existingServices) {
        final String SPACE = " ";
        final String TYPE = "Landscape";
        final String OTHER_SEPARATOR = ": ";
        for (CheckBox c : checkBoxes) {
             String checkBoxText = c.getText().toString();
            if (existingServices.toLowerCase().contains(checkBoxText.toLowerCase())) {
                int startIndex = existingServices.indexOf(c.getText().toString());
                int endIndex = existingServices.length();
                switch (checkBoxText.toLowerCase()) {
                    case "other":
                        int tempStartIndex = startIndex - TYPE.length() - SPACE.length();
                        while(!existingServices.substring(tempStartIndex, startIndex -SPACE.length()).equals(TYPE) & startIndex != -1) {
                            startIndex = existingServices.indexOf(c.getText().toString(), startIndex + checkBoxText.length());
                            tempStartIndex = startIndex - TYPE.length() - SPACE.length();
                        }
                        if (startIndex != -1) {
                            int otherTextStartIndex = startIndex +
                                    checkBoxText.length() + OTHER_SEPARATOR.length();
                            startIndex = tempStartIndex;

                            for(int i = otherTextStartIndex; i < existingServices.length(); i++ ) {
                                if(Util.DELIMITER.equals(existingServices.substring(i, i+1))) {
                                    endIndex = i;
                                    break;
                                }
                            }

                            otherText.setText(existingServices.substring(otherTextStartIndex, endIndex));
                        }
                        break;
                    case "dump":
                        endIndex = existingServices.length();
                        for(int i = startIndex; i < existingServices.length(); i++ ) {
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
                        endIndex = c.getText().toString().length() + Util.DELIMITER.length() + startIndex;
                        if(endIndex > existingServices.length()) {
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
        return existingServices;
    }

    public List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public void setCheckBoxes(List<CheckBox> checkBoxes) {
        this.checkBoxes = checkBoxes;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public String markedCheckBoxes() {
        final String SPACE = " ";
        final String TYPE = "Landscape";
        StringBuilder servicesStringBuilder = new StringBuilder();
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {
                String checkBoxText = c.getText().toString().toLowerCase();
                switch (checkBoxText) {
                    case "other":
                        String otherString = otherText.getText().toString();
                        if (!otherString.isEmpty()) {
                            otherString = TYPE + SPACE + c.getText().toString() + ": " + otherString + Util.DELIMITER;
                            servicesStringBuilder.append(otherString);
                        }
                        break;
                    case "dump":
                        String dumpString = dumpQuantity.getText().toString();
                        String dumpAmount = "Dump " + dumpString + dumpMeasurementSpinner.getSelectedItem().toString() +
                                " " + dumpTypeSpinner.getSelectedItem().toString() + Util.DELIMITER;
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
}
