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

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUVoltage;
import com.grarak.kerneladiutor.fragments.statistics.DeviceFragment;
import com.grarak.kerneladiutor.fragments.statistics.OverallFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpuvoltage.Voltage;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutordonate.R;

import java.util.LinkedHashMap;

public class NavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static LinkedHashMap<Integer, BaseFragment> sFragments = new LinkedHashMap<>();

    static {
        sFragments.put(R.string.statistics, null);
        sFragments.put(R.string.overall, new OverallFragment());
        sFragments.put(R.string.device, new DeviceFragment());
        sFragments.put(R.string.kernel, null);
        sFragments.put(R.string.cpu, new CPUFragment());
        if (Voltage.supported()) {
            sFragments.put(R.string.cpu_voltage, new CPUVoltage());
        }
    }

    private Handler mHandler = new Handler();
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private boolean mExit;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolBar;
    private int mSelection = R.string.overall;

    @Override
    protected boolean setStatusBarColor() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolBar, 0, 0);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        appendFragments(mNavigationView.getMenu());

        if (savedInstanceState != null) {
            mSelection = savedInstanceState.getInt("selection");
        }

        onItemSelected(mSelection);
    }

    private void appendFragments(Menu menu) {
        SubMenu lastSubMenu = null;
        for (int menuRes : sFragments.keySet()) {
            if (sFragments.get(menuRes) == null) {
                lastSubMenu = menu.addSubMenu(menuRes);
            } else {
                MenuItem menuItem;
                if (lastSubMenu == null) {
                    menuItem = menu.add(0, menuRes, 0, menuRes);
                } else {
                    menuItem = lastSubMenu.add(0, menuRes, 0, menuRes);
                }
                menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_dots));
                menuItem.setCheckable(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (!sFragments.get(mSelection).onBackPressed()) {
            if (mExit) {
                mExit = false;
                RootUtils.closeSU();
                super.onBackPressed();
            } else {
                Utils.toast(R.string.press_back_again_exit, this);
                mExit = true;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mExit = false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selection", mSelection);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        onItemSelected(item.getItemId());
        return true;
    }

    private void onItemSelected(int res) {
        getSupportActionBar().setTitle(getString(res));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, getFragment(res),
                res + "_key").commit();
        mNavigationView.setCheckedItem(res);
        mDrawer.closeDrawer(GravityCompat.START);
        mSelection = res;
    }

    private Fragment getFragment(int res) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(res + "_key");
        if (fragment == null) return sFragments.get(res);
        return fragment;
    }

    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }

}
