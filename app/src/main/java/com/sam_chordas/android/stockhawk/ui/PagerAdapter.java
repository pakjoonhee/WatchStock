package com.sam_chordas.android.stockhawk.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by joonheepak on 11/18/16.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        GraphFragment tab1 = new GraphFragment();
        return tab1;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}