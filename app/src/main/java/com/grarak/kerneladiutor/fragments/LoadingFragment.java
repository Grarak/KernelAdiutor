/*
 * Copyright (C) 2018 Willi Ye <williye97@gmail.com>
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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 08.03.18.
 */

public class LoadingFragment extends BaseFragment {

    private String mTitle;
    private String mSummary;

    private TextView mTitleView;
    private TextView mSummaryView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loading, container, false);

        mTitleView = rootView.findViewById(R.id.title);
        mSummaryView = rootView.findViewById(R.id.summary);

        setup();
        return rootView;
    }

    public void setTitle(String title) {
        mTitle = title;
        setup();
    }

    public void setSummary(String summary) {
        mSummary = summary;
        setup();
    }

    private void setup() {
        if (mTitleView != null) {
            if (mTitle == null) {
                mTitleView.setVisibility(View.GONE);
            } else {
                mTitleView.setVisibility(View.VISIBLE);
                mTitleView.setText(mTitle);
            }

            mSummaryView.setText(mSummary);
        }
    }
}
