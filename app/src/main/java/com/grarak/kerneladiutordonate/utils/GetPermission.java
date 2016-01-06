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
package com.grarak.kerneladiutordonate.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by willi on 28.12.15.
 */
public class GetPermission {

    private Activity activity;
    private String[] permissions;
    private static PermissionCallBack permissionCallBack;

    public GetPermission(Activity activity, String... permissions) {
        this.activity = activity;
        this.permissions = permissions;
    }

    public interface PermissionCallBack {
        void granted(String permission);

        void denied(String permission);
    }

    public void ask(PermissionCallBack permissionCallBack) {
        GetPermission.permissionCallBack = permissionCallBack;
        PermissionActivity.permissions = permissions;
        activity.startActivity(new Intent(activity, PermissionActivity.class));
    }

    public static class PermissionActivity extends Activity {

        private static String[] permissions;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            for (int i = 0; i < permissions.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(permissions[i])) {
                            permissionCallBack.denied(permissions[i]);
                            if (permissions.length == i + 1) finish();
                        } else {
                            requestPermissions(permissions, i + 1);
                        }
                    } else {
                        permissionCallBack.granted(permissions[i]);
                        if (permissions.length == i + 1) finish();
                    }
                } else {
                    permissionCallBack.granted(permissions[i]);
                    finish();
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (permissions.length >= requestCode) {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCallBack.granted(permissions[requestCode - 1]);
                } else {
                    permissionCallBack.denied(permissions[requestCode - 1]);
                }
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

            if (requestCode == permissions.length) finish();
        }

    }

}
