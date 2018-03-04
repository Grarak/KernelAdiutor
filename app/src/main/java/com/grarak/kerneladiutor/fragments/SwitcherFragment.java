/*
 * Copyright (C) 2017 Willi Ye <williye97@gmail.com>
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
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 17.09.17.
 */

public class SwitcherFragment extends BaseFragment {

    private static final String PACKAGE = SwitcherFragment.class.getCanonicalName();
    private static final String INTENT_TITLE = PACKAGE + ".INTENT.TITLE";
    private static final String INTENT_SUMMARY = PACKAGE + ".INTENT.SUMMARY";
    private static final String INTENT_CHECKED = PACKAGE + ".INTENT.CHECKED";

    public static SwitcherFragment newInstance(String title, String summary, boolean checked,
                                               CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        Bundle args = new Bundle();
        args.putString(INTENT_TITLE, title);
        args.putString(INTENT_SUMMARY, summary);
        args.putBoolean(INTENT_CHECKED, checked);
        SwitcherFragment fragment = new SwitcherFragment();
        fragment.setArguments(args);
        fragment.mOnCheckedChangeListener = onCheckedChangeListener;
        return fragment;
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_switcher, container, false);

        String title = getArguments().getString(INTENT_TITLE);
        String summary = getArguments().getString(INTENT_SUMMARY);
        boolean checked = getArguments().getBoolean(INTENT_CHECKED);

        ((TextView) view.findViewById(R.id.title)).setText(title);
        ((TextView) view.findViewById(R.id.summary)).setText(summary);
        SwitchCompat mSwitch = view.findViewById(R.id.switcher);
        mSwitch.setChecked(checked);
        mSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        return view;
    }

}
