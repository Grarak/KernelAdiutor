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
package com.grarak.kerneladiutordonate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;

import com.grarak.kerneladiutordonate.fragments.BaseFragment;
import com.grarak.kerneladiutordonate.utils.Prefs;
import com.plattysoft.leonids.ParticleSystem;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 28.12.15.
 */
public class WelcomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static List<GenericFragment> fragments = new ArrayList<>();
    private AppCompatButton mBackButton;
    private AppCompatButton mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        CirclePageIndicator mCirclePageIndicator = (CirclePageIndicator) findViewById(R.id.circlepageindicator);
        mBackButton = (AppCompatButton) findViewById(R.id.back_button);
        mNextButton = (AppCompatButton) findViewById(R.id.next_button);

        if (savedInstanceState == null) {
            fragments.add(new GenericFragment(R.layout.welcome_screen_1) {
                @Override
                public void init(Bundle savedInstanceState) {
                    super.init(savedInstanceState);
                    findViewById(R.id.welcome_image_screenshot).setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animate();
                        }
                    }, 1000);
                }

                @Override
                public void animate() {
                    super.animate();
                    findViewById(R.id.welcome_image_screenshot).setVisibility(View.VISIBLE);
                    findViewById(R.id.welcome_image_screenshot).startAnimation(AnimationUtils
                            .loadAnimation(getActivity(), R.anim.slide_in_bottom_half));
                }
            });
            fragments.add(new GenericFragment(R.layout.welcome_screen_2) {

                @Override
                public void init(Bundle savedInstanceState) {
                    super.init(savedInstanceState);
                    findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                            Prefs.saveBoolean("firststart", false, getActivity());
                            getActivity().finish();
                            getActivity().overridePendingTransition(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                        }
                    });
                }

                @Override
                public void animate() {
                    super.animate();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ps(R.drawable.ic_star_pink);
                            ps(R.drawable.ic_star_white);
                        }
                    }, 1000);
                }

                private void ps(int res) {
                    ParticleSystem ps = new ParticleSystem(getActivity(), 100, res, 800);
                    ps.setScaleRange(0.7f, 1.3f);
                    ps.setSpeedRange(0.1f, 0.25f);
                    ps.setRotationSpeedRange(90, 180);
                    ps.setFadeOut(200, new AccelerateInterpolator());
                    ps.oneShot(findViewById(R.id.birthday_text), 70);
                }
            });
        }
        mViewPager.setAdapter(new Adapter(fragments, getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(this);
        mCirclePageIndicator.setViewPager(mViewPager);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });

        buttonVisibility(mViewPager.getCurrentItem());
    }

    public static abstract class GenericFragment extends BaseFragment {
        private final int res;

        public GenericFragment(int res) {
            this.res = res;
        }

        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);
            setContentView(res);
        }

        public void animate() {
        }

        public void onSwitch() {
            animate();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        buttonVisibility(position);
        fragments.get(position).onSwitch();
    }

    private void buttonVisibility(int position) {
        if (position == 0) mBackButton.setVisibility(View.INVISIBLE);
        else mBackButton.setVisibility(View.VISIBLE);
        if (position == fragments.size() - 1) mNextButton.setVisibility(View.INVISIBLE);
        else mNextButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private class Adapter extends FragmentPagerAdapter {

        private final List<GenericFragment> fragments;

        public Adapter(List<GenericFragment> fragments, FragmentManager fm) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public GenericFragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

}
