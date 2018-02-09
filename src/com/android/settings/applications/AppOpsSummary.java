/**
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.android.settings.applications;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.R;

public class AppOpsSummary extends InstrumentedPreferenceFragment {
    // layout inflater object used to inflate views
    private LayoutInflater mInflater;

    private ViewGroup mContentContainer;
    private View mRootView;
    private ViewPager mViewPager;

    CharSequence[] mPageNames;
    static AppOpsState.OpsTemplate[] sPageTemplates = new AppOpsState.OpsTemplate[] {
        AppOpsState.LOCATION_TEMPLATE,
        AppOpsState.PERSONAL_TEMPLATE,
        AppOpsState.MESSAGING_TEMPLATE,
        AppOpsState.MEDIA_TEMPLATE,
        AppOpsState.DEVICE_TEMPLATE,
        AppOpsState.AUTO_START_TEMPLATE
    };

    int mCurPos;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.APP_OPS_SUMMARY;
    }

    class MyPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new AppOpsCategory(sPageTemplates[position]);
        }

        @Override
        public int getCount() {
            return sPageTemplates.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageNames[position];
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mCurPos = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                //updateCurrentTab(mCurPos);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // initialize the inflater
        mInflater = inflater;

        View rootView = mInflater.inflate(R.layout.app_ops_summary,
                container, false);
        mContentContainer = container;
        mRootView = rootView;

        mPageNames = getResources().getTextArray(R.array.app_ops_categories_custom);

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(adapter);
        PagerTabStrip tabs = (PagerTabStrip) rootView.findViewById(R.id.tabs);

        // HACK - https://code.google.com/p/android/issues/detail?id=213359
        ((ViewPager.LayoutParams)tabs.getLayoutParams()).isDecor = true;

        // Set actionbar elevation 0 to make tab and actionbar look uniform.
        getActivity().getActionBar().setElevation(0);
        
        Resources.Theme theme = tabs.getContext().getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true);
        final int colorAccent = typedValue.resourceId != 0
                ? getContext().getColor(typedValue.resourceId)
                : getContext().getColor(R.color.fingerprint_title_area_bg);
        tabs.setTabIndicatorColor(colorAccent);
        tabs.setDrawFullUnderline(true);

	    theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
	    final int colorPrimary = getContext().getColor(typedValue.resourceId);
        tabs.setBackgroundColor(colorPrimary);

	// We have to do this now because PreferenceFrameLayout looks at it
        // only when the view is added.
        if (container instanceof PreferenceFrameLayout) {
            ((PreferenceFrameLayout.LayoutParams) rootView.getLayoutParams()).removeBorders = true;
        }

        return rootView;
    }
}
