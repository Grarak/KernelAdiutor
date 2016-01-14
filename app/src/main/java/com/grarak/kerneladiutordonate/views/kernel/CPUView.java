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
package com.grarak.kerneladiutordonate.views.kernel;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutordonate.R;
import com.grarak.kerneladiutordonate.elements.recyclerview.Adapter;
import com.grarak.kerneladiutordonate.elements.views.XYGraph;
import com.grarak.kerneladiutordonate.fragments.BaseFragment;
import com.grarak.kerneladiutordonate.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutordonate.utils.kernel.CPU;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 31.12.15.
 */
public class CPUView extends RecyclerViewFragment.ViewInterface {

    private LoadFragment loadFragment;
    private LoadsFragment loadsFragment;
    private float[] cpuUsages;

    public CPUView(Context context) {
        super(context);
    }

    @Override
    public List<Adapter.RecyclerItem> getViews(Bundle savedInstanceState) {
        return new ArrayList<>();
    }

    @Override
    public List<BaseFragment> getViewPagerFragments(Bundle savedInstanceState) {
        List<BaseFragment> list = new ArrayList<>();
        list.add(loadFragment = new LoadFragment());
        if (CPU.getCoreCount() > 1)
            list.add(loadsFragment = new LoadsFragment());
        return list;
    }

    @Override
    public boolean refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cpuUsages = CPU.getCpuUsage();
            }
        }).start();
        if (loadFragment != null) loadFragment.refresh(cpuUsages);
        if (loadsFragment != null) loadsFragment.refresh(cpuUsages);
        return true;
    }

    public static class LoadFragment extends BaseFragment {

        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);
            setContentView(R.layout.views_usage);
        }

        public void refresh(float[] cpuUsages) {
            if (cpuUsages != null) {
                ((TextView) findViewById(R.id.usage_load_text))
                        .setText(String.format("%d%%", (int) cpuUsages[0]));
                ((XYGraph) findViewById(R.id.usage_graph))
                        .addPercentage((int) cpuUsages[0]);
            }
        }

        @Override
        public boolean retainInstance() {
            return false;
        }
    }

    public static class LoadsFragment extends BaseFragment {

        private View[] usageView;

        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);
            LinearLayout rootView = new LinearLayout(getActivity());
            rootView.setOrientation(LinearLayout.VERTICAL);
            setContentView(rootView);

            LinearLayout[] subViews = new LinearLayout[CPU.getCoreCount() / 2];
            for (int i = 0; i < CPU.getCoreCount() / 2; i++) {
                rootView.addView(subViews[i] = new LinearLayout(getActivity()));
                LinearLayout.LayoutParams params = new LinearLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params.weight = 1;
                subViews[i].setLayoutParams(params);
            }

            usageView = new View[CPU.getCoreCount()];
            for (int i = 0; i < CPU.getCoreCount(); i++) {
                usageView[i] = getInflater().inflate(R.layout.views_usage,
                        subViews[i / 2], false);
                subViews[i / 2].addView(usageView[i]);
                LinearLayout.LayoutParams params = new LinearLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params.weight = 1;
                usageView[i].setLayoutParams(params);
            }
        }

        private void refresh(float[] cpuUsages) {
            if (cpuUsages != null) {
                for (int i = 0; i < CPU.getCoreCount(); i++) {
                    int curFreq = CPU.getCurFreq(i);
                    if (curFreq == 0) {
                        usageView[i].findViewById(R.id.usage_offline_text).setVisibility(View.VISIBLE);
                        usageView[i].findViewById(R.id.usage_load_text).setVisibility(View.GONE);
                        usageView[i].findViewById(R.id.usage_freq_text).setVisibility(View.GONE);
                        usageView[i].findViewById(R.id.usage_core_text).setVisibility(View.GONE);
                        ((XYGraph) usageView[i].findViewById(R.id.usage_graph))
                                .addPercentage(0);
                    } else {
                        usageView[i].findViewById(R.id.usage_offline_text).setVisibility(View.GONE);
                        TextView load = (TextView) usageView[i].findViewById(R.id.usage_load_text);
                        TextView freq = (TextView) usageView[i].findViewById(R.id.usage_freq_text);
                        TextView core = (TextView) usageView[i].findViewById(R.id.usage_core_text);
                        load.setVisibility(View.VISIBLE);
                        freq.setVisibility(View.VISIBLE);
                        core.setVisibility(View.VISIBLE);
                        load.setText(String.format("%d%%", (int) cpuUsages[i + 1]));
                        freq.setText(String.format("%d" + getString(R.string.mhz), curFreq / 1000));
                        core.setText(getString(R.string.core, i + 1));
                        ((XYGraph) usageView[i].findViewById(R.id.usage_graph))
                                .addPercentage((int) cpuUsages[i + 1]);
                    }
                }
            }
        }

        @Override
        public boolean retainInstance() {
            return false;
        }
    }

}
