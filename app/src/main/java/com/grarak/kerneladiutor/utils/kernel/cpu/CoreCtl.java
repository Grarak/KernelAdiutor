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
package com.grarak.kerneladiutor.utils.kernel.cpu;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 03.05.16.
 */
public class CoreCtl {

    public static final String PARENT = "/sys/devices/system/cpu/cpu%d/core_ctl";
    public static final String MIN_CPUS = PARENT + "/min_cpus";

    private static Boolean MIN_CPUS_SUPPORTED;

    public static void setMinCpus(int min, int cpu, Context context) {
        setMinCpus(min, cpu, ApplyOnBootFragment.CPU, context);
    }

    public static void setMinCpus(int min, int cpu, String category, Context context) {
        Control.runSetting(Control.write(String.valueOf(min), Utils.strFormat(MIN_CPUS, cpu)), category,
                Utils.strFormat(MIN_CPUS, cpu), context);
    }

    public static boolean hasMinCpus() {
        if (MIN_CPUS_SUPPORTED != null) return MIN_CPUS_SUPPORTED;
        return MIN_CPUS_SUPPORTED = Utils.existFile(Utils.strFormat(MIN_CPUS, 0));
    }

    public static boolean supported() {
        return hasMinCpus();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
