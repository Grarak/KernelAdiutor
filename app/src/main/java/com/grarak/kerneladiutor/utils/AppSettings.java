/*
 * Copyright (C) 2018 Willi Ye <williye97@gmail.com>
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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 11.03.18.
 */

public class AppSettings {

    private static final String FORCE_ENGLISH = "forceenglish";
    private static final String PASSWORD = "password";
    private static final String FRAGMENT_ENABLED_POSTFIX = "%s_enabled";
    private static final String CPUSPY_OFFSETS_PREFIX = "offsets%d";
    private static final String BANNER_SIZE = "banner_size";
    private static final String HIDE_BANNER = "hide_banner";
    private static final String FORCE_CARDS = "forcecards";
    private static final String GH_AD_SHOWN_POSTFIX = "%s_shown";
    private static final String CORE_CTL_MIN_CPUS_BIG = "core_ctl_min_cpus_big";
    private static final String INITD_ONBOOT = "initd_onboot";
    private static final String PROFILE_TILE = "profiletile";
    private static final String SHOW_TASKER_TOAST = "showtaskertoast";
    private static final String RECOVERY_OPTION = "recovery_option";
    private static final String KGAMMA_PROFILE = "kgamma_profile";
    private static final String GAMMA_CONTROL_PROFILE = "gamma_control_profile";
    private static final String DSI_PANEL_PROFILE = "dsi_panel_profile";
    private static final String DATA_SHARING = "data_sharing";
    private static final String SECTION_ICONS = "section_icons";
    private static final String FRAGMENT_OPENED_POSTFIX = "%s_opened";
    private static final String FINGERPRINT = "fingerprint";
    private static final String APPLY_ON_BOOT_DELAY = "applyonbootdelay";
    private static final String APPLY_ON_BOOT_HIDE = "applyonboothide";
    private static final String APPLY_ON_BOOT_CONFIRMATION_NOTIFICATION = "applyonbootconfirmationnotification";
    private static final String APPLY_ON_BOOT_TOAST = "applyonboottoast";
    private static final String APPLY_ON_BOOT_SCRIPT = "applyonbootscript";
    private static final String PREVIEW_PICTURE = "previewpicture";

    public static boolean getBoolean(String key, boolean defaults, Context context) {
        return Prefs.getBoolean(key, defaults, context);
    }

    public static void saveBoolean(String key, boolean value, Context context) {
        Prefs.saveBoolean(key, value, context);
    }

    public static boolean isForceEnglish(Context context) {
        return Prefs.getBoolean(FORCE_ENGLISH, false, context);
    }

    public static String getPassword(Context context) {
        return Prefs.getString(PASSWORD, "", context);
    }

    public static void savePassword(String password, Context context) {
        Prefs.saveString(PASSWORD, password, context);
    }

    public static void resetPassword(Context context) {
        Prefs.remove(PASSWORD, context);
    }

    public static boolean isFragmentEnabled(Class<? extends Fragment> fragmentClass,
                                            Context context) {
        return Prefs.getBoolean(String.format(FRAGMENT_ENABLED_POSTFIX,
                fragmentClass.getSimpleName()), true, context);
    }

    public static String getCpuSpyOffsets(int core, Context context) {
        return Prefs.getString(String.format(CPUSPY_OFFSETS_PREFIX, core),
                "", context);
    }

    public static void saveCpuSpyOffsets(String offsets, int core, Context context) {
        Prefs.saveString(String.format(CPUSPY_OFFSETS_PREFIX, core), offsets, context);
    }

    public static int getBannerSize(@NonNull Context context) {
        int min = Math.round(context.getResources().getDimension(R.dimen.banner_min_height));
        int max = Math.round(context.getResources().getDimension(R.dimen.banner_max_height));

        int height = Prefs.getInt(BANNER_SIZE, Math.round(context.getResources().getDimension(
                R.dimen.banner_default_height)), context);
        if (height > max) {
            height = max;
            Prefs.saveInt("banner_size", max, context);
        } else if (height < min) {
            height = min;
            Prefs.saveInt("banner_size", min, context);
        }

        return height;
    }

    public static void saveBannerSize(int height, Context context) {
        Prefs.saveInt(BANNER_SIZE, height, context);
    }

    public static boolean isHideBanner(Context context) {
        return Prefs.getBoolean(HIDE_BANNER, false, context) && Utils.isDonated(context);
    }

    public static boolean isForceCards(Context context) {
        return Prefs.getBoolean(FORCE_CARDS, false, context);
    }

    public static int getGHAdShown(String name, Context context) {
        return Prefs.getInt(String.format(GH_AD_SHOWN_POSTFIX, name), 0, context);
    }

    public static void saveGHAdShown(String name, int shown, Context context) {
        Prefs.saveInt(String.format(GH_AD_SHOWN_POSTFIX, name), shown, context);
    }

