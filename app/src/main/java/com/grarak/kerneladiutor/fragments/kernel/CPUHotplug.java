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

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseControlFragment;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.BluPlug;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.IntelliPlug;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.MPDecision;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.MSMHotplug;
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

    private List<SwitchView> mEnableViews = new ArrayList<>();
    private AlertDialog.Builder mWarningDialog;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.CPU_HOTPLUG));

        if (mWarningDialog != null) {
            mWarningDialog.show();
        }
    }

    @Override
    protected List<RecyclerViewItem> addItems(List<RecyclerViewItem> items) {
        if (MPDecision.supported()) {
            mpdecisionInit(items);
        }
        if (IntelliPlug.supported()) {
            intelliPlugInit(items);
        }
        if (BluPlug.supported()) {
            bluPlugInit(items);
        }
        if (MSMHotplug.supported()) {
            msmHotplugInit(items);
        }

        for (SwitchView view : mEnableViews) {
            view.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    for (SwitchView view : mEnableViews) {
                        if (!view.getTitle().equals(switchView.getTitle()) && view.isChecked()
                                && switchView.isChecked()) {
                            mWarningDialog = ViewUtils.dialogBuilder(getString(R.string.hotplug_warning),
                                    null, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }, new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            mWarningDialog = null;
                                        }
                                    }, getActivity());
                            mWarningDialog.show();
                        }
                    }
                }
            });
        }
        return items;
    }

    private void mpdecisionInit(List<RecyclerViewItem> items) {
        SwitchView mpdecision = new SwitchView();
        mpdecision.setTitle(getString(R.string.mpdecision));
        mpdecision.setSummary(getString(R.string.mpdecision_summary));
        mpdecision.setChecked(MPDecision.isMpdecisionEnabled());
        mpdecision.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                MPDecision.enableMpdecision(isChecked, getActivity());
            }
        });

        items.add(mpdecision);
        mEnableViews.add(mpdecision);
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
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    IntelliPlug.enableIntelliPlug(isChecked, getActivity());
                }
            });

            intelliplug.add(enable);
            mEnableViews.add(enable);
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
            eco.addOnSwitchListener(new SwitchView.OnSwitchListener() {
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
            touchBoost.addOnSwitchListener(new SwitchView.OnSwitchListener() {
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
            debug.setTitle(getString(R.string.debug_mask));
            debug.setSummary(getString(R.string.debug_mask_summary));
            debug.setChecked(IntelliPlug.isIntelliPlugDebugEnabled());
            debug.addOnSwitchListener(new SwitchView.OnSwitchListener() {
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
            suspend.addOnSwitchListener(new SwitchView.OnSwitchListener() {
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

    private void bluPlugInit(List<RecyclerViewItem> items) {
        final List<RecyclerViewItem> bluplug = new ArrayList<>();
        TitleView title = new TitleView();
        title.setText(getString(R.string.blu_plug));

        if (BluPlug.hasBluPlugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.blu_plug));
            enable.setSummary(getString(R.string.blu_plug_summary));
            enable.setChecked(BluPlug.isBluPlugEnabled());
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    BluPlug.enableBluPlug(isChecked, getActivity());
                }
            });

            bluplug.add(enable);
            mEnableViews.add(enable);
        }

        if (BluPlug.hasBluPlugPowersaverMode()) {
            SwitchView powersaverMode = new SwitchView();
            powersaverMode.setTitle(getString(R.string.powersaver_mode));
            powersaverMode.setSummary(getString(R.string.powersaver_mode_summary));
            powersaverMode.setChecked(BluPlug.isBluPlugPowersaverModeEnabled());
            powersaverMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    BluPlug.enableBluPlugPowersaverMode(isChecked, getActivity());
                }
            });

            bluplug.add(powersaverMode);
        }

        if (BluPlug.hasBluPlugMinOnline()) {
            SeekBarView minOnline = new SeekBarView();
            minOnline.setTitle(getString(R.string.min_cpu_online));
            minOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minOnline.setMax(CPUFreq.getCpuCount());
            minOnline.setMin(1);
            minOnline.setProgress(BluPlug.getBluPlugMinOnline() - 1);
            minOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugMinOnline(position + 1, getActivity());
                }
            });

            bluplug.add(minOnline);
        }

        if (BluPlug.hasBluPlugMaxOnline()) {
            SeekBarView maxOnline = new SeekBarView();
            maxOnline.setTitle(getString(R.string.max_cpu_online));
            maxOnline.setSummary(getString(R.string.max_cpu_online_summary));
            maxOnline.setMax(CPUFreq.getCpuCount());
            maxOnline.setMin(1);
            maxOnline.setProgress(BluPlug.getBluPlugMaxOnline() - 1);
            maxOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugMaxOnline(position + 1, getActivity());
                }
            });

            bluplug.add(maxOnline);
        }

        if (BluPlug.hasBluPlugMaxCoresScreenOff()) {
            SeekBarView maxCoresScreenOff = new SeekBarView();
            maxCoresScreenOff.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCoresScreenOff.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCoresScreenOff.setMax(CPUFreq.getCpuCount());
            maxCoresScreenOff.setMin(1);
            maxCoresScreenOff.setProgress(BluPlug.getBluPlugMaxCoresScreenOff() - 1);
            maxCoresScreenOff.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugMaxCoresScreenOff(position + 1, getActivity());
                }
            });

            bluplug.add(maxCoresScreenOff);
        }

        if (BluPlug.hasBluPlugMaxFreqScreenOff() && CPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(CPUFreq.getAdjustedFreq(getActivity()));

            SeekBarView maxFreqScreenOff = new SeekBarView();
            maxFreqScreenOff.setTitle(getString(R.string.cpu_max_screen_off_freq));
            maxFreqScreenOff.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            maxFreqScreenOff.setItems(list);
            maxFreqScreenOff.setProgress(BluPlug.getBluPlugMaxFreqScreenOff());
            maxFreqScreenOff.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugMaxFreqScreenOff(position, getActivity());
                }
            });

            bluplug.add(maxFreqScreenOff);
        }

        if (BluPlug.hasBluPlugUpThreshold()) {
            SeekBarView upThreshold = new SeekBarView();
            upThreshold.setTitle(getString(R.string.up_threshold));
            upThreshold.setSummary(getString(R.string.up_threshold_summary));
            upThreshold.setUnit("%");
            upThreshold.setMax(100);
            upThreshold.setProgress(BluPlug.getBluPlugUpThreshold());
            upThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugUpThreshold(position, getActivity());
                }
            });

            bluplug.add(upThreshold);
        }

        if (BluPlug.hasBluPlugUpTimerCnt()) {
            List<String> list = new ArrayList<>();
            for (float i = 0; i < 21; i++) {
                list.add(String.valueOf(i * 0.5f));
            }

            SeekBarView upTimerCnt = new SeekBarView();
            upTimerCnt.setTitle(getString(R.string.up_timer_cnt));
            upTimerCnt.setSummary(getString(R.string.up_timer_cnt_summary));
            upTimerCnt.setItems(list);
            upTimerCnt.setProgress(BluPlug.getBluPlugUpTimerCnt());
            upTimerCnt.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugUpTimerCnt(position, getActivity());
                }
            });

            bluplug.add(upTimerCnt);
        }

        if (BluPlug.hasBluPlugDownTimerCnt()) {
            List<String> list = new ArrayList<>();
            for (float i = 0; i < 21; i++) {
                list.add(String.valueOf(i * 0.5f));
            }

            SeekBarView downTimerCnt = new SeekBarView();
            downTimerCnt.setTitle(getString(R.string.down_timer_cnt));
            downTimerCnt.setSummary(getString(R.string.down_timer_cnt_summary));
            downTimerCnt.setItems(list);
            downTimerCnt.setProgress(BluPlug.getBluPlugDownTimerCnt());
            downTimerCnt.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugDownTimerCnt(position, getActivity());
                }
            });

            bluplug.add(downTimerCnt);
        }

        if (bluplug.size() > 0) {
            items.add(title);
            items.addAll(bluplug);
        }
    }

    private void msmHotplugInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> msmHotplug = new ArrayList<>();
        TitleView title = new TitleView();
        title.setText(getString(R.string.msm_hotplug));

        if (MSMHotplug.hasMsmHotplugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.msm_hotplug));
            enable.setSummary(getString(R.string.msm_hotplug_summary));
            enable.setChecked(MSMHotplug.isMsmHotplugEnabled());
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMHotplug.enableMsmHotplug(isChecked, getActivity());
                }
            });

            msmHotplug.add(enable);
            mEnableViews.add(enable);
        }

        if (MSMHotplug.hasMsmHotplugDebugMask()) {
            SwitchView debugMask = new SwitchView();
            debugMask.setTitle(getString(R.string.debug_mask));
            debugMask.setSummary(getString(R.string.debug_mask_summary));
            debugMask.setChecked(MSMHotplug.isMsmHotplugDebugMaskEnabled());
            debugMask.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMHotplug.enableMsmHotplugDebugMask(isChecked, getActivity());
                }
            });

            msmHotplug.add(debugMask);
        }

        if (MSMHotplug.hasMsmHotplugMinCpusOnline()) {
            SeekBarView minCpusOnline = new SeekBarView();
            minCpusOnline.setTitle(getString(R.string.min_cpu_online));
            minCpusOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minCpusOnline.setMax(CPUFreq.getCpuCount());
            minCpusOnline.setMin(1);
            minCpusOnline.setProgress(MSMHotplug.getMsmHotplugMinCpusOnline() - 1);
            minCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugMinCpusOnline(position + 1, getActivity());
                }
            });

            msmHotplug.add(minCpusOnline);
        }

        if (MSMHotplug.hasMsmHotplugMaxCpusOnline()) {
            SeekBarView maxCpusOnline = new SeekBarView();
            maxCpusOnline.setTitle(getString(R.string.max_cpu_online));
            maxCpusOnline.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpusOnline.setMax(CPUFreq.getCpuCount());
            maxCpusOnline.setMin(1);
            maxCpusOnline.setProgress(MSMHotplug.getMsmHotplugMaxCpusOnline() - 1);
            maxCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugMaxCpusOnline(position + 1, getActivity());
                }
            });

            msmHotplug.add(maxCpusOnline);
        }

        if (MSMHotplug.hasMsmHotplugCpusBoosted()) {
            SeekBarView cpusBoosted = new SeekBarView();
            cpusBoosted.setTitle(getString(R.string.cpus_boosted));
            cpusBoosted.setSummary(getString(R.string.cpus_boosted_summary));
            cpusBoosted.setMax(CPUFreq.getCpuCount());
            cpusBoosted.setMin(1);
            cpusBoosted.setProgress(MSMHotplug.getMsmHotplugCpusBoosted());
            cpusBoosted.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugCpusBoosted(position, getActivity());
                }
            });

            msmHotplug.add(cpusBoosted);
        }

        if (MSMHotplug.hasMsmHotplugMaxCpusOnlineSusp()) {
            SeekBarView maxCpusOnlineSusp = new SeekBarView();
            maxCpusOnlineSusp.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCpusOnlineSusp.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCpusOnlineSusp.setMax(CPUFreq.getCpuCount());
            maxCpusOnlineSusp.setMin(1);
            maxCpusOnlineSusp.setProgress(MSMHotplug.getMsmHotplugMaxCpusOnlineSusp() - 1);
            maxCpusOnlineSusp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugMaxCpusOnlineSusp(position + 1, getActivity());
                }
            });

            msmHotplug.add(maxCpusOnlineSusp);
        }

        if (MSMHotplug.hasMsmHotplugBoostLockDuration()) {
            SeekBarView boostLockDuration = new SeekBarView();
            boostLockDuration.setTitle(getString(R.string.boost_lock_duration));
            boostLockDuration.setSummary(getString(R.string.boost_lock_duration_summary));
            boostLockDuration.setMax(5000);
            boostLockDuration.setMin(1);
            boostLockDuration.setProgress(MSMHotplug.getMsmHotplugBoostLockDuration() - 1);
            boostLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugBoostLockDuration(position + 1, getActivity());
                }
            });

            msmHotplug.add(boostLockDuration);
        }

        if (MSMHotplug.hasMsmHotplugDownLockDuration()) {
            SeekBarView downLockDuration = new SeekBarView();
            downLockDuration.setTitle(getString(R.string.down_lock_duration));
            downLockDuration.setSummary(getString(R.string.down_lock_duration_summary));
            downLockDuration.setMax(5000);
            downLockDuration.setMin(1);
            downLockDuration.setProgress(MSMHotplug.getMsmHotplugDownLockDuration() - 1);
            downLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugDownLockDuration(position + 1, getActivity());
                }
            });

            msmHotplug.add(downLockDuration);
        }

        if (MSMHotplug.hasMsmHotplugHistorySize()) {
            SeekBarView historySize = new SeekBarView();
            historySize.setTitle(getString(R.string.history_size));
            historySize.setSummary(getString(R.string.history_size_summary));
            historySize.setMax(60);
            historySize.setMin(1);
            historySize.setProgress(MSMHotplug.getMsmHotplugHistorySize() - 1);
            historySize.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugHistorySize(position + 1, getActivity());
                }
            });

            msmHotplug.add(historySize);
        }

        if (MSMHotplug.hasMsmHotplugUpdateRate()) {
            SeekBarView updateRate = new SeekBarView();
            updateRate.setTitle(getString(R.string.update_rate));
            updateRate.setSummary(getString(R.string.update_rate_summary));
            updateRate.setMax(60);
            updateRate.setProgress(MSMHotplug.getMsmHotplugUpdateRate());
            updateRate.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugUpdateRate(position, getActivity());
                }
            });

            msmHotplug.add(updateRate);
        }

        if (MSMHotplug.hasMsmHotplugFastLaneLoad()) {
            SeekBarView fastLaneLoad = new SeekBarView();
            fastLaneLoad.setTitle(getString(R.string.fast_lane_load));
            fastLaneLoad.setSummary(getString(R.string.fast_lane_load_summary));
            fastLaneLoad.setMax(400);
            fastLaneLoad.setProgress(MSMHotplug.getMsmHotplugFastLaneLoad());
            fastLaneLoad.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugFastLaneLoad(position, getActivity());
                }
            });

            msmHotplug.add(fastLaneLoad);
        }

        if (MSMHotplug.hasMsmHotplugFastLaneMinFreq() && CPUFreq.getFreqs() != null) {
            SelectView fastLaneMinFreq = new SelectView();
            fastLaneMinFreq.setTitle(getString(R.string.fast_lane_min_freq));
            fastLaneMinFreq.setSummary(getString(R.string.fast_lane_min_freq_summary));
            fastLaneMinFreq.setItems(CPUFreq.getAdjustedFreq(getActivity()));
            fastLaneMinFreq.setItem((MSMHotplug.getMsmHotplugFastLaneMinFreq() / 1000) + getString(R.string.mhz));
            fastLaneMinFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    MSMHotplug.setMsmHotplugFastLaneMinFreq(CPUFreq.getFreqs().get(position), getActivity());
                }
            });

            msmHotplug.add(fastLaneMinFreq);
        }

        if (MSMHotplug.hasMsmHotplugOfflineLoad()) {
            SeekBarView offlineLoad = new SeekBarView();
            offlineLoad.setTitle(getString(R.string.offline_load));
            offlineLoad.setSummary(getString(R.string.offline_load_summary));
            offlineLoad.setProgress(MSMHotplug.getMsmHotplugOfflineLoad());
            offlineLoad.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugOfflineLoad(position, getActivity());
                }
            });

            msmHotplug.add(offlineLoad);
        }

        if (MSMHotplug.hasMsmHotplugIoIsBusy()) {
            SwitchView ioIsBusy = new SwitchView();
            ioIsBusy.setTitle(getString(R.string.io_is_busy));
            ioIsBusy.setSummary(getString(R.string.io_is_busy_summary));
            ioIsBusy.setChecked(MSMHotplug.isMsmHotplugIoIsBusyEnabled());
            ioIsBusy.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMHotplug.enableMsmHotplugIoIsBusy(isChecked, getActivity());
                }
            });

            msmHotplug.add(ioIsBusy);
        }

        if (MSMHotplug.hasMsmHotplugSuspendMaxCpus()) {
            SeekBarView suspendMaxCpus = new SeekBarView();
            suspendMaxCpus.setTitle(getString(R.string.max_cpu_online_screen_off));
            suspendMaxCpus.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            suspendMaxCpus.setMax(CPUFreq.getCpuCount());
            suspendMaxCpus.setMin(1);
            suspendMaxCpus.setProgress(MSMHotplug.getMsmHotplugSuspendMaxCpus());
            suspendMaxCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugSuspendMaxCpus(position, getActivity());
                }
            });

            msmHotplug.add(suspendMaxCpus);
        }

        if (MSMHotplug.hasMsmHotplugSuspendFreq() && CPUFreq.getFreqs() != null) {
            SelectView suspendFreq = new SelectView();
            suspendFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            suspendFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            suspendFreq.setItems(CPUFreq.getAdjustedFreq(getActivity()));
            suspendFreq.setItem((MSMHotplug.getMsmHotplugSuspendFreq() / 1000) + getString(R.string.mhz));
            suspendFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    MSMHotplug.setMsmHotplugSuspendFreq(CPUFreq.getFreqs().get(position), getActivity());
                }
            });

            msmHotplug.add(suspendFreq);
        }

        if (MSMHotplug.hasMsmHotplugSuspendDeferTime()) {
            SeekBarView suspendDeferTime = new SeekBarView();
            suspendDeferTime.setTitle(getString(R.string.suspend_defer_time));
            suspendDeferTime.setUnit(getString(R.string.ms));
            suspendDeferTime.setMax(5000);
            suspendDeferTime.setOffset(10);
            suspendDeferTime.setProgress(MSMHotplug.getMsmHotplugSuspendDeferTime() / 10);
            suspendDeferTime.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMHotplug.setMsmHotplugSuspendDeferTime(position * 10, getActivity());
                }
            });

            msmHotplug.add(suspendDeferTime);
        }

        if (msmHotplug.size() > 0) {
            items.add(title);
            items.addAll(msmHotplug);
        }
    }

}
