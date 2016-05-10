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
package com.grarak.kerneladiutor.boot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.database.Settings;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.HashMap;

/**
 * Created by willi on 03.05.16.
 */
public class Service extends android.app.Service {

    private static final String TAG = Service.class.getSimpleName();
    private static boolean sCancel;

    private HashMap<String, Boolean> mCategoryEnabled = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean enabled = false;
        final Settings settings = new Settings(this);
        for (Settings.SettingsItem item : settings.getAllSettings()) {
            if (!mCategoryEnabled.containsKey(item.getCategory())) {
                mCategoryEnabled.put(item.getCategory(), Prefs.getBoolean(item.getCategory(), false, this));
                if (mCategoryEnabled.get(item.getCategory())) {
                    enabled = true;
                }
            }
        }
        if (!enabled) return super.onStartCommand(intent, flags, startId);

        final int seconds = 10;
        PendingIntent cancelIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, CancelReceiver.class), 0);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.apply_on_boot_text, seconds))
                .setSmallIcon(R.drawable.ic_restore)
                .addAction(0, getString(R.string.cancel), cancelIntent)
                .setAutoCancel(true)
                .setWhen(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_MAX);
        }

        final NotificationCompat.Builder builderComplete = new NotificationCompat.Builder(this);
        builderComplete.setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_restore);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < seconds; i++) {
                    if (sCancel) {
                        break;
                    }
                    builder.setContentText(getString(R.string.apply_on_boot_text, seconds - i));
                    builder.setProgress(seconds, i, false);
                    notificationManager.notify(0, builder.build());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                builderComplete.setContentText(getString(sCancel ? R.string.applu_on_boot_canceled :
                        R.string.apply_on_boot_complete));
                notificationManager.notify(0, builderComplete.build());

                if (sCancel) return;
                RootUtils.SU su = new RootUtils.SU(true);
                for (Settings.SettingsItem item : settings.getAllSettings()) {
                    if (mCategoryEnabled.get(item.getCategory())) {
                        synchronized (this) {
                            su.runCommand(item.getSetting());
                            Log.i(TAG, item.getSetting());
                        }
                    }
                }
                su.close();
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    public static class CancelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sCancel = true;
        }

    }

}
