/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.fragments.kernel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.elements.DDivider;
import com.grarak.kerneladiutor.elements.cards.CardViewItem;
import com.grarak.kerneladiutor.elements.cards.PopupCardView;
import com.grarak.kerneladiutor.elements.cards.SeekBarCardView;
import com.grarak.kerneladiutor.elements.cards.SwitchCardView;
import com.grarak.kerneladiutor.elements.cards.UsageCardView;
import com.grarak.kerneladiutor.fragments.PathReaderFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.fragments.ViewPagerFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.root.Control;
import com.kerneladiutor.library.root.RootFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by willi on 07.04.15.
 */
public class CPUFragment extends ViewPagerFragment implements Constants {

    private static CPUFragment cpuFragment;
    private CPUPart cpuPart;
    private GovernorPart governorPart;
    private int core;

    @Override
    public void preInit(Bundle savedInstanceState) {
        super.preInit(savedInstanceState);
        showTabs(false);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        cpuFragment = this;

        allowSwipe(false);
        addFragment(new ViewPagerItem(cpuPart == null ? cpuPart = new CPUPart() : cpuPart, null));
        addFragment(new ViewPagerItem(governorPart == null ? governorPart = new GovernorPart() : governorPart, null));
    }

    @Override
    public void onSwipe(int page) {
        super.onSwipe(page);
        allowSwipe(page == 1);
    }

    @Override
    public boolean onBackPressed() {
        if (getCurrentPage() == 1) {
            setCurrentItem(0);
            return true;
        }
        return false;
    }

    public static class CPUPart extends RecyclerViewFragment implements View.OnClickListener,
            PopupCardView.DPopupCard.OnDPopupCardListener, CardViewItem.DCardView.OnDCardListener,
            SeekBarCardView.DSeekBarCard.OnDSeekBarCardListener,
            SwitchCardView.DSwitchCard.OnDSwitchCardListener {

        List<DAdapter.DView> views = new ArrayList<>();
        List<String> freqs = new ArrayList<>();

        private UsageCardView.DUsageCard mUsageCard;

        private CardViewItem.DCardView mTempCard;

        private AppCompatCheckBox[] mCoreCheckBox;
        private ProgressBar[] mCoreProgressBar;
        private AppCompatTextView[] mCoreUsageText;
        private AppCompatTextView[] mCoreFreqText;

        private PopupCardView.DPopupCard mMaxFreqCard, mMinFreqCard, mMaxScreenOffFreqCard, mMSM_LimiterResumeMaxFreqNoPerCoreCard, mMSM_LimiterSuspendMinFreqNoPerCoreCard, mMSM_LimiterSuspendMaxFreqCard;
        private PopupCardView.DPopupCard mMSM_LimiterResumeMaxFreqCard[], mMSM_LimiterSuspendMinFreqCard[];

        private PopupCardView.DPopupCard mGovernorCard;
        private PopupCardView.DPopupCard mMSM_LimiterGovernorNoPerCoreCard, mMSM_LimiterGovernorPerCoreCard[];
        private CardViewItem.DCardView mGovernorTunableNoPerCoreCard;
        private CardViewItem.DCardView mGovernorTunableCoreCard[];

        private AppCompatCheckBox[] mCoreCheckBoxLITTLE;
        private ProgressBar[] mCoreProgressBarLITTLE;
        private AppCompatTextView[] mCoreUsageTextLITTLE;
        private AppCompatTextView[] mCoreFreqTextLITTLE;

        private PopupCardView.DPopupCard mMaxFreqLITTLECard, mMinFreqLITTLECard, mMaxScreenOffFreqLITTLECard;

        private PopupCardView.DPopupCard mGovernorLITTLECard;
        private CardViewItem.DCardView mGovernorTunableLITTLECard;

        private PopupCardView.DPopupCard mMcPowerSavingCard;

        private SwitchCardView.DSwitchCard mPowerSavingWqCard;

        private PopupCardView.DPopupCard mCFSSchedulerCard;

        private SwitchCardView.DSwitchCard mCpuQuietEnableCard;
        private PopupCardView.DPopupCard mCpuQuietGovernorCard;

        private SwitchCardView.DSwitchCard mCpuBoostEnableCard;
        private SwitchCardView.DSwitchCard mCpuBoostDebugMaskCard;
        private SeekBarCardView.DSeekBarCard mCpuBoostMsCard;
        private PopupCardView.DPopupCard mCpuBoostSyncThresholdCard;
        private SeekBarCardView.DSeekBarCard mCpuBoostInputMsCard;
        private PopupCardView.DPopupCard[] mCpuBoostInputFreqCard;
        private SwitchCardView.DSwitchCard mCpuBoostWakeupCard;
        private SwitchCardView.DSwitchCard mCpuBoostHotplugCard;
        private SwitchCardView.DSwitchCard mMSM_Limiter_EnableCard, mPerCoreControlCard;

        private SwitchCardView.DSwitchCard mCpuTouchBoostCard;

        @Override
        public String getClassName() {
            return CPUFragment.class.getSimpleName();
        }

        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);

