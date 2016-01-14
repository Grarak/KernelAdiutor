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
import android.util.Log;

import com.grarak.kerneladiutordonate.utils.Constants;
import com.grarak.kerneladiutordonate.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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

    public static int getCurFreq(int core) {
        if (Utils.existFile(String.format(Constants.CPU_CUR_FREQ, core))) {
            String value = Utils.readFile(String.format(Constants.CPU_CUR_FREQ, core));
            if (value != null) return Utils.strToInt(value);
        }
        return 0;
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

    public static float[] getCpuUsage() {
        try {
            Usage[] usage1 = getUsages();
            Thread.sleep(500);
            Usage[] usage2 = getUsages();

            if (usage1 != null && usage2 != null) {
                float[] pers = new float[usage1.length];
                for (int i = 0; i < usage1.length; i++) {
                    long idle1 = usage1[i].getIdle();
                    long up1 = usage1[i].getUptime();

                    long idle2 = usage2[i].getIdle();
                    long up2 = usage2[i].getUptime();

                    float cpu = -1f;
                    if (idle1 >= 0 && up1 >= 0 && idle2 >= 0 && up2 >= 0) {
                        if ((up2 + idle2) > (up1 + idle1) && up2 >= up1) {
                            cpu = (up2 - up1) / (float) ((up2 + idle2) - (up1 + idle1));
                            cpu *= 100.0f;
                        }
                    }

                    pers[i] = cpu > -1 ? cpu : 0;
                }
                return pers;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Usage[] getUsages() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            Usage[] usage = new Usage[getCoreCount() + 1];
            for (int i = 0; i < usage.length; i++)
                usage[i] = new Usage(reader.readLine());
            reader.close();
            return usage;
        } catch (FileNotFoundException e) {
            Log.i(Constants.TAG, "/proc/stat does not exist");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Usage {

        private long[] stats;

        public Usage(String stats) {
            if (stats == null) return;

            String[] values = stats.replace("  ", " ").split(" ");
            this.stats = new long[values.length - 1];
            for (int i = 0; i < this.stats.length; i++)
                this.stats[i] = Utils.strToLong(values[i + 1]);
        }

        public long getUptime() {
            if (stats == null) return -1L;
            long l = 0L;
            for (int i = 0; i < stats.length; i++)
                if (i != 3) l += stats[i];
            return l;
        }

        public long getIdle() {
            try {
                return stats == null ? -1L : stats[3];
            } catch (ArrayIndexOutOfBoundsException e) {
                return -1L;
            }
        }

    }

}
