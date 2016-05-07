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

import android.support.v7.widget.AppCompatEditText;
import android.view.View;

import com.grarak.kerneladiutordonate.R;

/**
 * Created by willi on 05.05.16.
 */
public class GenericSelectView extends Expander {

    public interface OnGenericValueListener {
        void onGenericValueSelected(GenericSelectView genericSelectView, String value);
    }

    private AppCompatEditText mEditText;

    private CharSequence mEditTextText;
    private int mInputType = -1;
    private OnGenericValueListener mOnGenericValueListener;

    public GenericSelectView() {
        super(R.dimen.rv_generic_select_view_selector_height, R.layout.rv_generic_select_view);
    }

    @Override
    protected void onCreateExpandView(View view) {
        mEditText = (AppCompatEditText) view;

        refresh();
    }

    @Override
    public void setValue(CharSequence value) {
        super.setValue(value);
        refresh();
    }

    public void setValueRaw(String value) {
        mEditTextText = value;
        refresh();
    }

    public void setInputType(int type) {
        mInputType = type;
        refresh();
    }

    public void setOnGenericValueListener(OnGenericValueListener onGenericValueListener) {
        mOnGenericValueListener = onGenericValueListener;
    }

    @Override
    protected void onCollapse() {
        super.onCollapse();
        if (mOnGenericValueListener != null && mEditText != null) {
            setValueRaw(mEditText.getText().toString());
            mOnGenericValueListener.onGenericValueSelected(this, mEditText.getText().toString());
        }
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mEditTextText != null && mEditText != null) {
            mEditText.setText(mEditTextText);
        }
        if (mEditText != null && mInputType >= 0) {
            mEditText.setInputType(mInputType);
        }
    }
}
