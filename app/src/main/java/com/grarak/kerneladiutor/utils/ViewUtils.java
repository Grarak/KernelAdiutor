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
package com.grarak.kerneladiutor.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 16.04.16.
 */
public class ViewUtils {

    public interface OnDialogEditTextListener {
        void onClick(String text);
    }

    public static AlertDialog.Builder dialogEditText(String text, DialogInterface.OnClickListener negativeListener,
                                                     final OnDialogEditTextListener onDialogEditTextListener,
                                                     Activity activity) {
        LinearLayout layout = new LinearLayout(activity);
        int padding = (int) activity.getResources().getDimension(R.dimen.dialog_edittext_padding);
        layout.setPadding(padding, padding, padding, padding);

        final EditText editText = new EditText(activity);
        editText.setGravity(Gravity.CENTER);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.setText(text);

        layout.addView(editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(layout);
        if (negativeListener != null) {
            builder.setNegativeButton(activity.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDialogEditTextListener.onClick(editText.getText().toString());
                }
            });
        }
        return builder;
    }

    public static AlertDialog.Builder dialogBuilder(CharSequence message, DialogInterface.OnClickListener negativeListener,
                                                    DialogInterface.OnClickListener positiveListener,
                                                    DialogInterface.OnDismissListener dismissListener, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        if (negativeListener != null) {
            builder.setNegativeButton(activity.getString(R.string.cancel), negativeListener);
        }
        if (positiveListener != null) {
            builder.setPositiveButton(activity.getString(R.string.ok), positiveListener);
        }
        if (dismissListener != null) {
            builder.setOnDismissListener(dismissListener);
        }
        return builder;
    }

    public static Bitmap scaleDownBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newWidth = width;
        int newHeight = height;

        if (maxWidth != 0 && newWidth > maxWidth) {
            newHeight = Math.round((float) maxWidth / newWidth * newHeight);
            newWidth = maxWidth;
        }

        if (maxHeight != 0 && newHeight > maxHeight) {
            newWidth = Math.round((float) maxHeight / newHeight * newWidth);
            newHeight = maxHeight;
        }

        return width != newWidth || height != newHeight ? resizeBitmap(bitmap, newWidth, newHeight) : bitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

}
