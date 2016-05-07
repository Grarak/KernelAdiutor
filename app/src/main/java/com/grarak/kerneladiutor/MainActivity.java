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

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUBoost;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.kernel.cpu.CoreCtl;
import com.grarak.kerneladiutor.utils.kernel.cpu.MSMPerformance;
import com.grarak.kerneladiutor.utils.kernel.cpu.Temperature;
import com.grarak.kerneladiutor.utils.kernel.cpuvoltage.Voltage;
import com.grarak.kerneladiutor.utils.kernel.hotplug.QcomBcl;
import com.grarak.kerneladiutor.utils.kernel.thermal.MSMThermal;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutordonate.R;

import io.fabric.sdk.android.Fabric;

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
        Fabric.with(this, new Crashlytics());
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
            CoreCtl.supported();
            Device.CPUInfo.load();
            Device.MemInfo.load();
            Device.ROMInfo.load();
            Device.TrustZone.supported();
            MSMPerformance.supported();
            MSMThermal.supported();
            QcomBcl.supported();
            Temperature.supported(MainActivity.this);
            Voltage.supported();

            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName(CPUFreq.isBigLITTLE() ? "big.LITTLE" : "not big.LITTLE")
                    .putContentType(Device.getBoard()));
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
            mCheck = false;

            if (!mHasRoot || !mHasBusybox) {
                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                intent.putExtra(TextActivity.MESSAGE_INTENT, getString(mHasRoot ?
                        R.string.no_busybox : R.string.no_root));
                intent.putExtra(TextActivity.SUMMARY_INTENT,
                        mHasRoot ? "https://play.google.com/store/apps/details?id=stericson.busybox" :
                                "https://www.google.com/search?site=&source=hp&q=root+"
                                        + Device.getVendor() + "+" + Device.getModel());
                startActivity(intent);
                finish();
                return;
            }

            startActivity(new Intent(MainActivity.this, NavigationActivity.class));
            finish();
        }

    }
}
