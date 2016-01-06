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

import android.os.Build;

import com.grarak.kerneladiutordonate.utils.root.RootUtils;

import java.lang.reflect.Field;

/**
 * Created by willi on 31.12.15.
 */
public class Device {

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
