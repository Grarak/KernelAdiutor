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
package com.grarak.kerneladiutor.fragments.tools;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.database.Settings;
import com.grarak.kerneladiutor.database.tools.customcontrols.Controls;
import com.grarak.kerneladiutor.database.tools.profiles.Profiles;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.recyclerview.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.AppSettings;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 04.08.16.
 */
public class OnBootFragment extends RecyclerViewFragment {

    private Settings mSettings;
    private Controls mControls;
    private Profiles mProfiles;

    private Dialog mDeleteDialog;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.welcome),
                getString(R.string.on_boot_welcome_summary)));

        if (mDeleteDialog != null) {
            mDeleteDialog.show();
        }

        if (mSettings == null) {
            mSettings = new Settings(getActivity());
        }
        if (mControls == null) {
            mControls = new Controls(getActivity());
        }
        if (mProfiles == null) {
            mProfiles = new Profiles(getActivity());
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        load(items);
    }

    private void reload() {
        getHandler().postDelayed(() -> {
            clearItems();
            reload(new ReloadHandler<>());
        }, 250);
    }

    @Override
    protected void load(List<RecyclerViewItem> items) {
        super.load(items);

        List<RecyclerViewItem> applyOnBoot = new ArrayList<>();
        TitleView applyOnBootTitle = new TitleView();
        applyOnBootTitle.setText(getString(R.string.apply_on_boot));

        List<Settings.SettingsItem> settings = mSettings.getAllSettings();
        HashMap<String, Boolean> applyOnBootEnabled = new HashMap<>();
        List<ApplyOnBootItem> applyOnBootItems = new ArrayList<>();
        for (int i = 0; i < settings.size(); i++) {
            Settings.SettingsItem item = settings.get(i);
            boolean enabled;
            if (applyOnBootEnabled.containsKey(item.getCategory())) {
                enabled = applyOnBootEnabled.get(item.getCategory());
            } else {
                applyOnBootEnabled.put(item.getCategory(),
                        enabled = AppSettings.getBoolean(settings.get(i).getCategory(),
                                false, getActivity()));
            }
            if (enabled) {
                applyOnBootItems.add(new ApplyOnBootItem(item.getSetting(),
                        item.getCategory(), i));
            }
        }

        for (int i = 0; i < applyOnBootItems.size(); i++) {
            final ApplyOnBootItem applyOnBootItem = applyOnBootItems.get(i);
            DescriptionView applyOnBootView = new DescriptionView();
            applyOnBootView.setSummary(
                    (i + 1)
                            + ". " + applyOnBootItem.mCategory.replace("_onboot", "")
                            + ": " + applyOnBootItem.mCommand);

            applyOnBootView.setOnItemClickListener(item -> {
                mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.delete_question,
                        applyOnBootItem.mCommand),
                        (dialogInterface, i1) -> {
                        },
                        (dialogInterface, i1) -> {
                            mSettings.delete(applyOnBootItem.mPosition);
                            mSettings.commit();
                            reload();
                        },
                        dialogInterface -> mDeleteDialog = null, getActivity());
                mDeleteDialog.show();
            });

            applyOnBoot.add(applyOnBootView);
        }

        if (applyOnBoot.size() > 0) {
            items.add(applyOnBootTitle);
            items.addAll(applyOnBoot);
        }

        List<RecyclerViewItem> customControls = new ArrayList<>();
        TitleView customControlTitle = new TitleView();
        customControlTitle.setText(getString(R.string.custom_controls));

        for (final Controls.ControlItem controlItem : mControls.getAllControls()) {
            if (controlItem.isOnBootEnabled() && controlItem.getArguments() != null) {
                DescriptionView controlView = new DescriptionView();
                controlView.setTitle(controlItem.getTitle());
                controlView.setSummary(getString(R.string.arguments, controlItem.getArguments()));
                controlView.setOnItemClickListener(item -> {
                    mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.disable_question,
                            controlItem.getTitle()),
                            (dialogInterface, i) -> {
                            },
                            (dialogInterface, i) -> {
                                controlItem.enableOnBoot(false);
                                mControls.commit();
                                reload();
                            },
                            dialogInterface -> mDeleteDialog = null, getActivity());
                    mDeleteDialog.show();
                });

                customControls.add(controlView);
            }
        }

        if (customControls.size() > 0) {
            items.add(customControlTitle);
            items.addAll(customControls);
        }

        List<RecyclerViewItem> profiles = new ArrayList<>();
        TitleView profileTitle = new TitleView();
        profileTitle.setText(getString(R.string.profile));

        for (final Profiles.ProfileItem profileItem : mProfiles.getAllProfiles()) {
            if (profileItem.isOnBootEnabled()) {
                DescriptionView profileView = new DescriptionView();
                profileView.setSummary(profileItem.getName());
                profileView.setOnItemClickListener(item -> {
                    mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.disable_question,
                            profileItem.getName()),
                            (dialogInterface, i) -> {
                            },
                            (dialogInterface, i) -> {
                                profileItem.enableOnBoot(false);
                                mProfiles.commit();
                                reload();
                            },
                            dialogInterface -> mDeleteDialog = null, getActivity());
                    mDeleteDialog.show();
                });

                profiles.add(profileView);
            }
        }

        if (profiles.size() > 0) {
            items.add(profileTitle);
            items.addAll(profiles);
        }

        if (AppSettings.isInitdOnBoot(getActivity())) {
            TitleView initdTitle = new TitleView();
            initdTitle.setText(getString(R.string.initd));
            items.add(initdTitle);

            DescriptionView emulateInitd = new DescriptionView();
            emulateInitd.setSummary(getString(R.string.emulate_initd));
            emulateInitd.setOnItemClickListener(item -> {
                mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.disable_question,
                        getString(R.string.emulate_initd)),
                        (dialogInterface, i) -> {
                        },
                        (dialogInterface, i) -> {
                            AppSettings.saveInitdOnBoot(false, getActivity());
                            reload();
                        },
                        dialogInterface -> mDeleteDialog = null, getActivity());
                mDeleteDialog.show();
            });

            items.add(emulateInitd);
        }
    }

    private class ApplyOnBootItem {
        private final String mCommand;
        private final String mCategory;
        private final int mPosition;

        private ApplyOnBootItem(String command, String category, int position) {
            mCommand = command;
            mCategory = category;
            mPosition = position;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSettings = null;
        mControls = null;
        mProfiles = null;
    }

}
