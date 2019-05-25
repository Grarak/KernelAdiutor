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

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.recyclerview.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.wake.Dt2s;
import com.grarak.kerneladiutor.utils.kernel.wake.Dt2w;
import com.grarak.kerneladiutor.utils.kernel.wake.Gestures;
import com.grarak.kerneladiutor.utils.kernel.wake.Misc;
import com.grarak.kerneladiutor.utils.kernel.wake.S2s;
import com.grarak.kerneladiutor.utils.kernel.wake.S2w;
import com.grarak.kerneladiutor.utils.kernel.wake.T2w;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 23.06.16.
 */
public class WakeFragment extends RecyclerViewFragment {

    private Dt2w mDt2w;
    private S2w mS2w;
    private T2w mT2w;
    private Dt2s mDt2s;
    private S2s mS2s;
    private Misc mMisc;

    @Override
    protected void init() {
        super.init();

        mDt2w = Dt2w.getInstance();
        mS2w = S2w.getInstance();
        mT2w = T2w.getInstance();
        mDt2s = Dt2s.getInstance();
        mS2s = S2s.getInstance();
        mMisc = Misc.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (mDt2w.supported()) {
            dt2wInit(items);
        }
        s2wInit(items);
        if (mT2w.supported()) {
            t2wInit(items);
        }
        if (mDt2s.supported()) {
            dt2sInit(items);
        }
        if (mS2s.supported()) {
            s2sInit(items);
        }
        if (mMisc.hasWake()) {
            wakeMiscInit(items);
        }
        if (Gestures.supported()) {
            gestureInit(items);
        }
        if (mMisc.hasCamera()) {
            cameraInit(items);
        }
        if (mMisc.hasPocket()) {
            pocketInit(items);
        }
        if (mMisc.hasTimeout()) {
            timeoutInit(items);
        }
        if (mMisc.hasChargeTimeout()) {
            chargetimeoutInit(items);
        }
        if (mMisc.hasPowerKeySuspend()) {
            powerKeySuspendInit(items);
        }
        if (mMisc.hasKeyPowerMode()) {
            keyPowerModeInit(items);
        }
        if (mMisc.hasChargingMode()) {
            chargingModeInit(items);
        }
        areaInit(items);
        vibrationInit(items);
    }

    private void dt2wInit(List<RecyclerViewItem> items) {
        SelectView dt2w = new SelectView();
        dt2w.setTitle(getString(R.string.dt2w));
        dt2w.setSummary(getString(R.string.dt2w_summary));
        dt2w.setItems(mDt2w.getMenu(getActivity()));
        dt2w.setItem(mDt2w.get());
        dt2w.setOnItemSelected((selectView, position, item)
                -> mDt2w.set(position, getActivity()));

        items.add(dt2w);
    }

    private void s2wInit(List<RecyclerViewItem> items) {
        if (mS2w.supported()) {
            SelectView s2w = new SelectView();
            s2w.setTitle(getString(R.string.s2w));
            s2w.setSummary(getString(R.string.s2w_summary));
            s2w.setItems(mS2w.getMenu(getActivity()));
            s2w.setItem(mS2w.get());
            s2w.setOnItemSelected((selectView, position, item)
                    -> mS2w.set(position, getActivity()));

            items.add(s2w);
        }

        if (mS2w.hasLenient()) {
            SwitchView lenient = new SwitchView();
            lenient.setTitle(getString(R.string.lenient));
            lenient.setSummary(getString(R.string.lenient_summary));
            lenient.setChecked(mS2w.isLenientEnabled());
            lenient.addOnSwitchListener((switchView, isChecked)
                    -> mS2w.enableLenient(isChecked, getActivity()));

            items.add(lenient);
        }
    }

    private void t2wInit(List<RecyclerViewItem> items) {
        SelectView t2w = new SelectView();
        t2w.setTitle(getString(R.string.t2w));
        t2w.setSummary(getString(R.string.t2w_summary));
        t2w.setItems(mT2w.getMenu(getActivity()));
        t2w.setItem(mT2w.get());
        t2w.setOnItemSelected((selectView, position, item)
                -> mT2w.set(position, getActivity()));

        items.add(t2w);
    }

    private void dt2sInit(List<RecyclerViewItem> items) {
        SelectView dt2s = new SelectView();
        dt2s.setTitle(getString(R.string.dt2s));
        dt2s.setSummary(getString(R.string.dt2s_summary));
        dt2s.setItems(mDt2s.getMenu(getActivity()));
        dt2s.setItem(mDt2s.get());
        dt2s.setOnItemSelected((selectView, position, item)
                -> mDt2s.set(position, getActivity()));

        items.add(dt2s);
    }

    private void s2sInit(List<RecyclerViewItem> items) {
        SelectView s2s = new SelectView();
        s2s.setTitle(getString(R.string.s2s));
        s2s.setSummary(getString(R.string.s2s_summary));
        s2s.setItems(mS2s.getMenu(getActivity()));
        s2s.setItem(mS2s.get());
        s2s.setOnItemSelected((selectView, position, item)
                -> mS2s.set(position, getActivity()));

        items.add(s2s);
    }

    private void wakeMiscInit(List<RecyclerViewItem> items) {
        SelectView wake = new SelectView();
        wake.setSummary(getString(R.string.wake));
        wake.setItems(mMisc.getWakeMenu(getActivity()));
        wake.setItem(mMisc.getWake());
        wake.setOnItemSelected((selectView, position, item)
                -> mMisc.setWake(position, getActivity()));

        items.add(wake);
    }

