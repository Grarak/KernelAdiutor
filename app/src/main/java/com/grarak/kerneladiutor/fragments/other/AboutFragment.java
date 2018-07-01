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
package com.grarak.kerneladiutor.fragments.other;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.recyclerview.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.util.List;

/**
 * Created by willi on 22.07.16.
 */
public class AboutFragment extends RecyclerViewFragment {

    @Override
    protected Fragment getDialogFragment() {
        return new LicenseFragment();
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(new InfoFragment());
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        librariesInit(items);
    }

    private void librariesInit(List<RecyclerViewItem> items) {
        DescriptionView licenses = new DescriptionView();
        licenses.setTitle(getString(R.string.software_licenses));
        licenses.setSummary(getString(R.string.software_licenses_summary));
        licenses.setOnItemClickListener(item -> showDialog());

        items.add(licenses);
    }

    public static class InfoFragment extends BaseFragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_info, container, false);

            ((TextView) rootView.findViewById(R.id.about_me)).setText(getString(R.string.about_me, 21));

            rootView.findViewById(R.id.image).setOnClickListener(view
                    -> Utils.launchUrl("https://github.com/Grarak", requireActivity()));
            return rootView;
        }
    }

    public static class LicenseFragment extends BaseFragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            WebView webView = new WebView(getActivity());
            webView.loadUrl("file:///android_asset/licenses.html");

            return webView;
        }
    }
}
