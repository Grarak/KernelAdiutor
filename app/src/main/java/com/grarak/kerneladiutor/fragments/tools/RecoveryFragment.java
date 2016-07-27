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

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.FilePickerActivity;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.root.RootFile;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.utils.tools.Recovery;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 12.07.16.
 */
public class RecoveryFragment extends RecyclerViewFragment {

    private int mRecoveryOption;

    private AlertDialog.Builder mAddDialog;
    private AlertDialog.Builder mFlashDialog;

    private List<Recovery> mCommands = new ArrayList<>();

    @Override
    public int getSpanCount() {
        return 1;
    }

    @Override
    protected Drawable getTopFabDrawable() {
        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add));
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.white));
        return drawable;
    }

    @Override
    protected boolean showTopFab() {
        return true;
    }

    @Override
    protected void init() {
        super.init();

        mRecoveryOption = Prefs.getInt("recovery_option", 0, getActivity());
        addViewPagerFragment(OptionsFragment.newInstance(this));

        if (mAddDialog != null) {
            mAddDialog.show();
        }
        if (mFlashDialog != null) {
            mFlashDialog.show();
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();

        add();
    }

    private void add() {
        mAddDialog = new AlertDialog.Builder(getActivity()).setItems(getResources().getStringArray(
                R.array.recovery_commands), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        addAction(Recovery.RECOVERY_COMMAND.WIPE_DATA, null);
                        break;
                    case 1:
                        addAction(Recovery.RECOVERY_COMMAND.WIPE_CACHE, null);
                        break;
                    case 2:
                        Intent intent = new Intent(getActivity(), FilePickerActivity.class);
                        intent.putExtra(FilePickerActivity.PATH_INTENT, "/");
                        intent.putExtra(FilePickerActivity.EXTENSION_INTENT, ".zip");
                        startActivityForResult(intent, 0);
                        break;
                }
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mAddDialog = null;
            }
        });
        mAddDialog.show();
    }

    private void addAction(Recovery.RECOVERY_COMMAND recovery_command, String path) {
        String summary = null;
        switch (recovery_command) {
            case WIPE_DATA:
                summary = getString(R.string.wipe_data);
                break;
            case WIPE_CACHE:
                summary = getString(R.string.wipe_cache);
                break;
            case FLASH_ZIP:
                summary = new File(path).getName();
                break;
        }

        final Recovery recovery = new Recovery(recovery_command, path == null ? null : new File(path));
        mCommands.add(recovery);

        CardView cardView = new CardView(getActivity());
        cardView.setOnMenuListener(new CardView.OnMenuListener() {
            @Override
            public void onMenuReady(final CardView cardView, PopupMenu popupMenu) {
                popupMenu.getMenu().add(Menu.NONE, 0, Menu.NONE, getString(R.string.delete));
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == 0) {
                            mCommands.remove(recovery);
                            removeItem(cardView);
                        }
                        return false;
                    }
                });
            }
        });

        DescriptionView descriptionView = new DescriptionView();
        if (path != null) {
            descriptionView.setTitle(getString(R.string.flash_zip));
        }
        descriptionView.setSummary(summary);

        cardView.addItem(descriptionView);
        addItem(cardView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null) {
            addAction(Recovery.RECOVERY_COMMAND.FLASH_ZIP,
                    data.getStringExtra(FilePickerActivity.RESULT_INTENT));
        }
    }

    private void flashNow() {
        mFlashDialog = ViewUtils.dialogBuilder(getString(R.string.flash_now_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String file = "/cache/recovery/" + mCommands.get(0).getFile(mRecoveryOption == 1 ?
                                Recovery.RECOVERY.TWRP : Recovery.RECOVERY.CWM);
                        RootFile recoveryFile = new RootFile(file);
                        recoveryFile.delete();
                        for (Recovery commands : mCommands) {
                            for (String command : commands.getCommands(mRecoveryOption == 1 ?
                                    Recovery.RECOVERY.TWRP :
                                    Recovery.RECOVERY.CWM))
                                recoveryFile.write(command, true);
                        }
                        RootUtils.runCommand("reboot recovery");
                    }
                }, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        mFlashDialog = null;
                    }
                }, getActivity());
        mFlashDialog.show();
    }

    public static class OptionsFragment extends BaseFragment {
        public static OptionsFragment newInstance(RecoveryFragment recoveryFragment) {
            OptionsFragment fragment = new OptionsFragment();
            fragment.mRecoveryFragment = recoveryFragment;
            return fragment;
        }

        private RecoveryFragment mRecoveryFragment;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setGravity(Gravity.CENTER);
            layout.setOrientation(LinearLayout.VERTICAL);
            String[] options = getResources().getStringArray(R.array.recovery_options);

            final List<AppCompatCheckBox> checkBoxes = new ArrayList<>();
            for (int i = 0; i < options.length; i++) {
                AppCompatCheckBox checkBox = new AppCompatCheckBox(getActivity());
                checkBox.setText(options[i]);
                checkBox.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                checkBox.setChecked(i == mRecoveryFragment.mRecoveryOption);
                checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                final int position = i;
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < checkBoxes.size(); i++) {
                            checkBoxes.get(i).setChecked(position == i);
                        }
                        Prefs.saveInt("recovery_option", position, getActivity());
                        mRecoveryFragment.mRecoveryOption = position;
                    }
                });

                checkBoxes.add(checkBox);
                layout.addView(checkBox);
            }

            FloatingActionButton button = new FloatingActionButton(getActivity());
            button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_done));
            button.setSize(FloatingActionButton.SIZE_MINI);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mRecoveryFragment.itemsSize() > 0) {
                        mRecoveryFragment.flashNow();
                    } else {
                        Utils.toast(R.string.add_action_first, getActivity());
                    }
                }
            });

            layout.addView(button);

            return layout;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCommands.clear();
    }
}
