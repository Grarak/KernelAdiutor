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
package com.grarak.kerneladiutor.views.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grarak.kerneladiutordonate.R;

/**
 * Created by willi on 04.05.16.
 */
public abstract class Expander extends RecyclerViewItem {

    private final int mExpandHeight;
    private final int mExpandLayout;
    private View mExpandView;

    private Drawable mForeground;

    private FrameLayout mRootView;
    private TextView mTitle;
    private TextView mSummary;
    private TextView mValue;
    private ProgressBar mProgress;
    private View mSelector;

    private CharSequence mTitleText;
    private CharSequence mSummaryText;
    private CharSequence mValueText;

    private boolean mSelectorVisible;
    private ValueAnimator mAnimator;

    public Expander(int expandHeight, int expandLayout) {
        mExpandHeight = expandHeight;
        mExpandLayout = expandLayout;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_expander;
    }

    @Override
    public void onCreateView(View view) {
        TypedArray ta = view.getContext().obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
        mForeground = ta.getDrawable(0);
        ta.recycle();

        mRootView = (FrameLayout) view;
        mTitle = (TextView) view.findViewById(R.id.title);
        mSummary = (TextView) view.findViewById(R.id.summary);
        mValue = (TextView) view.findViewById(R.id.value);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        mSelector = view.findViewById(R.id.selector);

        mRootView.setForeground(mSelectorVisible ? null : mForeground);

        refresh();

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnimator != null) mAnimator.cancel();
                mAnimator = ValueAnimator.ofInt(mSelector.getHeight(), 0);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        setHeight((int) animation.getAnimatedValue());
                    }
                });
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        setHeight(0);
                        mRootView.setForeground(mForeground);
                        mSelectorVisible = false;
                        onCollapse();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mRootView.setForeground(mForeground);
                        mSelectorVisible = false;
                        onCollapse();
                    }
                });
                mAnimator.start();
            }
        });

        LinearLayout expandingLayout = (LinearLayout) view.findViewById(R.id.expanding_layout);
        expandingLayout.removeAllViews();
        if (mExpandView == null) {
            mExpandView = LayoutInflater.from(view.getContext()).inflate(mExpandLayout, expandingLayout, false);
        }
        onCreateExpandView(mExpandView);
        ViewGroup viewGroup = (ViewGroup) mExpandView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(mExpandView);
        }
        expandingLayout.addView(mExpandView);
    }

    protected abstract void onCreateExpandView(View view);

    protected void onExpand() {
    }

    protected void onCollapse() {
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummaryText = summary;
        refresh();
    }

    public void setValue(CharSequence value) {
        mValueText = value;
        refresh();
    }

    private void refresh() {
        if (mTitle != null) {
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
                mTitle.setVisibility(View.VISIBLE);
            } else {
                mTitle.setVisibility(View.GONE);
            }
        }
        if (mSummary != null && mSummaryText != null) {
            mSummary.setText(mSummaryText);
        }
        if (mValue != null && mValueText != null) {
            mValue.setText(mValueText);
        }
        if (mProgress != null && mValueText != null) {
            mProgress.setVisibility(View.GONE);
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectorVisible) return;
                    mSelectorVisible = true;
                    if (mAnimator != null) mAnimator.cancel();
                    final float height = mRootView.getResources().getDimension(mExpandHeight);
                    mAnimator = ValueAnimator.ofFloat(0f, height);
                    mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            setHeight((int) (float) animation.getAnimatedValue());
                        }
                    });
                    mAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            setHeight((int) height);
                            mRootView.setForeground(null);
                            mSelectorVisible = true;
                            onExpand();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRootView.setForeground(null);
                            mSelectorVisible = true;
                            onExpand();
                        }
                    });
                    mAnimator.start();
                }
            });
        }
        if (mSelector != null) {
            if (mSelectorVisible) {
                float height = mSelector.getResources().getDimension(R.dimen.rv_select_view_selector_height);
                setHeight((int) height);
            } else {
                setHeight(0);
            }
        }
    }

    private void setHeight(int height) {
        if (mSelector != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSelector.getLayoutParams();
            params.height = height;
            mSelector.requestLayout();
            viewChanged();
        }
    }

}
