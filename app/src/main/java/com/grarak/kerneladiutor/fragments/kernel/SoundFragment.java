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
package com.grarak.kerneladiutor.fragments.kernel;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.kernel.sound.Sound;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.List;

/**
 * Created by willi on 26.06.16.
 */
public class SoundFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Sound.hasSoundControlEnable()) {
            soundControlEnableInit(items);
        }
        if (Sound.hasHighPerfModeEnable()) {
            highPerfModeEnableInit(items);
        }
        if (Sound.hasHeadphoneGain()) {
            headphoneGainInit(items);
        }
        if (Sound.hasHeadphonePowerAmpGain()) {
            headphonePowerAmpGainInit(items);
        }
        if (Sound.hasSpeakerGain()) {
            speakerGainInit(items);
        }
        if (Sound.hasHandsetMicrophoneGain()) {
            handsetMicrophoneGainInit(items);
        }
        if (Sound.hasCamMicrophoneGain()) {
            camMicrophoneGainInit(items);
        }
        if (Sound.hasHeadphoneTpaGain()) {
            headphoneTpaGainInit(items);
        }
        if (Sound.hasLockOutputGain()) {
            lockOutputGainInit(items);
        }
        if (Sound.hasLockMicGain()) {
            lockMicGainInit(items);
        }
        if (Sound.hasMicrophoneGain()) {
            microphoneGainInit(items);
        }
        if (Sound.hasVolumeGain()) {
            volumeGainInit(items);
        }
    }

    private void soundControlEnableInit(List<RecyclerViewItem> items) {
        SwitchView soundControl = new SwitchView();
        soundControl.setSummary(getString(R.string.sound_control));
        soundControl.setChecked(Sound.isSoundControlEnabled());
        soundControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableSoundControl(isChecked, getActivity());
            }
        });

        items.add(soundControl);
    }

    private void highPerfModeEnableInit(List<RecyclerViewItem> items) {
        SwitchView highPerfMode = new SwitchView();
        highPerfMode.setSummary(getString(R.string.headset_highperf_mode));
        highPerfMode.setChecked(Sound.isHighPerfModeEnabled());
        highPerfMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableHighPerfMode(isChecked, getActivity());
            }
        });

        items.add(highPerfMode);
    }

    // ------------
    // This method's layout will also be used for speakerGainInit() and headphonePowerAmpGainInit().
    // The comments won't be repeated.
    // ------------
    private void headphoneGainInit(List<RecyclerViewItem> items) {
    	// Note: All RecyclerViewItems are declared as final so they can be accessed
    	// by the SeekBarManager class declared below.

        // Create a CardView and add our stuff to it.
        final CardView hpGainCard = new CardView(getActivity());
        hpGainCard.setTitle(getString(R.string.headphone_gain));

        // Set this boolean to false if it doesn't exist
        if (!(Prefs.getBoolean("fauxsound_perchannel_headphone_gain", false, getActivity())))
        	Prefs.saveBoolean("fauxsound_perchannel_headphone_gain", false, getActivity());

        // Now create our SwitchView to toggle per-channel controls.
        // We won't add a OnSwitchListener to it yet. But add it to hpGainCard anyway.
        final SwitchView perChannel = new SwitchView();
        perChannel.setTitle(getString(R.string.per_channel_controls));
        perChannel.setSummary(getString(R.string.per_channel_controls_summary));
        perChannel.setChecked(Prefs.getBoolean("fauxsound_perchannel_headphone_gain", false, getActivity()));
        hpGainCard.addItem(perChannel);

        // We'll have three different SeekBarViews.
        // This seekbar will control gain for all channels.
        final SeekBarView headphoneGain = new SeekBarView();
        headphoneGain.setTitle(getString(R.string.all_channels));
        headphoneGain.setItems(Sound.getHeadphoneGainLimits());
        headphoneGain.setProgress(Sound.getHeadphoneGainLimits().indexOf(Sound.getHeadphoneGain("all")));
        headphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphoneGain("all", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        // This seekbar will control gain for the left channel only.
        final SeekBarView headphoneGainLeft = new SeekBarView();
        headphoneGainLeft.setTitle(getString(R.string.left_channel));
        headphoneGainLeft.setItems(Sound.getHeadphoneGainLimits());
        headphoneGainLeft.setProgress(Sound.getHeadphoneGainLimits().indexOf(Sound.getHeadphoneGain("left")));
        headphoneGainLeft.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphoneGain("left", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        // This seekbar will control gain for the right channel only.
        final SeekBarView headphoneGainRight = new SeekBarView();
        headphoneGainRight.setTitle(getString(R.string.right_channel));
        headphoneGainRight.setItems(Sound.getHeadphoneGainLimits());
        headphoneGainRight.setProgress(Sound.getHeadphoneGainLimits().indexOf(Sound.getHeadphoneGain("right")));
        headphoneGainRight.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphoneGain("right", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        // This class will show or hide the seekbars according to perChannel's state
        class SeekBarManager {
        	public void showPerChannelSeekbars (boolean enable) {
        		if (enable == true) {
        			hpGainCard.removeItem(headphoneGain);
        			hpGainCard.addItem(headphoneGainLeft);
        			hpGainCard.addItem(headphoneGainRight);
        		} else {
        			hpGainCard.removeItem(headphoneGainLeft);
        			hpGainCard.removeItem(headphoneGainRight);
        			hpGainCard.addItem(headphoneGain);
        		}
        	}
        }

        // Create a new instance of SeekBarManager
        final SeekBarManager manager = new SeekBarManager();

        // Call the newly-instantiated SeekBarManager above
        if (Prefs.getBoolean("fauxsound_perchannel_headphone_gain", false, getActivity()) == true) {
        	manager.showPerChannelSeekbars(true);
        } else {
        	manager.showPerChannelSeekbars(false);
        }

        // Now we'll add the OnSwitchListener to perChannel.
        perChannel.addOnSwitchListener(new SwitchView.OnSwitchListener() {
        	@Override
        	public void onChanged(SwitchView switchview, boolean isChecked) {
        		Prefs.saveBoolean("fauxsound_perchannel_headphone_gain", isChecked, getActivity());
        		manager.showPerChannelSeekbars(isChecked);
        	}
        });

        // Now add the CardView with all its items to our main List<RecyclerViewItem>
        items.add(hpGainCard);
    }

    private void handsetMicrophoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView handsetMicrophoneGain = new SeekBarView();
        handsetMicrophoneGain.setTitle(getString(R.string.handset_microphone_gain));
        handsetMicrophoneGain.setItems(Sound.getHandsetMicrophoneGainLimits());
        handsetMicrophoneGain.setProgress(Sound.getHandsetMicrophoneGainLimits()
                .indexOf(Sound.getHandsetMicrophoneGain()));
        handsetMicrophoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHandsetMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(handsetMicrophoneGain);
    }

    private void camMicrophoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView camMicrophoneGain = new SeekBarView();
        camMicrophoneGain.setTitle(getString(R.string.cam_microphone_gain));
        camMicrophoneGain.setItems(Sound.getCamMicrophoneGainLimits());
        camMicrophoneGain.setProgress(Sound.getCamMicrophoneGainLimits().indexOf(Sound.getCamMicrophoneGain()));
        camMicrophoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setCamMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(camMicrophoneGain);
    }

    private void speakerGainInit(List<RecyclerViewItem> items) {
        final CardView speakerGainCard = new CardView(getActivity());
        speakerGainCard.setTitle(getString(R.string.speaker_gain));

        if (!(Prefs.getBoolean("fauxsound_perchannel_speaker_gain", false, getActivity())))
        	Prefs.saveBoolean("fauxsound_perchannel_speaker_gain", false, getActivity());

        final SwitchView perChannel = new SwitchView();
        perChannel.setTitle(getString(R.string.per_channel_controls));
        perChannel.setSummary(getString(R.string.per_channel_controls_summary));
        perChannel.setChecked(Prefs.getBoolean("fauxsound_perchannel_speaker_gain", false, getActivity()));
        speakerGainCard.addItem(perChannel);

        final SeekBarView speakerGain = new SeekBarView();
        speakerGain.setTitle(getString(R.string.all_channels));
        speakerGain.setItems(Sound.getSpeakerGainLimits());
        speakerGain.setProgress(Sound.getSpeakerGainLimits().indexOf(Sound.getSpeakerGain("all")));
        speakerGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setSpeakerGain("all", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        final SeekBarView speakerGainLeft = new SeekBarView();
        speakerGainLeft.setTitle(getString(R.string.left_channel));
        speakerGainLeft.setItems(Sound.getSpeakerGainLimits());
        speakerGainLeft.setProgress(Sound.getSpeakerGainLimits().indexOf(Sound.getSpeakerGain("left")));
        speakerGainLeft.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setSpeakerGain("left", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        final SeekBarView speakerGainRight = new SeekBarView();
        speakerGainRight.setTitle(getString(R.string.right_channel));
        speakerGainRight.setItems(Sound.getSpeakerGainLimits());
        speakerGainRight.setProgress(Sound.getSpeakerGainLimits().indexOf(Sound.getSpeakerGain("right")));
        speakerGainRight.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setSpeakerGain("right", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        class SeekBarManager {
        	public void showPerChannelSeekbars (boolean enable) {
        		if (enable == true) {
        			speakerGainCard.removeItem(speakerGain);
        			speakerGainCard.addItem(speakerGainLeft);
        			speakerGainCard.addItem(speakerGainRight);
        		} else {
        			speakerGainCard.removeItem(speakerGainLeft);
        			speakerGainCard.removeItem(speakerGainRight);
        			speakerGainCard.addItem(speakerGain);
        		}
        	}
        }

        final SeekBarManager manager = new SeekBarManager();

        if (Prefs.getBoolean("fauxsound_perchannel_speaker_gain", false, getActivity()) == true) {
        	manager.showPerChannelSeekbars(true);
        } else {
        	manager.showPerChannelSeekbars(false);
        }

        perChannel.addOnSwitchListener(new SwitchView.OnSwitchListener() {
        	@Override
        	public void onChanged(SwitchView switchview, boolean isChecked) {
        		Prefs.saveBoolean("fauxsound_perchannel_speaker_gain", isChecked, getActivity());
        		manager.showPerChannelSeekbars(isChecked);
        	}
        });

        items.add(speakerGainCard);
    }

    private void headphonePowerAmpGainInit(List<RecyclerViewItem> items) {
        final CardView hpPAGainCard = new CardView(getActivity());
        hpPAGainCard.setTitle(getString(R.string.headphone_poweramp_gain));

        if (!(Prefs.getBoolean("fauxsound_perchannel_headphone_pa_gain", false, getActivity())))
        	Prefs.saveBoolean("fauxsound_perchannel_headphone_pa_gain", false, getActivity());

        final SwitchView perChannel = new SwitchView();
        perChannel.setTitle(getString(R.string.per_channel_controls));
        perChannel.setSummary(getString(R.string.per_channel_controls_summary));
        perChannel.setChecked(Prefs.getBoolean("fauxsound_perchannel_headphone_pa_gain", false, getActivity()));
        hpPAGainCard.addItem(perChannel);

        final SeekBarView headphonePAGain = new SeekBarView();
        headphonePAGain.setTitle(getString(R.string.all_channels));
        headphonePAGain.setItems(Sound.getHeadphonePowerAmpGainLimits());
        headphonePAGain.setProgress(Sound.getHeadphonePowerAmpGainLimits().indexOf(Sound.getHeadphonePowerAmpGain("all")));
        headphonePAGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphonePowerAmpGain("all", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        final SeekBarView headphonePAGainLeft = new SeekBarView();
        headphonePAGainLeft.setTitle(getString(R.string.left_channel));
        headphonePAGainLeft.setItems(Sound.getHeadphonePowerAmpGainLimits());
        headphonePAGainLeft.setProgress(Sound.getHeadphonePowerAmpGainLimits().indexOf(Sound.getHeadphonePowerAmpGain("left")));
        headphonePAGainLeft.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphonePowerAmpGain("left", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        final SeekBarView headphonePAGainRight = new SeekBarView();
        headphonePAGainRight.setTitle(getString(R.string.right_channel));
        headphonePAGainRight.setItems(Sound.getHeadphonePowerAmpGainLimits());
        headphonePAGainRight.setProgress(Sound.getHeadphonePowerAmpGainLimits().indexOf(Sound.getHeadphonePowerAmpGain("right")));
        headphonePAGainRight.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphonePowerAmpGain("right", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        class SeekBarManager {
        	public void showPerChannelSeekbars (boolean enable) {
        		if (enable == true) {
        			hpPAGainCard.removeItem(headphonePAGain);
        			hpPAGainCard.addItem(headphonePAGainLeft);
        			hpPAGainCard.addItem(headphonePAGainRight);
        		} else {
        			hpPAGainCard.removeItem(headphonePAGainLeft);
        			hpPAGainCard.removeItem(headphonePAGainRight);
        			hpPAGainCard.addItem(headphonePAGain);
        		}
        	}
        }

        final SeekBarManager manager = new SeekBarManager();

        // Call the newly-instantiated SeekBarManager above
        if (Prefs.getBoolean("fauxsound_perchannel_headphone_pa_gain", false, getActivity()) == true) {
        	manager.showPerChannelSeekbars(true);
        } else {
        	manager.showPerChannelSeekbars(false);
        }

        // Now we'll add the OnSwitchListener to perChannel.
        perChannel.addOnSwitchListener(new SwitchView.OnSwitchListener() {
        	@Override
        	public void onChanged(SwitchView switchview, boolean isChecked) {
        		Prefs.saveBoolean("fauxsound_perchannel_headphone_pa_gain", isChecked, getActivity());
        		manager.showPerChannelSeekbars(isChecked);
        	}
        });

        // Now add the CardView with all its items to our main List<RecyclerViewItem>
        items.add(hpPAGainCard);
    }

    private void headphoneTpaGainInit(List<RecyclerViewItem> items) {
        SeekBarView headphoneTpaGain = new SeekBarView();
        headphoneTpaGain.setTitle(getString(R.string.headphone_tpa6165_gain));
        headphoneTpaGain.setItems(Sound.getHeadphoneTpaGainLimits());
        headphoneTpaGain.setProgress(Sound.getHeadphoneTpaGainLimits()
                .indexOf(Sound.getHeadphoneTpaGain()));
        headphoneTpaGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphoneTpaGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(headphoneTpaGain);
    }

    private void lockOutputGainInit(List<RecyclerViewItem> items) {
        SwitchView lockOutputGain = new SwitchView();
        lockOutputGain.setTitle(getString(R.string.lock_output_gain));
        lockOutputGain.setSummary(getString(R.string.lock_output_gain_summary));
        lockOutputGain.setChecked(Sound.isLockOutputGainEnabled());
        lockOutputGain.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableLockOutputGain(isChecked, getActivity());
            }
        });

        items.add(lockOutputGain);
    }

    private void lockMicGainInit(List<RecyclerViewItem> items) {
        SwitchView lockMicGain = new SwitchView();
        lockMicGain.setTitle(getString(R.string.lock_mic_gain));
        lockMicGain.setSummary(getString(R.string.lock_mic_gain_summary));
        lockMicGain.setChecked(Sound.isLockMicGainEnabled());
        lockMicGain.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableLockMicGain(isChecked, getActivity());
            }
        });

        items.add(lockMicGain);
    }

    private void microphoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView microphoneGain = new SeekBarView();
        microphoneGain.setTitle(getString(R.string.microphone_gain));
        microphoneGain.setItems(Sound.getMicrophoneGainLimits());
        microphoneGain.setProgress(Sound.getMicrophoneGainLimits().indexOf(Sound.getMicrophoneGain()));
        microphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(microphoneGain);
    }

    private void volumeGainInit(List<RecyclerViewItem> items) {
        SeekBarView volumeGain = new SeekBarView();
        volumeGain.setTitle(getString(R.string.volume_gain));
        volumeGain.setItems(Sound.getVolumeGainLimits());
        volumeGain.setProgress(Sound.getVolumeGainLimits().indexOf(Sound.getVolumeGain()));
        volumeGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setVolumeGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(volumeGain);
    }

}
