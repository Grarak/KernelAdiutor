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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.bvalosek.cpuspy.CpuSpyApp;
import com.bvalosek.cpuspy.CpuStateMonitor;
import com.grarak.kerneladiutordonate.R;
import com.grarak.kerneladiutordonate.elements.recyclerview.Adapter;
import com.grarak.kerneladiutordonate.elements.recyclerview.MainCard;
import com.grarak.kerneladiutordonate.elements.recyclerview.Text;
import com.grarak.kerneladiutordonate.elements.views.BarGraph;
import com.grarak.kerneladiutordonate.fragments.BaseFragment;
import com.grarak.kerneladiutordonate.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutordonate.utils.Constants;
import com.grarak.kerneladiutordonate.utils.kernel.CPU;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 04.01.16.
 */
public class FrequencyTable extends RecyclerViewFragment.ViewInterface {

    private static final String UPDATEDATA_INTENT = "updatedata";

    private static List<Adapter.RecyclerItem> frequencyCards;
    private static FrequencyTable frequencyTable;

    private CpuSpyApp cpuSpyApp;
    private CpuSpyApp cpuSpyAppLITTLE;

    /**
     * whether or not we're updating the data in the background
     */
    private boolean updateData;

    public FrequencyTable(Context context) {
        super(context);
        if (frequencyCards != null) return;
        frequencyTable = this;

        frequencyCards = new ArrayList<>();

        MainCard frequencyCard = new MainCard(getContext());
        if (CPU.isBigLITTLE())
            frequencyCard.setTitle(getString(R.string.big));

        frequencyCards.add(frequencyCard);
        if (CPU.isBigLITTLE()) {
            MainCard frequencyCardLITTLE = new MainCard(getContext());
            frequencyCardLITTLE.setTitle(getString(R.string.little));
            frequencyCards.add(frequencyCardLITTLE);
        }
    }

    @Override
    public List<Adapter.RecyclerItem> getViews(Bundle savedInstanceState) {
        cpuSpyApp = new CpuSpyApp(CPU.getBigCore());
        if (frequencyCards.size() > 1)
            cpuSpyAppLITTLE = new CpuSpyApp(CPU.getLITTLEcore());

        if (savedInstanceState != null)
            updateData = savedInstanceState.getBoolean(UPDATEDATA_INTENT);

        refreshData();

        return frequencyCards;
    }

    @Override
    public List<BaseFragment> getViewPagerFragments(Bundle savedInstanceState) {
        List<BaseFragment> views = new ArrayList<>();
        views.add(new ButtonsFragment());
        return views;
    }

    /**
     * Generate and update all UI elements
     */
    private void updateView(CpuStateMonitor monitor, MainCard frequencyCard) {
        frequencyCard.clearItems();

        // update the total state time
        final long totTime = monitor.getTotalStateTime() / 100;
        frequencyCard.addItem(new Text(new Text.TextOptions() {
            @Override
            public CharSequence getTitle(Text text) {
                return getString(R.string.uptime);
            }

            @Override
            public CharSequence getSummary(Text text) {
                return sToString(totTime);
            }
        }));

        /** Get the CpuStateMonitor from the app, and iterate over all states,
         * creating a row if the duration is > 0 or otherwise marking it in
         * extraStates (missing) */
        List<String> extraStates = new ArrayList<>();
        for (CpuStateMonitor.CpuState state : monitor.getStates()) {
            if (state.duration > 0) {
                generateStateRow(monitor, state, frequencyCard);
            } else {
                if (state.freq == 0) {
                    extraStates.add(getString(R.string.deep_sleep));
                } else {
                    extraStates.add(state.freq / 1000 + getString(R.string.mhz));
                }
            }
        }

        if (monitor.getStates().size() == 0) {
            frequencyCard.clearItems();
            frequencyCard.addItem(new Text(new Text.TextOptions() {
                @Override
                public CharSequence getTitle(Text text) {
                    return null;
                }

                @Override
                public CharSequence getSummary(Text text) {
                    return getString(R.string.error_frequency_table);
                }
            }));
            return;
        }

        // for all the 0 duration states, add the the Unused State area
        if (extraStates.size() > 0) {
            int n = 0;
            String str = "";

            for (String s : extraStates) {
                if (n++ > 0)
                    str += ", ";
                str += s;
            }

            final String states = str;
            frequencyCard.addItem(new Text(new Text.TextOptions() {
                @Override
                public CharSequence getTitle(Text text) {
                    return getString(R.string.unused_frequencies);
                }

                @Override
                public CharSequence getSummary(Text text) {
                    return states;
                }
            }));
        }
    }

