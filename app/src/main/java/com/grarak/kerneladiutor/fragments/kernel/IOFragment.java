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
import com.grarak.kerneladiutor.fragments.recyclerview.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.io.IO;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 27.06.16.
 */
public class IOFragment extends RecyclerViewFragment {

    private PathReaderFragment mIOTunableFragment;
    private IO mIO;

    @Override
    protected BaseFragment getForegroundFragment() {
        return mIOTunableFragment = new PathReaderFragment();
    }

    @Override
    protected void init() {
        super.init();

        mIO = IO.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        storageInit(IO.Storage.Internal, items);
        if (mIO.hasExternal()) {
            storageInit(IO.Storage.External, items);
        }
    }

    private void storageInit(final IO.Storage storage, List<RecyclerViewItem> items) {
        List<RecyclerViewItem> views = new ArrayList<>();
        TitleView title = new TitleView();
        title.setText(getString(storage == IO.Storage.Internal ? R.string.internal_storage
                : R.string.external_storage));

        if (mIO.hasScheduler(storage)) {
            SelectView scheduler = new SelectView();
            scheduler.setTitle(getString(R.string.scheduler));
            scheduler.setSummary(getString(R.string.scheduler_summary));
            scheduler.setItems(mIO.getSchedulers(storage));
            scheduler.setItem(mIO.getScheduler(storage));
            scheduler.setOnItemSelected((selectView, position, item)
                    -> mIO.setScheduler(storage, item, getActivity()));

            views.add(scheduler);

            DescriptionView tunable = new DescriptionView();
            tunable.setTitle(getString(R.string.scheduler_tunable));
            tunable.setSummary(getString(R.string.scheduler_tunable_summary));
            tunable.setOnItemClickListener(item
                    -> showTunables(mIO.getScheduler(storage), mIO.getIOSched(storage)));

            views.add(tunable);
        }

        if (mIO.hasReadahead(storage)) {
            SeekBarView readahead = new SeekBarView();
            readahead.setTitle(getString(R.string.read_ahead));
            readahead.setSummary(getString(R.string.read_ahead_summary));
            readahead.setUnit(getString(R.string.kb));
            readahead.setMax(8192);
            readahead.setMin(128);
            readahead.setOffset(128);
            readahead.setProgress(mIO.getReadahead(storage) / 128 - 1);
            readahead.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIO.setReadahead(storage, (position + 1) * 128, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            views.add(readahead);
        }

        if (mIO.hasRotational(storage)) {
            SwitchView rotational = new SwitchView();
            rotational.setTitle(getString(R.string.rotational));
            rotational.setSummary(getString(R.string.rotational_summary));
            rotational.setChecked(mIO.isRotationalEnabled(storage));
            rotational.addOnSwitchListener((switchView, isChecked)
                    -> mIO.enableRotational(storage, isChecked, getActivity()));

            views.add(rotational);
        }

        if (mIO.hasIOStats(storage)) {
            SwitchView iostats = new SwitchView();
            iostats.setTitle(getString(R.string.iostats));
            iostats.setSummary(getString(R.string.iostats_summary));
            iostats.setChecked(mIO.isIOStatsEnabled(storage));
            iostats.addOnSwitchListener((switchView, isChecked)
                    -> mIO.enableIOstats(storage, isChecked, getActivity()));

            views.add(iostats);
        }

        if (mIO.hasAddRandom(storage)) {
            SwitchView addRandom = new SwitchView();
            addRandom.setTitle(getString(R.string.add_random));
            addRandom.setSummary(getString(R.string.add_random_summary));
            addRandom.setChecked(mIO.isAddRandomEnabled(storage));
            addRandom.addOnSwitchListener((switchView, isChecked)
                    -> mIO.enableAddRandom(storage, isChecked, getActivity()));

            views.add(addRandom);
        }

        if (mIO.hasRqAffinity(storage)) {
            SeekBarView rqAffinity = new SeekBarView();
            rqAffinity.setTitle(getString(R.string.rq_affitiny));
            rqAffinity.setSummary(getString(R.string.rq_affinity_summary));
            rqAffinity.setMax(2);
            rqAffinity.setProgress(mIO.getRqAffinity(storage));
            rqAffinity.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIO.setRqAffinity(storage, position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            views.add(rqAffinity);
        }

        if (views.size() > 0) {
            items.add(title);
            items.addAll(views);
        }
    }

    private void showTunables(String scheduler, String path) {
        setForegroundText(scheduler);
        mIOTunableFragment.setError(getString(R.string.tunables_error, scheduler));
        mIOTunableFragment.setPath(path, ApplyOnBootFragment.IO);
        showForeground();
    }

}
