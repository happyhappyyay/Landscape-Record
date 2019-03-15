package com.happyhappyyay.landscaperecord.utility;

import android.content.Context;
import android.preference.Preference;

import com.happyhappyyay.landscaperecord.R;

public class Loading extends Preference {
    public Loading(Context context) {
        super(context);
        setLayoutResource(R.layout.progress_bar);
    }
}