    /**
     * Attempt to update the time-in-state info
     */
    private void refreshData() {
        if (!updateData) {
            new RefreshStateDataTask().execute();
        }
    }

    /**
     * @return A nicely formatted String representing tSec seconds
     */
    private static String sToString(long tSec) {
        long h = (long) Math.floor(tSec / (60 * 60));
        long m = (long) Math.floor((tSec - h * 60 * 60) / 60);
        long s = tSec % 60;
        String sDur;
        sDur = h + ":";
        if (m < 10)
            sDur += "0";
        sDur += m + ":";
        if (s < 10)
            sDur += "0";
        sDur += s;

        return sDur;
    }

    /**
     * Creates a View that correpsonds to a CPU freq state row as specified
     * by the state parameter
     */
    private void generateStateRow(CpuStateMonitor monitor, CpuStateMonitor.CpuState state,
                                  MainCard frequencyCard) {
        // what percentage we've got
        float per = (float) state.duration * 100 / monitor.getTotalStateTime();

        String sFreq;
        if (state.freq == 0) {
            sFreq = getString(R.string.deep_sleep);
        } else {
            sFreq = state.freq / 1000 + getString(R.string.mhz);
        }

        // duration
        long tSec = state.duration / 100;
        String sDur = sToString(tSec);

        FrequencyState frequencyState = new FrequencyState();
        frequencyState.setFreq(sFreq);
        frequencyState.setPercentage((int) per);
        frequencyState.setDuration(sDur);

        frequencyCard.addItem(frequencyState);
    }

    private class RefreshStateDataTask extends AsyncTask<Void, Void, Void> {

        private CpuStateMonitor monitor;
        private CpuStateMonitor monitorLITTLE;

        @Override
        protected Void doInBackground(Void... params) {
            monitor = cpuSpyApp.getCpuStateMonitor();
            if (cpuSpyAppLITTLE != null)
                monitorLITTLE = cpuSpyAppLITTLE.getCpuStateMonitor();
            try {
                monitor.updateStates();
                if (monitorLITTLE != null)
                    monitorLITTLE.updateStates();
            } catch (CpuStateMonitor.CpuStateMonitorException e) {
                Log.e(Constants.TAG, "Problem getting CPU states");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            updateData = true;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateData = false;
            updateView(monitor, (MainCard) frequencyCards.get(0));
            if (monitorLITTLE != null)
                updateView(monitorLITTLE, (MainCard) frequencyCards.get(1));
        }
    }

    private class FrequencyState implements Adapter.RecyclerItem {

        private BarGraph mBarGraphView;
        private TextView mFreqView;
        private TextView mPercentageView;
        private TextView mDurationView;

        private int mPercentage = -1;
        private String mFreq;
        private String mDuration;

        @Override
        public void onBindView(View view) {
            mBarGraphView = (BarGraph) view.findViewById(R.id.frequency_bargraph);
            mFreqView = (TextView) view.findViewById(R.id.frequency_freq);
            mPercentageView = (TextView) view.findViewById(R.id.frequency_percentage);
            mDurationView = (TextView) view.findViewById(R.id.frequency_duration);

            refresh();
        }

        @Override
        public View getView(LayoutInflater layoutInflater, ViewGroup parent) {
            return layoutInflater.inflate(R.layout.views_frequency_state, parent, false);
        }

        public void setPercentage(int percentage) {
            mPercentage = percentage;
            refresh();
        }

        public void setFreq(String freq) {
            mFreq = freq;
            refresh();
        }

        public void setDuration(String duration) {
            mDuration = duration;
            refresh();
        }

