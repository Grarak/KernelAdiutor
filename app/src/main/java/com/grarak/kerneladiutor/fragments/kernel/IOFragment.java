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
package com.grarak.kerneladiutor.fragments.kernel;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.io.IO;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 27.06.16.
 */
public class IOFragment extends RecyclerViewFragment {

    private PathReaderFragment mIOTunableFragment;

    @Override
    protected BaseFragment getForegroundFragment() {
        return mIOTunableFragment = new PathReaderFragment();
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        storageInit(IO.Storage.Internal, items);
        if (IO.hasExternal()) {
            storageInit(IO.Storage.External, items);
        }
    }

    private void storageInit(final IO.Storage storage, List<RecyclerViewItem> items) {
        CardView ioCard = new CardView(getActivity());
        ioCard.setTitle(getString(storage == IO.Storage.Internal ? R.string.internal_storage
                : R.string.external_storage));

        if (IO.hasScheduler(storage)) {
            SelectView scheduler = new SelectView();
            scheduler.setTitle(getString(R.string.scheduler));
            scheduler.setSummary(getString(R.string.scheduler_summary));
            scheduler.setItems(IO.getSchedulers(storage));
            scheduler.setItem(IO.getScheduler(storage));
            scheduler.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    IO.setScheduler(storage, item, getActivity());
                }
            });

            ioCard.addItem(scheduler);

            DescriptionView tunable = new DescriptionView();
            tunable.setTitle(getString(R.string.scheduler_tunable));
            tunable.setSummary(getString(R.string.scheduler_tunable_summary));
            tunable.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    showTunables(IO.getScheduler(storage), IO.getIOSched(storage));
                }
            });

            ioCard.addItem(tunable);
        }

        if (IO.hasReadahead(storage)) {
            SeekBarView readahead = new SeekBarView();
            readahead.setTitle(getString(R.string.read_ahead));
            readahead.setSummary(getString(R.string.read_ahead_summary));
            readahead.setUnit(getString(R.string.kb));
            readahead.setMax(8192);
            readahead.setMin(128);
            readahead.setOffset(128);
            readahead.setProgress(IO.getReadahead(storage) / 128 - 1);
            readahead.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    IO.setReadahead(storage, (position + 1) * 128, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            ioCard.addItem(readahead);
        }

        if (IO.hasRotational(storage)) {
            SwitchView rotational = new SwitchView();
            rotational.setTitle(getString(R.string.rotational));
            rotational.setSummary(getString(R.string.rotational_summary));
            rotational.setChecked(IO.isRotationalEnabled(storage));
            rotational.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    IO.enableRotational(storage, isChecked, getActivity());
                }
            });

            ioCard.addItem(rotational);
        }

        if (IO.hasIOStats(storage)) {
            SwitchView iostats = new SwitchView();
            iostats.setTitle(getString(R.string.iostats));
            iostats.setSummary(getString(R.string.iostats_summary));
            iostats.setChecked(IO.isIOStatsEnabled(storage));
            iostats.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    IO.enableIOstats(storage, isChecked, getActivity());
                }
            });

            ioCard.addItem(iostats);
        }

        if (IO.hasAddRandom(storage)) {
            SwitchView addRandom = new SwitchView();
            addRandom.setTitle(getString(R.string.add_random));
            addRandom.setSummary(getString(R.string.add_random_summary));
            addRandom.setChecked(IO.isAddRandomEnabled(storage));
            addRandom.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    IO.enableAddRandom(storage, isChecked, getActivity());
                }
            });

            ioCard.addItem(addRandom);
        }

        if (ioCard.size() > 0) {
            items.add(ioCard);
        }
    }

    private void showTunables(String scheduler, String path) {
        setForegroundText(scheduler);
        mIOTunableFragment.setError(getString(R.string.tunables_error, scheduler));
        mIOTunableFragment.setPath(path, ApplyOnBootFragment.IO);
        showForeground();
    }

}
