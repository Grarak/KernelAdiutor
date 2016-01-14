/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutordonate.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import com.grarak.kerneladiutordonate.R;
import com.grarak.kerneladiutordonate.elements.recyclerview.Adapter;
import com.grarak.kerneladiutordonate.utils.Utils;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 31.12.15.
 */
public class RecyclerViewFragment extends BaseFragment {

    public abstract static class ViewInterface {

        private final Context context;

        public ViewInterface(Context context) {
            this.context = context;
        }

        public abstract List<Adapter.RecyclerItem> getViews(Bundle savedInstanceState);

        public List<BaseFragment> getViewPagerFragments(Bundle savedInstanceState) {
            return null;
        }

        public Context getContext() {
            return context;
        }

        public void onSaveInstanceState(Bundle outState) {
        }

        public void onResume() {
        }

        public String getString(int res) {
            return getContext().getString(res);
        }

        public String getString(int res, Object o) {
            return getContext().getString(res, o);
        }

        public boolean refresh() {
            return false;
        }

    }

    private static final String UPDATEDATA_INTENT = "updatedata";
    private static final String IMAGE_CONTAINER_TRANSLATION_INTENT = "image_container_translation";
    private static final String TOOLBAR_TRANSLATION_INTENT = "toolbar_translation";

    private ViewInterface viewInterface;
    private FrameLayout mContainer;
    private int toolbarDistance;
    private CharSequence title;
    private ActionBar actionBar;
    private boolean updateData;
    private Handler handler;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        setContentView(R.layout.fragment_recyclerview);

        if (savedInstanceState != null)
            updateData = savedInstanceState.getBoolean(UPDATEDATA_INTENT);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mContainer = (FrameLayout) findViewById(R.id.container);

        Adapter adapter = new Adapter(new ArrayList<Adapter.RecyclerItem>());

        recyclerView.clearOnScrollListeners();
        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.setAdapter(adapter);

        getAppBarLayout().setBackgroundColor(Color.TRANSPARENT);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (savedInstanceState != null) {
            mContainer.setTranslationY(savedInstanceState.getFloat(IMAGE_CONTAINER_TRANSLATION_INTENT, 0));
            getAppBarLayout().setTranslationY(savedInstanceState.getFloat(TOOLBAR_TRANSLATION_INTENT, 0));
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int scrollDistance = recyclerView.computeVerticalScrollOffset();

                mContainer.setTranslationY(-scrollDistance);

                AppBarLayout appBarLayout = getAppBarLayout();
                if (appBarLayout != null && scrollDistance
                        > mContainer.getHeight() - appBarLayout.getHeight()) {
                    toolbarDistance += dy;
                    if (toolbarDistance >= appBarLayout.getHeight())
                        toolbarDistance = appBarLayout.getHeight();
                    else if (toolbarDistance < 0)
                        toolbarDistance = 0;
                } else if (scrollDistance <= mContainer.getHeight() - getAppBarLayout().getHeight()) {
                    actionBar.setTitle("");
                    getAppBarLayout().setBackgroundColor(Color.TRANSPARENT);

                    toolbarDistance = 0;
                } else toolbarDistance = 0;

                if (scrollDistance >= mContainer.getHeight()) {
                    actionBar.setTitle(title);
                    getAppBarLayout().setBackgroundColor(Utils.getColorPrimary(getActivity()));
                }

                if (appBarLayout != null) appBarLayout.setTranslationY(-toolbarDistance);
            }
        });

        if (viewInterface != null) {
            if (!updateData)
                new ViewTask(viewInterface, adapter).execute(savedInstanceState);

            List<BaseFragment> fragments = viewInterface.getViewPagerFragments(savedInstanceState);
            if (fragments != null) {
                ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
                mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), fragments));
                if (fragments.size() > 1) {
                    CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.circlepageindicator);
                    mIndicator.setViewPager(mViewPager);
                }
            }
        }

        handler = new Handler();
    }

    private class ViewTask extends AsyncTask<Bundle, Void, List<Adapter.RecyclerItem>> {

        private final ViewInterface viewInterface;
        private final Adapter adapter;

        public ViewTask(ViewInterface viewInterface, Adapter adapter) {
            this.viewInterface = viewInterface;
            this.adapter = adapter;
        }

        @Override
        protected List<Adapter.RecyclerItem> doInBackground(Bundle... params) {
            return viewInterface.getViews(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            updateData = true;
        }

        @Override
        protected void onPostExecute(List<Adapter.RecyclerItem> recyclerItems) {
            super.onPostExecute(recyclerItems);
            if (recyclerItems != null)
                for (Adapter.RecyclerItem item : recyclerItems)
                    adapter.addView(item);
            updateData = false;
        }
    }

    public void setViewInterface(ViewInterface viewInterface) {
        this.viewInterface = viewInterface;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getAppBarLayout().setBackgroundColor(Utils.getColorPrimary(getActivity()));
        getAppBarLayout().setTranslationY(0);
        handler.removeCallbacks(refreshRun);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(UPDATEDATA_INTENT, updateData);
        outState.putFloat(IMAGE_CONTAINER_TRANSLATION_INTENT, mContainer.getTranslationY());
        outState.putFloat(TOOLBAR_TRANSLATION_INTENT, getAppBarLayout().getTranslationY());
        if (viewInterface != null) viewInterface.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (actionBar != null) {
            if (title == null) title = actionBar.getTitle();
            actionBar.setTitle("");
        }
        if (viewInterface != null) viewInterface.onResume();
        handler.post(refreshRun);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRun);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<BaseFragment> fragments;

        public ViewPagerAdapter(FragmentManager fragmentManager, List<BaseFragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

    private final Runnable refreshRun = new Runnable() {
        @Override
        public void run() {
            if (viewInterface != null && viewInterface.refresh())
                handler.postDelayed(this, 1000);
        }
    };

}
