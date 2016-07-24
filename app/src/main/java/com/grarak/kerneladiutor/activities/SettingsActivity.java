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
package com.grarak.kerneladiutor.activities;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.services.boot.Service;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;

/**
 * Created by willi on 08.05.16.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        initToolBar();

        getFragmentManager().beginTransaction().replace(R.id.content_frame, getFragment(), "fragment").commit();
        findViewById(R.id.content_frame).setPadding(0, Math.round(Utils.getActionBarSize(this)), 0, 0);
    }

    private Fragment getFragment() {
        Fragment settingsFragment = getFragmentManager().findFragmentByTag("fragment");
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }
        return settingsFragment;
    }

    @Override
    public void finish() {
        getFragmentManager().beginTransaction().remove(getFragment()).commit();
        super.finish();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        private static final String KEY_FORCE_ENGLISH = "forceenglish";
        private static final String KEY_APPLY_ON_BOOT_TEST = "applyonboottest";
        private static final String KEY_LOGCAT = "logcat";
        private static final String KEY_LAST_KMSG = "lastkmsg";
        private static final String KEY_DMESG = "dmesg";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            SwitchPreference forceEnglish = (SwitchPreference) findPreference(KEY_FORCE_ENGLISH);
            if (Resources.getSystem().getConfiguration().locale.getLanguage().startsWith("en")) {
                getPreferenceScreen().removePreference(forceEnglish);
            } else {
                forceEnglish.setOnPreferenceChangeListener(this);
            }

            findPreference(KEY_APPLY_ON_BOOT_TEST).setOnPreferenceClickListener(this);
            findPreference(KEY_LOGCAT).setOnPreferenceClickListener(this);

            if (Utils.existFile("/proc/last_kmsg") || Utils.existFile("/sys/fs/pstore/console-ramoops")) {
                findPreference(KEY_LAST_KMSG).setOnPreferenceClickListener(this);
            } else {
                getPreferenceScreen().removePreference(findPreference(KEY_LAST_KMSG));
            }
            findPreference(KEY_DMESG).setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String key = preference.getKey();
            if (key.equals(KEY_FORCE_ENGLISH)) {
                boolean checked = (boolean) o;
                if (!checked) {
                    Utils.setLocale(Resources.getSystem().getConfiguration().locale.getLanguage(), getActivity());
                }
                NavigationActivity.restart();
                getActivity().recreate();
                return true;
            }
            return false;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            if (key.equals(KEY_APPLY_ON_BOOT_TEST)) {
                Intent intent = new Intent(getActivity(), Service.class);
                intent.putExtra("messenger", new Messenger(new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.arg1 == 1) {
                            Utils.toast(R.string.nothing_apply, getActivity());
                        }
                    }
                }));
                getActivity().startService(intent);
                return true;
            } else if (key.equals(KEY_LOGCAT)) {
                new Execute().execute("logcat -d > /sdcard/logcat.txt");
                return true;
            } else if (key.equals(KEY_LAST_KMSG)) {
                if (Utils.existFile("/proc/last_kmsg")) {
                    new Execute().execute("cat /proc/last_kmsg > /sdcard/last_kmsg.txt");
                } else if (Utils.existFile("/sys/fs/pstore/console-ramoops")) {
                    new Execute().execute("cat /sys/fs/pstore/console-ramoops > /sdcard/last_kmsg.txt");
                }
                return true;
            } else if (key.equals(KEY_DMESG)) {
                new Execute().execute("dmesg > /sdcard/dmesg.txt");
                return true;
            }
            return false;
        }

        private class Execute extends AsyncTask<String, Void, Void> {
            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.executing));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(String... params) {
                RootUtils.runCommand(params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mProgressDialog.dismiss();
            }
        }
    }

}
