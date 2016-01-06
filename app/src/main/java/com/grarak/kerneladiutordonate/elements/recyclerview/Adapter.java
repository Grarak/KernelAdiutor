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
package com.grarak.kerneladiutordonate.elements.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by willi on 01.01.16.
 */
public class Adapter extends RecyclerView.Adapter {

    public interface RecyclerItem {
        void onBindView(View view);

        View getView(LayoutInflater layoutInflater, ViewGroup parent);

        void refresh();
    }

    private List<RecyclerItem> recyclerItemList;

    public Adapter(List<RecyclerItem> recyclerItemList) {
        this.recyclerItemList = recyclerItemList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        recyclerItemList.get(position).onBindView(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return recyclerItemList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(recyclerItemList.get(viewType)
                .getView(LayoutInflater.from(parent.getContext()), parent)) {
        };
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addView(RecyclerItem recyclerItem) {
        recyclerItemList.add(recyclerItem);
        notifyDataSetChanged();
    }

}