            usageInit();
            if (CPU.hasTemp()) tempInit();
            if (CPU.getFreqs() != null) {
                if (CPU.isBigLITTLE()) {
                    DDivider bigDivider = new DDivider();
                    bigDivider.setText(getString(R.string.big).toLowerCase(Locale.getDefault()));
                    addView(bigDivider);
                }
                coreInit();
                freqInit();
            }
            if (CPU.getAvailableGovernors() != null) governorInit();
            DDivider othersDivider = null;
            if (CPU.isBigLITTLE()) {
                DDivider LITTLEDivider = new DDivider();
                LITTLEDivider.setText(getString(R.string.little).toUpperCase(Locale.getDefault()));
                addView(LITTLEDivider);

                if (CPU.getFreqs(CPU.getLITTLEcore()) != null) {
                    coreLITTLEInit();
                    freqLITTLEInit();
                }
                if (CPU.getAvailableGovernors(CPU.getLITTLEcore()) != null) governorLITTLEInit();

                othersDivider = new DDivider();
                othersDivider.setText(getString(R.string.other));
                addView(othersDivider);
            }
            int count = getCount();
            if (CPU.hasMcPowerSaving()) mcPowerSavingInit();
            if (CPU.hasPowerSavingWq()) powerSavingWqInit();
            if (CPU.hasCFSScheduler()) cfsSchedulerInit();
            if (CPU.hasCpuQuiet()) cpuQuietInit();
            if (CPU.hasCpuBoost()) cpuBoostInit();
            if (CPU.hasCpuTouchBoost()) cpuTouchBoostInit();
            if (othersDivider != null && (count == getCount() || getView(count) instanceof DDivider))
                removeView(othersDivider);
        }

        private void usageInit() {
            mUsageCard = new UsageCardView.DUsageCard();
            mUsageCard.setText(getString(R.string.cpu_usage));
            addView(mUsageCard);
        }

        private void tempInit() {
            mTempCard = new CardViewItem.DCardView();
            mTempCard.setTitle(getString(R.string.cpu_temp));
            mTempCard.setDescription(CPU.getTemp());

            addView(mTempCard);
        }

        private void coreInit() {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);

            mCoreCheckBox = new AppCompatCheckBox[CPU.getBigCoreRange().size()];
            mCoreProgressBar = new ProgressBar[mCoreCheckBox.length];
            mCoreUsageText = new AppCompatTextView[mCoreCheckBox.length];
            mCoreFreqText = new AppCompatTextView[mCoreCheckBox.length];
            for (int i = 0; i < mCoreCheckBox.length; i++) {
                View view = inflater.inflate(R.layout.coreview, container, false);

                mCoreCheckBox[i] = (AppCompatCheckBox) view.findViewById(R.id.core_checkbox);
                mCoreCheckBox[i].setText(getString(R.string.core, i + 1));
                mCoreCheckBox[i].setOnClickListener(this);

                mCoreProgressBar[i] = (ProgressBar) view.findViewById(R.id.progressbar);
                mCoreProgressBar[i].setMax(CPU.getFreqs().size());

                mCoreUsageText[i] = (AppCompatTextView) view.findViewById(R.id.usage);

                mCoreFreqText[i] = (AppCompatTextView) view.findViewById(R.id.freq);

                layout.addView(view);
            }

            CardViewItem.DCardView coreCard = new CardViewItem.DCardView();
            coreCard.setTitle(getString(R.string.current_freq));
            coreCard.setView(layout);

            addView(coreCard);
        }

        private void freqInit() {
            views.clear();
            freqs.clear();

            if (CPU.isMSM_LimiterActive()) {
                DDivider mMSM_LimiterHeaderCard = new DDivider();
                mMSM_LimiterHeaderCard.setText("MSM_Limiter");
                addView(mMSM_LimiterHeaderCard);
            }

            if (CPU.hasMSM_Limiter()) {
                mMSM_Limiter_EnableCard = new SwitchCardView.DSwitchCard();
                mMSM_Limiter_EnableCard.setTitle(getString(R.string.cpu_msm_limiter));
                mMSM_Limiter_EnableCard.setDescription(getString(R.string.cpu_msm_limiter_summary));
                mMSM_Limiter_EnableCard.setChecked(CPU.isMSM_LimiterActive());
                mMSM_Limiter_EnableCard.setOnDSwitchCardListener(this);

                views.add(mMSM_Limiter_EnableCard);
            }

            if (CPU.hasPerCoreControl() && CPU.isMSM_LimiterActive()) {
                mPerCoreControlCard = new SwitchCardView.DSwitchCard();
                mPerCoreControlCard.setDescription(getString(R.string.cpu_per_core_control));
                mPerCoreControlCard.setChecked(CPU.isPerCoreControlActive(getActivity()));
                mPerCoreControlCard.setOnDSwitchCardListener(this);

                views.add(mPerCoreControlCard);

            }

            if (!CPU.isPerCoreControlActive(getActivity()) && !CPU.isMSM_LimiterActive()) {
                for (int freq : CPU.getFreqs())
                    freqs.add(freq / 1000 + getString(R.string.mhz));

                mMaxFreqCard = new PopupCardView.DPopupCard(freqs);
                mMaxFreqCard.setTitle(getString(R.string.cpu_max_freq));
                mMaxFreqCard.setDescription(getString(R.string.cpu_max_freq_summary));
                mMaxFreqCard.setItem(CPU.getMaxFreq(true) / 1000 + getString(R.string.mhz));
                mMaxFreqCard.setOnDPopupCardListener(this);

                mMinFreqCard = new PopupCardView.DPopupCard(freqs);
                mMinFreqCard.setTitle(getString(R.string.cpu_min_freq));
                mMinFreqCard.setDescription(getString(R.string.cpu_min_freq_summary));
                mMinFreqCard.setItem(CPU.getMinFreq(true) / 1000 + getString(R.string.mhz));
                mMinFreqCard.setOnDPopupCardListener(this);

                views.add(mMaxFreqCard);
                views.add(mMinFreqCard);

            }
            if (CPU.isMSM_LimiterActive()) {

                if (!CPU.isPerCoreControlActive(getActivity())){

                    if (CPU.hasMSM_LimiterResumeMaxFreq()) {
                        List<String> freqs = new ArrayList<>();
                        for (int freq : CPU.getFreqs())
                            freqs.add(freq / 1000 + getString(R.string.mhz));

                        mMSM_LimiterResumeMaxFreqNoPerCoreCard = new PopupCardView.DPopupCard(freqs);
                        mMSM_LimiterResumeMaxFreqNoPerCoreCard.setTitle(getString(R.string.cpu_msm_limiter_resume_max));
                        mMSM_LimiterResumeMaxFreqNoPerCoreCard.setDescription(getString(R.string.cpu_msm_limiter_resume_max_summary));
                        mMSM_LimiterResumeMaxFreqNoPerCoreCard.setItem(CPU.getMSM_LimiterResumeMaxFreq() / 1000 + getString(R.string.mhz));
                        mMSM_LimiterResumeMaxFreqNoPerCoreCard.setOnDPopupCardListener(this);

                        views.add(mMSM_LimiterResumeMaxFreqNoPerCoreCard);
                    }

                    if (CPU.hasMSM_LimiterSuspendMinFreq()) {
                        List<String> freqs = new ArrayList<>();
                        for (int freq : CPU.getFreqs())
                            freqs.add(freq / 1000 + getString(R.string.mhz));

                        mMSM_LimiterSuspendMinFreqNoPerCoreCard = new PopupCardView.DPopupCard(freqs);
                        mMSM_LimiterSuspendMinFreqNoPerCoreCard.setTitle(getString(R.string.cpu_msm_limiter_suspend_min));
                        mMSM_LimiterSuspendMinFreqNoPerCoreCard.setDescription(getString(R.string.cpu_msm_limiter_suspend_min_summary));
                        mMSM_LimiterSuspendMinFreqNoPerCoreCard.setItem(CPU.getMSM_LimiterSuspendMinFreq() / 1000 + getString(R.string.mhz));
                        mMSM_LimiterSuspendMinFreqNoPerCoreCard.setOnDPopupCardListener(this);

                        views.add(mMSM_LimiterSuspendMinFreqNoPerCoreCard);
                    }
                }

                else if (CPU.isPerCoreControlActive(getActivity())) {
                    for (int freq : CPU.getFreqs())
                        freqs.add(freq / 1000 + getString(R.string.mhz));

                    DDivider mMaxFreqPerCoreCard = new DDivider();
                    mMaxFreqPerCoreCard.setText("Max Frequency per Core");
                    mMaxFreqPerCoreCard.setDescription(getString(R.string.cpu_msm_limiter_resume_max_summary_per_core));
                    views.add(mMaxFreqPerCoreCard);

                    mMSM_LimiterResumeMaxFreqCard = new PopupCardView.DPopupCard[CPU.getCoreCount()];
                    for (int i = 0; i < CPU.getCoreCount(); i++) {
                        mMSM_LimiterResumeMaxFreqCard[i] = new PopupCardView.DPopupCard(freqs);
                        mMSM_LimiterResumeMaxFreqCard[i].setTitle(String.format(getString(R.string.cpu_msm_limiter_resume_max_per_core), i));
                        mMSM_LimiterResumeMaxFreqCard[i].setDescription("");
                        mMSM_LimiterResumeMaxFreqCard[i].setItem(CPU.getMSM_LimiterResumeMaxFreqPerCore(i) / 1000 + getString(R.string.mhz));
                        mMSM_LimiterResumeMaxFreqCard[i].setOnDPopupCardListener(this);
                        views.add(mMSM_LimiterResumeMaxFreqCard[i]);
                    }

                    DDivider mMinFreqPerCoreCard = new DDivider();
                    mMinFreqPerCoreCard.setText("Min Frequency per Core");
                    mMinFreqPerCoreCard.setDescription(getString(R.string.cpu_msm_limiter_suspend_min_summary_per_core));
                    views.add(mMinFreqPerCoreCard);

                    mMSM_LimiterSuspendMinFreqCard = new PopupCardView.DPopupCard[CPU.getCoreCount()];
                    for (int i = 0; i < CPU.getCoreCount(); i++) {
                        mMSM_LimiterSuspendMinFreqCard[i] = new PopupCardView.DPopupCard(freqs);
                        mMSM_LimiterSuspendMinFreqCard[i].setTitle(String.format(getString(R.string.cpu_msm_limiter_suspend_min_per_core), i));
                        mMSM_LimiterSuspendMinFreqCard[i].setDescription("");
                        mMSM_LimiterSuspendMinFreqCard[i].setItem(CPU.getMSM_LimiterResumeMaxFreqPerCore(i) / 1000 + getString(R.string.mhz));
                        mMSM_LimiterSuspendMinFreqCard[i].setOnDPopupCardListener(this);
                        views.add(mMSM_LimiterSuspendMinFreqCard[i]);
                    }
                }

                if (CPU.hasMSM_LimiterSuspendMaxFreq()) {
                    List<String> freqs = new ArrayList<>();
                    for (int freq : CPU.getFreqs())
                        freqs.add(freq / 1000 + getString(R.string.mhz));

                    mMSM_LimiterSuspendMaxFreqCard = new PopupCardView.DPopupCard(freqs);
                    mMSM_LimiterSuspendMaxFreqCard.setTitle(getString(R.string.cpu_msm_limiter_suspend_max));
                    mMSM_LimiterSuspendMaxFreqCard.setDescription(getString(R.string.cpu_msm_limiter_suspend_max_summary));
                    mMSM_LimiterSuspendMaxFreqCard.setItem(CPU.getMSM_LimiterSuspendMaxFreq() / 1000 + getString(R.string.mhz));
                    mMSM_LimiterSuspendMaxFreqCard.setOnDPopupCardListener(this);

                    views.add(mMSM_LimiterSuspendMaxFreqCard);
                }
            }

            if (CPU.hasMaxScreenOffFreq()) {
                mMaxScreenOffFreqCard = new PopupCardView.DPopupCard(freqs);
                mMaxScreenOffFreqCard.setTitle(getString(R.string.cpu_max_screen_off_freq));
                mMaxScreenOffFreqCard.setDescription(getString(R.string.cpu_max_screen_off_freq_summary));
                mMaxScreenOffFreqCard.setItem(CPU.getMaxScreenOffFreq(true) / 1000 + getString(R.string.mhz));
                mMaxScreenOffFreqCard.setOnDPopupCardListener(this);

                views.add(mMaxScreenOffFreqCard);
            }

            addAllViews(views);
        }

        private void governorInit() {
            views.clear();

            if (!CPU.isPerCoreControlActive(getActivity()) && !CPU.isMSM_LimiterActive()) {

                mGovernorCard = new PopupCardView.DPopupCard(CPU.getAvailableGovernors());
                mGovernorCard.setTitle(getString(R.string.cpu_governor));
                mGovernorCard.setDescription(getString(R.string.cpu_governor_summary));
                mGovernorCard.setItem(CPU.getCurGovernor(true));
                mGovernorCard.setOnDPopupCardListener(this);
                views.add(mGovernorCard);

            }
            if (CPU.isMSM_LimiterActive()) {
                if (!CPU.isPerCoreControlActive(getActivity())) {
                    mMSM_LimiterGovernorNoPerCoreCard = new PopupCardView.DPopupCard(CPU.getAvailableGovernors());
                    mMSM_LimiterGovernorNoPerCoreCard.setTitle(getString(R.string.cpu_governor));
                    mMSM_LimiterGovernorNoPerCoreCard.setDescription(getString(R.string.cpu_governor_summary));
                    mMSM_LimiterGovernorNoPerCoreCard.setItem(CPU.getMSMLimiterGoveror());
                    mMSM_LimiterGovernorNoPerCoreCard.setOnDPopupCardListener(this);
                    views.add(mMSM_LimiterGovernorNoPerCoreCard);

                }

                if (CPU.isPerCoreControlActive(getActivity())) {

                    DDivider mMSM_LimiterGovernorPerCoreDivCard = new DDivider();
                    mMSM_LimiterGovernorPerCoreDivCard.setText("Select Governor per Core");
                    mMSM_LimiterGovernorPerCoreDivCard.setDescription(getString(R.string.cpu_governor_summary));
                    views.add(mMSM_LimiterGovernorPerCoreDivCard);

                    mMSM_LimiterGovernorPerCoreCard = new PopupCardView.DPopupCard[CPU.getCoreCount()];

                    for (int i = 0; i < CPU.getCoreCount(); i++) {
                        mMSM_LimiterGovernorPerCoreCard[i] = new PopupCardView.DPopupCard(CPU.getAvailableGovernors());
                        mMSM_LimiterGovernorPerCoreCard[i].setTitle(String.format(getString(R.string.cpu_msm_limiter_governor_per_core), i));
                        mMSM_LimiterGovernorPerCoreCard[i].setDescription("");
                        mMSM_LimiterGovernorPerCoreCard[i].setItem(CPU.getMSMLimiterGovernorPerCore(i));
                        mMSM_LimiterGovernorPerCoreCard[i].setOnDPopupCardListener(this);
                        views.add(mMSM_LimiterGovernorPerCoreCard[i]);
                    }
                }
            }

            if (!CPU.isPerCoreControlActive(getActivity())) {

                mGovernorTunableNoPerCoreCard = new CardViewItem.DCardView();
                mGovernorTunableNoPerCoreCard.setTitle(getString(R.string.cpu_governor_tunables));
                mGovernorTunableNoPerCoreCard.setDescription(getString(R.string.cpu_governor_tunables_summary));
                mGovernorTunableNoPerCoreCard.setOnDCardListener(this);
                views.add(mGovernorTunableNoPerCoreCard);
            }

            if (CPU.isPerCoreControlActive(getActivity())) {

                DDivider mGovernorTunablePerCoreDivider = new DDivider();
                mGovernorTunablePerCoreDivider.setText(getString(R.string.cpu_governor_tunables_per_core_header));
                mGovernorTunablePerCoreDivider.setDescription(getString(R.string.cpu_governor_tunables_per_core_summary));
                views.add(mGovernorTunablePerCoreDivider);

                mGovernorTunableCoreCard = new CardViewItem.DCardView[CPU.getCoreCount()];
                for (int i = 0; i < CPU.getCoreCount(); i++) {
                    mGovernorTunableCoreCard[i] = new CardViewItem.DCardView();
                    mGovernorTunableCoreCard[i].setTitle(String.format(getString(R.string.cpu_governor_tunables_per_core_tunable), i) + " " + CPU.getMSMLimiterGovernorPerCore(i));
                    mGovernorTunableCoreCard[i].setOnDCardListener(this);

                    views.add(mGovernorTunableCoreCard[i]);
                }
            }

            addAllViews(views);

        }

        private void coreLITTLEInit() {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);

            mCoreCheckBoxLITTLE = new AppCompatCheckBox[CPU.getLITTLECoreRange().size()];
            mCoreProgressBarLITTLE = new ProgressBar[mCoreCheckBoxLITTLE.length];
            mCoreUsageTextLITTLE = new AppCompatTextView[mCoreCheckBoxLITTLE.length];
            mCoreFreqTextLITTLE = new AppCompatTextView[mCoreCheckBoxLITTLE.length];
            for (int i = 0; i < mCoreCheckBoxLITTLE.length; i++) {
                View view = inflater.inflate(R.layout.coreview, container, false);

                mCoreCheckBoxLITTLE[i] = (AppCompatCheckBox) view.findViewById(R.id.core_checkbox);
                mCoreCheckBoxLITTLE[i].setText(getString(R.string.core, i + 1));
                mCoreCheckBoxLITTLE[i].setOnClickListener(this);

                mCoreProgressBarLITTLE[i] = (ProgressBar) view.findViewById(R.id.progressbar);
                mCoreProgressBarLITTLE[i].setMax(CPU.getFreqs(CPU.getLITTLEcore()).size());

                mCoreUsageTextLITTLE[i] = (AppCompatTextView) view.findViewById(R.id.usage);

                mCoreFreqTextLITTLE[i] = (AppCompatTextView) view.findViewById(R.id.freq);

                layout.addView(view);
            }

            CardViewItem.DCardView coreCard = new CardViewItem.DCardView();
            coreCard.setTitle(getString(R.string.current_freq));
            coreCard.setView(layout);

            addView(coreCard);
        }

        private void freqLITTLEInit() {
            List<String> freqs = new ArrayList<>();
            for (int freq : CPU.getFreqs(CPU.getLITTLEcore()))
                freqs.add(freq / 1000 + getString(R.string.mhz));

            mMaxFreqLITTLECard = new PopupCardView.DPopupCard(freqs);
            mMaxFreqLITTLECard.setDescription(getString(R.string.cpu_max_freq));
            mMaxFreqLITTLECard.setItem(CPU.getMaxFreq(CPU.getLITTLEcore(), true) / 1000 + getString(R.string.mhz));
            mMaxFreqLITTLECard.setOnDPopupCardListener(this);

            mMinFreqLITTLECard = new PopupCardView.DPopupCard(freqs);
            mMinFreqLITTLECard.setDescription(getString(R.string.cpu_min_freq));
            mMinFreqLITTLECard.setItem(CPU.getMinFreq(CPU.getLITTLEcore(), true) / 1000 + getString(R.string.mhz));
            mMinFreqLITTLECard.setOnDPopupCardListener(this);

            addView(mMaxFreqLITTLECard);
            addView(mMinFreqLITTLECard);

            if (CPU.hasMaxScreenOffFreq()) {
                mMaxScreenOffFreqLITTLECard = new PopupCardView.DPopupCard(freqs);
                mMaxScreenOffFreqLITTLECard.setDescription(getString(R.string.cpu_max_screen_off_freq));
                mMaxScreenOffFreqLITTLECard.setItem(CPU.getMaxScreenOffFreq(CPU.getLITTLEcore(), true) / 1000 +
                        getString(R.string.mhz));
                mMaxScreenOffFreqLITTLECard.setOnDPopupCardListener(this);

                addView(mMaxScreenOffFreqLITTLECard);
            }
        }

        private void governorLITTLEInit() {
            mGovernorLITTLECard = new PopupCardView.DPopupCard(CPU.getAvailableGovernors(CPU.getLITTLEcore()));
            mGovernorLITTLECard.setDescription(getString(R.string.cpu_governor));
            mGovernorLITTLECard.setItem(CPU.getCurGovernor(CPU.getLITTLEcore(), true));
            mGovernorLITTLECard.setOnDPopupCardListener(this);

            mGovernorTunableLITTLECard = new CardViewItem.DCardView();
            mGovernorTunableLITTLECard.setDescription(getString(R.string.cpu_governor_tunables));
            mGovernorTunableLITTLECard.setOnDCardListener(this);

            addView(mGovernorLITTLECard);
            addView(mGovernorTunableLITTLECard);
        }

        private void mcPowerSavingInit() {
            mMcPowerSavingCard = new PopupCardView.DPopupCard(new ArrayList<>(Arrays.asList(
                    CPU.getMcPowerSavingItems(getActivity()))));
            mMcPowerSavingCard.setTitle(getString(R.string.mc_power_saving));
            mMcPowerSavingCard.setDescription(getString(R.string.mc_power_saving_summary));
            mMcPowerSavingCard.setItem(CPU.getCurMcPowerSaving());
            mMcPowerSavingCard.setOnDPopupCardListener(this);

            addView(mMcPowerSavingCard);
        }

        private void powerSavingWqInit() {
            mPowerSavingWqCard = new SwitchCardView.DSwitchCard();
            mPowerSavingWqCard.setDescription(getString(R.string.power_saving_wq));
            mPowerSavingWqCard.setChecked(CPU.isPowerSavingWqActive());
            mPowerSavingWqCard.setOnDSwitchCardListener(this);

            addView(mPowerSavingWqCard);
        }

        private void cfsSchedulerInit() {
            mCFSSchedulerCard = new PopupCardView.DPopupCard(CPU.getAvailableCFSSchedulers());
            mCFSSchedulerCard.setTitle(getString(R.string.cfs_scheduler_policy));
            mCFSSchedulerCard.setDescription(getString(R.string.cfs_scheduler_policy_summary));
            mCFSSchedulerCard.setItem(CPU.getCurrentCFSScheduler());
            mCFSSchedulerCard.setOnDPopupCardListener(this);

            addView(mCFSSchedulerCard);
        }

        private void cpuQuietInit() {
            if (CPU.hasCpuQuietEnable()) {
                mCpuQuietEnableCard = new SwitchCardView.DSwitchCard();
                mCpuQuietEnableCard.setTitle(getString(R.string.cpu_quiet));
                mCpuQuietEnableCard.setDescription(getString(R.string.cpu_quiet_summary));
                mCpuQuietEnableCard.setChecked(CPU.isCpuQuietActive());
                mCpuQuietEnableCard.setOnDSwitchCardListener(this);

                addView(mCpuQuietEnableCard);
            }

            if (CPU.hasCpuQuietGovernors()) {
                mCpuQuietGovernorCard = new PopupCardView.DPopupCard(CPU.getCpuQuietAvailableGovernors());
                mCpuQuietGovernorCard.setDescription(getString(R.string.cpu_quiet_governor));
                mCpuQuietGovernorCard.setItem(CPU.getCpuQuietCurGovernor());
                mCpuQuietGovernorCard.setOnDPopupCardListener(this);

                addView(mCpuQuietGovernorCard);
            }
        }

        private void cpuBoostInit() {
            views.clear();
            if (CPU.hasCpuBoostEnable()) {
                mCpuBoostEnableCard = new SwitchCardView.DSwitchCard();
                mCpuBoostEnableCard.setDescription(getString(R.string.cpu_boost));
                mCpuBoostEnableCard.setChecked(CPU.isCpuBoostActive());
                mCpuBoostEnableCard.setOnDSwitchCardListener(this);

                views.add(mCpuBoostEnableCard);
            }

            if (CPU.hasCpuBoostDebugMask()) {
                mCpuBoostDebugMaskCard = new SwitchCardView.DSwitchCard();
                mCpuBoostDebugMaskCard.setTitle(getString(R.string.debug_mask));
                mCpuBoostDebugMaskCard.setDescription(getString(R.string.debug_mask_summary));
                mCpuBoostDebugMaskCard.setChecked(CPU.isCpuBoostDebugMaskActive());
                mCpuBoostDebugMaskCard.setOnDSwitchCardListener(this);

                views.add(mCpuBoostDebugMaskCard);
            }

            if (CPU.hasCpuBoostMs()) {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 5001; i += 10)
                    list.add(i + getString(R.string.ms));

                mCpuBoostMsCard = new SeekBarCardView.DSeekBarCard(list);
                mCpuBoostMsCard.setTitle(getString(R.string.interval));
                mCpuBoostMsCard.setDescription(getString(R.string.interval_summary));
                mCpuBoostMsCard.setProgress(CPU.getCpuBootMs() / 10);
                mCpuBoostMsCard.setOnDSeekBarCardListener(this);

                views.add(mCpuBoostMsCard);
            }

            if (CPU.hasCpuBoostSyncThreshold() && CPU.getFreqs() != null) {
                List<String> list = new ArrayList<>();
                list.add(getString(R.string.disabled));
                for (int freq : CPU.getFreqs())
                    list.add((freq / 1000) + getString(R.string.mhz));

                mCpuBoostSyncThresholdCard = new PopupCardView.DPopupCard(list);
                mCpuBoostSyncThresholdCard.setTitle(getString(R.string.sync_threshold));
                mCpuBoostSyncThresholdCard.setDescription(getString(R.string.sync_threshold_summary));
                mCpuBoostSyncThresholdCard.setItem(CPU.getCpuBootSyncThreshold());
                mCpuBoostSyncThresholdCard.setOnDPopupCardListener(this);

                views.add(mCpuBoostSyncThresholdCard);
            }

            if (CPU.hasCpuBoostInputMs()) {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 5001; i += 10)
                    list.add(i + getString(R.string.ms));

                mCpuBoostInputMsCard = new SeekBarCardView.DSeekBarCard(list);
                mCpuBoostInputMsCard.setTitle(getString(R.string.input_interval));
                mCpuBoostInputMsCard.setDescription(getString(R.string.input_interval_summary));
                mCpuBoostInputMsCard.setProgress(CPU.getCpuBootInputMs() / 10);
                mCpuBoostInputMsCard.setOnDSeekBarCardListener(this);

                views.add(mCpuBoostInputMsCard);
            }

            if (CPU.hasCpuBoostInputFreq() && CPU.getFreqs() != null) {
                List<String> list = new ArrayList<>();
                list.add(getString(R.string.disabled));
                for (int freq : CPU.getFreqs())
                    list.add((freq / 1000) + getString(R.string.mhz));

                List<Integer> freqs = CPU.getCpuBootInputFreq();
                mCpuBoostInputFreqCard = new PopupCardView.DPopupCard[freqs.size()];

                for (int i = 0; i < freqs.size(); i++) {
                    mCpuBoostInputFreqCard[i] = new PopupCardView.DPopupCard(list);
                    if (i == 0) {
                        if (freqs.size() > 1)
                            mCpuBoostInputFreqCard[i].setTitle(getString(R.string.input_boost_freq_core, i + 1));
                        else
                            mCpuBoostInputFreqCard[i].setTitle(getString(R.string.input_boost_freq));
                        mCpuBoostInputFreqCard[i].setDescription(getString(R.string.input_boost_freq_summary));
                    } else {
                        mCpuBoostInputFreqCard[i].setDescription(getString(R.string.input_boost_freq_core, i + 1));
                    }
                    mCpuBoostInputFreqCard[i].setItem(freqs.get(i));
                    mCpuBoostInputFreqCard[i].setOnDPopupCardListener(this);

                    views.add(mCpuBoostInputFreqCard[i]);
                }
            }

            if (CPU.hasCpuBoostWakeup()) {
                mCpuBoostWakeupCard = new SwitchCardView.DSwitchCard();
                mCpuBoostWakeupCard.setTitle(getString(R.string.wakeup_boost));
                mCpuBoostWakeupCard.setDescription(getString(R.string.wakeup_boost_summary));
                mCpuBoostWakeupCard.setChecked(CPU.isCpuBoostWakeupActive());
                mCpuBoostWakeupCard.setOnDSwitchCardListener(this);

                views.add(mCpuBoostWakeupCard);
            }

            if (CPU.hasCpuBoostHotplug()) {
                mCpuBoostHotplugCard = new SwitchCardView.DSwitchCard();
                mCpuBoostHotplugCard.setTitle(getString(R.string.hotplug_boost));
                mCpuBoostHotplugCard.setDescription(getString(R.string.hotplug_boost_summary));
                mCpuBoostHotplugCard.setChecked(CPU.isCpuBoostHotplugActive());
                mCpuBoostHotplugCard.setOnDSwitchCardListener(this);

                views.add(mCpuBoostHotplugCard);
            }

            if (views.size() > 0) {
                DDivider mCpuBoostDividerCard = new DDivider();
                mCpuBoostDividerCard.setText(getString(R.string.cpu_boost));
                addView(mCpuBoostDividerCard);

                addAllViews(views);
            }

        }

        private void cpuTouchBoostInit() {
            mCpuTouchBoostCard = new SwitchCardView.DSwitchCard();
            mCpuTouchBoostCard.setTitle(getString(R.string.touch_boost));
            mCpuTouchBoostCard.setDescription(getString(R.string.touch_boost_summary));
            mCpuTouchBoostCard.setChecked(CPU.isCpuTouchBoostEnabled());
            mCpuTouchBoostCard.setOnDSwitchCardListener(this);
            addView(mCpuTouchBoostCard);
        }

        @Override
        public void onClick(View v) {
            for (int i = 0; i < mCoreCheckBox.length; i++)
                if (v == mCoreCheckBox[i]) {
                    List<Integer> range = CPU.getBigCoreRange();
                    if (range.get(i) == 0) {
                        mCoreCheckBox[i].setChecked(true);
                        return;
                    }
                    CPU.activateCore(range.get(i), ((CheckBox) v).isChecked(), getActivity());
                    return;
                }
            if (mCoreCheckBoxLITTLE != null) for (int i = 0; i < mCoreCheckBoxLITTLE.length; i++)
                if (v == mCoreCheckBoxLITTLE[i]) {
                    List<Integer> range = CPU.getLITTLECoreRange();
                    if (range.get(i) == 0) {
                        mCoreCheckBoxLITTLE[i].setChecked(true);
                        return;
                    }
                    CPU.activateCore(range.get(i), ((CheckBox) v).isChecked(), getActivity());
                    return;
                }
        }

        @Override
        public void onItemSelected(PopupCardView.DPopupCard dPopupCard, int position) {
            for (int i = 0; i < CPU.getCoreCount(); i++) {
                if ( dPopupCard == mMSM_LimiterResumeMaxFreqCard[i] ) {
                    CPU.setMSM_LimiterResumeMaxFreqPerCore(CPU.getFreqs().get(position), i, getActivity());
                }
                if ( dPopupCard == mMSM_LimiterSuspendMinFreqCard[i] ) {
                    CPU.setMSM_LimiterSuspendMinFreqPerCore(CPU.getFreqs().get(position), i, getActivity());
                }
                if ( dPopupCard == mMSM_LimiterGovernorPerCoreCard[i]) {
                    CPU.setMSMLimiterGovernorPerCore(CPU.getAvailableGovernors().get(position), getActivity(), i);
                }
            }
            if (dPopupCard == mMaxFreqCard)
                CPU.setMaxFreq(CPU.getFreqs().get(position), getActivity());
            else if (dPopupCard == mMinFreqCard)
                CPU.setMinFreq(CPU.getFreqs().get(position), getActivity());
            else if (dPopupCard == mMSM_LimiterResumeMaxFreqNoPerCoreCard)
                CPU.setMSM_LimiterResumeMaxFreq(CPU.getFreqs().get(position), getActivity());
            else if (dPopupCard == mMSM_LimiterSuspendMaxFreqCard)
                CPU.setMSM_LimiterSuspendMaxFreq(CPU.getFreqs().get(position), getActivity());
            else if (dPopupCard == mMSM_LimiterSuspendMinFreqNoPerCoreCard)
                CPU.setMSM_LimiterSuspendMinFreq(CPU.getFreqs().get(position), getActivity());
            else if (dPopupCard == mMaxScreenOffFreqCard)
                CPU.setMaxScreenOffFreq(CPU.getFreqs().get(position), getActivity());
            else if (dPopupCard == mGovernorCard)
                CPU.setGovernor(CPU.getAvailableGovernors().get(position), getActivity());
            else if (dPopupCard == mMSM_LimiterGovernorNoPerCoreCard)
                CPU.setMSMLimiterGovernor(CPU.getAvailableGovernors().get(position), getActivity());
            if (dPopupCard == mMaxFreqLITTLECard)
                CPU.setMaxFreq(Control.CommandType.CPU_LITTLE, CPU.getFreqs(CPU.getLITTLEcore()).get(position), getActivity());
            else if (dPopupCard == mMinFreqLITTLECard)
                CPU.setMinFreq(Control.CommandType.CPU_LITTLE, CPU.getFreqs(CPU.getLITTLEcore()).get(position), getActivity());
            else if (dPopupCard == mMaxScreenOffFreqLITTLECard)
                CPU.setMaxScreenOffFreq(Control.CommandType.CPU_LITTLE, CPU.getFreqs(CPU.getLITTLEcore()).get(position),
                        getActivity());
            else if (dPopupCard == mGovernorLITTLECard)
                CPU.setGovernor(Control.CommandType.CPU_LITTLE, CPU.getAvailableGovernors(CPU.getLITTLEcore()).get(position),
                        getActivity());
            else if (dPopupCard == mMcPowerSavingCard)
                CPU.setMcPowerSaving(position, getActivity());
            else if (dPopupCard == mCFSSchedulerCard)
                CPU.setCFSScheduler(CPU.getAvailableCFSSchedulers().get(position), getActivity());
            else if (dPopupCard == mCpuQuietGovernorCard)
                CPU.setCpuQuietGovernor(CPU.getCpuQuietAvailableGovernors().get(position), getActivity());
            else if (dPopupCard == mCpuBoostSyncThresholdCard)
                CPU.setCpuBoostSyncThreshold(position == 0 ? 0 : CPU.getFreqs().get(position - 1), getActivity());
            else {
                if (mCpuBoostInputFreqCard != null)
                    for (int i = 0; i < mCpuBoostInputFreqCard.length; i++)
                        if (dPopupCard == mCpuBoostInputFreqCard[i]) {
                            CPU.setCpuBoostInputFreq(position == 0 ? 0 : CPU.getFreqs().get(position - 1), i, getActivity());
                            return;
                        }
            }
        }

        @Override
        public void onClick(CardViewItem.DCardView dCardView) {
            for (int i = 0; i < CPU.getCoreCount(); i++) {
                if ( dCardView == mGovernorTunableCoreCard[i] ) {
                    cpuFragment.core = i;
                    cpuFragment.governorPart.reload();
                    cpuFragment.setCurrentItem(1);
                }
            }
            if (dCardView == mGovernorTunableNoPerCoreCard) {
                cpuFragment.core = CPU.getBigCore();
                cpuFragment.governorPart.reload();
                cpuFragment.setCurrentItem(1);
            } else if (dCardView == mGovernorTunableLITTLECard) {
                cpuFragment.core = CPU.getLITTLEcore();
                cpuFragment.governorPart.reload();
                cpuFragment.setCurrentItem(1);
            }
        }

        @Override
        public void onChanged(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
        }

        @Override
        public void onStop(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
            if (dSeekBarCard == mCpuBoostMsCard)
                CPU.setCpuBoostMs(position * 10, getActivity());
            else if (dSeekBarCard == mCpuBoostInputMsCard)
                CPU.setCpuBoostInputMs(position * 10, getActivity());
        }

        @Override
        public void onChecked(SwitchCardView.DSwitchCard dSwitchCard, boolean checked) {
            if (dSwitchCard == mCpuQuietEnableCard)
                CPU.activateCpuQuiet(checked, getActivity());
            else if (dSwitchCard == mCpuBoostEnableCard)
                CPU.activateCpuBoost(checked, getActivity());
            else if (dSwitchCard == mCpuBoostDebugMaskCard)
                CPU.activateCpuBoostDebugMask(checked, getActivity());
            else if (dSwitchCard == mPowerSavingWqCard)
                CPU.activatePowerSavingWq(checked, getActivity());
            else if (dSwitchCard == mCpuBoostWakeupCard)
                CPU.activateCpuBoostWakeup(checked, getActivity());
            else if (dSwitchCard == mCpuBoostHotplugCard)
                CPU.activateCpuBoostHotplug(checked, getActivity());
            else if (dSwitchCard == mCpuTouchBoostCard)
                CPU.activateCpuTouchBoost(checked, getActivity());
            else if (dSwitchCard == mMSM_Limiter_EnableCard) {
                CPU.activateMSM_Limiter(checked, getActivity());
                ForceRefresh();
            }
            else if (dSwitchCard == mPerCoreControlCard) {
                CPU.activatePerCoreControl(checked, getActivity());
                ForceRefresh();
            }
        }

        private void ForceRefresh() {
            CPUFragment.cpuFragment.cpuPart.view.invalidate();
            getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }

        @Override
        public boolean onRefresh() {
            if (mMSM_LimiterGovernorPerCoreCard != null) {
                for (int i = 0; i < CPU.getCoreCount(); i++) {
                    String msm_limiter_governor = CPU.getMSMLimiterGovernorPerCore(i);

                        if (mMSM_LimiterGovernorPerCoreCard[i] != null && !msm_limiter_governor.isEmpty()) {
                           mMSM_LimiterGovernorPerCoreCard[i].setItem(msm_limiter_governor);
                       }
                }
            }
            if (mTempCard != null) mTempCard.setDescription(CPU.getTemp());

            if (mCoreCheckBox != null && mCoreProgressBar != null && mCoreFreqText != null) {
                List<Integer> range = CPU.getBigCoreRange();
                for (int i = 0; i < mCoreCheckBox.length; i++) {
                    int cur = CPU.getCurFreq(range.get(i));
                    if (mCoreCheckBox[i] != null) mCoreCheckBox[i].setChecked(cur != 0);
                    if (mCoreProgressBar[i] != null)
                        mCoreProgressBar[i].setProgress(CPU.getFreqs().indexOf(cur) + 1);
                    if (mCoreFreqText[i] != null)
                        mCoreFreqText[i].setText(cur == 0 ? getString(R.string.offline) : cur / 1000 +
                                getString(R.string.mhz));
                }
            }

            if (mMaxFreqCard != null) {
                int maxFreq = CPU.getMaxFreq(false);
                if (maxFreq != 0) mMaxFreqCard.setItem(maxFreq / 1000 + getString(R.string.mhz));
            }
            if (mMinFreqCard != null) {
                int minFreq = CPU.getMinFreq(false);
                if (minFreq != 0) mMinFreqCard.setItem(minFreq / 1000 + getString(R.string.mhz));
            }
            if (mGovernorCard != null) {
                String governor = CPU.getCurGovernor(false);
                if (!governor.isEmpty()) mGovernorCard.setItem(governor);
            }
            if (mMSM_LimiterGovernorNoPerCoreCard != null) {
                String msm_limiter_governor = CPU.getMSMLimiterGoveror();
                if (!msm_limiter_governor.isEmpty()) mMSM_LimiterGovernorNoPerCoreCard.setItem(msm_limiter_governor);
            }
            if (mCoreCheckBoxLITTLE != null && mCoreProgressBarLITTLE != null && mCoreFreqTextLITTLE != null) {
                List<Integer> range = CPU.getLITTLECoreRange();
                for (int i = 0; i < mCoreCheckBoxLITTLE.length; i++) {
                    int cur = CPU.getCurFreq(range.get(i));
                    if (mCoreCheckBoxLITTLE[i] != null) mCoreCheckBoxLITTLE[i].setChecked(cur != 0);
                    if (mCoreProgressBarLITTLE[i] != null)
                        mCoreProgressBarLITTLE[i].setProgress(CPU.getFreqs(CPU.getLITTLEcore()).indexOf(cur) + 1);
                    if (mCoreFreqTextLITTLE[i] != null)
                        mCoreFreqTextLITTLE[i].setText(cur == 0 ? getString(R.string.offline) : cur / 1000 +
                                getString(R.string.mhz));
                }
            }

            if (mMaxFreqLITTLECard != null) {
                int maxFreq = CPU.getMaxFreq(CPU.getLITTLEcore(), false);
                if (maxFreq != 0)
                    mMaxFreqLITTLECard.setItem((maxFreq / 1000) + getString(R.string.mhz));
            }
            if (mMinFreqLITTLECard != null) {
                int minFreq = CPU.getMinFreq(CPU.getLITTLEcore(), false);
                if (minFreq != 0)
                    mMinFreqLITTLECard.setItem(minFreq / 1000 + getString(R.string.mhz));
            }
            if (mGovernorLITTLECard != null) {
                String governor = CPU.getCurGovernor(CPU.getLITTLEcore(), false);
                if (!governor.isEmpty()) mGovernorLITTLECard.setItem(governor);
            }

            return true;
        }

        private final Runnable cpuUsage = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final float[] usage = CPU.getCpuUsage();
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (usage != null) {
                                        if (mUsageCard != null)
                                            mUsageCard.setProgress(Math.round(usage[0]));

                                        if (mCoreUsageText != null) {
                                            List<Integer> cores = CPU.getBigCoreRange();
                                            for (int i = 0; i < mCoreUsageText.length; i++) {
                                                String message = Math.round(usage[cores.get(i) + 1]) + "%";
                                                if (mCoreUsageText[i] != null)
                                                    mCoreUsageText[i].setText(message);
                                                if (mCoreProgressBar != null && mCoreProgressBar[i].getProgress() == 0)
                                                    mCoreUsageText[i].setText("");
                                            }
                                        }

                                        if (mCoreUsageTextLITTLE != null) {
                                            List<Integer> cores = CPU.getLITTLECoreRange();
                                            for (int i = 0; i < mCoreUsageTextLITTLE.length; i++) {
                                                String message = Math.round(usage[cores.get(i) + 1]) + "%";
                                                if (mCoreUsageTextLITTLE[i] != null)
                                                    mCoreUsageTextLITTLE[i].setText(message);
                                                if (mCoreProgressBarLITTLE != null && mCoreProgressBarLITTLE[i].getProgress() == 0)
                                                    mCoreUsageTextLITTLE[i].setText("");
                                            }
                                        }
                                    }
                                }
                            });
                        } catch (NullPointerException ignored) {
                        }
                    }
                }).start();

                getHandler().postDelayed(cpuUsage, 1000);
            }
        };

        @Override
        public void onResume() {
            super.onResume();
            Handler hand;
            if ((hand = getHandler()) != null) hand.post(cpuUsage);
        }

        @Override
        public void onPause() {
            super.onPause();
            Handler hand;
            if ((hand = getHandler()) != null) hand.removeCallbacks(cpuUsage);
        }

    }

    public static class GovernorPart extends PathReaderFragment {

        @Override
        public String getName() {
            if (!CPU.isPerCoreControlActive(getActivity())) {
                return CPU.getCurGovernor(cpuFragment.core, true);
            }
            if (CPU.isPerCoreControlActive(getActivity())) {
                return CPU.getMSMLimiterGovernorPerCore(cpuFragment.core);
            }
            return(null);
        }

        @Override
        public String getPath() {
            if (!CPU.isPerCoreControlActive(getActivity())) {
                return getPath(CPU.isBigLITTLE() ? String.format(CPU_GOVERNOR_TUNABLES_CORE, cpuFragment.core) :
                        CPU_GOVERNOR_TUNABLES, CPU.getCurGovernor(cpuFragment.core, true));
            }
            if (CPU.isPerCoreControlActive(getActivity())) {
                return getPath(CPU_GOVERNOR_TUNABLES, CPU.getMSMLimiterGovernorPerCore(cpuFragment.core));
            }
            return(null);
        }

        private String getPath(String path, String governor) {
                if (Utils.existFile(path + "/" + governor)) return path + "/" + governor;
                else for (String file : new RootFile(path).list())
                    if (governor.contains(file))
                        return path + "/" + file;
                return null;
        }

        @Override
        public PATH_TYPE getType() {
                return PATH_TYPE.GOVERNOR;
        }

        @Override
        public String getError(Context context) {
            if (!CPU.isPerCoreControlActive(getActivity())) {
                return context.getString(R.string.not_tunable, CPU.getCurGovernor(cpuFragment.core, true));
            }
            if (CPU.isPerCoreControlActive(getActivity())) {
                return context.getString(R.string.not_tunable, CPU.getMSMLimiterGovernorPerCore(cpuFragment.core));
            }

            return(null);
        }
    }
}