    private void gestureInit(List<RecyclerViewItem> items) {
        List<String> gestures = Gestures.getMenu(getActivity());
        for (int i = 0; i < gestures.size(); i++) {
            SwitchView gesture = new SwitchView();
            gesture.setSummary(gestures.get(i));
            gesture.setChecked(Gestures.isEnabled(i));

            final int position = i;
            gesture.addOnSwitchListener((switchView, isChecked)
                    -> Gestures.enable(isChecked, position, getActivity()));

            items.add(gesture);
        }
    }

    private void cameraInit(List<RecyclerViewItem> items) {
        SwitchView camera = new SwitchView();
        camera.setTitle(getString(R.string.camera_gesture));
        camera.setSummary(getString(R.string.camera_gesture_summary));
        camera.setChecked(mMisc.isCameraEnabled());
        camera.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enableCamera(isChecked, getActivity()));

        items.add(camera);
    }

    private void pocketInit(List<RecyclerViewItem> items) {
        SwitchView pocket = new SwitchView();
        pocket.setTitle(getString(R.string.pocket_mode));
        pocket.setSummary(getString(R.string.pocket_mode_summary));
        pocket.setChecked(mMisc.isPocketEnabled());
        pocket.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enablePocket(isChecked, getActivity()));

        items.add(pocket);
    }

    private void timeoutInit(List<RecyclerViewItem> items) {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.disabled));
        for (int i = 1; i <= mMisc.getTimeoutMax(); i++)
            list.add(i + getString(R.string.min));

        SeekBarView timeout = new SeekBarView();
        timeout.setTitle(getString(R.string.timeout));
        timeout.setSummary(getString(R.string.timeout_summary));
        timeout.setItems(list);
        timeout.setProgress(mMisc.getTimeout());
        timeout.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mMisc.setTimeout(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(timeout);
    }

    private void chargetimeoutInit(List<RecyclerViewItem> items) {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.disabled));
        for (int i = 1; i <= 60; i++) {
            list.add(i + getString(R.string.min));
        }

        SeekBarView chargetimeout = new SeekBarView();
        chargetimeout.setTitle(getString(R.string.charge_timeout));
        chargetimeout.setSummary(getString(R.string.charge_timeout_summary));
        chargetimeout.setItems(list);
        chargetimeout.setProgress(mMisc.getChargeTimeout());
        chargetimeout.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mMisc.setChargeTimeout(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(chargetimeout);
    }

    private void powerKeySuspendInit(List<RecyclerViewItem> items) {
        SwitchView powerKeySuspend = new SwitchView();
        powerKeySuspend.setTitle(getString(R.string.power_key_suspend));
        powerKeySuspend.setSummary(getString(R.string.power_key_suspend_summary));
        powerKeySuspend.setChecked(mMisc.isPowerKeySuspendEnabled());
        powerKeySuspend.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enablePowerKeySuspend(isChecked, getActivity()));

        items.add(powerKeySuspend);
    }

    private void keyPowerModeInit(List<RecyclerViewItem> items) {
        SwitchView keyPowerMode = new SwitchView();
        keyPowerMode.setTitle(getString(R.string.key_power_mode));
        keyPowerMode.setSummary(getString(R.string.key_power_mode_summary));
        keyPowerMode.setChecked(mMisc.isKeyPowerModeEnabled());
        keyPowerMode.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enableKeyPowerMode(isChecked, getActivity()));

        items.add(keyPowerMode);
    }

    private void chargingModeInit(List<RecyclerViewItem> items) {
        SwitchView chargingMode = new SwitchView();
        chargingMode.setTitle(getString(R.string.charging_mode));
        chargingMode.setSummary(getString(R.string.charging_mode_summary));
        chargingMode.setChecked(mMisc.isChargingModeEnabled());
        chargingMode.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enableChargingMode(isChecked, getActivity()));

        items.add(chargingMode);
    }

    private void areaInit(List<RecyclerViewItem> items) {
        CardView areaCard = new CardView();
        areaCard.setTitle(getString(R.string.area));

        if (mDt2s.hasWidth()) {
            final int w = getResources().getDisplayMetrics().widthPixels;
            SeekBarView width = new SeekBarView();
            width.setTitle(getString(R.string.width));
            width.setUnit(getString(R.string.px));
            width.setMax(w);
            width.setMin(Math.round(w / 28.8f));
            width.setProgress(mDt2s.getWidth() - Math.round(w / 28.8f));
            width.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mDt2s.setWidth(position + Math.round(w / 28.8f), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            areaCard.addItem(width);
        }

        if (mDt2s.hasHeight()) {
            final int h = getResources().getDisplayMetrics().heightPixels;
            SeekBarView height = new SeekBarView();
            height.setTitle(getString(R.string.height));
            height.setUnit(getString(R.string.px));
            height.setMax(h);
            height.setMin(Math.round(h / 51.2f));
            height.setProgress(mDt2s.getHeight() - Math.round(h / 51.2f));
            height.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mDt2s.setHeight(position + Math.round(h / 51.2f), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            areaCard.addItem(height);
        }

        if (areaCard.size() > 0) {
            items.add(areaCard);
        }
    }

    private void vibrationInit(List<RecyclerViewItem> items) {
        if (mMisc.hasVibration()) {
            SwitchView vibration = new SwitchView();
            vibration.setSummary(getString(R.string.vibration));
            vibration.setChecked(mMisc.isVibrationEnabled());
            vibration.addOnSwitchListener((switchView, isChecked)
                    -> mMisc.enableVibration(isChecked, getActivity()));

            items.add(vibration);
        }

        if (mMisc.hasVibVibration()) {
            SeekBarView vibVibration = new SeekBarView();
            vibVibration.setTitle(getString(R.string.vibration_strength));
            vibVibration.setUnit("%");
            vibVibration.setMax(90);
            vibVibration.setProgress(mMisc.getVibVibration());
            vibVibration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMisc.setVibVibration(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(vibVibration);
        }
    }

}
