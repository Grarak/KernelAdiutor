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
package com.grarak.kerneladiutor.utils.kernel.screen;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 01.06.16.
 */
public class Calibration {

    private static final String SCREEN_KCAL = "/sys/devices/platform/kcal_ctrl.0";
    private static final String SCREEN_KCAL_CTRL = SCREEN_KCAL + "/kcal";
    private static final String SCREEN_KCAL_CTRL_CTRL = SCREEN_KCAL + "/kcal_ctrl";
    private static final String SCREEN_KCAL_CTRL_ENABLE = SCREEN_KCAL + "/kcal_enable";
    private static final String SCREEN_KCAL_CTRL_MIN = SCREEN_KCAL + "/kcal_min";
    private static final String SCREEN_KCAL_CTRL_INVERT = SCREEN_KCAL + "/kcal_invert";
    private static final String SCREEN_KCAL_CTRL_SAT = SCREEN_KCAL + "/kcal_sat";
    private static final String SCREEN_KCAL_CTRL_HUE = SCREEN_KCAL + "/kcal_hue";
    private static final String SCREEN_KCAL_CTRL_VAL = SCREEN_KCAL + "/kcal_val";
    private static final String SCREEN_KCAL_CTRL_CONT = SCREEN_KCAL + "/kcal_cont";

    private static final String SCREEN_DIAG0 = "/sys/devices/platform/DIAG0.0";
    private static final String SCREEN_DIAG0_POWER = SCREEN_DIAG0 + "/power_rail";
    private static final String SCREEN_DIAG0_POWER_CTRL = SCREEN_DIAG0 + "/power_rail_ctrl";

    private static final String SCREEN_COLOR = "/sys/class/misc/colorcontrol";
    private static final String SCREEN_COLOR_CONTROL = SCREEN_COLOR + "/multiplier";
    private static final String SCREEN_COLOR_CONTROL_CTRL = SCREEN_COLOR + "/safety_enabled";

    private static final String SCREEN_SAMOLED_COLOR = "/sys/class/misc/samoled_color";
    private static final String SCREEN_SAMOLED_COLOR_RED = SCREEN_SAMOLED_COLOR + "/red_multiplier";
    private static final String SCREEN_SAMOLED_COLOR_GREEN = SCREEN_SAMOLED_COLOR + "/green_multiplier";
    private static final String SCREEN_SAMOLED_COLOR_BLUE = SCREEN_SAMOLED_COLOR + "/blue_multiplier";

    private static final String SCREEN_FB0_RGB = "/sys/class/graphics/fb0/rgb";
    private static final String SCREEN_FB_KCAL = "/sys/class/graphics/fb0/kcal";

    private static final String SCREEN_HBM = "/sys/class/graphics/fb0/hbm";

    private static final List<String> sColors = new ArrayList<>();
    private static final List<String> sColorEnables = new ArrayList<>();
    private static final List<String> sNewKCAL = new ArrayList<>();

    static {
        sColors.add(SCREEN_KCAL_CTRL);
        sColors.add(SCREEN_DIAG0_POWER);
        sColors.add(SCREEN_COLOR_CONTROL);
        sColors.add(SCREEN_SAMOLED_COLOR);
        sColors.add(SCREEN_FB0_RGB);
        sColors.add(SCREEN_FB_KCAL);

        sColorEnables.add(SCREEN_KCAL_CTRL_CTRL);
        sColorEnables.add(SCREEN_KCAL_CTRL_ENABLE);
        sColorEnables.add(SCREEN_DIAG0_POWER_CTRL);
        sColorEnables.add(SCREEN_COLOR_CONTROL_CTRL);

        sNewKCAL.add(SCREEN_KCAL_CTRL_ENABLE);
        sNewKCAL.add(SCREEN_KCAL_CTRL_INVERT);
        sNewKCAL.add(SCREEN_KCAL_CTRL_SAT);
        sNewKCAL.add(SCREEN_KCAL_CTRL_HUE);
        sNewKCAL.add(SCREEN_KCAL_CTRL_VAL);
        sNewKCAL.add(SCREEN_KCAL_CTRL_CONT);
    }

    private static String COLOR;
    private static String COLOR_ENABLE;

    private static boolean HBM_NEW;

    public static void enableScreenHBM(boolean enable, Context context) {
        run(Control.write(enable ? HBM_NEW ? "2" : "1" : "0", SCREEN_HBM), SCREEN_HBM, context);
    }

