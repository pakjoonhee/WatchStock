package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by joonheepak on 11/18/16.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    private ArrayList<String> allSymbols = new ArrayList<>();
    private ArrayList<String> allPrices = new ArrayList<>();

    public PagerAdapter(FragmentManager fm, ArrayList<String> allSymbols, ArrayList<String> allPrices) {
        super(fm);
        this.allSymbols = allSymbols;
        this.allPrices = allPrices;
    }

    @Override
    public int getCount() {
        return allSymbols.size();
    }

    @Override
    public Fragment getItem(int position) {
        return GraphFragment.newInstance(allSymbols.get(position), allPrices.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return allSymbols.get(position);
    }
}