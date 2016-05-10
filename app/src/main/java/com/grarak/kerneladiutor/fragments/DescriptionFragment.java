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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 01.05.16.
 */
public class DescriptionFragment extends BaseFragment {

    public static DescriptionFragment newInstance(String title, String summary) {
        Bundle args = new Bundle();
        DescriptionFragment fragment = new DescriptionFragment();
        args.putString("title", title);
        args.putString("summary", summary);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_description, container, false);

        TextView title = (TextView) rootView.findViewById(R.id.title);
        TextView summary = (TextView) rootView.findViewById(R.id.summary);
        summary.setSelected(true);

        String titleText = getArguments().getString("title");
        if (titleText != null) {
            title.setText(titleText);
        } else {
            title.setVisibility(View.GONE);
        }

        String summaryText = getArguments().getString("summary");
        if (summaryText != null) {
            summary.setText(summaryText);
        } else {
            summary.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    protected boolean retainInstance() {
        return false;
    }
}
