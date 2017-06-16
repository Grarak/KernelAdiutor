package com.grarak.kerneladiutor.utils.kernel.cpuhotplug;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.root.Control;
/**
 * Created by dark on 17.03.17.
 */

public class MtStrategy {
    // Defines
    private static final String MT_HOTPLUG = "/proc/hps";
    private static final String MT_HOTPLUG_ENABLED = MT_HOTPLUG + "/enabled";
    private static final String MT_HOTPLUG_ULTRA_POWER_SAVING = MT_HOTPLUG + "/num_limit_ultra_power_saving";
    private static final String MT_RUSH_BOOST_ENABLED = MT_HOTPLUG + "/rush_boost_enabled";
    private static final String MT_RUSH_BOOST_THRESHOLD = MT_HOTPLUG + "/rush_boost_threshold";
    private static final String MT_INPUTBOOST_ENABLED = MT_HOTPLUG + "/input_boost_enabled";
    private static final String MT_INPUTBOOST_CPU_NUM = MT_HOTPLUG + "/input_boost_cpu_num";
    private static final String MT_UP_THRESHOLD = MT_HOTPLUG + "/up_threshold";
    private static final String MT_DOWN_THRESHOLD = MT_HOTPLUG + "/down_threshold";
    // Functions

    public static boolean supported() {
        return Utils.existFile(MT_HOTPLUG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

    public static boolean isMtStrategyEnabled() {
        return Utils.readFile(MT_HOTPLUG_ENABLED).equals("1");
    }

    public static void enableMtStrategy(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MT_HOTPLUG_ENABLED), MT_HOTPLUG_ENABLED, context);
    }

    public static int getUltraPowerSaving() {
        return Utils.strToInt(Utils.readFile(MT_HOTPLUG_ULTRA_POWER_SAVING));
    }

    public static void setUltraPowerSaving(int value, Context context) {
        run(Control.write(String.valueOf(value), MT_HOTPLUG_ULTRA_POWER_SAVING), MT_HOTPLUG_ULTRA_POWER_SAVING, context);
    }

    public static boolean getRushBoost() {
        return Utils.readFile(MT_RUSH_BOOST_ENABLED).equals("1");
    }

    public static void setRushBoost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MT_RUSH_BOOST_ENABLED), MT_RUSH_BOOST_ENABLED, context);
    }

    public static int getRushBoostThreshold() {
        return Utils.strToInt(Utils.readFile(MT_RUSH_BOOST_THRESHOLD)) - 1;
    }

    public static void setRushBoostThreshold(int value, Context context) {
        value = value + 1;
        run(Control.write(String.valueOf(value), MT_RUSH_BOOST_THRESHOLD), MT_RUSH_BOOST_THRESHOLD, context);
    }

    public static boolean getInputBoost() {
        return Utils.readFile(MT_INPUTBOOST_ENABLED).equals("1");
    }

    public static void setInputBoost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MT_INPUTBOOST_ENABLED), MT_INPUTBOOST_ENABLED, context);
    }

    public static int getInputBoostCpu() {
        return Utils.strToInt(Utils.readFile(MT_INPUTBOOST_CPU_NUM)) - 1;
    }

    public static void setInputBoostCpu(int value, Context context) {
        value = value + 1;
        run(Control.write(String.valueOf(value), MT_INPUTBOOST_CPU_NUM), MT_INPUTBOOST_CPU_NUM, context);
    }

    public static int getUpThreshold() {
        return Utils.strToInt(Utils.readFile(MT_UP_THRESHOLD)) - 1;
    }

    public static void setUpThreshold(int value, Context context) {
        value = value + 1;
        run(Control.write(String.valueOf(value), MT_UP_THRESHOLD), MT_UP_THRESHOLD, context);
    }

    public static int getDownThreshold() {
        return Utils.strToInt(Utils.readFile(MT_DOWN_THRESHOLD)) - 1;
    }

    public static void setDownThreshold(int value, Context context) {
        value = value + 1;
        run(Control.write(String.valueOf(value), MT_DOWN_THRESHOLD), MT_DOWN_THRESHOLD, context);
    }

}
