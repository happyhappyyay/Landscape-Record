package com.happyhappyyay.landscaperecord.Utility;

import android.app.Activity;
import android.widget.AdapterView;

public interface PopulateSpinner {
    Authentication getAuthentication();

    Activity getActivity();

    int getViewID();

    AdapterView.OnItemSelectedListener getItemSelectedListener();
}