    public static boolean isScreenHBMEnabled() {
        if (HBM_NEW) {
            return Utils.readFile(SCREEN_HBM).contains("= 2");
        }
        return Utils.readFile(SCREEN_HBM).equals("1");
    }

    public static boolean hasScreenHBM() {
        boolean supported = Utils.existFile(SCREEN_HBM);
        if (supported) {
            HBM_NEW = Utils.readFile(SCREEN_HBM).contains("2-->HBM Enabled");
            return true;
        }
        return false;
    }

    public static void setScreenContrast(int value, Context context) {
        run(Control.write(String.valueOf(value), SCREEN_KCAL_CTRL_CONT), SCREEN_KCAL_CTRL_CONT, context);
    }

    public static int getScreenContrast() {
        return Utils.strToInt(Utils.readFile(SCREEN_KCAL_CTRL_CONT));
    }

    public static boolean hasScreenContrast() {
        return Utils.existFile(SCREEN_KCAL_CTRL_CONT);
    }

    public static void setScreenValue(int value, Context context) {
        run(Control.write(String.valueOf(value), SCREEN_KCAL_CTRL_VAL), SCREEN_KCAL_CTRL_VAL, context);
    }

    public static int getScreenValue() {
        return Utils.strToInt(Utils.readFile(SCREEN_KCAL_CTRL_VAL));
    }

    public static boolean hasScreenValue() {
        return Utils.existFile(SCREEN_KCAL_CTRL_VAL);
    }

    public static void setScreenHue(int value, Context context) {
        run(Control.write(String.valueOf(value), SCREEN_KCAL_CTRL_HUE), SCREEN_KCAL_CTRL_HUE, context);
    }

    public static int getScreenHue() {
        return Utils.strToInt(Utils.readFile(SCREEN_KCAL_CTRL_HUE));
    }

    public static boolean hasScreenHue() {
        return Utils.existFile(SCREEN_KCAL_CTRL_HUE);
    }

    public static void enableGrayscaleMode(boolean enable, Context context) {
        run(Control.write(enable ? "128" : "255", SCREEN_KCAL_CTRL_SAT), SCREEN_KCAL_CTRL_SAT, context);
    }

    public static void setSaturationIntensity(int value, Context context) {
        run(Control.write(String.valueOf(value), SCREEN_KCAL_CTRL_SAT), SCREEN_KCAL_CTRL_SAT, context);
    }

    public static int getSaturationIntensity() {
        return Utils.strToInt(Utils.readFile(SCREEN_KCAL_CTRL_SAT));
    }

    public static boolean hasSaturationIntensity() {
        return Utils.existFile(SCREEN_KCAL_CTRL_SAT);
    }

    public static void enableInvertScreen(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", SCREEN_KCAL_CTRL_INVERT), SCREEN_KCAL_CTRL_INVERT, context);
    }

    public static boolean isInvertScreenEnabled() {
        return Utils.readFile(SCREEN_KCAL_CTRL_INVERT).equals("1");
    }

    public static boolean hasInvertScreen() {
        return Utils.existFile(SCREEN_KCAL_CTRL_INVERT);
    }

    public static void setMinColor(int value, Context context) {
        run(Control.write(String.valueOf(value), SCREEN_KCAL_CTRL_MIN), SCREEN_KCAL_CTRL_MIN, context);
    }

    public static int getMinColor() {
        return Utils.strToInt(Utils.readFile(SCREEN_KCAL_CTRL_MIN));
    }

    public static boolean hasMinColor() {
        return Utils.existFile(SCREEN_KCAL_CTRL_MIN);
    }

