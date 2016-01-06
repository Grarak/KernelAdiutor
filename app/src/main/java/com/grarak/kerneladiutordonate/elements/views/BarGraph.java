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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.grarak.kerneladiutordonate.R;
import com.grarak.kerneladiutordonate.utils.Utils;

/**
 * Created by willi on 04.01.16.
 */
public class BarGraph extends View {

    private Paint mPaint;
    private int mPercentage;

    public BarGraph(Context context) {
        this(context, null);
    }

    public BarGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BarGraph, defStyleAttr, 0);
        mPaint.setColor(a.getColor(R.styleable.BarGraph_barcolor, Utils.getThemeAccentColor(context)));
        mPercentage = a.getInteger(R.styleable.BarGraph_barpercentage, 0);

        a.recycle();
    }

    public void setBarPercentage(int percentage) {
        mPercentage = percentage;
        invalidate();
    }

    public void setBarColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        boolean isRTL = Utils.isRTL(this);

        float left = 0;
        float right = mPercentage <= 0 ? 0 : ((float) mPercentage / 100) * width;
        if (isRTL) {
            left = mPercentage <= 0 ? 0 : ((float) (100 - mPercentage) / 100) * width;
            right = 0;
        }
        float top = 0;

        canvas.drawRect(left, top, right, (float) height, mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float desiredWidth = getResources().getDisplayMetrics().widthPixels;
        float desiredHeight = getResources().getDimension(R.dimen.bargraph_bar_height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float width;
        float height;

        if (widthMode == MeasureSpec.EXACTLY) width = widthSize;
        else if (widthMode == MeasureSpec.AT_MOST) width = Math.min(desiredWidth, widthSize);
        else width = desiredWidth;

        if (heightMode == MeasureSpec.EXACTLY) height = heightSize;
        else if (heightMode == MeasureSpec.AT_MOST) height = Math.min(desiredHeight, heightSize);
        else height = desiredHeight;

        setMeasuredDimension((int) width, (int) height);
    }

}
