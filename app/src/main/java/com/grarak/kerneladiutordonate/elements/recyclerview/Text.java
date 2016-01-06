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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutordonate.R;

/**
 * Created by willi on 02.01.16.
 */
public class Text implements Adapter.RecyclerItem {

    private TextOptions textOptions;

    public interface TextOptions {
        CharSequence getTitle(Text text);

        CharSequence getSummary(Text text);
    }

    private TextView mTitleView;
    private TextView mSummaryView;

    public Text(TextOptions textOptions) {
        this.textOptions = textOptions;
    }

    @Override
    public void onBindView(View view) {
        mTitleView = (TextView) view.findViewById(R.id.text_title);
        mSummaryView = (TextView) view.findViewById(R.id.text_summary);
        refresh();
    }

    @Override
    public View getView(LayoutInflater layoutInflater, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.views_text, parent, false);
    }

    @Override
    public void refresh() {
        if (mTitleView != null) {
            CharSequence title = textOptions.getTitle(this);
            if (title != null) mTitleView.setText(title);
            else mTitleView.setVisibility(View.GONE);
        }

        if (mSummaryView != null) {
            CharSequence summary = textOptions.getSummary(this);
            if (summary != null) mSummaryView.setText(summary);
            else mSummaryView.setVisibility(View.GONE);
        }
    }
}
