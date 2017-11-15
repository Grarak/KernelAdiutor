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
package com.grarak.kerneladiutor.utils.kernel.battery;

import android.content.Context;
import android.support.annotation.NonNull;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.06.16.
 */
public class Battery {

    private static Battery sInstance;

    public static Battery getInstance(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new Battery(context);
        }
        return sInstance;
    }

    private static final String FORCE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
    private static final String BLX = "/sys/devices/virtual/misc/batterylifeextender/charging_limit";

    private static final String CHARGE_RATE = "/sys/kernel/thundercharge_control";
    private static final String CHARGE_RATE_ENABLE = CHARGE_RATE + "/enabled";
    private static final String CUSTOM_CURRENT = CHARGE_RATE + "/custom_current";

    private static final String QUICK_CHARGE = "/sys/kernel/Quick_Charge";
    private static final String QC_ENABLE = QUICK_CHARGE + "/QC_Toggle";
    private static final String CURRENT_NOW = "/sys/class/power_supply/battery/current_now";
    private static final String QC_CURRENT = QUICK_CHARGE + "/custom_current";
    private static final String CHARGE_PROFILE = QUICK_CHARGE + "/Charging_Profile";

    private int mCapacity;

    private Battery(Context context) {
        if (mCapacity == 0) {
            try {
                Class<?> powerProfile = Class.forName("com.android.internal.os.PowerProfile");
                Constructor constructor = powerProfile.getDeclaredConstructor(Context.class);
                Object powerProInstance = constructor.newInstance(context);
                Method batteryCap = powerProfile.getMethod("getBatteryCapacity");
                mCapacity = Math.round((long) (double) batteryCap.invoke(powerProInstance));
            } catch (Exception e) {
                e.printStackTrace();
                mCapacity = 0;
            }
        }
    }

    public void setChargingCurrent(int value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_CURRENT), CUSTOM_CURRENT, context);
    }

    public int getChargingCurrent() {
        return Utils.strToInt(Utils.readFile(CUSTOM_CURRENT));
    }

    public boolean hasChargingCurrent() {
        return Utils.existFile(CUSTOM_CURRENT);
    }

    public void enableChargeRate(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CHARGE_RATE_ENABLE), CHARGE_RATE_ENABLE, context);
    }

    public boolean isChargeRateEnabled() {
        return Utils.readFile(CHARGE_RATE_ENABLE).equals("1");
    }

    public boolean hasChargeRateEnable() {
        return Utils.existFile(CHARGE_RATE_ENABLE);
    }

    public int getCurrentNow() {
        return Utils.strToInt(Utils.readFile(CURRENT_NOW));
    }

    public void setQCCurrent(int value, Context context) {
        run(Control.write(String.valueOf(value), QC_CURRENT), QC_CURRENT, context);
    }

    public int getQCCurrent() {
       return Utils.strToInt(Utils.readFile(QC_CURRENT));
    }

    public boolean hasQCCurrent() {
        return Utils.existFile(QC_CURRENT);
    }

    public void enableQuickCharge(boolean enable, Context context) {
         run(Control.write(enable ? "1" : "0", QC_ENABLE), QC_ENABLE, context);
    }

    public boolean isQuickChargeEnabled() {
         return Utils.readFile(QC_ENABLE).equals("1");
    }

    public boolean hasQuickChargeEnable() {
         return Utils.existFile(QC_ENABLE);
    }

    public boolean hasChargeProfile() {
        return Utils.existFile(CHARGE_PROFILE);
    }

    public int getProfiles() {
        String file = CHARGE_PROFILE;
        return Utils.strToInt(Utils.readFile(file));
    }

    public List<String> getProfilesMenu(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.slowcharge));
        list.add(context.getString(R.string.balancedcharge));
        list.add(context.getString(R.string.thundercharge));
        return list;
    }

    public void setchargeProfile(int value, Context context) {
        String file = CHARGE_PROFILE;
        run(Control.write(String.valueOf(value), file), file, context);
   }

    public void setBlx(int value, Context context) {
        run(Control.write(String.valueOf(value == 0 ? 101 : value - 1), BLX), BLX, context);
    }

    public int getBlx() {
        int value = Utils.strToInt(Utils.readFile(BLX));
        return value > 100 ? 0 : value + 1;
    }

    public boolean hasBlx() {
        return Utils.existFile(BLX);
    }

    public void enableForceFastCharge(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", FORCE_FAST_CHARGE), FORCE_FAST_CHARGE, context);
    }

    public boolean isForceFastChargeEnabled() {
        return Utils.readFile(FORCE_FAST_CHARGE).equals("1");
    }

    public boolean hasForceFastCharge() {
        return Utils.existFile(FORCE_FAST_CHARGE);
    }

    public int getCapacity() {
        return mCapacity;
    }

    public boolean hasCapacity() {
        return getCapacity() != 0;
    }

    public boolean supported() {
        return hasCapacity();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.BATTERY, id, context);
    }

}
