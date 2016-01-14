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
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.grarak.kerneladiutordonate.elements.views.CustomNavigationView;
import com.grarak.kerneladiutordonate.elements.views.NavHeaderView;
import com.grarak.kerneladiutordonate.fragments.BaseFragment;
import com.grarak.kerneladiutordonate.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutordonate.utils.Constants;
import com.grarak.kerneladiutordonate.utils.Utils;
import com.grarak.kerneladiutordonate.utils.root.RootUtils;

public class NavigationActivity extends AppCompatActivity
        implements CustomNavigationView.OnCustomNavigationListener {

    private static final String CUR_POSITION_INTENT = "cur_position_intent";

    private DrawerLayout mDrawer;
    private static NavHeaderView navHeaderView;
    private int cur_position;
    private boolean pressAgain;
    private MenuItem lastItem;
    private AppBarLayout appBarLayout;
    private CustomNavigationView mNavigationView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        pressAgain = true;

        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawer, toolbar, 0, 0);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        if (navHeaderView == null) {
            navHeaderView = new NavHeaderView(this);
        } else {
            ((ViewGroup) navHeaderView.getParent()).removeView(navHeaderView);
        }

        mNavigationView = (CustomNavigationView) findViewById(R.id.nav_view);
        mNavigationView.addHeaderView(navHeaderView);
        mNavigationView.setOnCustomNavigationListener(this);

        for (CustomNavigationView.NavigationViewItem navigationViewItem : Constants.ITEMS)
            mNavigationView.addItem(navigationViewItem);

        if (savedInstanceState != null)
            cur_position = savedInstanceState.getInt(CUR_POSITION_INTENT, 0);

        if (mNavigationView.size() > 0)
            onSelect(mNavigationView.getMenuItem(cur_position),
                    mNavigationView.getFragmentClass(cur_position),
                    null, cur_position, mNavigationView.getViewInterface(cur_position),
                    savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (Constants.ITEMS.size() == 0) {
                super.onBackPressed();
            } else {
                Fragment fragment = getSupportFragmentManager()
                        .findFragmentByTag(mNavigationView
                                .getMenuItem(cur_position).getTitle() + "_key");
                if (fragment == null || !(fragment instanceof BaseFragment) ||
                        !((BaseFragment) fragment).onBackPressed()) {
                    if (pressAgain) {
                        Utils.toast(getString(R.string.press_back_again), this);
                        pressAgain = false;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    pressAgain = true;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        super.onBackPressed();
                        RootUtils.closeSU();
                    }
                }
            }
        }
    }

    @Override
    public void onSelect(MenuItem item, Class fragmentclass, Intent intent,
                         int position, RecyclerViewFragment.ViewInterface viewInterface,
                         Bundle savedInstanceState) {
        if (fragmentclass != null) {
            if (lastItem != null) lastItem.setChecked(false);
            item.setChecked(true);
            lastItem = item;
            cur_position = position;
            ActionBar actionBar;
            if ((actionBar = getSupportActionBar()) != null)
                actionBar.setTitle(item.getTitle());
        }

        if (savedInstanceState == null)
            if (fragmentclass != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        getFragment(fragmentclass, item.getTitle() + "_key", viewInterface),
                        item.getTitle() + "_key").commitAllowingStateLoss();
            } else if (intent != null) {
                startActivity(intent);
            }

        if (savedInstanceState == null)
            mDrawer.closeDrawer(GravityCompat.START);

    }

    private Fragment getFragment(Class fragmentClass, String key,
                                 RecyclerViewFragment.ViewInterface viewInterface) {
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag(key);
        if (mFragment == null) mFragment = Fragment.instantiate(this, fragmentClass.getName());
        if (mFragment instanceof RecyclerViewFragment && viewInterface != null)
            ((RecyclerViewFragment) mFragment).setViewInterface(viewInterface);
        return mFragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CUR_POSITION_INTENT, cur_position);
    }

    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.ITEMS.size() == 0) {
            RootUtils.closeSU();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

}