    public static void setColors(String values, Context context) {
        if (hasColorEnable() && SCREEN_COLOR_CONTROL_CTRL.equals(COLOR_ENABLE)) {
            run(Control.write("0", SCREEN_COLOR_CONTROL_CTRL), SCREEN_COLOR_CONTROL_CTRL, context);
        }

        switch (COLOR) {
            case SCREEN_COLOR_CONTROL: {
                String[] colors = values.split(" ");
                String red = String.valueOf(Utils.strToLong(colors[0]) * 10000000L);
                String green = String.valueOf(Utils.strToLong(colors[1]) * 10000000L);
                String blue = String.valueOf(Utils.strToLong(colors[2]) * 10000000L);
                run(Control.write(red + " " + green + " " + blue, SCREEN_COLOR_CONTROL),
                        SCREEN_COLOR_CONTROL, context);
                break;
            }
            case SCREEN_SAMOLED_COLOR: {
                String[] colors = values.split(" ");
                run(Control.write(String.valueOf(Utils.strToLong(colors[0]) * 10000000),
                        SCREEN_SAMOLED_COLOR_RED), SCREEN_SAMOLED_COLOR_RED, context);
                run(Control.write(String.valueOf(Utils.strToLong(colors[1]) * 10000000),
                        SCREEN_SAMOLED_COLOR_RED), SCREEN_SAMOLED_COLOR_GREEN, context);
                run(Control.write(String.valueOf(Utils.strToLong(colors[2]) * 10000000),
                        SCREEN_SAMOLED_COLOR_RED), SCREEN_SAMOLED_COLOR_BLUE, context);
                break;
            }
            default:
                run(Control.write(values, COLOR), COLOR, context);
                break;
        }

        if (hasColorEnable() && !SCREEN_COLOR_CONTROL_CTRL.equals(COLOR_ENABLE)) {
            run(Control.write("1", COLOR_ENABLE), COLOR_ENABLE, context);
        }
    }

    public static List<String> getLimits() {
        List<String> list = new ArrayList<>();
        switch (COLOR) {
            case SCREEN_COLOR_CONTROL:
            case SCREEN_SAMOLED_COLOR:
                for (int i = 60; i <= 400; i++) {
                    list.add(String.valueOf(i));
                }
                break;
            case SCREEN_FB0_RGB:
                for (int i = 255; i <= 32768; i++) {
                    list.add(String.valueOf(i));
                }
                break;
            case SCREEN_FB_KCAL:
                for (int i = 0; i < 256; i++) {
                    list.add(String.valueOf(i));
                }
                break;
            default:
                int max = hasNewKCAL() ? 256 : 255;
                for (int i = 0; i <= max; i++) {
                    list.add(String.valueOf(i));
                }
                break;
        }
        return list;
    }

    private static boolean hasNewKCAL() {
        for (String file : sNewKCAL) {
            if (Utils.existFile(file)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getColors() {
        List<String> list = new ArrayList<>();
        switch (COLOR) {
            case SCREEN_COLOR_CONTROL:
                for (String color : Utils.readFile(SCREEN_COLOR_CONTROL).split(" ")) {
                    list.add(String.valueOf(Utils.strToLong(color) / 10000000L));
                }
                break;
            case SCREEN_SAMOLED_COLOR:
                if (Utils.existFile(SCREEN_SAMOLED_COLOR_RED)) {
                    long color = Utils.strToLong(Utils.readFile(SCREEN_SAMOLED_COLOR_RED)) / 10000000L;
                    list.add(String.valueOf(color));
                }
                if (Utils.existFile(SCREEN_SAMOLED_COLOR_GREEN)) {
                    long color = Utils.strToLong(Utils.readFile(SCREEN_SAMOLED_COLOR_GREEN)) / 10000000L;
                    list.add(String.valueOf(color));
                }
                if (Utils.existFile(SCREEN_SAMOLED_COLOR_BLUE)) {
                    long color = Utils.strToLong(Utils.readFile(SCREEN_SAMOLED_COLOR_BLUE)) / 10000000L;
                    list.add(String.valueOf(color));
                }
                break;
            default:
                for (String color : Utils.readFile(COLOR).split(" ")) {
                    list.add(String.valueOf(Utils.strToLong(color)));
                }
                break;
        }
        return list;
    }

    private static boolean hasColorEnable() {
        if (COLOR_ENABLE != null) return true;
        for (String file : sColorEnables) {
            if (Utils.existFile(file)) {
                COLOR_ENABLE = file;
                return true;
            }
        }
        return false;
    }

    public static boolean hasColors() {
        if (COLOR != null) return true;
        for (String file : sColors) {
            if (Utils.existFile(file)) {
                COLOR = file;
                return true;
            }
        }
        return false;
    }

    public static boolean supported() {
        return hasColors() || hasInvertScreen() || hasSaturationIntensity() || hasScreenHue()
                || hasScreenValue() | hasScreenContrast() || hasScreenHBM();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SCREEN, id, context);
    }

}
