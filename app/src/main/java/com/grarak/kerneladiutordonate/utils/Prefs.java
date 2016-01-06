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

import android.content.Context;

/**
 * Created by willi on 01.01.16.
 */
public class Prefs {

    public static int getInt(String name, int defaults, Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).getInt(name, defaults);
    }

    public static void saveInt(String name, int value, Context context) {
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit().putInt(name, value).apply();
    }

    public static boolean getBoolean(String name, boolean defaults, Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).getBoolean(name, defaults);
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(name, value).apply();
    }

    public static String getString(String name, String defaults, Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).getString(name, defaults);
    }

    public static void saveString(String name, String value, Context context) {
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit().putString(name, value).apply();
    }

}
