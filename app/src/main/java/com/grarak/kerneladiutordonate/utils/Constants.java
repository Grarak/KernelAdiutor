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
package com.grarak.kerneladiutordonate.utils;

import com.grarak.kerneladiutordonate.elements.views.CustomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 28.12.15.
 */
public class Constants {

    public static final String TAG = "Kernel Adiutor";
    public static final String PREF_NAME = "prefs";

    public static final List<CustomNavigationView.NavigationViewItem> ITEMS = new ArrayList<>();

    // CPU
    public static final String CPU_CUR_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq";
    public static final String CPU_AVAILABLE_FREQS = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_available_frequencies";
    public static final String CPU_TIME_STATE = "/sys/devices/system/cpu/cpufreq/stats/cpu%d/time_in_state";
    public static final String CPU_TIME_STATE_2 = "/sys/devices/system/cpu/cpu%d/cpufreq/stats/time_in_state";

}
