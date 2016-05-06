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
package com.grarak.kerneladiutor.database;

import com.grarak.kerneladiutor.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 15.04.15.
 */
public abstract class Provider {

    /**
     * JSON Objects
     */
    private JSONObject databaseMain;
    private JSONArray databaseItems;

    /**
     * JSON file location
     */
    private final String path;

    /**
     * JSON Database is used to store large amount of datasets
     *
     * @param path    location of the JSON file
     * @param version If version doesn't match with the dataset, remove all saved datas
     */
    public Provider(String path, int version) {
        this.path = path;
        try {
            String json = Utils.readFile(path, false);
            if (json != null) {
                databaseMain = new JSONObject(json);
                if (databaseMain.getInt("version") == version) {
                    databaseItems = databaseMain.getJSONArray("database");
                }
            }
        } catch (JSONException ignored) {
        }

        if (databaseItems == null) {
            databaseItems = new JSONArray();
        }
        try {
            databaseMain = new JSONObject();
            databaseMain.put("version", version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a dataset
     *
     * @param items the dataset will put into the JSONArray
     */
    public void putItem(JSONObject items) {
        databaseItems.put(items);
    }

    /**
     * Read all sets
     *
     * @return all sets in a list
     */
    public List<DBJsonItem> getAllItems() {
        List<DBJsonItem> items = new ArrayList<>();
        try {
            for (int i = 0; i < length(); i++) {
                items.add(getItem(databaseItems.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    public abstract DBJsonItem getItem(JSONObject item);

    public void delete(int position) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < length(); i++) {
                if (i != position) {
                    jsonArray.put(databaseItems.getJSONObject(i));
                }
            }
            databaseItems = jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int length() {
        return databaseItems.length();
    }

    /**
     * Write the dataset as JSON file
     */
    public void commit() {
        try {
            databaseMain.put("database", databaseItems);
            Utils.writeFile(path, databaseMain.toString(), false, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class DBJsonItem {

        protected JSONObject item;

        public DBJsonItem() {
            item = new JSONObject();
        }

        public JSONObject getItem() {
            return item;
        }

    }

}