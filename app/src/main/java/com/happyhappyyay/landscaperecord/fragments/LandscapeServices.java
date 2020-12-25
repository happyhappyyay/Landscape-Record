package com.happyhappyyay.landscaperecord.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.FragmentExchange;
import com.happyhappyyay.landscaperecord.pojo.Material;

import java.util.List;

public class LandscapeServices extends Fragment implements FragmentExchange {
    LandscapingMaterials landscapingMaterials;
    LandscapingOther  landscapingOther;
    private FragmentExchange mListener;


    public LandscapeServices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        landscapingMaterials = new LandscapingMaterials();
        landscapingOther = new LandscapingOther();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landscaping, container, false);
        ViewPager mPager = view.findViewById(R.id.fragment_landscaping_view_pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.fragment_landscaping_tabLayout);
        tabLayout.setupWithViewPager(mPager);
        return view;
    }

    public LandscapingMaterials getLandscapingMaterials() {
        return landscapingMaterials;
    }

    public void setLandscapingMaterials(LandscapingMaterials landscapingMaterials) {
        this.landscapingMaterials = landscapingMaterials;
    }

    public LandscapingOther getLandscapingOther() {
        return landscapingOther;
    }

    public void setLandscapingOther(LandscapingOther landscapingOther) {
        this.landscapingOther = landscapingOther;
    }

    public void markedCheckBoxes() {
        if (landscapingOther.getView() != null) {
            landscapingOther.markedCheckBoxes();
        }
        landscapingMaterials.retrieveMaterials();
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

    @Override
    public String getServices() {
        return mListener.getServices();
    }

    @Override
    public void setServices(String services) {
        mListener.setServices(services);
    }

    @Override
    public void appendServices(String services) {
        mListener.appendServices(services);
    }

    public List<Material> getMaterials() {
        return mListener.getMaterials();
    }

    @Override
    public void setMaterials(List<Material> materials) {
        mListener.setMaterials(materials);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        final int NUM_PAGES = 2;
        private ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return position == 0? landscapingOther: landscapingMaterials;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.recycler_view_customer_services);
                case 1:
                    return getString(R.string.fragment_landscaping_materials);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}