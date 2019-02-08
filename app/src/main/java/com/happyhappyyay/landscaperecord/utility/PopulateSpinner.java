package com.happyhappyyay.landscaperecord.utility;

import android.app.Activity;
import android.widget.AdapterView;

public interface PopulateSpinner {
    Authentication getAuthentication();

    Activity getActivity();

    int getViewID();

    AdapterView.OnItemSelectedListener getItemSelectedListener();
}
