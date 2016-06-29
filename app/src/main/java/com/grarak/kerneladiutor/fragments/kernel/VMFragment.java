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

import android.text.InputType;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseControlFragment;
import com.grarak.kerneladiutor.utils.kernel.vm.VM;
import com.grarak.kerneladiutor.views.recyclerview.GenericSelectView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class VMFragment extends BaseControlFragment {

    private HashMap<Integer, GenericSelectView> mVMs = new HashMap<>();

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.VM));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mVMs.clear();
        for (int i = 0; i < VM.size(); i++) {
            if (VM.exists(i)) {
                GenericSelectView vm = new GenericSelectView();
                vm.setSummary(VM.getName(i));
                vm.setValue(VM.getValue(i));
                vm.setValueRaw(vm.getValue());
                vm.setInputType(InputType.TYPE_CLASS_NUMBER);

                final int position = i;
                vm.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                    @Override
                    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                        VM.setValue(value, position, getActivity());
                        genericSelectView.setValue(value);
                        refreshVMs();
                    }
                });

                items.add(vm);
                mVMs.put(i, vm);
            }
        }
    }

    private void refreshVMs() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int position : mVMs.keySet()) {
                    mVMs.get(position).setValue(VM.getValue(position));
                    mVMs.get(position).setValueRaw(mVMs.get(position).getValue());
                }
            }
        }, 250);
    }

}
