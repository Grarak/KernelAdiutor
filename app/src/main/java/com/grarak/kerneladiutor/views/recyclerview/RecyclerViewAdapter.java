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
package com.grarak.kerneladiutor.views.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by willi on 17.04.16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<RecyclerViewItem> mItems;
    private final RecyclerViewItem.OnViewChangeListener mOnViewChangeListener;

    public RecyclerViewAdapter(List<RecyclerViewItem> items,
                               RecyclerViewItem.OnViewChangeListener onViewChangeListener) {
        mItems = items;
        mOnViewChangeListener = onViewChangeListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mItems.get(position).setOnViewChangeListener(mOnViewChangeListener);
        mItems.get(position).onCreateView(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        mItems.get(position).onCreateHolder(parent);
        return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                mItems.get(position).getLayoutRes(), parent, false)) {
        };
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
