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
package com.grarak.kerneladiutordonate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutordonate.elements.views.CustomNavigationView;
import com.grarak.kerneladiutordonate.fragments.DonateFragment;
import com.grarak.kerneladiutordonate.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutordonate.utils.Constants;
import com.grarak.kerneladiutordonate.utils.Device;
import com.grarak.kerneladiutordonate.utils.Prefs;
import com.grarak.kerneladiutordonate.utils.Utils;
import com.grarak.kerneladiutordonate.utils.root.RootUtils;
import com.grarak.kerneladiutordonate.views.information.DeviceInformation;
import com.grarak.kerneladiutordonate.views.information.FrequencyTable;
import com.grarak.kerneladiutordonate.views.kernel.CPU;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Prefs.getBoolean("firststart", true, this)) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }
        Utils.colorizeStatusbar(this);
        setContentView(R.layout.activity_main);
        splashPanel();

        new AsyncTask<Void, Void, Void>() {

            private boolean hasRoot;
            private boolean hasBusybox;

            private TextView rootText;
            private TextView busyboxText;
            private TextView infoText;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                rootText = (TextView) findViewById(R.id.root_access_text);
                busyboxText = (TextView) findViewById(R.id.busybox_text);
                infoText = (TextView) findViewById(R.id.info_collect_text);
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (RootUtils.rooted()) hasRoot = RootUtils.rootAccess();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rootText.setTextColor(ContextCompat.getColor(MainActivity.this,
                                hasRoot ? R.color.material_green : R.color.material_red));
                    }
                });
                if (hasRoot) hasBusybox = RootUtils.busyboxInstalled();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyboxText.setTextColor(ContextCompat.getColor(MainActivity.this,
                                hasBusybox ? R.color.material_green : R.color.material_red));
                    }
                });

                if (hasRoot && hasBusybox)
                    addItems();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoText.setTextColor(ContextCompat.getColor(MainActivity.this,
                                hasRoot && hasBusybox ? R.color.material_green : R.color.material_red));
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (!hasRoot || !hasBusybox) {
                    Intent intent = new Intent(MainActivity.this, TextActivity.class);
                    intent.putExtra(TextActivity.MESSAGE_INTENT, getString(hasRoot ?
                            R.string.no_busybox : R.string.no_root));
                    intent.putExtra(TextActivity.SUMMARY_INTENT,
                            !hasRoot ? "https://play.google.com/store/apps/details?id=stericson.busybox" :
                                    "https://www.google.com/search?site=&source=hp&q=root+"
                                            + Device.getVendor() + "+" + Device.getModel());
                    startActivity(intent);
                    return;
                }

                startActivity(new Intent(MainActivity.this, NavigationActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }.execute();
    }

    private void addItems() {
        Constants.ITEMS.clear();

        Constants.ITEMS.add(new CustomNavigationView.NavigationHeader(getString(R.string.information)));
        Constants.ITEMS.add(new CustomNavigationView.NavigationItem(getString(R.string.device_information),
                RecyclerViewFragment.class, null, ContextCompat.getDrawable(this, R.drawable.ic_info),
                new DeviceInformation(this)));
        Constants.ITEMS.add(new CustomNavigationView.NavigationItem(getString(R.string.frequency_table),
                RecyclerViewFragment.class, null, ContextCompat.getDrawable(this, R.drawable.ic_graph),
                new FrequencyTable(this)));
        Constants.ITEMS.add(new CustomNavigationView.NavigationHeader(getString(R.string.kernel)));
        Constants.ITEMS.add(new CustomNavigationView.NavigationItem(getString(R.string.cpu),
                RecyclerViewFragment.class, null, ContextCompat.getDrawable(this, R.drawable.ic_cpu),
                new CPU(this)));
        Constants.ITEMS.add(new CustomNavigationView.NavigationItem(getString(R.string.donate),
                DonateFragment.class, null, ContextCompat.getDrawable(this, R.drawable.ic_donate), null));
        Constants.ITEMS.add(new CustomNavigationView.NavigationItem(getString(R.string.settings),
                null, new Intent(this, SettingsActivity.class),
                ContextCompat.getDrawable(this, R.drawable.ic_settings), null));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        splashPanel();
    }

    private void splashPanel() {
        if (Utils.getScreenOrientation(this) == Configuration.ORIENTATION_LANDSCAPE)
            findViewById(R.id.splash_background).setVisibility(View.GONE);
    }

}
