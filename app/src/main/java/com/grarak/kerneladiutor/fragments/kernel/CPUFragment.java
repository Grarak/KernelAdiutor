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
import android.util.SparseArray;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUBoost;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.kernel.cpu.Misc;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;
import com.grarak.kerneladiutor.views.recyclerview.XYGraphView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by willi on 01.05.16.
 */
public class CPUFragment extends RecyclerViewFragment {

    private CPUFreq mCPUFreq;
    private CPUBoost mCPUBoost;

    private XYGraphView mCPUUsageBig;
    private SelectView mCPUMaxBig;
    private SelectView mCPUMinBig;
    private SelectView mCPUMaxScreenOffBig;
    private SelectView mCPUGovernorBig;

    private XYGraphView mCPUUsageLITTLE;
    private SelectView mCPUMaxLITTLE;
    private SelectView mCPUMinLITTLE;
    private SelectView mCPUMaxScreenOffLITTLE;
    private SelectView mCPUGovernorLITTLE;

    private SparseArray<SwitchView> mCoresBig = new SparseArray<>();
    private SparseArray<SwitchView> mCoresLITTLE = new SparseArray<>();

    private float[] mCPUUsages;
    private int mCPUMaxFreqBig;
    private int mCPUMinFreqBig;
    private int mCPUMaxScreenOffFreqBig;
    private String mCPUGovernorStrBig;
    private int mCPUMaxFreqLITTLE;
    private int mCPUMinFreqLITTLE;
    private int mCPUMaxScreenOffFreqLITTLE;
    private String mCPUGovernorStrLITTLE;

    private PathReaderFragment mGovernorTunableFragment;
    private Dialog mGovernorTunableErrorDialog;

    private Thread mRefreshThread;

    @Override
    protected BaseFragment getForegroundFragment() {
        return mGovernorTunableFragment = new PathReaderFragment();
    }

