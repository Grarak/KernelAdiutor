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
package com.grarak.kerneladiutordonate.elements.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutordonate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 02.01.16.
 */
public class MainCard implements Adapter.RecyclerItem {

    public MainCard(Context context) {
        this.context = context;
    }

    private final Context context;
    private List<Adapter.RecyclerItem> mainCardItems = new ArrayList<>();
    private List<View> views = new ArrayList<>();

    private LinearLayout mMainLayout;
    private TextView mTitleView;

    private CharSequence mTitle;

    @Override
    public void onBindView(View view) {
        mMainLayout = (LinearLayout) view.findViewById(R.id.maincard_layout);
        mTitleView = (TextView) view.findViewById(R.id.maincard_title);
        setUpCard();
    }

    @Override
    public View getView(LayoutInflater layoutInflater, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.views_maincard, parent, false);
    }

    public void addItem(Adapter.RecyclerItem item) {
        mainCardItems.add(item);
        views.add(item.getView(LayoutInflater.from(context), null));
        setUpCard();
    }

    public void clearItems() {
        mainCardItems.clear();
        views.clear();
        setUpCard();
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        setUpCard();
    }

    private void setUpCard() {
        if (mMainLayout != null) {
            mMainLayout.removeAllViews();
            for (int i = 0; i < mainCardItems.size(); i++) {
                ViewGroup parent = (ViewGroup) views.get(i).getParent();
                if (parent != null) parent.removeView(views.get(i));
                mMainLayout.addView(views.get(i));
                mainCardItems.get(i).onBindView(views.get(i));
            }
        }

        if (mTitleView != null) {
            if (mTitle != null) {
                mTitleView.setText(mTitle);
                mTitleView.setVisibility(View.VISIBLE);
            } else mTitleView.setVisibility(View.GONE);
        }
    }

    @Override
    public void refresh() {
        setUpCard();
    }
}
