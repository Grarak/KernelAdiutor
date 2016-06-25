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
package com.grarak.kerneladiutor;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUBoost;
import com.grarak.kerneladiutor.utils.kernel.cpu.MSMPerformance;
import com.grarak.kerneladiutor.utils.kernel.cpu.Temperature;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.Hotplug;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.QcomBcl;
import com.grarak.kerneladiutor.utils.kernel.cpuvoltage.Voltage;
import com.grarak.kerneladiutor.utils.kernel.gpu.GPU;
import com.grarak.kerneladiutor.utils.kernel.screen.Screen;
import com.grarak.kerneladiutor.utils.kernel.thermal.Thermal;
import com.grarak.kerneladiutor.utils.kernel.wake.Wake;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.io.File;

/**
 * Created by willi on 14.04.16.
 */
public class MainActivity extends BaseActivity {

    private boolean mCheck;
    private TextView mRootAccess;
    private TextView mBusybox;
    private TextView mCollectInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View splashBackground = findViewById(R.id.splash_background);
        mRootAccess = (TextView) findViewById(R.id.root_access_text);
        mBusybox = (TextView) findViewById(R.id.busybox_text);
        mCollectInfo = (TextView) findViewById(R.id.info_collect_text);

        if (Utils.getOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
            splashBackground.setVisibility(View.GONE);
        }

        if (savedInstanceState != null) {
            mCheck = savedInstanceState.getBoolean("check");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mCheck) new CheckingTask().execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("check", mCheck);
    }

    private class CheckingTask extends AsyncTask<Void, Integer, Void> {

        private boolean mHasRoot;
        private boolean mHasBusybox;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCheck = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mHasRoot = RootUtils.rooted() && RootUtils.rootAccess();
            publishProgress(0);
            if (mHasRoot) {
                mHasBusybox = RootUtils.busyboxInstalled();
                publishProgress(1);
                if (mHasBusybox) {
                    collectData();
                    publishProgress(2);
                }
            }
            return null;
        }

        private void collectData() {
            CPUBoost.supported();
            Device.CPUInfo.load();
            Device.MemInfo.load();
            Device.ROMInfo.load();
            Device.TrustZone.supported();
            GPU.supported();
            Hotplug.supported();
            MSMPerformance.supported();
            QcomBcl.supported();
            Screen.supported();
            Temperature.supported(MainActivity.this);
            Thermal.supported();
            Voltage.supported();
            Wake.supported();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int red = ContextCompat.getColor(MainActivity.this, R.color.red);
            int green = ContextCompat.getColor(MainActivity.this, R.color.green);
            switch (values[0]) {
                case 0:
                    mRootAccess.setTextColor(mHasRoot ? green : red);
                    break;
                case 1:
                    mBusybox.setTextColor(mHasBusybox ? green : red);
                    break;
                case 2:
                    mCollectInfo.setTextColor(green);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!mHasRoot || !mHasBusybox) {
                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                intent.putExtra(TextActivity.MESSAGE_INTENT, getString(mHasRoot ?
                        R.string.no_busybox : R.string.no_root));
                intent.putExtra(TextActivity.SUMMARY_INTENT,
                        mHasRoot ? "https://play.google.com/store/apps/details?id=stericson.busybox" :
                                "https://www.google.com/search?site=&source=hp&q=root+"
                                        + Device.getVendor() + "+" + Device.getModel());
                startActivity(intent);
                mCheck = false;
                finish();
                return;
            }

            new AsyncTask<Void, Void, Boolean>() {

                private ApplicationInfo mApplicationInfo;
                private PackageInfo mPackageInfo;
                private boolean mPatched;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    try {
                        mApplicationInfo = getPackageManager().getApplicationInfo(
                                "com.grarak.kerneladiutordonate", 0);
                        mPackageInfo = getPackageManager().getPackageInfo(
                                "com.grarak.kerneladiutordonate", 0);
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    if (mApplicationInfo != null && mPackageInfo != null && mPackageInfo.versionCode == 130) {
                        mPatched = !Utils.checkMD5("5c7a92a5b2dcec409035e1114e815b00",
                                new File(mApplicationInfo.publicSourceDir)) || Utils.isPatched(mApplicationInfo);
                    }
                    return mApplicationInfo != null && mPackageInfo != null && mPackageInfo.versionCode == 130
                            && !mPatched;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    mCheck = false;
                    if (aBoolean) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName("com.grarak.kerneladiutordonate",
                                "com.grarak.kerneladiutordonate.MainActivity"));
                        startActivityForResult(intent, 0);
                    } else {
                        launch(mPatched ? 3 : -1);
                    }
                }
            }.execute();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            launch(data.getIntExtra("result", -1));
        }
    }

    private void launch(int code) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra("result", code);
        Prefs.saveInt("license", code, this);
        startActivity(intent);
        finish();
    }

}
