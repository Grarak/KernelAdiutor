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

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.AppSettings;
import com.grarak.kerneladiutor.utils.Themes;
import com.grarak.kerneladiutor.utils.ViewUtils;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Created by willi on 14.04.16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Don't initialize analytics with debug build
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Themes.Theme theme = Themes.getTheme(this, Themes.isDarkTheme(this));
        if (Themes.isDarkTheme(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setTheme(theme.getStyle());
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && setStatusBarColor()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        if (AppSettings.isForceEnglish(newBase)) {
            super.attachBaseContext(wrap(newBase, new Locale("en_US")));
        } else {
            super.attachBaseContext(newBase);
        }
    }

    public static ContextWrapper wrap(Context context, Locale newLocale) {

        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale);

            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            context = context.createConfigurationContext(configuration);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale);
            context = context.createConfigurationContext(configuration);
        } else {
            configuration.locale = newLocale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

        return new ContextWrapper(context);
    }

    public AppBarLayout getAppBarLayout() {
        return (AppBarLayout) findViewById(R.id.appbarlayout);
    }

    public Toolbar getToolBar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    public void initToolBar() {
        Toolbar toolbar = getToolBar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> finish());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected boolean setStatusBarColor() {
        return true;
    }

    protected int statusBarColor() {
        return ViewUtils.getColorPrimaryDarkColor(this);
    }

}
