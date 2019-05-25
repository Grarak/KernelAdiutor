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

import android.support.design.widget.Snackbar;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.recyclerview.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootFile;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.util.List;

/**
 * Created by willi on 04.05.16.
 */
public class PathReaderFragment extends RecyclerViewFragment {

    private String mPath;
    private int mMin;
    private int mMax;
    private String mError;
    private String mCategory;

    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected boolean isForeground() {
        return true;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
    }

    public void setPath(String path, String category) {
        setPath(path, -1, -1, category);
    }

    public void setPath(String path, int min, int max, String category) {
        mPath = path;
        mMin = min;
        mMax = max;
        mCategory = category;
        reload();
    }

    public void setError(String error) {
        mError = error;
    }

    private void reload() {
        getHandler().postDelayed(() -> {
            clearItems();
            reload(new ReloadHandler());
        }, 250);
    }

    private static class ReloadHandler extends RecyclerViewFragment.ReloadHandler<PathReaderFragment> {
        @Override
        public void onPostExecute(PathReaderFragment fragment, List<RecyclerViewItem> items) {
            super.onPostExecute(fragment, items);
            if (fragment.itemsSize() < 1 && fragment.mError != null) {
                Snackbar.make(fragment.getRootView(),
                        fragment.mError, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void load(List<RecyclerViewItem> items) {
        super.load(items);

        if (mPath == null) return;
        String path = mPath;
        if (path.contains("%d")) {
            path = Utils.strFormat(mPath, mMin);
        }
        RootFile files = new RootFile(path);
        for (final RootFile file : files.listFiles()) {
            final String name = file.getName();
            final String value = file.readFile();
            if (value != null && !value.isEmpty() && !value.contains("\n")) {
                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setTitle(name);
                descriptionView.setSummary(value);
                descriptionView.setOnItemClickListener(item -> {
                    List<Integer> freqs = CPUFreq.getInstance(getActivity()).getFreqs(mMin);
                    int freq = Utils.strToInt(value);
                    if (freqs != null && freq != 0 && freqs.contains(freq)) {
                        String[] values = new String[freqs.size()];
                        for (int i = 0; i < values.length; i++) {
                            values[i] = String.valueOf(freqs.get(i));
                        }
                        showArrayDialog(value, values, mPath + "/" + name, name);
                    } else {
                        showEditTextDialog(value, name);
                    }
                });
                items.add(descriptionView);
            }
        }
    }

    private void showArrayDialog(final String value, final String[] values, final String path,
                                 final String name) {
        new Dialog(getActivity()).setItems(
                getResources().getStringArray(R.array.path_reader_options),
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            new Dialog(getActivity()).setItems(values, (dialog1, which1) -> {
                                run(path, values[which1], path);
                                reload();
                            }).setTitle(name).show();
                            break;
                        case 1:
                            showEditTextDialog(value, name);
                            break;
                    }
                }).show();
    }

    private void showEditTextDialog(String value, final String name) {
        ViewUtils.dialogEditText(value, (dialog, which) -> {
        }, text -> {
            run(mPath + "/" + name, text, mPath + "/" + name);
            reload();
        }, getActivity()).show();
    }

    private void run(String path, String value, String id) {
        if (ApplyOnBootFragment.CPU.equals(mCategory) && mPath.contains("%d")) {
            CPUFreq.getInstance(getActivity()).applyCpu(path, value, mMin, mMax, getActivity());
        } else {
            Control.runSetting(Control.write(value, path), mCategory, id, getActivity());
        }
    }

}
