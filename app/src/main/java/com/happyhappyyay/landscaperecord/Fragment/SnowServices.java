package com.happyhappyyay.landscaperecord.Fragment;

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
        String existingServices = ExistingService.getExistingService().getServices();
        if (existingServices != null && !existingServices.isEmpty()) {
            ExistingService.getExistingService().setServices(updateCheckBoxesAndExistingService(ExistingService.getExistingService().getServices()));
        }
        return view;
    }

    private String updateCheckBoxesAndExistingService(String existingServices) {
        final String SPACE = " ";
        final String TYPE = "Snow";
        int lengthOfServiceString = existingServices.length();
        for (CheckBox c : checkBoxes) {
            String checkBoxText = c.getText().toString().toLowerCase();
            if (existingServices.toLowerCase().contains(checkBoxText)) {
                int startIndex = existingServices.toLowerCase().indexOf(checkBoxText);
                int endIndex = lengthOfServiceString;
                switch (checkBoxText) {
                    case "other:":
                        int tempStartIndex = startIndex - TYPE.length() - SPACE.length();
                        while(!existingServices.substring(tempStartIndex, startIndex -SPACE.length()).equals(TYPE) & startIndex != -1) {
                            startIndex = existingServices.indexOf(c.getText().toString(), startIndex + checkBoxText.length());
                            tempStartIndex = startIndex - TYPE.length() - SPACE.length();
                        }
                        if (startIndex != -1) {
                            int otherTextStartIndex = startIndex +
                                    checkBoxText.length() + SPACE.length();
                            startIndex = tempStartIndex;

                            for(int i = otherTextStartIndex; i < lengthOfServiceString; i++ ) {
                                if(Util.DELIMITER.equals(existingServices.substring(i, i+1))) {
                                    endIndex = i;
                                    break;
                                }
                            }

                            otherText.setText(existingServices.substring(otherTextStartIndex, endIndex));
                        }
                        break;
                    case "salt":
                        endIndex = lengthOfServiceString;
                        for(int i = startIndex; i < lengthOfServiceString; i++ ) {
                            if(Util.DELIMITER.equals(existingServices.substring(i, i+1))) {
                                endIndex = i;
                                break;
                            }
                        }
                        if(endIndex - startIndex > checkBoxText.length()) {
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

                        break;
                    default:
                        endIndex = c.getText().toString().length() + Util.DELIMITER.length() + startIndex;
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
        final String TYPE = "Snow";
        StringBuilder servicesStringBuilder = new StringBuilder();
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {
                String checkBoxText = c.getText().toString();
                switch (checkBoxText.toLowerCase()) {
                    case "other:":
                        String otherString = otherText.getText().toString();
                        if (!otherString.isEmpty()) {
                            otherString = TYPE + SPACE + checkBoxText + ": " + otherString + Util.DELIMITER;
                            servicesStringBuilder.append(otherString);
                        }
                        break;
                    case "salt":
                        String saltString = saltText.getText().toString();
                        if (!saltString.isEmpty()) {
                            String saltAmount = checkBoxText+ SPACE + saltString + saltSpinner.getSelectedItem().toString() + Util.DELIMITER;
                            servicesStringBuilder.append(saltAmount);
                        } else {
                            String salt = checkBoxText + Util.DELIMITER;
                            servicesStringBuilder.append(salt);
                        }
                        break;
                    default:
                        String serviceString = checkBoxText + Util.DELIMITER;
                        servicesStringBuilder.append(serviceString);
                        break;
                }
            }
        }
        return servicesStringBuilder.toString();
    }
}
