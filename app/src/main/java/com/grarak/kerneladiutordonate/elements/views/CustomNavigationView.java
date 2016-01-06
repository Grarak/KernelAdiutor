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
package com.grarak.kerneladiutordonate.elements.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.grarak.kerneladiutordonate.fragments.RecyclerViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 28.12.15.
 */
public class CustomNavigationView extends NavigationView {

    private SubMenu lastHeader;
    private List<MenuItem> itemList = new ArrayList<>();
    private List<Class> fragmentList = new ArrayList<>();
    private List<Intent> intentList = new ArrayList<>();
    private List<RecyclerViewFragment.ViewInterface> viewInterfaceList = new ArrayList<>();
    private OnCustomNavigationListener onCustomNavigationListener;

    public interface OnCustomNavigationListener {
        void onSelect(MenuItem item, Class fragmentclass, Intent intent, int position,
                      RecyclerViewFragment.ViewInterface viewInterface, Bundle savedInstanceState);
    }

    public interface NavigationViewItem {
        boolean isHeader();

        SubMenu addSubMenu(Menu menu);

        MenuItem addMenuItem(Menu menu, int id);

        Class getFragmentClass();

        Intent getIntent();

        String getTitle();

        RecyclerViewFragment.ViewInterface getViewInterface();
    }

    public static class NavigationHeader implements NavigationViewItem {

        private final String title;
        private final Drawable icon;

        public NavigationHeader(String title) {
            this(title, null);
        }

        public NavigationHeader(String title, Drawable icon) {
            this.title = title;
            this.icon = icon;
        }

        @Override
        public boolean isHeader() {
            return true;
        }

        @Override
        public SubMenu addSubMenu(Menu menu) {
            return menu.addSubMenu(title).setHeaderIcon(icon);
        }

        @Override
        public MenuItem addMenuItem(Menu menu, int id) {
            return null;
        }

        @Override
        public Class getFragmentClass() {
            return null;
        }

        @Override
        public Intent getIntent() {
            return null;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public RecyclerViewFragment.ViewInterface getViewInterface() {
            return null;
        }
    }

    public static class NavigationItem implements NavigationViewItem {

        private final String title;
        private final Class fragment;
        private final Intent intent;
        private final Drawable icon;
        private final RecyclerViewFragment.ViewInterface viewInterface;

        public NavigationItem(String title, Class fragment, Intent intent) {
            this(title, fragment, intent, null);
        }

        public NavigationItem(String title, Class fragment, Intent intent,
                              RecyclerViewFragment.ViewInterface viewInterface) {
            this(title, fragment, intent, null, viewInterface);
        }

        public NavigationItem(String title, Class fragment, Intent intent, Drawable icon,
                              RecyclerViewFragment.ViewInterface viewInterface) {
            this.title = title;
            this.fragment = fragment;
            this.intent = intent;
            this.icon = icon;
            this.viewInterface = viewInterface;
        }

        @Override
        public boolean isHeader() {
            return false;
        }

        @Override
        public SubMenu addSubMenu(Menu menu) {
            return null;
        }

        @Override
        public MenuItem addMenuItem(Menu menu, int id) {
            return menu.add(0, id, 0, title).setIcon(icon).setCheckable(intent == null);
        }

        @Override
        public Class getFragmentClass() {
            return fragment;
        }

        @Override
        public Intent getIntent() {
            return intent;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public RecyclerViewFragment.ViewInterface getViewInterface() {
            return viewInterface;
        }
    }

    public CustomNavigationView(Context context) {
        this(context, null);
    }

    public CustomNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (onCustomNavigationListener != null) {
                    onCustomNavigationListener.onSelect(item,
                            fragmentList.get(item.getItemId()), intentList.get(item.getItemId()),
                            item.getItemId(), viewInterfaceList.get(item.getItemId()), null);
                    return true;
                }
                return false;
            }
        });
    }

    public void addItem(NavigationViewItem navigationViewItem) {
        if (navigationViewItem.isHeader())
            lastHeader = navigationViewItem.addSubMenu(getMenu());
        else {
            itemList.add(navigationViewItem.addMenuItem(lastHeader != null ?
                    lastHeader : getMenu(), itemList.size()));
            fragmentList.add(navigationViewItem.getFragmentClass());
            intentList.add(navigationViewItem.getIntent());
            viewInterfaceList.add(navigationViewItem.getViewInterface());
        }
    }

    public int size() {
        return itemList.size();
    }

    public MenuItem getMenuItem(int position) {
        return itemList.get(position);
    }

    public Class getFragmentClass(int position) {
        return fragmentList.get(position);
    }

    public RecyclerViewFragment.ViewInterface getViewInterface(int position) {
        return viewInterfaceList.get(position);
    }

    public void setOnCustomNavigationListener(OnCustomNavigationListener onCustomNavigationListener) {
        this.onCustomNavigationListener = onCustomNavigationListener;
    }

}