    @Override
    protected void init() {
        super.init();

        mCPUFreq = CPUFreq.getInstance(getActivity());
        mCPUBoost = CPUBoost.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        addViewPagerFragment(DescriptionFragment.newInstance(getString(mCPUFreq.getCpuCount() > 1 ?
                R.string.cores : R.string.cores_singular, mCPUFreq.getCpuCount()), Device.getBoard()));

        if (mGovernorTunableErrorDialog != null) {
            mGovernorTunableErrorDialog.show();
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        freqInit(items);
        if (Misc.hasMcPowerSaving()) {
            mcPowerSavingInit(items);
        }
        if (Misc.hasPowerSavingWq()) {
            powerSavingWqInit(items);
        }
        if (Misc.hasCFSScheduler()) {
            cfsSchedulerInit(items);
        }
        if (Misc.hasCpuQuiet()) {
            cpuQuietInit(items);
        }
        if (mCPUBoost.supported()) {
            cpuBoostInit(items);
        }
        if (Misc.hasCpuTouchBoost()) {
            cpuTouchBoostInit(items);
        }
        if (Misc.hascpuinputboost()) {
            cpuinputboostInit(items);
        }
    }

    private void freqInit(List<RecyclerViewItem> items) {
        mCPUUsageBig = new XYGraphView();
        if (mCPUFreq.isBigLITTLE()) {
            mCPUUsageBig.setTitle(getString(R.string.cpu_usage_string, getString(R.string.cluster_big)));
        } else {
            mCPUUsageBig.setTitle(getString(R.string.cpu_usage));
        }

        items.add(mCPUUsageBig);

        CardView bigCoresCard = new CardView(getActivity());
        if (mCPUFreq.isBigLITTLE()) {
            bigCoresCard.setTitle(getString(R.string.cores_string, getString(R.string.cluster_big)));
        }

        final List<Integer> bigCores = mCPUFreq.getBigCpuRange();

        mCoresBig.clear();
        for (final int core : bigCores) {
            SwitchView coreSwitch = new SwitchView();
            coreSwitch.setSummary(getString(R.string.core, core + 1));

            mCoresBig.put(core, coreSwitch);
            bigCoresCard.addItem(coreSwitch);
        }

        CardView bigFrequenciesCard = new CardView(getActivity());
        if (mCPUFreq.isBigLITTLE()) {
            bigFrequenciesCard.setTitle(getString(R.string.frequencies_string, getString(R.string.cluster_big)));
        }

        mCPUMaxBig = new SelectView();
        mCPUMaxBig.setTitle(getString(R.string.cpu_max_freq));
        mCPUMaxBig.setSummary(getString(R.string.cpu_max_freq_summary));
        mCPUMaxBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
        mCPUMaxBig.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                mCPUFreq.setMaxFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                        bigCores.get(bigCores.size() - 1), getActivity());
            }
        });
        bigFrequenciesCard.addItem(mCPUMaxBig);

        mCPUMinBig = new SelectView();
        mCPUMinBig.setTitle(getString(R.string.cpu_min_freq));
        mCPUMinBig.setSummary(getString(R.string.cpu_min_freq_summary));
        mCPUMinBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
        mCPUMinBig.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                mCPUFreq.setMinFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                        bigCores.get(bigCores.size() - 1), getActivity());
            }
        });
        bigFrequenciesCard.addItem(mCPUMinBig);

        if (mCPUFreq.hasMaxScreenOffFreq()) {
            mCPUMaxScreenOffBig = new SelectView();
            mCPUMaxScreenOffBig.setTitle(getString(R.string.cpu_max_screen_off_freq));
            mCPUMaxScreenOffBig.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            mCPUMaxScreenOffBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            mCPUMaxScreenOffBig.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                            bigCores.get(bigCores.size() - 1), getActivity());
                }
            });
            bigFrequenciesCard.addItem(mCPUMaxScreenOffBig);
        }

        CardView bigGovernorsCard = new CardView(getActivity());
        if (mCPUFreq.isBigLITTLE()) {
            bigGovernorsCard.setTitle(getString(R.string.governors_string, getString(R.string.cluster_big)));
        }

        mCPUGovernorBig = new SelectView();
        mCPUGovernorBig.setTitle(getString(R.string.cpu_governor));
        mCPUGovernorBig.setSummary(getString(R.string.cpu_governor_summary));
        mCPUGovernorBig.setItems(mCPUFreq.getGovernors());
        mCPUGovernorBig.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                mCPUFreq.setGovernor(item, bigCores.get(0), bigCores.get(bigCores.size() - 1),
                        getActivity());
            }
        });
        bigGovernorsCard.addItem(mCPUGovernorBig);

        DescriptionView governorTunablesBig = new DescriptionView();
        governorTunablesBig.setTitle(getString(R.string.cpu_governor_tunables));
        governorTunablesBig.setSummary(getString(R.string.governor_tunables_summary));
        governorTunablesBig.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                showGovernorTunables(bigCores.get(0), bigCores.get(bigCores.size() - 1));
            }
        });
        bigGovernorsCard.addItem(governorTunablesBig);

        items.add(bigCoresCard);
        items.add(bigFrequenciesCard);
        items.add(bigGovernorsCard);

        if (mCPUFreq.isBigLITTLE()) {
            mCPUUsageLITTLE = new XYGraphView();
            mCPUUsageLITTLE.setTitle(getString(R.string.cpu_usage_string, getString(R.string.cluster_little)));

            items.add(mCPUUsageLITTLE);

            CardView LITTLECoresCard = new CardView(getActivity());
            LITTLECoresCard.setTitle(getString(R.string.cores_string, getString(R.string.cluster_little)));

            final List<Integer> LITTLECores = mCPUFreq.getLITTLECpuRange();

            mCoresLITTLE.clear();
            for (final int core : LITTLECores) {
                SwitchView coreSwitch = new SwitchView();
                coreSwitch.setSummary(getString(R.string.core, core + 1));

                mCoresLITTLE.put(core, coreSwitch);
                LITTLECoresCard.addItem(coreSwitch);
            }

            CardView LITTLEFrequenciesCard = new CardView(getActivity());
            LITTLEFrequenciesCard.setTitle(getString(R.string.frequencies_string, getString(R.string.cluster_little)));

            mCPUMaxLITTLE = new SelectView();
            mCPUMaxLITTLE.setTitle(getString(R.string.cpu_max_freq));
            mCPUMaxLITTLE.setSummary(getString(R.string.cpu_max_freq_summary));
            mCPUMaxLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            mCPUMaxLITTLE.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mCPUFreq.setMaxFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                            LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity());
                }
            });
            LITTLEFrequenciesCard.addItem(mCPUMaxLITTLE);

            mCPUMinLITTLE = new SelectView();
            mCPUMinLITTLE.setTitle(getString(R.string.cpu_min_freq));
            mCPUMinLITTLE.setSummary(getString(R.string.cpu_min_freq_summary));
            mCPUMinLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            mCPUMinLITTLE.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mCPUFreq.setMinFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                            LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity());
                }
            });
            LITTLEFrequenciesCard.addItem(mCPUMinLITTLE);

            if (mCPUFreq.hasMaxScreenOffFreq(mCPUFreq.getLITTLECpu())) {
                mCPUMaxScreenOffLITTLE = new SelectView();
                mCPUMaxScreenOffLITTLE.setTitle(getString(R.string.cpu_max_screen_off_freq));
                mCPUMaxScreenOffLITTLE.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
                mCPUMaxScreenOffLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
                mCPUMaxScreenOffLITTLE.setOnItemSelected(new SelectView.OnItemSelected() {
                    @Override
                    public void onItemSelected(SelectView selectView, int position, String item) {
                        mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                                LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity());
                    }
                });
                LITTLEFrequenciesCard.addItem(mCPUMaxScreenOffLITTLE);
            }

            CardView LITTLEGovernorsCard = new CardView(getActivity());
            LITTLEGovernorsCard.setTitle(getString(R.string.governors_string, getString(R.string.cluster_little)));

            mCPUGovernorLITTLE = new SelectView();
            mCPUGovernorLITTLE.setTitle(getString(R.string.cpu_governor));
            mCPUGovernorLITTLE.setSummary(getString(R.string.cpu_governor_summary));
            mCPUGovernorLITTLE.setItems(mCPUFreq.getGovernors());
            mCPUGovernorLITTLE.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mCPUFreq.setGovernor(item, LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1),
                            getActivity());
                }
            });
            LITTLEGovernorsCard.addItem(mCPUGovernorLITTLE);

            DescriptionView governorTunablesLITTLE = new DescriptionView();
            governorTunablesLITTLE.setTitle(getString(R.string.cpu_governor_tunables));
            governorTunablesLITTLE.setSummary(getString(R.string.governor_tunables_summary));
            governorTunablesLITTLE.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    showGovernorTunables(LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1));
                }
            });
            LITTLEGovernorsCard.addItem(governorTunablesLITTLE);

            items.add(LITTLECoresCard);
            items.add(LITTLEFrequenciesCard);
            items.add(LITTLEGovernorsCard);
        }
    }

    private void showGovernorTunables(int min, int max) {
        boolean offline = mCPUFreq.isOffline(min);
        if (offline) {
            mCPUFreq.onlineCpu(min, true, false, null);
        }
        String governor = mCPUFreq.getGovernor(min, false);
        if (governor.isEmpty()) {
            mGovernorTunableErrorDialog = ViewUtils.dialogBuilder(getString(R.string.cpu_governor_tunables_read_error),
                    null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }, new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mGovernorTunableErrorDialog = null;
                        }
                    }, getActivity());
            mGovernorTunableErrorDialog.show();
        } else {
            setForegroundText(governor);
            mGovernorTunableFragment.setError(getString(R.string.tunables_error, governor));
            mGovernorTunableFragment.setPath(mCPUFreq.getGovernorTunablesPath(min, governor), min, max,
                    ApplyOnBootFragment.CPU);
            showForeground();
        }
        if (offline) {
            mCPUFreq.onlineCpu(min, false, false, null);
        }
    }

    private void mcPowerSavingInit(List<RecyclerViewItem> items) {
        SelectView mcPowerSaving = new SelectView();
        mcPowerSaving.setTitle(getString(R.string.mc_power_saving));
        mcPowerSaving.setSummary(getString(R.string.mc_power_saving_summary));
        mcPowerSaving.setItems(Arrays.asList(getResources().getStringArray(R.array.mc_power_saving_items)));
        mcPowerSaving.setItem(Misc.getCurMcPowerSaving());
        mcPowerSaving.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Misc.setMcPowerSaving(position, getActivity());
            }
        });

        items.add(mcPowerSaving);
    }

    private void powerSavingWqInit(List<RecyclerViewItem> items) {
        SwitchView powerSavingWq = new SwitchView();
        powerSavingWq.setSummary(getString(R.string.power_saving_wq));
        powerSavingWq.setChecked(Misc.isPowerSavingWqEnabled());
        powerSavingWq.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enablePowerSavingWq(isChecked, getActivity());
            }
        });

        items.add(powerSavingWq);
    }

    private void cfsSchedulerInit(List<RecyclerViewItem> items) {
        SelectView cfsScheduler = new SelectView();
        cfsScheduler.setTitle(getString(R.string.cfs_scheduler_policy));
        cfsScheduler.setSummary(getString(R.string.cfs_scheduler_policy_summary));
        cfsScheduler.setItems(Misc.getAvailableCFSSchedulers());
        cfsScheduler.setItem(Misc.getCurrentCFSScheduler());
        cfsScheduler.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Misc.setCFSScheduler(item, getActivity());
            }
        });

        items.add(cfsScheduler);
    }

    private void cpuQuietInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> views = new ArrayList<>();
        CardView cpuQuietCard = new CardView(getActivity());
        cpuQuietCard.setTitle(getString(R.string.cpu_quiet));

        if (Misc.hasCpuQuietEnable()) {
            SwitchView cpuQuietEnable = new SwitchView();
            cpuQuietEnable.setSummary(getString(R.string.cpu_quiet));
            cpuQuietEnable.setChecked(Misc.isCpuQuietEnabled());
            cpuQuietEnable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enableCpuQuiet(isChecked, getActivity());
                }
            });

            views.add(cpuQuietEnable);
        }

        if (Misc.hasCpuQuietGovernors()) {
            SelectView cpuQuietGovernors = new SelectView();
            cpuQuietGovernors.setSummary(getString(R.string.cpu_quiet_governor));
            cpuQuietGovernors.setItems(Misc.getCpuQuietAvailableGovernors());
            cpuQuietGovernors.setItem(Misc.getCpuQuietCurGovernor());
            cpuQuietGovernors.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    Misc.setCpuQuietGovernor(item, getActivity());
                }
            });

            views.add(cpuQuietGovernors);
        }

        if (views.size() > 0) {
            DescriptionView descriptionView = new DescriptionView();
            descriptionView.setSummary(getString(R.string.cpu_quiet_summary));
            cpuQuietCard.addItem(descriptionView);

            for (RecyclerViewItem item : views) {
                cpuQuietCard.addItem(item);
            }
            items.add(cpuQuietCard);
        }
    }

    private void cpuBoostInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> cpuBoost = new ArrayList<>();

        TitleView title = new TitleView();
        title.setText(getString(R.string.cpu_boost));

        if (mCPUBoost.hasEnable()) {
            SwitchView enable = new SwitchView();
            enable.setSummary(getString(R.string.cpu_boost));
            enable.setChecked(mCPUBoost.isEnabled());
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCPUBoost.enableCpuBoost(isChecked, getActivity());
                }
            });

            items.add(enable);
        }

        if (mCPUBoost.hasCpuBoostDebugMask()) {
            SwitchView debugMask = new SwitchView();
            debugMask.setTitle(getString(R.string.debug_mask));
            debugMask.setSummary(getString(R.string.debug_mask_summary));
            debugMask.setChecked(mCPUBoost.isCpuBoostDebugMaskEnabled());
            debugMask.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCPUBoost.enableCpuBoostDebugMask(isChecked, getActivity());
                }
            });

            cpuBoost.add(debugMask);
        }

        if (mCPUBoost.hasCpuBoostMs()) {
            SeekBarView ms = new SeekBarView();
            ms.setTitle(getString(R.string.interval));
            ms.setSummary(getString(R.string.interval_summary));
            ms.setUnit(getString(R.string.ms));
            ms.setMax(5000);
            ms.setOffset(10);
            ms.setProgress(mCPUBoost.getCpuBootMs() / 10);
            ms.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCPUBoost.setCpuBoostMs(position * 10, getActivity());
                }
            });

            cpuBoost.add(ms);
        }

        if (mCPUBoost.hasCpuBoostSyncThreshold() && mCPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(mCPUFreq.getAdjustedFreq(getActivity()));

            SelectView syncThreshold = new SelectView();
            syncThreshold.setTitle(getString(R.string.sync_threshold));
            syncThreshold.setSummary(getString(R.string.sync_threshold_summary));
            syncThreshold.setItems(list);
            syncThreshold.setItem(mCPUBoost.getCpuBootSyncThreshold());
            syncThreshold.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mCPUBoost.setCpuBoostSyncThreshold(position == 0 ? 0 : mCPUFreq.getFreqs().get(position - 1),
                            getActivity());
                }
            });

            cpuBoost.add(syncThreshold);
        }

        if (mCPUBoost.hasCpuBoostInputMs()) {
            SeekBarView inputMs = new SeekBarView();
            inputMs.setTitle(getString(R.string.input_interval));
            inputMs.setSummary(getString(R.string.input_interval_summary));
            inputMs.setUnit(getString(R.string.ms));
            inputMs.setMax(5000);
            inputMs.setOffset(10);
            inputMs.setProgress(mCPUBoost.getCpuBootInputMs() / 10);
            inputMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCPUBoost.setCpuBoostInputMs(position * 10, getActivity());
                }
            });

            cpuBoost.add(inputMs);
        }

        if (mCPUBoost.hasCpuBoostInputFreq()) {
            List<Integer> freqs = mCPUBoost.getCpuBootInputFreq();
            for (int i = 0; i < freqs.size(); i++) {
                List<String> list = new ArrayList<>();
                list.add(getString(R.string.disabled));
                list.addAll(mCPUFreq.getAdjustedFreq(i, getActivity()));

                SelectView inputCard = new SelectView();
                if (freqs.size() > 1) {
                    inputCard.setTitle(getString(R.string.input_boost_freq_core, i + 1));
                } else {
                    inputCard.setTitle(getString(R.string.input_boost_freq));
                }
                inputCard.setSummary(getString(R.string.input_boost_freq_summary));
                inputCard.setItems(list);
                inputCard.setItem(freqs.get(i));

                final int core = i;
                inputCard.setOnItemSelected(new SelectView.OnItemSelected() {
                    @Override
                    public void onItemSelected(SelectView selectView, int position, String item) {
                        mCPUBoost.setCpuBoostInputFreq(position == 0 ? 0
                                : mCPUFreq.getFreqs(core).get(position - 1), core, getActivity());
                    }
                });

                cpuBoost.add(inputCard);
            }
        }

        if (mCPUBoost.hasCpuBoostWakeup()) {
            SwitchView wakeup = new SwitchView();
            wakeup.setTitle(getString(R.string.wakeup_boost));
            wakeup.setSummary(getString(R.string.wakeup_boost_summary));
            wakeup.setChecked(mCPUBoost.isCpuBoostWakeupEnabled());
            wakeup.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCPUBoost.enableCpuBoostWakeup(isChecked, getActivity());
                }
            });

            cpuBoost.add(wakeup);
        }

        if (mCPUBoost.hasCpuBoostHotplug()) {
            SwitchView hotplug = new SwitchView();
            hotplug.setTitle(getString(R.string.hotplug_boost));
            hotplug.setSummary(getString(R.string.hotplug_boost_summary));
            hotplug.setChecked(mCPUBoost.isCpuBoostHotplugEnabled());
            hotplug.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCPUBoost.enableCpuBoostHotplug(isChecked, getActivity());
                }
            });

            cpuBoost.add(hotplug);
        }

        if (cpuBoost.size() > 0) {
            items.add(title);
            items.addAll(cpuBoost);
        }
    }

    private void cpuTouchBoostInit(List<RecyclerViewItem> items) {
        SwitchView touchBoost = new SwitchView();
        touchBoost.setTitle(getString(R.string.touch_boost));
        touchBoost.setSummary(getString(R.string.touch_boost_summary));
        touchBoost.setChecked(Misc.isCpuTouchBoostEnabled());
        touchBoost.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enableCpuTouchBoost(isChecked, getActivity());
            }
        });

        items.add(touchBoost);
    }

    private void cpuinputboostInit(List<RecyclerViewItem> items) {
        if (Misc.hascpuinputboost()) {
            SwitchView cpuinputboost = new SwitchView();
            cpuinputboost.setTitle(getString(R.string.cpu_input_boost));
            cpuinputboost.setSummary(getString(R.string.cpu_input_boost_summary));
            cpuinputboost.setChecked(Misc.iscpuinputboostEnabled());
            cpuinputboost.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enablecpuinputboost(isChecked, getActivity());
                }
            });

            items.add(cpuinputboost);
        }
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mRefreshThread == null) {
            mRefreshThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (mRefreshThread == null) break;
                        try {
                            mCPUUsages = mCPUFreq.getCpuUsage();

                            if (mCPUMaxBig != null) {
                                mCPUMaxFreqBig = mCPUFreq.getMaxFreq(mCPUMaxFreqBig == 0);
                            }
                            if (mCPUMinBig != null) {
                                mCPUMinFreqBig = mCPUFreq.getMinFreq(mCPUMinFreqBig == 0);
                            }
                            if (mCPUMaxScreenOffBig != null) {
                                mCPUMaxScreenOffFreqBig = mCPUFreq.getMaxScreenOffFreq(mCPUMaxScreenOffFreqBig == 0);
                            }
                            if (mCPUGovernorBig != null) {
                                mCPUGovernorStrBig = mCPUFreq.getGovernor(mCPUGovernorStrBig == null);
                            }
                            if (mCPUMaxLITTLE != null) {
                                mCPUMaxFreqLITTLE = mCPUFreq.getMaxFreq(mCPUFreq.getLITTLECpu(), mCPUMaxFreqLITTLE == 0);
                            }
                            if (mCPUMinLITTLE != null) {
                                mCPUMinFreqLITTLE = mCPUFreq.getMinFreq(mCPUFreq.getLITTLECpu(), mCPUMinFreqLITTLE == 0);
                            }
                            if (mCPUMaxScreenOffLITTLE != null) {
                                mCPUMaxScreenOffFreqLITTLE = mCPUFreq.getMaxScreenOffFreq(mCPUFreq.getLITTLECpu(),
                                        mCPUMaxScreenOffFreqLITTLE == 0);
                            }
                            if (mCPUGovernorLITTLE != null) {
                                mCPUGovernorStrLITTLE = mCPUFreq.getGovernor(mCPUFreq.getLITTLECpu(),
                                        mCPUGovernorStrLITTLE == null);
                            }

                        } catch (InterruptedException ignored) {
                            mRefreshThread = null;
                        }
                    }
                }
            });
            mRefreshThread.start();
        }

        if (mCPUUsages != null) {
            refreshUsages(mCPUUsages, mCPUUsageBig, mCPUFreq.getBigCpuRange());
            if (mCPUFreq.isBigLITTLE()) {
                refreshUsages(mCPUUsages, mCPUUsageLITTLE, mCPUFreq.getLITTLECpuRange());
            }
        }

        if (mCPUMaxBig != null && mCPUMaxFreqBig != 0) {
            mCPUMaxBig.setItem((mCPUMaxFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinBig != null && mCPUMinFreqBig != 0) {
            mCPUMinBig.setItem((mCPUMinFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxScreenOffBig != null && mCPUMaxScreenOffFreqBig != 0) {
            mCPUMaxScreenOffBig.setItem((mCPUMaxScreenOffFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUGovernorBig != null && mCPUGovernorStrBig != null && !mCPUGovernorStrBig.isEmpty()) {
            mCPUGovernorBig.setItem(mCPUGovernorStrBig);
        }
        if (mCPUMaxLITTLE != null && mCPUMaxFreqLITTLE != 0) {
            mCPUMaxLITTLE.setItem((mCPUMaxFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinLITTLE != null && mCPUMinFreqLITTLE != 0) {
            mCPUMinLITTLE.setItem((mCPUMinFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxScreenOffLITTLE != null && mCPUMaxScreenOffFreqLITTLE != 0) {
            mCPUMaxScreenOffLITTLE.setItem((mCPUMaxScreenOffFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUGovernorLITTLE != null && mCPUGovernorStrLITTLE != null && !mCPUGovernorStrLITTLE.isEmpty()) {
            mCPUGovernorLITTLE.setItem(mCPUGovernorStrLITTLE);
        }

        refreshCores(mCoresBig);
        if (mCPUFreq.isBigLITTLE()) {
            refreshCores(mCoresLITTLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRefreshThread != null) {
            mRefreshThread.interrupt();
            mRefreshThread = null;
        }
    }

    private void refreshUsages(float[] usages, XYGraphView graph, List<Integer> cores) {
        if (graph != null) {
            float average = 0;
            int size = 0;
            for (int core : cores) {
                if (core + 1 < usages.length) {
                    if (!mCPUFreq.isOffline(core)) {
                        average += usages[core + 1];
                    }
                    size++;
                }
            }
            average /= size;
            graph.setText(Math.round(average) + "%");
            graph.addPercentage(Math.round(average));
        }
    }

    private void refreshCores(SparseArray<SwitchView> array) {
        try {
            for (int i = 0; i < array.size(); i++) {
                SwitchView switchView = array.valueAt(i);
                if (switchView != null) {
                    final int core = array.keyAt(i);
                    int freq = mCPUFreq.getCurFreq(core);

                    String freqText = freq == 0 ? getString(R.string.offline) : (freq / 1000)
                            + getString(R.string.mhz);
                    switchView.clearOnSwitchListener();
                    switchView.setChecked(freq != 0);
                    switchView.setSummary(getString(R.string.core, core + 1) + " - " + freqText);
                    switchView.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                        @Override
                        public void onChanged(SwitchView switchView, boolean isChecked) {
                            if (core == 0) {
                                Utils.toast(R.string.no_offline_core, getActivity());
                            } else {
                                mCPUFreq.onlineCpu(core, isChecked, true, getActivity());
                            }
                        }
                    });
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }
}
