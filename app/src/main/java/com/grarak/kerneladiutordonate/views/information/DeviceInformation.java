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
package com.grarak.kerneladiutordonate.views.information;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.TextView;

import com.grarak.kerneladiutordonate.R;
import com.grarak.kerneladiutordonate.elements.recyclerview.Adapter;
import com.grarak.kerneladiutordonate.elements.recyclerview.MainCard;
import com.grarak.kerneladiutordonate.elements.recyclerview.Text;
import com.grarak.kerneladiutordonate.fragments.BaseFragment;
import com.grarak.kerneladiutordonate.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutordonate.utils.Device;
import com.grarak.kerneladiutordonate.utils.Utils;
import com.grarak.kerneladiutordonate.utils.kernel.CPU;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 02.01.16.
 */
public class DeviceInformation extends RecyclerViewFragment.ViewInterface {

    private static List<Adapter.RecyclerItem> views;

    public DeviceInformation(final Context context) {
        super(context);
        if (views != null) return;
        views = new ArrayList<>();

        MainCard deviceCard = new MainCard(context);
        String vendor = Device.getVendor();
        vendor = vendor.substring(0, 1).toUpperCase() + vendor.substring(1);
        deviceCard.setTitle(vendor + " " + Device.getModel());

        String[][] deviceInfos = {
                {getString(R.string.android_version), Device.getVersion()},
                {getString(R.string.android_api_level), String.valueOf(Device.getSDK())},
                {getString(R.string.android_codename), Device.getCodename()},
                {getString(R.string.fingerprint), Device.getFingerprint()},
                {getString(R.string.build_display_id), Device.getBuildDisplayId()},
                {getString(R.string.baseband), Device.getBaseBand()},
                {getString(R.string.bootloader), Device.getBootloader()}
        };

        for (String[] deviceInfo : deviceInfos) {
            final String title = deviceInfo[0];
            final String summary = deviceInfo[1];
            if (summary != null && !summary.isEmpty())
                deviceCard.addItem(new Text(new Text.TextOptions() {
                    @Override
                    public CharSequence getTitle(Text text) {
                        return title;
                    }

                    @Override
                    public CharSequence getSummary(Text text) {
                        return summary;
                    }
                }));
        }

        views.add(deviceCard);

        MainCard boardCard = new MainCard(context);
        boardCard.setTitle(Device.getBoard());

        String[][] boardInfos = {
                {getString(R.string.hardware), Device.getHardware()},
                {getString(R.string.architecture), Device.getArchitecture()},
                {getString(R.string.kernel), Device.getKernelVersion()}
        };

        for (String[] boardInfo : boardInfos) {
            final String title = boardInfo[0];
            final String summary = boardInfo[1];
            if (summary != null && !summary.isEmpty())
                boardCard.addItem(new Text(new Text.TextOptions() {
                    @Override
                    public CharSequence getTitle(Text text) {
                        return title;
                    }

                    @Override
                    public CharSequence getSummary(Text text) {
                        return summary;
                    }
                }));
        }

        views.add(boardCard);
    }

    @Override
    public List<Adapter.RecyclerItem> getViews(Bundle savedInstanceState) {
        return views;
    }

    @Override
    public List<BaseFragment> getViewPagerFragments(Bundle savedInstanceState) {
        List<BaseFragment> fragments = new ArrayList<>();

        fragments.add(CoresFragment.newInstance(getString(R.string.core_count, CPU.getCoreCount())));
        String bigText;
        List<Integer> freqs = CPU.getFreqs(CPU.getBigCore());
        if (CPU.isBigLITTLE()) {
            bigText = getString(R.string.big) + " @"
                    + Utils.round((float) freqs.get(freqs.size() - 1) / 1000000, 2) + getString(R.string.ghz);
        } else {
            bigText = "@" + Utils.round((float) freqs.get(freqs.size() - 1) / 1000000, 2) + getString(R.string.ghz);
        }
        fragments.add(CoresFragment.newInstance(bigText));
        if (CPU.isBigLITTLE()) {
            List<Integer> freqsLITTLE = CPU.getFreqs(CPU.getLITTLEcore());
            fragments.add(CoresFragment.newInstance(getString(R.string.little) + " @"
                    + Utils.round((float) freqsLITTLE.get(freqsLITTLE.size() - 1) / 1000000, 2)
                    + getString(R.string.ghz)));
        }

        return fragments;
    }

    public static class CoresFragment extends BaseFragment {

        private static final String MESSAGE_INTENT = "message";

        public static CoresFragment newInstance(CharSequence message) {
            Bundle args = new Bundle();
            CoresFragment fragment = new CoresFragment();
            args.putCharSequence(MESSAGE_INTENT, message);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);
            TextView textView = new TextView(getActivity());
            setContentView(textView);

            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            textView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            textView.setText(getArguments().getCharSequence(MESSAGE_INTENT, ""));
            textView.setTextSize(getResources().getDimension(R.dimen.deviceinfo_fragment_textsize));
        }

        @Override
        public boolean retainInstance() {
            return false;
        }
    }

}
