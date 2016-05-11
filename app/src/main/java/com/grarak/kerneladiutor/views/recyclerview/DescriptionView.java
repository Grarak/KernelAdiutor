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

import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 17.04.16.
 */
public class DescriptionView extends RecyclerViewItem {

    private View mRootView;
    private TextView mTitleView;
    private TextView mSummaryView;

    private CharSequence mTitle;
    private CharSequence mSummary;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_description_view;
    }

    @Override
    public void onCreateView(View view) {
        mRootView = view;
        mTitleView = (TextView) view.findViewById(R.id.title);
        mSummaryView = (TextView) view.findViewById(R.id.summary);

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummary = summary;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitleView != null && mTitle != null) {
            mTitleView.setText(mTitle);
        }
        if (mSummaryView != null && mSummary != null) {
            mSummaryView.setText(mSummary);
        }
        if (mRootView != null && getOnItemClickListener() != null && mTitleView != null
                && mSummaryView != null) {
            mTitleView.setTextIsSelectable(false);
            mSummaryView.setTextIsSelectable(false);
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getOnItemClickListener() != null) {
                        getOnItemClickListener().onClick(DescriptionView.this);
                    }
                }
            });
        }
    }
}
