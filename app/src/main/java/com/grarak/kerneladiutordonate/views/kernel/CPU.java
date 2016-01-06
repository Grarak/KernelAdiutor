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
package com.grarak.kerneladiutordonate.views.kernel;

import android.content.Context;
import android.os.Bundle;

import com.grarak.kerneladiutordonate.elements.recyclerview.Adapter;
import com.grarak.kerneladiutordonate.fragments.RecyclerViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 31.12.15.
 */
public class CPU extends RecyclerViewFragment.ViewInterface {

    public CPU(Context context) {
        super(context);
    }

    @Override
    public List<Adapter.RecyclerItem> getViews(Bundle savedInstanceState) {
        return new ArrayList<>();
    }

}
