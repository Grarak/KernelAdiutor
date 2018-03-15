/*
 * Copyright (C) 2015-2018 Willi Ye <williye97@gmail.com>
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
import android.app.Activity;
import android.support.v4.view.AsyncLayoutInflater;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by willi on 18.04.16.
 */
public class CardView extends RecyclerViewItem {

    public interface OnMenuListener {
        void onMenuReady(CardView cardView, PopupMenu popupMenu);
    }

    private android.support.v7.widget.CardView mRootView;
    private View mTitleParent;
    private TextView mTitle;
    private AppCompatImageView mArrow;
    private View mLayoutParent;
    private LinearLayout mLayout;
    private View mMenuButton;

    private CharSequence mTitleText;
    private PopupMenu mPopupMenu;
    private OnMenuListener mOnMenuListener;

    private final Map<RecyclerViewItem, View> mViews = new LinkedHashMap<>();

    private AsyncLayoutInflater mAsyncLayoutInflater;
    private final Object mAsyncSemaphore = new Object();
    private boolean mInflaterBusy;
    private final Queue<RecyclerViewItem> mInflaterQueue = new LinkedBlockingQueue<>();
    private final Queue<RecyclerViewItem> mInflaterNotReadyQueue = new LinkedBlockingQueue<>();

    private int mLayoutHeight;
    private ValueAnimator mLayoutAnimator;
    private boolean mShowLayout = true;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_card_view;
    }

    @Override
    public void onRecyclerViewCreate(Activity activity) {
        super.onRecyclerViewCreate(activity);

        mAsyncLayoutInflater = new AsyncLayoutInflater(activity);
        while (mInflaterNotReadyQueue.size() != 0) {
            addView(mInflaterNotReadyQueue.poll());
        }

        for (RecyclerViewItem item : mViews.keySet()) {
            item.onRecyclerViewCreate(activity);
        }
    }

    private void initLayouts(View view) {
        mRootView = (android.support.v7.widget.CardView) view;
        mTitleParent = view.findViewById(R.id.title_parent);
        mTitle = view.findViewById(R.id.card_title);
        mArrow = view.findViewById(R.id.arrow_image);
        mLayoutParent = view.findViewById(R.id.layout_parent);
        mLayout = view.findViewById(R.id.card_layout);
    }

    @Override
    void onCreateHolder(ViewGroup parent, View view) {
        super.onCreateHolder(parent, view);
        initLayouts(view);
        if (mLayout.getChildCount() == 0) {
            setupLayout();
        }
    }

    @Override
    public void onCreateView(View view) {
        initLayouts(view);

        mMenuButton = view.findViewById(R.id.menu_button);
        mMenuButton.setOnClickListener(v -> {
            if (mPopupMenu != null) {
                mPopupMenu.show();
            }
        });

        mLayoutParent.setVisibility(mShowLayout ? View.VISIBLE : View.GONE);
        mArrow.setRotationX(mShowLayout ? 0 : 180);

        mTitleParent.setOnClickListener(v -> {
            if (mLayoutParent.getVisibility() == View.VISIBLE) {
                mLayoutHeight = mLayoutParent.getHeight();
            }
            if (mLayoutAnimator == null) {
                mShowLayout = !mShowLayout;
                animateLayout(!mShowLayout);
                viewChanged();
            }
        });
        super.onCreateView(view);
    }

    private void animateLayout(final boolean collapse) {
        mArrow.animate().rotationX(collapse ? 180 : 0).setDuration(500).start();
        mLayoutAnimator = ValueAnimator.ofInt(collapse ? mLayoutHeight : 0, collapse ? 0 : mLayoutHeight);
        mLayoutAnimator.addUpdateListener(animation
                -> setLayoutParentHeight((int) animation.getAnimatedValue()));
        mLayoutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mLayoutParent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLayoutParent.setVisibility(collapse ? View.GONE : View.VISIBLE);
                setLayoutParentHeight(collapse ? 0 : ViewGroup.LayoutParams.MATCH_PARENT);
                mLayoutAnimator = null;
            }
        });
        mLayoutAnimator.setDuration(500);
        mLayoutAnimator.start();
    }

    private void setLayoutParentHeight(int height) {
        ViewGroup.LayoutParams layoutParams = mLayout.getLayoutParams();
        layoutParams.height = height;
        mLayout.requestLayout();
        viewChanged();
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void addItem(final RecyclerViewItem item) {
        if (item instanceof CardView) {
            throw new IllegalStateException("Cardinception!");
        }
        addView(item);
    }

    public void setOnMenuListener(OnMenuListener onMenuListener) {
        mOnMenuListener = onMenuListener;
        refresh();
    }

    public int size() {
        return mViews.size();
    }

    public void removeItem(RecyclerViewItem item) {
        mViews.remove(item);
        if (mLayout != null) {
            mLayout.removeView(mViews.get(item));
        }
    }

    public void clearItems() {
        mViews.clear();
        if (mLayout != null) {
            mLayout.removeAllViews();
        }
    }

    private void setupLayout() {
        if (mLayout != null) {
            mLayout.removeAllViews();
            for (final RecyclerViewItem item : mViews.keySet()) {
                addView(item);
            }
        }
    }

    private void addView(final RecyclerViewItem item) {
        if (item == null) return;

        synchronized (mAsyncSemaphore) {
            if (mAsyncLayoutInflater == null) {
                mInflaterNotReadyQueue.offer(item);
                return;
            }

            if (mInflaterBusy) {
                mInflaterQueue.offer(item);
                return;
            }

            mInflaterBusy = true;
            mAsyncLayoutInflater.inflate(item.getLayoutRes(), mLayout, (view, resid, parent) -> {
                mViews.put(item, view);
                item.setOnViewChangeListener(getOnViewChangedListener());
                item.onCreateView(view);
                if (mLayout != null) {
                    mLayout.addView(view);
                }

                mInflaterBusy = false;
                addView(mInflaterQueue.poll());
            });
        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (RecyclerViewItem item : mViews.keySet()) {
            item.onDestroy();
        }
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null) {
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
                mTitleParent.setVisibility(View.VISIBLE);
                if (mLayoutParent != null) {
                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) mLayout.getLayoutParams();
                    layoutParams.topMargin = -mLayout.getPaddingLeft();
                    mLayout.requestLayout();
                    mLayout.setPadding(mLayout.getPaddingLeft(), 0,
                            mLayout.getPaddingRight(), mLayout.getPaddingBottom());
                }
            } else {
                mTitleParent.setVisibility(View.GONE);
            }
        }
        if (mMenuButton != null && mOnMenuListener != null) {
            mMenuButton.setVisibility(View.VISIBLE);
            mPopupMenu = new PopupMenu(mMenuButton.getContext(), mMenuButton);
            mOnMenuListener.onMenuReady(this, mPopupMenu);
        }
        if (mRootView != null && getOnItemClickListener() != null) {
            mRootView.setOnClickListener(view
                    -> getOnItemClickListener().onClick(CardView.this));
        }
    }

    @Override
    protected boolean cardCompatible() {
        return false;
    }

}
