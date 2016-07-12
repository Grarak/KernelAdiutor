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
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.tools.ProfileActivity;
import com.grarak.kerneladiutor.database.tools.Profiles;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 10.07.16.
 */
public class ProfileFragment extends RecyclerViewFragment {

    private Profiles mProfiles;

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;
    private boolean mLoaded;

    private LinkedHashMap<String, String> mCommands;
    private AlertDialog.Builder mDeleteDialog;
    private AlertDialog.Builder mApplyDialog;

    private DetailsFragment mDetailsFragment;

    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected boolean showBottomFab() {
        return true;
    }

    @Override
    protected BaseFragment getForegroundFragment() {
        return mDetailsFragment = new DetailsFragment();
    }

    @Override
    protected Drawable getBottomFabDrawable() {
        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add));
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.white));
        return drawable;
    }

    @Override
    public int getSpanCount() {
        int span = Utils.isTablet(getActivity()) ? Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 4 : 3 : Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        if (itemsSize() != 0 && span > itemsSize()) {
            span = itemsSize();
        }
        return span;
    }

    @Override
    protected void init() {
        super.init();

        if (mCommands != null) {
            create(mCommands);
        }
        if (mDeleteDialog != null) {
            mDeleteDialog.show();
        }
        if (mApplyDialog != null) {
            mApplyDialog.show();
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (!mLoaded) {
            mLoaded = true;
            load(items);
        }
    }

    private void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearItems();
                    mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            showProgress();
                        }

                        @Override
                        protected List<RecyclerViewItem> doInBackground(Void... voids) {
                            List<RecyclerViewItem> items = new ArrayList<>();
                            load(items);
                            return items;
                        }

                        @Override
                        protected void onPostExecute(List<RecyclerViewItem> items) {
                            super.onPostExecute(items);
                            for (RecyclerViewItem item : items) {
                                addItem(item);
                            }
                            hideProgress();
                            mLoader = null;
                        }
                    };
                    mLoader.execute();
                }
            }, 250);
        }
    }

    private void load(List<RecyclerViewItem> items) {
        if (mProfiles == null) {
            mProfiles = new Profiles(getActivity());
        }
        final List<Profiles.ProfileItem> profileItems = mProfiles.getAllProfiles();
        for (int i = 0; i < profileItems.size(); i++) {
            final int position = i;
            CardView cardView = new CardView(getActivity());
            cardView.setOnMenuListener(new CardView.OnMenuListener() {
                @Override
                public void onMenuReady(CardView cardView, PopupMenu popupMenu) {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.details));
                    final MenuItem onBoot = menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.on_boot)).setCheckable(true);
                    onBoot.setChecked(profileItems.get(position).isOnBootEnabled());
                    menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.delete));

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case 0:
                                    setForegroundText(profileItems.get(position).getName().toUpperCase());
                                    mDetailsFragment.setText(profileItems.get(position).getCommands());
                                    showForeground();
                                    break;
                                case 1:
                                    onBoot.setChecked(!onBoot.isChecked());
                                    profileItems.get(position).enableOnBoot(onBoot.isChecked());
                                    mProfiles.commit();
                                    break;
                                case 2:
                                    mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                }
                                            }, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    mProfiles.delete(position);
                                                    reload();
                                                }
                                            }, new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialogInterface) {
                                                    mDeleteDialog = null;
                                                }
                                            }, getActivity());
                                    mDeleteDialog.show();
                                    break;
                            }
                            return false;
                        }
                    });
                }
            });

            final DescriptionView descriptionView = new DescriptionView();
            descriptionView.setSummary(profileItems.get(i).getName());
            descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    mApplyDialog = ViewUtils.dialogBuilder(getString(R.string.apply_question,
                            descriptionView.getSummary()), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (String command : profileItems.get(position).getCommands()) {
                                Control.runSetting(command, null, null, null);
                            }
                        }
                    }, new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            mApplyDialog = null;
                        }
                    }, getActivity());
                    mApplyDialog.show();
                }
            });

            cardView.addItem(descriptionView);
            items.add(cardView);
        }
    }

    @Override
    protected void onBottomFabClick() {
        super.onBottomFabClick();
        startActivityForResult(new Intent(getActivity(), ProfileActivity.class), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null) {
            LinkedHashMap<String, String> commandsList = new LinkedHashMap<>();
            ArrayList<String> ids = data.getStringArrayListExtra(ProfileActivity.RESULT_ID_INTENT);
            ArrayList<String> commands = data.getStringArrayListExtra(ProfileActivity.RESULT_COMMAND_INTENT);
            for (int i = 0; i < ids.size(); i++) {
                commandsList.put(ids.get(i), commands.get(i));
            }
            create(commandsList);
        }
    }

    private void create(final LinkedHashMap<String, String> commands) {
        mCommands = commands;
        ViewUtils.dialogEditText(null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }, new ViewUtils.OnDialogEditTextListener() {
            @Override
            public void onClick(String text) {
                if (text.isEmpty()) {
                    Utils.toast(R.string.name_empty, getActivity());
                    return;
                }

                for (Profiles.ProfileItem profileItem : mProfiles.getAllProfiles()) {
                    if (text.equals(profileItem.getName())) {
                        Utils.toast(R.string.profile_already_exists, getActivity());
                        return;
                    }
                }

                mProfiles.putProfile(text, commands);
                mProfiles.commit();
                reload();
            }
        }, getActivity()).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mCommands = null;
            }
        }).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoader != null) {
            mLoader.cancel(true);
        }
        mLoaded = false;
    }

    public static class DetailsFragment extends BaseFragment {
        @Override
        protected boolean retainInstance() {
            return false;
        }

        private TextView mCodeText;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile_details, container, false);

            mCodeText = (TextView) rootView.findViewById(R.id.code_text);

            return rootView;
        }

        private void setText(List<String> commands) {
            StringBuilder commandsText = new StringBuilder();
            for (String command : commands) {
                commandsText.append(command).append("\n");
            }
            commandsText.setLength(commandsText.length() - 1);

            if (mCodeText != null) {
                mCodeText.setText(commandsText.toString());
            }
        }

    }

}