        @Override
        public void refresh() {
            if (mBarGraphView != null) mBarGraphView.setBarPercentage(mPercentage);
            if (mPercentageView != null)
                mPercentageView.setText(String.format("%d%%", mPercentage));

            if (mFreq != null && mFreqView != null) mFreqView.setText((mFreq));
            if (mDuration != null && mDurationView != null) mDurationView.setText(mDuration);
        }
    }

    public static class ButtonsFragment extends BaseFragment {

        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);
            setContentView(R.layout.frequency_buttons);

            FloatingActionButton refreshButton = (FloatingActionButton) findViewById(R.id.frequency_refresh);
            FloatingActionButton resetButton = (FloatingActionButton) findViewById(R.id.frequency_reset);
            FloatingActionButton restoreButton = (FloatingActionButton) findViewById(R.id.frequency_restore);

            Bitmap refreshImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_refresh);
            refreshButton.setImageBitmap(refreshImage);

            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.preScale(-1.0f, 1.0f);
            Bitmap resetImage = Bitmap.createBitmap(refreshImage, 0, 0, refreshImage.getWidth(),
                    refreshImage.getHeight(), matrix, true);
            resetButton.setImageBitmap(resetImage);

            restoreButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_restore));

            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rotate(v, false);
                    if (frequencyTable.cpuSpyApp == null) return;
                    frequencyTable.refreshData();
                }
            });
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rotate(v, true);
                    if (frequencyTable.cpuSpyApp == null) return;
                    CpuStateMonitor cpuStateMonitor = frequencyTable.cpuSpyApp.getCpuStateMonitor();
                    CpuStateMonitor cpuStateMonitorLITTLE = null;
                    if (frequencyTable.cpuSpyAppLITTLE != null)
                        cpuStateMonitorLITTLE = frequencyTable.cpuSpyAppLITTLE.getCpuStateMonitor();
                    try {
                        cpuStateMonitor.setOffsets();
                        if (cpuStateMonitorLITTLE != null)
                            cpuStateMonitorLITTLE.setOffsets();
                    } catch (CpuStateMonitor.CpuStateMonitorException ignored) {
                    }
                    frequencyTable.cpuSpyApp.saveOffsets(getActivity());
                    if (frequencyTable.cpuSpyAppLITTLE != null)
                        frequencyTable.cpuSpyAppLITTLE.saveOffsets(getActivity());
                    frequencyTable.updateView(cpuStateMonitor,
                            (MainCard) FrequencyTable.frequencyCards.get(0));
                    if (cpuStateMonitorLITTLE != null)
                        frequencyTable.updateView(cpuStateMonitorLITTLE,
                                (MainCard) FrequencyTable.frequencyCards.get(1));
                }
            });
            restoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rotate(v, true);
                    if (frequencyTable.cpuSpyApp == null) return;
                    CpuStateMonitor cpuStateMonitor = frequencyTable.cpuSpyApp.getCpuStateMonitor();
                    CpuStateMonitor cpuStateMonitorLITTLE = null;
                    if (frequencyTable.cpuSpyAppLITTLE != null)
                        cpuStateMonitorLITTLE = frequencyTable.cpuSpyAppLITTLE.getCpuStateMonitor();
                    cpuStateMonitor.removeOffsets();
                    if (cpuStateMonitorLITTLE != null)
                        cpuStateMonitorLITTLE.removeOffsets();
                    frequencyTable.cpuSpyApp.saveOffsets(getActivity());
                    if (frequencyTable.cpuSpyAppLITTLE != null)
                        frequencyTable.cpuSpyAppLITTLE.saveOffsets(getActivity());
                    frequencyTable.updateView(cpuStateMonitor,
                            (MainCard) FrequencyTable.frequencyCards.get(0));
                    if (frequencyTable.cpuSpyAppLITTLE != null)
                        frequencyTable.updateView(cpuStateMonitorLITTLE,
                                (MainCard) FrequencyTable.frequencyCards.get(1));
                }
            });
        }

        private void rotate(View v, boolean reverse) {
            v.animate().rotationBy(reverse ? -360 : 360).setDuration(500).setInterpolator(new LinearInterpolator()).start();
        }

        @Override
        public boolean retainInstance() {
            return false;
        }
    }

}
