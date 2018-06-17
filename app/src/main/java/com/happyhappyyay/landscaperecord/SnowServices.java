package com.happyhappyyay.landscaperecord;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class SnowServices extends Fragment implements FragmentListener {
    private final ServiceType SERVICE_TYPE = ServiceType.SNOW_SERVICES;
    private FragmentListener callBack;
    private CheckBox plow, shovel, snowBlow, salt, other;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_snow, container, false);
        checkBoxes = new ArrayList<>();
        plow = (CheckBox) view.findViewById(R.id.snow_services_plow);
        checkBoxes.add(plow);
        shovel = (CheckBox) view.findViewById(R.id.snow_services_shovel);
        checkBoxes.add(shovel);
        snowBlow = (CheckBox) view.findViewById(R.id.snow_services_snow_blow);
        checkBoxes.add(snowBlow);
        salt = (CheckBox) view.findViewById(R.id.snow_services_salt);
        checkBoxes.add(salt);
        other = (CheckBox) view.findViewById(R.id.snow_services_other);
        checkBoxes.add(other);
        otherText = view.findViewById(R.id.snow_services_other_text);
        saltText = view.findViewById(R.id.snow_services_salt_text);
        saltSpinner = view.findViewById(R.id.snow_services_salt_spinner);
        setRetainInstance(true);
        return view;
    }

    public void setFragmentListener(FragmentListener listener) {
        callBack = listener;
    }

    public String markedCheckBoxes() {
        String services = "";
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {

                if (c.getText().toString().toLowerCase().equals("other")) {
                    String otherString = otherText.getText().toString();
                    if (!otherString.isEmpty()) {
                        services += "Other " + otherString + "#*#";
                    }
                }
                else if (c.getText().toString().toLowerCase().equals("salt")) {
                        String saltString = saltText.getText().toString();
                        if (!saltString.isEmpty()) {
                            services += "Salt " + saltString + saltSpinner.getSelectedItem().toString() + "#*#";
                        } else {
                            services += c.getText().toString() + "#*#";
                        }
                }
                else {
                    services += c.getText().toString() + "#*#";
                }
            }
        }
        return services;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        callBack.pausedFragment(SERVICE_TYPE);
//        callBack.checkBoxData(markedCheckBoxes());
//    }


    @Override
    public void checkBoxData(String string) {

    }

    @Override
    public void pausedFragment(ServiceType serviceType) {

    }
}