    public static int getCoreCtlMinCpusBig(Context context) {
        return Prefs.getInt(CORE_CTL_MIN_CPUS_BIG, 2, context);
    }

    public static void saveCoreCtlMinCpusBig(int cores, Context context) {
        Prefs.saveInt(CORE_CTL_MIN_CPUS_BIG, cores, context);
    }

    public static void resetCoreCtlMinCpusBig(Context context) {
        Prefs.remove(CORE_CTL_MIN_CPUS_BIG, context);
    }

    public static boolean isInitdOnBoot(Context context) {
        return Prefs.getBoolean(INITD_ONBOOT, false, context);
    }

    public static void saveInitdOnBoot(boolean enabled, Context context) {
        Prefs.saveBoolean(INITD_ONBOOT, enabled, context);
    }

    public static boolean isProfileTile(Context context) {
        return Prefs.getBoolean(PROFILE_TILE, false, context);
    }

    public static void saveProfileTile(boolean enabled, Context context) {
        Prefs.saveBoolean(PROFILE_TILE, enabled, context);
    }

    public static boolean isShowTaskerToast(Context context) {
        return Prefs.getBoolean(SHOW_TASKER_TOAST, true, context);
    }

    public static void saveShowTaskerToast(boolean enabled, Context context) {
        Prefs.saveBoolean(SHOW_TASKER_TOAST, enabled, context);
    }

    public static int getRecoveryOption(Context context) {
        return Prefs.getInt(RECOVERY_OPTION, 0, context);
    }

    public static void saveRecoveryOption(int value, Context context) {
        Prefs.saveInt(RECOVERY_OPTION, value, context);
    }

    public static int getKGammaProfile(Context context) {
        return Prefs.getInt(KGAMMA_PROFILE, -1, context);
    }

    public static void saveKGammaProfile(int value, Context context) {
        Prefs.saveInt(KGAMMA_PROFILE, value, context);
    }

    public static int getGammaControlProfile(Context context) {
        return Prefs.getInt(GAMMA_CONTROL_PROFILE, -1, context);
    }

    public static void saveGammaControlProfile(int value, Context context) {
        Prefs.saveInt(GAMMA_CONTROL_PROFILE, value, context);
    }

    public static int getDsiPanelProfile(Context context) {
        return Prefs.getInt(DSI_PANEL_PROFILE, -1, context);
    }

    public static void saveDsiPanelProfile(int value, Context context) {
        Prefs.saveInt(DSI_PANEL_PROFILE, value, context);
    }

    public static boolean isDataSharing(Context context) {
        return Prefs.getBoolean(DATA_SHARING, true, context);
    }

    public static void saveDataSharing(boolean enabled, Context context) {
        Prefs.saveBoolean(DATA_SHARING, enabled, context);
    }

    public static boolean isSectionIcons(Context context) {
        return Prefs.getBoolean(SECTION_ICONS, false, context);
    }

    public static int getFragmentOpened(Class<? extends Fragment> fragmentClass,
                                        Context context) {
        return Prefs.getInt(String.format(FRAGMENT_OPENED_POSTFIX,
                fragmentClass.getSimpleName()), 0, context);
    }

    public static void saveFragmentOpened(Class<? extends Fragment> fragmentClass, int value,
                                          Context context) {
        Prefs.saveInt(String.format(FRAGMENT_OPENED_POSTFIX,
                fragmentClass.getSimpleName()), value, context);
    }

    public static boolean isFingerprint(Context context) {
        return Prefs.getBoolean(FINGERPRINT, false, context);
    }

    public static int getApplyOnBootDelay(Context context) {
        return Utils.strToInt(Prefs.getString(
                APPLY_ON_BOOT_DELAY, "10", context));
    }

    public static boolean isApplyOnBootHide(Context context) {
        return Prefs.getBoolean(APPLY_ON_BOOT_HIDE, false, context);
    }

    public static boolean isApplyOnBootConfirmationNotification(Context context) {
        return Prefs.getBoolean(APPLY_ON_BOOT_CONFIRMATION_NOTIFICATION, true, context);
    }

    public static boolean isApplyOnBootToast(Context context) {
        return Prefs.getBoolean(APPLY_ON_BOOT_TOAST, false, context);
    }

    public static boolean isApplyOnBootScript(Context context) {
        return Prefs.getBoolean(APPLY_ON_BOOT_SCRIPT, false, context);
    }

    public static String getPreviewPicture(Context context) {
        return Prefs.getString(PREVIEW_PICTURE, null, context);
    }

    public static void savePreviewPicture(String uri, Context context) {
        Prefs.saveString(PREVIEW_PICTURE, uri, context);
    }

    public static void resetPreviewPicture(Context context) {
        Prefs.remove(PREVIEW_PICTURE, context);
    }
}
