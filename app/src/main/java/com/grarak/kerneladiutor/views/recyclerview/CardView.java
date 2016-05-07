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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutordonate.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 18.04.16.
 */
public class CardView extends RecyclerViewItem {

    private TextView mTitle;
    private LinearLayout mLayout;

    private CharSequence mTitleText;

    private List<RecyclerViewItem> mItems = new ArrayList<>();
    private HashMap<RecyclerViewItem, View> mViews = new HashMap<>();

    @Override
    public int getLayoutRes() {
        return R.layout.rv_card_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitle = (TextView) view.findViewById(R.id.card_title);
        mLayout = (LinearLayout) view.findViewById(R.id.card_layout);

        setupLayout();
        super.onCreateView(view);
    }

    @Override
    public void onCreateHolder(ViewGroup parent) {
        super.onCreateHolder(parent);
        for (RecyclerViewItem item : mItems) {
            if (!mViews.containsKey(item)) {
                mViews.put(item, LayoutInflater.from(parent.getContext())
                        .inflate(item.getLayoutRes(), null, false));
            }
        }
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void addItem(RecyclerViewItem item) {
        mItems.add(item);
        setupLayout();
    }

    public int size() {
        return mItems.size();
    }

    public void removeItem(RecyclerViewItem item) {
        mItems.remove(item);
        if (mLayout != null) {
            mLayout.removeView(mViews.get(item));
            mViews.remove(item);
        }
    }

    public void clearItems() {
        mItems.clear();
        setupLayout();
    }

    private void setupLayout() {
        if (mLayout != null) {
            mLayout.removeAllViews();
            for (RecyclerViewItem item : mItems) {
                View view;
                if (mViews.containsKey(item)) {
                    view = mViews.get(item);
                } else {
                    mViews.put(item, view = LayoutInflater.from(mLayout.getContext())
                            .inflate(item.getLayoutRes(), null, false));
                }
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(view);
                }
                mLayout.addView(view);
                item.setOnViewChangeListener(getOnViewChangeListener());
                item.onCreateView(view);
            }
        }
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null) {
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
                mTitle.setVisibility(View.VISIBLE);
            } else {
                mTitle.setVisibility(View.GONE);
            }
        }
    }
}
