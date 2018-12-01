package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.net.Uri;
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
        setRetainInstance(true);
        return view;
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
                        String dumpString = dumpQuantity.getText().toString();
                        String dumpAmount = "Dump " + dumpString + dumpTypeSpinner.getSelectedItem().toString()
                                + ": " + dumpString + dumpMeasurementSpinner.getSelectedItem().toString() +
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
}
