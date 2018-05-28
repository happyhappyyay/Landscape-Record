package com.example.kingdenis.landscaperecord;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;


public class LawnServices extends Fragment implements FragmentListener {
    private final ServiceType SERVICE_TYPE = ServiceType.LAWN_SERVICES;
    private CheckBox sprayGrass, sprayLandscape, cut, prune, fertilize, rake, pullWeeds, cleanup;
    private CheckBox aerate, removeLeaves, other;
    private List<CheckBox> checkBoxes;
    private FragmentListener callBack;
    private Button submit;
    private String services;
    private boolean pause;


    public LawnServices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lawn, container, false);
        checkBoxes = new ArrayList<>();
        sprayGrass = (CheckBox) view.findViewById(R.id.lawn_services_spray_grass);
        checkBoxes.add(sprayGrass);
        sprayLandscape = (CheckBox) view.findViewById(R.id.lawn_services_spray_landscape);
        checkBoxes.add(sprayLandscape);
        cut = (CheckBox) view.findViewById(R.id.lawn_services_cut);
        checkBoxes.add(cut);
        prune = (CheckBox) view.findViewById(R.id.lawn_services_prune);
        checkBoxes.add(prune);
        fertilize = (CheckBox) view.findViewById(R.id.lawn_services_fertilize);
        checkBoxes.add(fertilize);
        rake = (CheckBox) view.findViewById(R.id.lawn_services_rake);
        checkBoxes.add(rake);
        pullWeeds = (CheckBox) view.findViewById(R.id.lawn_services_pull_weeds);
        checkBoxes.add(pullWeeds);
        cleanup = (CheckBox) view.findViewById(R.id.lawn_services_clean_up);
        checkBoxes.add(cleanup);
        aerate = (CheckBox) view.findViewById(R.id.lawn_services_aerate);
        checkBoxes.add(aerate);
        removeLeaves = (CheckBox) view.findViewById(R.id.lawn_services_remove_leaves);
        checkBoxes.add(removeLeaves);
        other = (CheckBox) view.findViewById(R.id.lawn_services_other);
        checkBoxes.add(other);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callBack = (FragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void setFragmentListener(FragmentListener listener) {
        callBack = listener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceStates) {
        super.onActivityCreated(savedInstanceStates);
//        getActivity().findViewById(R.id.job_services_submit_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity().getApplicationContext(), "pa", Toast.LENGTH_LONG).show();
//            }
//
//        });

    }

    public void submitButton(View view) {

    }

    public String markedCheckBoxes() {
        services = "";
        for (CheckBox c : checkBoxes) {
            if (c.isChecked()) {
                services += c.getText().toString() + " ";
            }
        }
        return services;
    }

    @Override
    public void checkBoxData(String string) {

    }

    @Override
    public void pausedFragment(ServiceType serviceType) {

    }
}
