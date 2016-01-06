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
package com.grarak.kerneladiutordonate.elements.views;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutordonate.R;

/**
 * Created by willi on 29.12.15.
 */
public class SendingBarView extends LinearLayout {

    private TextView mTitleText;
    private TextView mTitleTextRight;
    private TextView mSummaryText;
    private FloatingActionButton mSendingButton;
    private CardView mSendingCard;

    public SendingBarView(Context context) {
        this(context, null);
    }

    public SendingBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SendingBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(getContext()).inflate(R.layout.sending_bar, this);

        mTitleText = (TextView) findViewById(R.id.title_text);
        mTitleTextRight = (TextView) findViewById(R.id.title_text_right);
        mSummaryText = (TextView) findViewById(R.id.summary_text);

        mSendingButton = (FloatingActionButton) findViewById(R.id.sending_fab);

        mSendingCard = (CardView) findViewById(R.id.sending_card);
    }

    public void setTitle(int res) {
        setTitle(getContext().getString(res));
    }

    public void setTitle(CharSequence text) {
        mTitleText.setText(text);
    }

    public void setTitleRight(int res) {
        setTitleRight(getContext().getString(res));
    }

    public void setTitleRight(CharSequence text) {
        mTitleTextRight.setText(text);
    }

    public void setSummary(int res) {
        setSummary(getContext().getString(res));
    }

    public void setSummary(CharSequence text) {
        mSummaryText.setText(text);
    }

    public CharSequence getTitleRight() {
        return mTitleTextRight.getText();
    }

    public CharSequence getSummary() {
        return mSummaryText.getText();
    }

    public CardView getCard() {
        return mSendingCard;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mSendingButton.setOnClickListener(l);
    }
}
