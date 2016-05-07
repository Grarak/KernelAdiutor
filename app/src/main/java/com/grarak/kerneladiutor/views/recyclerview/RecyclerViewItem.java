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

import android.support.annotation.LayoutRes;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by willi on 24.04.16.
 */
public abstract class RecyclerViewItem {

    private boolean mFullspan;
    private View mView;

    public interface OnViewChangeListener {
        void onViewChanged();
    }

    public interface OnItemClickListener {
        void onClick(RecyclerViewItem item);
    }

    private OnViewChangeListener mOnViewChangeListener;
    private OnItemClickListener mOnItemClickListener;

    public void onCreateView(View view) {
        mView = view;
        refresh();
    }

    @LayoutRes
    public abstract int getLayoutRes();

    public void onCreateHolder(ViewGroup parent) {
    }

    public void setOnViewChangeListener(OnViewChangeListener onViewChangeListener) {
        mOnViewChangeListener = onViewChangeListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;

    }

    protected void viewChanged() {
        if (mOnViewChangeListener != null) {
            mOnViewChangeListener.onViewChanged();
        }
    }

    protected OnViewChangeListener getOnViewChangeListener() {
        return mOnViewChangeListener;
    }

    protected OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setFullSpan(boolean fullspan) {
        mFullspan = fullspan;
        refresh();
    }

    protected void refresh() {
        if (mFullspan && mView != null) {
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            mView.setLayoutParams(layoutParams);
        }
    }

}
