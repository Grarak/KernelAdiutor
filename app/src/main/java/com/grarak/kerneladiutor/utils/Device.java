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
package com.grarak.kerneladiutor.utils;

import android.os.Build;

import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by willi on 31.12.15.
 */
public class Device {

    public static class ROMInfo {

        private static String[] sProps = {
                "ro.cm.version",
                "ro.pa.version",
                "ro.pac.version",
                "ro.carbon.version",
                "ro.slim.version",
                "ro.mod.version",
        };

        private static String ROM_VERSION;

        public static String getVersion() {
            return ROM_VERSION;
        }

        public static void load() {
            for (String prop : sProps) {
                ROM_VERSION = RootUtils.getProp(prop);
                if (!ROM_VERSION.isEmpty()) {
                    break;
                }
            }
        }

    }

    public static class MemInfo {

        private static final String MEMINFO_PROC = "/proc/meminfo";

        private static String MEMINFO;

        public static long getTotalMem() {
            return getSize("MemTotal") / 1024L;
        }

        private static long getSize(String prefix) {
            try {
                for (String line : MEMINFO.split("\\r?\\n")) {
                    if (line.startsWith(prefix)) {
                        return Long.parseLong(line.split("\\s+")[1]);
                    }
                }
            } catch (Exception ignored) {
            }
            return -1;
        }

        public static void load() {
            MEMINFO = Utils.readFile(MEMINFO_PROC);
        }

    }

    public static class CPUInfo {

        private static final String CPUINFO_PROC = "/proc/cpuinfo";

        private static String CPUINFO;

        public static String getFeatures() {
            String features = getString("Features");
            if (!features.isEmpty()) return features;
            return getString("flags");
        }

        public static String getProcessor() {
            String pro = getString("Processor");
            if (!pro.isEmpty()) return pro;
            return getString("model name");
        }

        public static String getVendor() {
            String vendor = getString("Hardware");
            if (!vendor.isEmpty()) return vendor;
            return getString("vendor_id");
        }

        private static String getString(String prefix) {
            try {
                for (String line : CPUINFO.split("\\r?\\n")) {
                    if (line.startsWith(prefix)) {
                        return line.split(":")[1].trim();
                    }
                }
            } catch (Exception ignored) {
            }
            return "";
        }

        public static void load() {
            CPUINFO = Utils.readFile(CPUINFO_PROC);
        }

    }

    public static class TrustZone {
        private static HashMap<String, String> PARTITIONS = new HashMap<>();

        static {
            PARTITIONS.put("/dev/block/platform/msm_sdcc.1/by-name/tz", "QC_IMAGE_VERSION_STRING=");
            PARTITIONS.put("/dev/block/bootdevice/by-name/tz", "QC_IMAGE_VERSION_STRING=");
        }

        private static String PARTITION;
        private static String VERSION;

        public static String getVersion() {
            if (PARTITION == null) {
                supported();
                if (PARTITION == null) return "";
            }
            if (VERSION != null) return VERSION;
            String raw = RootUtils.runCommand("strings " + PARTITION + " | grep "
                    + PARTITIONS.get(PARTITION));
            for (String line : raw.split("\\r?\\n")) {
                if (line.startsWith(PARTITIONS.get(PARTITION))) {
                    return VERSION = line.replace(PARTITIONS.get(PARTITION), "");
                }
            }
            return "";
        }

        public static boolean supported() {
            if (PARTITION != null) return true;
            for (String partition : PARTITIONS.keySet()) {
                if (Utils.existFile(partition)) {
                    PARTITION = partition;
                    getVersion();
                    return true;
                }
            }
            return false;
        }
    }

    public static String getKernelVersion() {
        return RootUtils.runCommand("uname -r");
    }

    public static String getArchitecture() {
        return RootUtils.runCommand("uname -m");
    }

    public static String getHardware() {
        return Build.HARDWARE;
    }

    public static String getBootloader() {
        return Build.BOOTLOADER;
    }

    public static String getBaseBand() {
        return Build.getRadioVersion();
    }

    public static String getCodename() {
        String codeName = "";
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException ignored) {
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                codeName = fieldName;
                break;
            }
        }
        return codeName;
    }

    public static int getSDK() {
        return Build.VERSION.SDK_INT;
    }

    public static String getBoard() {
        return Build.BOARD;
    }

    public static String getBuildDisplayId() {
        return Build.DISPLAY;
    }

    public static String getFingerprint() {
        return Build.FINGERPRINT;
    }

    public static String getVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getVendor() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL;
    }

}