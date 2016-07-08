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

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by willi on 06.07.16.
 */
public class WebpageReader extends AsyncTask<String, Void, String> {

    private static final String TAG = WebpageReader.class.getSimpleName();

    public interface WebpageCallback {
        void onCallback(String raw, CharSequence html);
    }

    private final WebpageCallback mWebpageCallback;
    private HttpURLConnection mConnection;
    private boolean mConnected;
    private boolean mCancelled;

    public WebpageReader(WebpageCallback webpageCallback) {
        this.mWebpageCallback = webpageCallback;
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        try {
            String line;
            URL url = new URL(params[0]);
            mConnection = (HttpURLConnection) url.openConnection();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mConnection.connect();
                        mConnected = true;
                    } catch (IOException e) {
                        mCancelled = true;
                    }
                }
            }).start();
            while (true)
                if (mConnected) {
                    if (mConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return "";
                    }
                    is = mConnection.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is));

                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    break;
                } else if (mCancelled) {
                    mConnection.disconnect();
                    return "";
                }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Failed to read url: " + params[0]);
        } finally {
            try {
                if (is != null) is.close();
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mConnection != null) {
                mConnection.disconnect();
            }
        }
        return sb.toString().trim();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        mWebpageCallback.onCallback(s, Utils.htmlFrom(s));
    }

    public void cancel() {
        mCancelled = true;
    }

}
