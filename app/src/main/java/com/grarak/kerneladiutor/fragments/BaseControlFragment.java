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
package com.grarak.kerneladiutor.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutor.NavigationActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewAdapter;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 16.04.16.
 */
public class BaseControlFragment extends BaseFragment {

    private List<RecyclerViewItem> mItems = new ArrayList<>();
    private Handler mHandler = new Handler();

    private View mRootView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ViewPagerAdapter mViewPagerAdapter;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Scroller mScroller;
    private View mViewPagerParent;
    private AppBarLayout mAppBarLayout;
    private float mAppBarElevation;
    private Toolbar mToolBar;
    private Bundle mSavedInstanceState;
    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    private ValueAnimator mForegroundAnimator;
    private boolean mForegroundVisible;
    private View mForegroundParent;
    private TextView mForegroundText;
    private float mForegroundHeight;
    private CharSequence mForegroundStrText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
        mRootView = inflater.inflate(R.layout.fragment_base_control, container, false);

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview);
        ViewPager viewPager = (ViewPager) mRootView.findViewById(R.id.viewpager);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) mRootView.findViewById(R.id.indicator);

        mScroller = new Scroller();
        mRecyclerView.clearOnScrollListeners();
        mRecyclerView.setAdapter(mRecyclerViewAdapter == null ? mRecyclerViewAdapter
                = new RecyclerViewAdapter(mItems, new RecyclerViewAdapter.OnViewChangedListener() {
            @Override
            public void viewChanged() {
                mScroller.onScrolled(mRecyclerView, 0, 0);
            }
        }) : mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager = getLayoutManager());

        mViewPagerParent = mRootView.findViewById(R.id.viewpagerparent);
        if (!showViewPager()) {
            mViewPagerParent.setVisibility(View.GONE);
            mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(), 0, mRecyclerView.getPaddingRight(),
                    mRecyclerView.getPaddingBottom());
        } else {
            mRecyclerView.addOnScrollListener(mScroller);
        }
        mAppBarLayout = ((NavigationActivity) getActivity()).getAppBarLayout();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAppBarElevation = mAppBarLayout.getElevation();
            mAppBarLayout.setElevation(0);
        }
        mToolBar = ((NavigationActivity) getActivity()).getToolBar();

        viewPager.setAdapter(mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager()));
        circlePageIndicator.setViewPager(viewPager);
        setAppBarLayoutAlpha(0);

        BaseFragment foregroundFragmnet = getForegroundFragment();
        mForegroundVisible = false;
        if (foregroundFragmnet != null) {
            mForegroundParent = mRootView.findViewById(R.id.foreground_parent);
            mForegroundText = (TextView) mRootView.findViewById(R.id.foreground_text);
            mForegroundText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissForeground();
                }
            });
            getFragmentManager().beginTransaction().replace(R.id.foreground_content, foregroundFragmnet).commit();
            mForegroundHeight = getResources().getDisplayMetrics().heightPixels;
        }

        init();
        if (savedInstanceState == null) {
            mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {

                @Override
                protected List<RecyclerViewItem> doInBackground(Void... params) {
                    return addItems(new ArrayList<RecyclerViewItem>());
                }

                @Override
                protected void onPostExecute(List<RecyclerViewItem> recyclerViewItems) {
                    super.onPostExecute(recyclerViewItems);
                    if (isCancelled()) return;
                    for (RecyclerViewItem item : recyclerViewItems) {
                        addItem(item);
                    }
                    mRootView.findViewById(R.id.progress).setVisibility(View.GONE);
                }
            };
            mLoader.execute();
        } else {
            mRootView.findViewById(R.id.progress).setVisibility(View.GONE);
        }

        return mRootView;
    }

    protected void init() {
    }

    protected void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    protected Bundle getSavedInstanceState() {
        return mSavedInstanceState;
    }

    protected List<RecyclerViewItem> addItems(List<RecyclerViewItem> items) {
        return items;
    }

    private void setAppBarLayoutAlpha(int alpha) {
        Activity activity;
        if ((activity = getActivity()) != null && mAppBarLayout != null && mToolBar != null) {
            int colorPrimary = Utils.getColorPrimaryColor(activity);
            mAppBarLayout.setBackgroundDrawable(new ColorDrawable(Color.argb(alpha, Color.red(colorPrimary),
                    Color.green(colorPrimary), Color.blue(colorPrimary))));
            mToolBar.setTitleTextColor(Color.argb(alpha, 255, 255, 255));
        }
    }

    protected void addItem(RecyclerViewItem recyclerViewItem) {
        mItems.add(recyclerViewItem);
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.notifyItemInserted(mItems.size() - 1);
        }
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) mLayoutManager).setSpanCount(getSpanCount());
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL);
    }

    protected void clearItems() {
        mItems.clear();
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public int getSpanCount() {
        int span = Utils.isTablet(getActivity()) ? Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 3 : 2 : Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        if (mItems.size() != 0 && span > mItems.size()) {
            span = mItems.size();
        }
        return span;
    }

    public int itemsSize() {
        return mItems.size();
    }

    protected void addViewPagerFragment(Fragment fragment) {
        mViewPagerAdapter.addFragment(fragment);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
            notifyDataSetChanged();
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }
    }

    private class Scroller extends RecyclerView.OnScrollListener {

        protected int mScrollDistance;
        private int mAppBarLayoutDistance;
        private boolean mFade = true;
        private ValueAnimator mAlphaAnimator;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mScrollDistance = -mRecyclerViewAdapter.getFirstItem().getTop() + mRecyclerView.getPaddingTop();

            if (mScrollDistance > mViewPagerParent.getHeight() - mAppBarLayout.getHeight()) {
                mAppBarLayoutDistance += dy;
                fadeAppBarLayout(false);
            } else {
                fadeAppBarLayout(true);
            }

            if (mAppBarLayoutDistance > mAppBarLayout.getHeight()) {
                mAppBarLayoutDistance = mAppBarLayout.getHeight();
            } else if (mAppBarLayoutDistance < 0) {
                mAppBarLayoutDistance = 0;
            }

            mViewPagerParent.setTranslationY(-mScrollDistance);
            mAppBarLayout.setTranslationY(-mAppBarLayoutDistance);
        }

        private void fadeAppBarLayout(boolean fade) {
            if (mFade != fade) {
                mFade = fade;

                if (mAlphaAnimator != null) {
                    mAlphaAnimator.cancel();
                }

                mAlphaAnimator = ValueAnimator.ofInt(fade ? 255 : 0, fade ? 0 : 255);
                mAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        setAppBarLayoutAlpha((int) animation.getAnimatedValue());
                    }
                });
                mAlphaAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mAppBarLayout.setElevation(mFade ? 0 : mAppBarElevation);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mAlphaAnimator = null;
                    }
                });
                mAlphaAnimator.start();
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState != 0 || mAppBarLayoutDistance == 0
                    || mAppBarLayoutDistance == mAppBarLayout.getHeight()) {
                return;
            }

            boolean show = mAppBarLayoutDistance < mAppBarLayout.getHeight() * 0.5f
                    || mScrollDistance <= mViewPagerParent.getHeight();
            ValueAnimator animator = ValueAnimator.ofInt(mAppBarLayoutDistance, show ? 0 : mAppBarLayout.getHeight());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAppBarLayoutDistance = (int) animation.getAnimatedValue();
                    mAppBarLayout.setTranslationY(-mAppBarLayoutDistance);
                }
            });
            animator.start();
        }
    }

    protected BaseFragment getForegroundFragment() {
        return null;
    }

    public void setForegroundText(CharSequence text) {
        mForegroundStrText = text;
    }

    public void showForeground() {
        if (mForegroundStrText != null) {
            mForegroundText.setText(mForegroundStrText);
        }
        if (mForegroundAnimator != null) mForegroundAnimator.cancel();
        mForegroundAnimator = ValueAnimator.ofFloat(mForegroundHeight, 0f);
        mForegroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mForegroundParent.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        mForegroundAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mForegroundParent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mForegroundVisible = true;
                mForegroundAnimator = null;
            }
        });
        mForegroundAnimator.start();
    }

    public void dismissForeground() {
        float translation = mForegroundParent.getTranslationY();
        mForegroundAnimator = ValueAnimator.ofFloat(translation, mForegroundHeight);
        mForegroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mForegroundParent.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        mForegroundAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mForegroundParent.setVisibility(View.GONE);
                mForegroundVisible = false;
                mForegroundAnimator = null;
            }
        });
        mForegroundAnimator.start();
    }

    protected boolean showViewPager() {
        return true;
    }

    protected View getRootView() {
        return mRootView;
    }

    @Override
    public boolean onBackPressed() {
        if (mForegroundVisible) {
            dismissForeground();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(mRefresh);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRefresh);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mItems.clear();
        setAppBarLayoutAlpha(255);
        if (mAppBarLayout != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAppBarLayout.setElevation(mAppBarElevation);
        }
        if (mLoader != null) {
            mLoader.cancel(true);
            mLoader = null;
        }
        mHandler.removeCallbacks(mRefresh);
    }

    protected Handler getHandler() {
        return mHandler;
    }

    protected void refresh() {
    }

    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            refresh();
            mHandler.postDelayed(this, 1000);
        }
    };

}
