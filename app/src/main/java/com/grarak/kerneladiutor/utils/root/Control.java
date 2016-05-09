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
package com.grarak.kerneladiutor.utils.root;

import android.content.Context;
import android.util.Log;

import com.grarak.kerneladiutor.database.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 02.05.16.
 */
public class Control {

    private static final String TAG = Control.class.getSimpleName();
    private static final List<Thread> mTasks = new ArrayList<>();

    public static String startService(String prop) {
        return "start " + prop;
    }

    public static String stopService(String prop) {
        return "stop " + prop;
    }

    public static String write(String text, String path) {
        return "echo '" + text + "' > " + path;
    }

    private static void apply(String command, String category, String id, Context context) {
        if (context != null) {
            Settings settings = new Settings(context);
            List<Settings.SettingsItem> items = settings.getAllSettings();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getId().equals(id) && items.get(i).getCategory().equals(category)) {
                    settings.delete(i);
                }
            }
            settings.putSetting(category, command, id);
            settings.commit();
        }

        RootUtils.runCommand(command);
        Log.i(TAG, command);
    }

    public static void runSetting(final String command, final String category, final String id,
                                  final Context context) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                apply(command, category, id, context);
            }
        });

        // synchronized doesn't want to work here
        // use this ugly hack
        mTasks.add(thread);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (mTasks.get(0) == thread) {
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mTasks.remove(thread);
                        break;
                    }
                }
            }
        }).start();
    }

}
