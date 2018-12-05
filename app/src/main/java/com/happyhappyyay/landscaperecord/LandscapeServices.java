package com.happyhappyyay.landscaperecord;

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

import java.util.List;

public class LandscapeServices extends Fragment {
    LandscapingMaterials landscapingMaterials;
    LandscapingOther  landscapingOther;


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

    public String markedCheckBoxes() {
        StringBuilder servicesStringBuilder = new StringBuilder();

        if (landscapingOther.getView() != null) {
            servicesStringBuilder.append(landscapingOther.markedCheckBoxes());
        }


            servicesStringBuilder.append(landscapingMaterials.markedCheckBoxes());

        return servicesStringBuilder.toString();
    }

    public List<Material> getMaterials() {
        return landscapingMaterials.getMaterials();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        final int NUM_PAGES = 2;
        public ScreenSlidePagerAdapter(FragmentManager fm) {
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
