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
package com.grarak.kerneladiutor.views.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.recyclerview.RecyclerViewFragment;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

/**
 * Created by willi on 23.08.16.
 */

public class ViewPagerDialog extends DialogFragment {

    public static ViewPagerDialog newInstance(int height, List<Fragment> fragments) {
        ViewPagerDialog fragment = new ViewPagerDialog();
        fragment.mHeight = height;
        fragment.mFragments = fragments;
        return fragment;
    }

    private int mHeight;
    private List<Fragment> mFragments;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.viewpager_view, container, false);

        ViewPager viewPager = rootView.findViewById(R.id.viewpager);
        CirclePageIndicator indicator = rootView.findViewById(R.id.indicator);
        viewPager.setAdapter(new RecyclerViewFragment.ViewPagerAdapter(getChildFragmentManager(), mFragments));
        indicator.setViewPager(viewPager);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = mHeight;
                view.requestLayout();
            }
        });
    }
}
