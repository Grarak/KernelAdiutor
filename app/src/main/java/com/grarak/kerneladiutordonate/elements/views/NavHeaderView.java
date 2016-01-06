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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.grarak.kerneladiutordonate.R;
import com.grarak.kerneladiutordonate.utils.Prefs;
import com.grarak.kerneladiutordonate.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by willi on 28.12.15.
 */
public class NavHeaderView extends LinearLayout {

    private static ImageView image;

    public NavHeaderView(final Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.nav_header_main, this);
        image = (ImageView) findViewById(R.id.nav_header_pic);

        boolean noPic;
        try {
            String uri = Prefs.getString("previewpicture", null, image.getContext());
            if (uri == null || uri.equals("nopicture")) noPic = true;
            else {
                setImage(Uri.parse(uri));
                noPic = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            noPic = true;
        }

        if (noPic) Prefs.saveString("previewpicture", "nopicture", image.getContext());

        findViewById(R.id.nav_header_fab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext()).setItems(v.getResources()
                        .getStringArray(R.array.main_header_picture_items), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                v.getContext().startActivity(new Intent(v.getContext(), MainHeaderActivity.class));
                                break;
                            case 1:
                                if (Prefs.getString("previewpicture", null, v.getContext()).equals("nopicture"))
                                    return;
                                Prefs.saveString("previewpicture", "nopicture", v.getContext());
                                image.setImageDrawable(null);
                                animateBg();
                                break;
                        }

                    }
                }).show();
            }
        });
    }

    public static class MainHeaderActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            } else {
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
            }
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), 0);
        }

        @Override
        protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && requestCode == 0)
                try {
                    Uri selectedImageUri = data.getData();
                    setImage(selectedImageUri);
                    Prefs.saveString("previewpicture", selectedImageUri.toString(), this);
                    animateBg();
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.toast(getString(R.string.went_wrong), MainHeaderActivity.this);
                }
            finish();
        }

    }

    public static void animateBg() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setVisibility(View.INVISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        ((Activity) image.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.circleAnimate(image, image.getWidth() / 2, image.getHeight() / 2);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void setImage(Uri uri) throws IOException, NullPointerException {
        String selectedImagePath = null;
        try {
            selectedImagePath = getPath(uri, image.getContext());
        } catch (Exception ignored) {
        }
        Bitmap bitmap;
        if ((bitmap = selectedImagePath != null ? BitmapFactory.decodeFile(selectedImagePath) :
                uriToBitmap(uri, image.getContext())) != null)
            image.setImageBitmap(Utils.scaleDownBitmap(bitmap, 1024, 1024));
        else throw new NullPointerException("Getting Bitmap failed");
    }

    private static Bitmap uriToBitmap(Uri uri, Context context) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        }
        throw new IOException();
    }

    private static String getPath(Uri uri, Context context) {
        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA},
                null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        } else return null;
    }

}
