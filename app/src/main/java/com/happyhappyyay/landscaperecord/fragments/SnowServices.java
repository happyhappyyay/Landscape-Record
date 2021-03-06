package com.happyhappyyay.landscaperecord.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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


public class SnowServices extends Fragment {
    private List<CheckBox> checkBoxes;
    private EditText saltText;
    private Spinner saltSpinner;
    private FragmentExchange mListener;

    public SnowServices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        saltText = view.findViewById(R.id.snow_services_salt_text);
        saltSpinner = view.findViewById(R.id.snow_services_salt_spinner);
        String existingServices = mListener.getServices();
        if (existingServices != null && !existingServices.isEmpty()) {
            updateCheckBoxesAndExistingService();
        }
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
        final String SPACE = " ";
        final String SALT = getString(R.string.snow_services_salt);
        int lengthOfServiceString = existingServices.length();
        for (CheckBox c : checkBoxes) {
            String checkBoxText = c.getText().toString().toLowerCase();
            if (existingServices.toLowerCase().contains(checkBoxText)) {
                int startIndex = existingServices.toLowerCase().indexOf(checkBoxText);
                int endIndex = lengthOfServiceString;
                if (checkBoxText.equals(SALT.toLowerCase())) {
                    for (int i = startIndex; i < lengthOfServiceString; i++) {
                        if (Util.DELIMITER.equals(existingServices.substring(i, i + 1))) {
                            endIndex = i;
                            break;
                        }
                    }
                    if (endIndex - startIndex > checkBoxText.length()) {
                        String saltPhraseStringEnd = existingServices.substring(startIndex + checkBoxText.length() + SPACE.length(), endIndex);
                        int quantityEndIndex = 0;
                        for (int i = 0; i < saltPhraseStringEnd.length(); i++) {
                            if (Character.isLetter(saltPhraseStringEnd.charAt(i))) {
                                quantityEndIndex = i;
                                break;
                            }
                        }
                        if (quantityEndIndex > 0) {
                            saltText.setText(saltPhraseStringEnd.substring(0, quantityEndIndex));
                        }

                        String saltStringAfterQuantity = saltPhraseStringEnd.substring(quantityEndIndex);
                        for (int i = 0; i < saltSpinner.getAdapter().getCount(); i++)
                            if (saltSpinner.getAdapter().getItem(i).toString().equals(saltStringAfterQuantity)) {
                                saltSpinner.setSelection(i);
                            }
                    }
                }
                else {
                        endIndex = c.getText().toString().length() + Util.DELIMITER.length() + startIndex;
                        if(endIndex > lengthOfServiceString) {
                            endIndex = endIndex - Util.DELIMITER.length();
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

    public void markedCheckBoxes() {
        final String SPACE = " ";
        final String SALT = getString(R.string.snow_services_salt);
        StringBuilder servicesStringBuilder = new StringBuilder();
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {
                String checkBoxText = c.getText().toString();
                if (checkBoxText.toLowerCase().equals(SALT.toLowerCase())) {
                    String saltString = saltText.getText().toString();
                    if (!saltString.isEmpty()) {
                        String saltAmount = checkBoxText + SPACE + saltString + saltSpinner.getSelectedItem().toString() + Util.DELIMITER;
                        servicesStringBuilder.append(saltAmount);
                    } else {
                        String salt = checkBoxText + Util.DELIMITER;
                        servicesStringBuilder.append(salt);
                    }
                }
                    else {
                        String serviceString = checkBoxText + Util.DELIMITER;
                        servicesStringBuilder.append(serviceString);
                }
            }
        }
        mListener.appendServices(servicesStringBuilder.toString());
    }
}
