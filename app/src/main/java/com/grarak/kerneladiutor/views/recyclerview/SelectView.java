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

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

import java.util.List;

/**
 * Created by willi on 01.05.16.
 */
public class SelectView extends Expander {

    public interface OnItemSelected {
        void onItemSelected(SelectView selectView, int position, String item);
    }

    private LinearLayout mLayout;
    private List<String> mItems;
    private List<String> mItemsTmp;
    private OnItemSelected mOnItemSelected;

    public SelectView() {
        super(R.dimen.rv_select_view_selector_height, R.layout.rv_select_view);
    }

    public void setItem(int index) {
        if (mItems != null) {
            setItem(mItems.get(index));
        } else if (mItemsTmp != null) {
            setItem(mItemsTmp.get(index));
        }
    }

    public void setItem(CharSequence text) {
        setValue(text);
    }

    public void setItems(List<String> items) {
        mItems = items;
        refresh();
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        mOnItemSelected = onItemSelected;
    }

    @Override
    protected void onCreateExpandView(View view) {
        mLayout = (LinearLayout) view.findViewById(R.id.scrolllayout);

        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mLayout != null && mItems != null) {
            mLayout.removeAllViews();
            for (int i = 0; i < mItems.size(); i++) {
                final int position = i;
                View view = LayoutInflater.from(mLayout.getContext()).inflate(R.layout.rv_select_view_child,
                        mLayout, false);
                ((TextView) view.findViewById(R.id.text)).setText(mItems.get(i));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemSelected != null) {
                            mOnItemSelected.onItemSelected(SelectView.this, position, mItemsTmp.get(position));
                        }
                        setItem(position);
                    }
                });
                mLayout.addView(view);
            }
            mItemsTmp = mItems;
            mItems = null;
        }
    }
}
