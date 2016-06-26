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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseControlFragment;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.utils.kernel.battery.Battery;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.List;

/**
 * Created by willi on 26.06.16.
 */
public class BatteryFragment extends BaseControlFragment {

    private DescriptionFragment mLevel;

    private DescriptionView mVoltage;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(mLevel = DescriptionFragment.newInstance(getString(R.string.level), null));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        voltageInit(items);
        if (Battery.hasCapacity(getActivity())) {
            capacityInit(items);
        }
        if (Battery.hasForceFastCharge()) {
            forceFastChargeInit(items);
        }
        if (Battery.hasBlx()) {
            blxInit(items);
        }
        chargeRateInit(items);
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (itemsSize() > 2) {
            addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.BATTERY));
        }
    }

    private void voltageInit(List<RecyclerViewItem> items) {
        mVoltage = new DescriptionView();
        mVoltage.setTitle(getString(R.string.voltage));

        items.add(mVoltage);
    }

    private void capacityInit(List<RecyclerViewItem> items) {
        DescriptionView capacity = new DescriptionView();
        capacity.setTitle(getString(R.string.capacity));
        capacity.setSummary(Battery.getCapacity(getActivity()) + getString(R.string.mah));

        items.add(capacity);
    }

    private void forceFastChargeInit(List<RecyclerViewItem> items) {
        SwitchView forceFastCharge = new SwitchView();
        forceFastCharge.setTitle(getString(R.string.usb_fast_charge));
        forceFastCharge.setSummary(getString(R.string.usb_fast_charge_summary));
        forceFastCharge.setChecked(Battery.isForceFastChargeEnabled());
        forceFastCharge.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Battery.enableForceFastCharge(isChecked, getActivity());
            }
        });

        items.add(forceFastCharge);
    }

    private void blxInit(List<RecyclerViewItem> items) {
        SeekBarView blx = new SeekBarView();
        blx.setTitle(getString(R.string.blx));
        blx.setSummary(getString(R.string.blx_summary));
        blx.setProgress(Battery.getBlx());
        blx.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Battery.setBlx(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(blx);
    }

    private void chargeRateInit(List<RecyclerViewItem> items) {
        CardView chargeRateCard = new CardView(getActivity());
        chargeRateCard.setTitle(getString(R.string.charge_rate));

        if (Battery.hasChargeRateEnable()) {
            SwitchView chargeRate = new SwitchView();
            chargeRate.setSummary(getString(R.string.charge_rate));
            chargeRate.setChecked(Battery.isChargeRateEnabled());
            chargeRate.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Battery.enableChargeRate(isChecked, getActivity());
                }
            });

            chargeRateCard.addItem(chargeRate);
        }

        if (Battery.hasChargingCurrent()) {
            SeekBarView chargingCurrent = new SeekBarView();
            chargingCurrent.setTitle(getString(R.string.charging_current));
            chargingCurrent.setSummary(getString(R.string.charging_current_summary));
            chargingCurrent.setUnit(getString(R.string.ma));
            chargingCurrent.setMax(1500);
            chargingCurrent.setMin(100);
            chargingCurrent.setOffset(10);
            chargingCurrent.setProgress(Battery.getChargingCurrent() / 10 - 10);
            chargingCurrent.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setChargingCurrent((position + 10) * 100, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            chargeRateCard.addItem(chargingCurrent);
        }

        if (chargeRateCard.size() > 0) {
            items.add(chargeRateCard);
        }
    }

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            if (mLevel != null) {
                mLevel.setSummary(level + "%");
            }
            if (mVoltage != null) {
                mVoltage.setSummary(voltage + getString(R.string.mv));
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(mBatteryReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

}
