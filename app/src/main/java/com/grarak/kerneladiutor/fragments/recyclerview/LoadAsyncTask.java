/*
 * Copyright (C) 2018 Willi Ye <williye97@gmail.com>
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
package com.grarak.kerneladiutor.fragments.recyclerview;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * Created by willi on 08.03.18.
 */

class LoadAsyncTask<T extends RecyclerViewFragment, RESULT> extends AsyncTask<Void, Void, RESULT> {

    public abstract static class LoadHandler<T extends RecyclerViewFragment, RESULT> {
        public void onPreExecute(T fragment) {
        }

        public abstract RESULT doInBackground(T fragment);

        public void onPostExecute(T fragment, RESULT result) {
        }
    }

    private WeakReference<T> mRefFragment;
    private LoadHandler<T, RESULT> mListener;

    LoadAsyncTask(T fragment, @NonNull LoadHandler<T, RESULT> listener) {
        mRefFragment = new WeakReference<>(fragment);
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        T fragment = mRefFragment.get();
        if (fragment != null) {
            mListener.onPreExecute(fragment);
        }
    }

    @Override
    protected RESULT doInBackground(Void... voids) {
        T fragment = mRefFragment.get();
        if (fragment != null) {
            return mListener.doInBackground(fragment);
        }
        return null;
    }

    @Override
    protected void onPostExecute(RESULT result) {
        super.onPostExecute(result);

        T fragment = mRefFragment.get();
        if (fragment != null) {
            mListener.onPostExecute(fragment, result);
        }
    }
}
