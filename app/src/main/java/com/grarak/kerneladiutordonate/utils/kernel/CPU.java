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
package com.grarak.kerneladiutordonate.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutordonate.utils.Constants;
import com.grarak.kerneladiutordonate.utils.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by willi on 04.01.16.
 */
public class CPU {

    private static int cores;
    private static int bigCore = -1;
    private static int LITTLEcore = -1;
    private static Integer[][] mFreqs;

    public static List<Integer> getFreqs(int core) {
        if (mFreqs == null) mFreqs = new Integer[getCoreCount()][];
        if (mFreqs[core] == null)
            if (Utils.existFile(String.format(Constants.CPU_TIME_STATE, core))
                    || Utils.existFile(String.format(Constants.CPU_TIME_STATE_2, 0))) {
                String file;
                if (Utils.existFile(String.format(Constants.CPU_TIME_STATE, core))) {
                    file = String.format(Constants.CPU_TIME_STATE, core);
                } else {
                    if (core > 0) {
                        activateCore(core, true, null);
                        file = String.format(Constants.CPU_TIME_STATE_2, core);
                    } else file = String.format(Constants.CPU_TIME_STATE_2, 0);
                }
                String values;
                if ((values = Utils.readFile(file)) != null) {
                    String[] valueArray = values.split("\\r?\\n");
                    mFreqs[core] = new Integer[valueArray.length];
                    for (int i = 0; i < mFreqs[core].length; i++)
                        mFreqs[core][i] = Utils.strToInt(valueArray[i].split(" ")[0]);
                }
            } else if (Utils.existFile(String.format(Constants.CPU_AVAILABLE_FREQS, 0))) {
                if (core > 0) {
                    while (!Utils.existFile(String.format(Constants.CPU_AVAILABLE_FREQS, core)))
                        activateCore(core, true, null);
                }
                String values;
                if ((values = Utils.readFile(String.format(Constants.CPU_AVAILABLE_FREQS, core))) != null) {
                    String[] valueArray = values.split(" ");
                    mFreqs[core] = new Integer[valueArray.length];
                    for (int i = 0; i < mFreqs[core].length; i++)
                        mFreqs[core][i] = Utils.strToInt(valueArray[i]);
                }
            }
        if (mFreqs[core] == null) return null;
        List<Integer> freqs = Arrays.asList(mFreqs[core]);
        Collections.sort(freqs);
        return freqs;
    }

    public static void activateCore(int core, boolean active, Context context) {

    }

    public static int getLITTLEcore() {
        isBigLITTLE();
        return LITTLEcore == -1 ? 0 : LITTLEcore;
    }

    public static int getBigCore() {
        isBigLITTLE();
        return bigCore == -1 ? 0 : bigCore;
    }

    public static boolean isBigLITTLE() {
        boolean bigLITTLE = getCoreCount() > 4;
        if (!bigLITTLE) return false;

        if (bigCore == -1 || LITTLEcore == -1) {
            List<Integer> cpu0Freqs = getFreqs(0);
            List<Integer> cpu4Freqs = getFreqs(4);
            if (cpu0Freqs != null && cpu4Freqs != null) {
                if (cpu0Freqs.size() > cpu4Freqs.size()) {
                    bigCore = 0;
                    LITTLEcore = 4;
                } else {
                    bigCore = 4;
                    LITTLEcore = 0;
                }
            }
        }

        return bigCore != -1 && LITTLEcore != -1;
    }

    public static int getCoreCount() {
        return cores == 0 ? cores = Runtime.getRuntime().availableProcessors() : cores;
    }

}
