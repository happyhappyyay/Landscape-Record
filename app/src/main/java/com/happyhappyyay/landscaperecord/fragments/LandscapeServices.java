package com.happyhappyyay.landscaperecord.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

        @Override    public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SERVICES";
                case 1:
                    return "MATERIALS";
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