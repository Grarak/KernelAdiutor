//-----------------------------------------------------------------------------
//
// (C) Brandon Valosek, 2011 <bvalosek@gmail.com>
// (C) Willi Ye, 2015 <williye97@gmail.com>
//
//-----------------------------------------------------------------------------
// Modified by Willi Ye to work with big.LITTLE

package com.bvalosek.cpuspy;

import android.content.Context;
import android.util.SparseArray;

import com.grarak.kerneladiutor.utils.AppSettings;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * main application class
 */
public class CpuSpyApp {

    private final int mCore;
    private final Context mContext;

    /**
     * the long-living object used to monitor the system frequency states
     */
    private final CpuStateMonitor mMonitor;

    public CpuSpyApp(int core, Context context) {
        mCore = core;
        mContext = context;
        mMonitor = new CpuStateMonitor(core);
        loadOffsets();
    }

    /**
     * @return the internal CpuStateMonitor object
     */
    public CpuStateMonitor getCpuStateMonitor() {
        return mMonitor;
    }

    /**
     * Load the saved string of offsets from preferences and put it into the
     * state monitor
     */
    private void loadOffsets() {
        String prefs = AppSettings.getCpuSpyOffsets(mCore, mContext);

        if (prefs.isEmpty()) return;
        // split the string by peroids and then the info by commas and load
        SparseArray<Long> offsets = new SparseArray<>();
        String[] sOffsets = prefs.split(",");
        for (String offset : sOffsets) {
            String[] parts = offset.split(" ");
            offsets.put(Utils.strToInt(parts[0]), Utils.strToLong(parts[1]));
        }

        mMonitor.setOffsets(offsets);
    }

    /**
     * Save the state-time offsets as a string e.g. "100 24, 200 251, 500 124
     * etc
     */
    public void saveOffsets() {
        // build the string by iterating over the freq->duration map
        StringBuilder str = new StringBuilder();
        SparseArray<Long> offsets = mMonitor.getOffsets();
        for (int i = 0; i < offsets.size(); i++) {
            str.append(offsets.keyAt(i)).append(" ").append(offsets.valueAt(i)).append(",");
        }

        AppSettings.saveCpuSpyOffsets(str.toString(), mCore, mContext);
    }
}
