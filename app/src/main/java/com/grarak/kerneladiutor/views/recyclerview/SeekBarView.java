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

import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Log;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 06.05.16.
 */
public class SeekBarView extends RecyclerViewItem {

    private static final String TAG = SeekBarView.class.getSimpleName();

    public interface OnSeekBarListener {
        void onStop(SeekBarView seekBarView, int position, String value);

        void onMove(SeekBarView seekBarView, int position, String value);
    }

    private AppCompatTextView mTitle;
    private AppCompatTextView mSummary;
    private AppCompatTextView mValue;
    private DiscreteSeekBar mSeekBar;

    private CharSequence mTitleText;
    private CharSequence mSummaryText;

    private int mMin;
    private int mMax = 100;
    private int mProgress;
    private String mUnit;
    private List<String> mItems = new ArrayList<>();
    private int mOffset = 1;
    private boolean mEnabled = true;

    private OnSeekBarListener mOnSeekBarListener;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_seekbar_view;
    }

    @Override
    public void onCreateView(final View view) {
        mTitle = view.findViewById(R.id.title);
        mSummary = view.findViewById(R.id.summary);
        mValue = view.findViewById(R.id.value);
        mSeekBar = view.findViewById(R.id.seekbar);

        view.findViewById(R.id.button_minus).setOnClickListener(v -> {
            mSeekBar.setProgress(mSeekBar.getProgress() - 1);
            if (mOnSeekBarListener != null && mProgress < mItems.size() && mProgress >= 0) {
                mOnSeekBarListener.onStop(SeekBarView.this, mProgress, mItems.get(mProgress));
            }
        });
        view.findViewById(R.id.button_plus).setOnClickListener(v -> {
            mSeekBar.setProgress(mSeekBar.getProgress() + 1);
            if (mOnSeekBarListener != null && mProgress < mItems.size() && mProgress >= 0) {
                mOnSeekBarListener.onStop(SeekBarView.this, mProgress, mItems.get(mProgress));
            }
        });

        mSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (value < mItems.size() && value >= 0) {
                    mProgress = value;
                    String text = mItems.get(value);
                    if (mUnit != null) text += mUnit;
                    mValue.setText(text);
                    if (mOnSeekBarListener != null) {
                        mOnSeekBarListener.onMove(
                                SeekBarView.this, mProgress, mItems.get(mProgress));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                try {
                    if (mOnSeekBarListener != null) {
                        mOnSeekBarListener.onStop(
                                SeekBarView.this, mProgress, mItems.get(mProgress));
                    }
                } catch (Exception e) {
                    Log.crashlyticsE(TAG, e.getMessage());
                }
            }
        });
        mSeekBar.setFocusable(false);

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummaryText = summary;
        refresh();
    }

    public void setProgress(int progress) {
        mProgress = progress;
        refresh();
    }

    public void setMin(int min) {
        mMin = min;
        mItems.clear();
        refresh();
    }

    public void setUnit(String unit) {
        mUnit = unit;
        mItems.clear();
        refresh();
    }

    public void setMax(int max) {
        mMax = max;
        mItems.clear();
        refresh();
    }

    public void setItems(List<String> items) {
        mItems.clear();
        mItems.addAll(items);
        refresh();
    }

    public void setOffset(int offset) {
        mOffset = offset;
        mItems.clear();
        refresh();
    }

    public void setEnabled(boolean enable) {
        mEnabled = enable;
        refresh();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setOnSeekBarListener(OnSeekBarListener onSeekBarListener) {
        mOnSeekBarListener = onSeekBarListener;
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null) {
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
                mTitle.setVisibility(View.VISIBLE);
            } else {
                mTitle.setVisibility(View.GONE);
            }
        }
        if (mSummary != null && mSummaryText != null) {
            mSummary.setText(mSummaryText);
        }
        if (mItems.size() == 0) {
            for (int i = mMin; i <= mMax; i += mOffset) {
                mItems.add(String.valueOf(i));
            }
        }
        if (mSeekBar != null) {
            mSeekBar.setMax(mItems.size() - 1);
            mSeekBar.setMin(0);
            mSeekBar.setEnabled(mEnabled);
            if (mValue != null) {
                try {
                    String text = mItems.get(mProgress);
                    mSeekBar.setProgress(mProgress);
                    if (mUnit != null) text += mUnit;
                    mValue.setText(text);
                } catch (Exception ignored) {
                    mValue.setText(mValue.getResources().getString(R.string.not_in_range));
                }
            }
        }
    }
}
