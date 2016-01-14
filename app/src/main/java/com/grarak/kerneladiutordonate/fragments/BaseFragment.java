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
package com.grarak.kerneladiutordonate.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.grarak.kerneladiutordonate.NavigationActivity;

/**
 * Created by willi on 28.12.15.
 */
public abstract class BaseFragment extends Fragment {

    private LayoutInflater inflater;
    private ViewGroup container;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(retainInstance());
        this.inflater = inflater;
        this.container = container;
        init(savedInstanceState);
        return rootView;
    }

    public void init(Bundle savedInstanceState) {
    }

    public void setContentView(int layout) {
        setContentView(inflater.inflate(layout, container, false));
    }

    public void setContentView(View view) {
        rootView = view;
    }

    public View findViewById(int id) {
        return rootView.findViewById(id);
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("rotate", true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                try {
                    onViewFinished();
                    observer.removeOnGlobalLayoutListener(this);
                } catch (Exception ignored) {
                }
            }
        });
    }

    public void onViewFinished() {
    }

    public boolean onBackPressed() {
        return false;
    }

    public AppBarLayout getAppBarLayout() {
        Activity activity = getActivity();
        if (activity instanceof NavigationActivity)
            return ((NavigationActivity) activity).getAppBarLayout();
        return null;
    }

    public boolean retainInstance() {
        return true;
    }

}
