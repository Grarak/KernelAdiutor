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
package com.grarak.kerneladiutor.fragments.kernel;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseControlFragment;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.IntelliPlug;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.MPDecision;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;
import com.grarak.kerneladiutordonate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 */
public class CPUHotplug extends BaseControlFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.CPU_HOTPLUG));
    }

    @Override
    protected List<RecyclerViewItem> addItems(List<RecyclerViewItem> items) {
        if (MPDecision.supported()) {
            mpdecisionInit(items);
        }
        if (IntelliPlug.supported()) {
            intelliPlugInit(items);
        }
        return items;
    }

    private void mpdecisionInit(List<RecyclerViewItem> items) {
        SwitchView mpdecision = new SwitchView();
        mpdecision.setTitle(getString(R.string.mpdecision));
        mpdecision.setSummary(getString(R.string.mpdecision_summary));
        mpdecision.setChecked(MPDecision.isMpdecisionEnabled());
        mpdecision.setOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                MPDecision.enableMpdecision(isChecked, getActivity());
            }
        });

        items.add(mpdecision);
    }

    private void intelliPlugInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> intelliplug = new ArrayList<>();

        TitleView title = new TitleView();
        title.setText(getString(R.string.intelliplug));

        if (IntelliPlug.hasIntelliPlugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.intelliplug));
            enable.setSummary(getString(R.string.intelliplug_summary));
            enable.setChecked(IntelliPlug.isIntelliPlugEnabled());
            enable.setOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    IntelliPlug.enableIntelliPlug(isChecked, getActivity());
                }
            });

            intelliplug.add(enable);
        }

        if (IntelliPlug.hasIntelliPlugProfile()) {
            SelectView profile = new SelectView();
            profile.setTitle(getString(R.string.profile));
            profile.setSummary(getString(R.string.profile_summary));
            profile.setItems(IntelliPlug.getIntelliPlugProfileMenu(getActivity()));
            profile.setItem(IntelliPlug.getIntelliPlugProfile());
            profile.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    IntelliPlug.setIntelliPlugProfile(position, getActivity());
                }
            });

            intelliplug.add(profile);
        }

        if (IntelliPlug.hasIntelliPlugEco()) {
            SwitchView eco = new SwitchView();
            eco.setTitle(getString(R.string.eco_mode));
            eco.setSummary(getString(R.string.eco_mode_summary));
            eco.setChecked(IntelliPlug.isIntelliPlugEcoEnabled());
            eco.setOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    IntelliPlug.enableIntelliPlugEco(isChecked, getActivity());
                }
            });

            intelliplug.add(eco);
        }

        if (IntelliPlug.hasIntelliPlugTouchBoost()) {
            SwitchView touchBoost = new SwitchView();
            touchBoost.setTitle(getString(R.string.touch_boost));
            touchBoost.setSummary(getString(R.string.touch_boost_summary));
            touchBoost.setChecked(IntelliPlug.isIntelliPlugTouchBoostEnabled());
            touchBoost.setOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    IntelliPlug.enableIntelliPlugTouchBoost(isChecked, getActivity());
                }
            });

            intelliplug.add(touchBoost);
        }

        if (IntelliPlug.hasIntelliPlugHysteresis()) {
            SeekBarView hysteresis = new SeekBarView();
            hysteresis.setTitle(getString(R.string.hysteresis));
            hysteresis.setSummary(getString(R.string.hysteresis_summary));
            hysteresis.setMax(17);
            hysteresis.setProgress(IntelliPlug.getIntelliPlugHysteresis());
            hysteresis.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugHysteresis(position, getActivity());
                }
            });

            intelliplug.add(hysteresis);
        }

        if (IntelliPlug.hasIntelliPlugThresold()) {
            SeekBarView threshold = new SeekBarView();
            threshold.setTitle(getString(R.string.cpu_threshold));
            threshold.setSummary(getString(R.string.cpu_threshold_summary));
            threshold.setMax(1000);
            threshold.setProgress(IntelliPlug.getIntelliPlugThresold());
            threshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugThresold(position, getActivity());
                }
            });

            intelliplug.add(threshold);
        }

        if (IntelliPlug.hasIntelliPlugScreenOffMax() && CPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(CPUFreq.getAdjustedFreq(getActivity()));

            SelectView maxScreenOffFreq = new SelectView();
            maxScreenOffFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            maxScreenOffFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            maxScreenOffFreq.setItems(list);
            maxScreenOffFreq.setItem(IntelliPlug.getIntelliPlugScreenOffMax());
            maxScreenOffFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    IntelliPlug.setIntelliPlugScreenOffMax(position, getActivity());
                }
            });

            intelliplug.add(maxScreenOffFreq);
        }

        if (IntelliPlug.hasIntelliPlugDebug()) {
            SwitchView debug = new SwitchView();
            debug.setTitle(getString(R.string.debug));
            debug.setSummary(getString(R.string.debug_summary));
            debug.setChecked(IntelliPlug.isIntelliPlugDebugEnabled());
            debug.setOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    IntelliPlug.enableIntelliPlugDebug(isChecked, getActivity());
                }
            });

            intelliplug.add(debug);
        }

        if (IntelliPlug.hasIntelliPlugSuspend()) {
            SwitchView suspend = new SwitchView();
            suspend.setTitle(getString(R.string.suspend));
            suspend.setSummary(getString(R.string.suspend_summary));
            suspend.setChecked(IntelliPlug.isIntelliPlugSuspendEnabled());
            suspend.setOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    IntelliPlug.enableIntelliPlugSuspend(isChecked, getActivity());
                }
            });

            intelliplug.add(suspend);
        }

        if (IntelliPlug.hasIntelliPlugCpusBoosted()) {
            SeekBarView cpusBoosted = new SeekBarView();
            cpusBoosted.setTitle(getString(R.string.cpus_boosted));
            cpusBoosted.setSummary(getString(R.string.cpus_boosted_summary));
            cpusBoosted.setMax(CPUFreq.getCpuCount());
            cpusBoosted.setMin(1);
            cpusBoosted.setProgress(IntelliPlug.getIntelliPlugCpusBoosted() - 1);
            cpusBoosted.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugCpusBoosted(position + 1, getActivity());
                }
            });

            intelliplug.add(cpusBoosted);
        }

        if (IntelliPlug.hasIntelliPlugMinCpusOnline()) {
            SeekBarView minCpusOnline = new SeekBarView();
            minCpusOnline.setTitle(getString(R.string.min_cpu_online));
            minCpusOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minCpusOnline.setMax(CPUFreq.getCpuCount());
            minCpusOnline.setMin(1);
            minCpusOnline.setProgress(IntelliPlug.getIntelliPlugMinCpusOnline() - 1);
            minCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugMinCpusOnline(position + 1, getActivity());
                }
            });

            intelliplug.add(minCpusOnline);
        }

        if (IntelliPlug.hasIntelliPlugMaxCpusOnline()) {
            SeekBarView maxCpusOnline = new SeekBarView();
            maxCpusOnline.setTitle(getString(R.string.max_cpu_online));
            maxCpusOnline.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpusOnline.setMax(CPUFreq.getCpuCount());
            maxCpusOnline.setMin(1);
            maxCpusOnline.setProgress(IntelliPlug.getIntelliPlugMaxCpusOnline() - 1);
            maxCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugMaxCpusOnline(position + 1, getActivity());
                }
            });

            intelliplug.add(maxCpusOnline);
        }

        if (IntelliPlug.hasIntelliPlugMaxCpusOnlineSusp()) {
            SeekBarView maxCpusOnlineSusp = new SeekBarView();
            maxCpusOnlineSusp.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCpusOnlineSusp.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCpusOnlineSusp.setMax(CPUFreq.getCpuCount());
            maxCpusOnlineSusp.setMin(1);
            maxCpusOnlineSusp.setProgress(IntelliPlug.getIntelliPlugMaxCpusOnlineSusp() - 1);
            maxCpusOnlineSusp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugMaxCpusOnlineSusp(position + 1, getActivity());
                }
            });

            intelliplug.add(maxCpusOnlineSusp);
        }

        if (IntelliPlug.hasIntelliPlugSuspendDeferTime()) {
            SeekBarView suspendDeferTime = new SeekBarView();
            suspendDeferTime.setTitle(getString(R.string.suspend_defer_time));
            suspendDeferTime.setUnit(getString(R.string.ms));
            suspendDeferTime.setMax(5000);
            suspendDeferTime.setOffset(10);
            suspendDeferTime.setProgress(IntelliPlug.getIntelliPlugSuspendDeferTime() / 10);
            suspendDeferTime.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugSuspendDeferTime(position * 10, getActivity());
                }
            });

            intelliplug.add(suspendDeferTime);
        }

        if (IntelliPlug.hasIntelliPlugDeferSampling()) {
            SeekBarView deferSampling = new SeekBarView();
            deferSampling.setTitle(getString(R.string.defer_sampling));
            deferSampling.setUnit(getString(R.string.ms));
            deferSampling.setMax(1000);
            deferSampling.setProgress(IntelliPlug.getIntelliPlugDeferSampling());
            deferSampling.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugDeferSampling(position, getActivity());
                }
            });

            intelliplug.add(deferSampling);
        }

        if (IntelliPlug.hasIntelliPlugBoostLockDuration()) {
            SeekBarView boostLockDuration = new SeekBarView();
            boostLockDuration.setTitle(getString(R.string.boost_lock_duration));
            boostLockDuration.setSummary(getString(R.string.boost_lock_duration_summary));
            boostLockDuration.setUnit(getString(R.string.ms));
            boostLockDuration.setMax(5000);
            boostLockDuration.setMin(1);
            boostLockDuration.setProgress(IntelliPlug.getIntelliPlugBoostLockDuration() - 1);
            boostLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugBoostLockDuration(position + 1, getActivity());
                }
            });

            intelliplug.add(boostLockDuration);
        }

        if (IntelliPlug.hasIntelliPlugDownLockDuration()) {
            SeekBarView downLockDuration = new SeekBarView();
            downLockDuration.setTitle(getString(R.string.down_lock_duration));
            downLockDuration.setSummary(getString(R.string.down_lock_duration_summary));
            downLockDuration.setUnit(getString(R.string.ms));
            downLockDuration.setMax(5000);
            downLockDuration.setMin(1);
            downLockDuration.setProgress(IntelliPlug.getIntelliPlugDownLockDuration() - 1);
            downLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugDownLockDuration(position + 1, getActivity());
                }
            });

            intelliplug.add(downLockDuration);
        }

        if (IntelliPlug.hasIntelliPlugFShift()) {
            SeekBarView fShift = new SeekBarView();
            fShift.setTitle(getString(R.string.fshift));
            fShift.setMax(4);
            fShift.setProgress(IntelliPlug.getIntelliPlugFShift());
            fShift.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IntelliPlug.setIntelliPlugFShift(position, getActivity());
                }
            });

            intelliplug.add(fShift);
        }

        if (intelliplug.size() > 0) {
            intelliplug.add(title);
            items.addAll(intelliplug);
        }
    }

}
