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
package com.grarak.kerneladiutordonate.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.grarak.kerneladiutordonate.R;
import com.grarak.kerneladiutordonate.elements.views.SendingBarView;
import com.grarak.kerneladiutordonate.utils.Utils;

/**
 * Created by willi on 28.12.15.
 */
public class DonateFragment extends BaseFragment implements View.OnClickListener, BillingProcessor.IBillingHandler {

    private static final String DONATE_BAR_VISIBLE = "donate_bar_visible";
    private static final String DONATE_BAR_PRICE = "donate_bar_price";
    private static final String DONATE_BAR_SUMMARY = "donate_bar_summary";
    private static final String DONATE_PRODUCT_ID = "donate_product_id";

    private CardView _2euroCard;
    private CardView _5euroCard;
    private CardView _10euroCard;
    private CardView _20euroCard;
    private BillingProcessor bp;
    private SendingBarView mDonateBar;
    private String productId;

    private TouchListener onTouchListener;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        setContentView(R.layout.donate_fragment);

        _2euroCard = (CardView) findViewById(R.id._2euro_card);
        _5euroCard = (CardView) findViewById(R.id._5euro_card);
        _10euroCard = (CardView) findViewById(R.id._10euro_card);
        _20euroCard = (CardView) findViewById(R.id._20euro_card);

        TextView _2euroText = (TextView) findViewById(R.id._2euro_text);
        TextView _5euroText = (TextView) findViewById(R.id._5euro_text);
        TextView _10euroText = (TextView) findViewById(R.id._10euro_text);
        TextView _20euroText = (TextView) findViewById(R.id._20euro_text);

        mDonateBar = (SendingBarView) findViewById(R.id.donate_bar);
        mDonateBar.setTitle(R.string.same_price);

        _2euroCard.setOnClickListener(this);
        _5euroCard.setOnClickListener(this);
        _10euroCard.setOnClickListener(this);
        _20euroCard.setOnClickListener(this);

        if (Utils.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE) {
            _2euroText.setVisibility(View.GONE);
            _5euroText.setVisibility(View.GONE);
            _10euroText.setVisibility(View.GONE);
            _20euroText.setVisibility(View.GONE);
        } else {
            _2euroText.setVisibility(View.VISIBLE);
            _5euroText.setVisibility(View.VISIBLE);
            _10euroText.setVisibility(View.VISIBLE);
            _20euroText.setVisibility(View.VISIBLE);
        }

        if (savedInstanceState != null && savedInstanceState.getBoolean(DONATE_BAR_VISIBLE)) {
            mDonateBar.setVisibility(View.VISIBLE);
            mDonateBar.setTitleRight(savedInstanceState.getCharSequence(DONATE_BAR_PRICE));
            mDonateBar.setSummary(savedInstanceState.getCharSequence(DONATE_BAR_SUMMARY));
            productId = savedInstanceState.getString(DONATE_PRODUCT_ID);
        }

        mDonateBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bp == null) {
                    Utils.toast(R.string.play_store_not_available, getActivity());
                    return;
                }
                if (productId != null) bp.purchase(getActivity(), productId);
            }
        });

        mDonateBar.setOnTouchListener(onTouchListener = new TouchListener());

        if (BillingProcessor.isIabServiceAvailable(getActivity()))
            bp = new BillingProcessor(getActivity(), getString(R.string.license_key), this);
    }

    private void hideBar() {
        mDonateBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_bottom));
        mDonateBar.setVisibility(View.GONE);
        onTouchListener.firstTouch = -1;
        onTouchListener.lastTouch = 0;
    }

    private class TouchListener implements View.OnTouchListener {
        public float firstTouch = -1;
        public float lastTouch;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            float y = event.getY();

            if (firstTouch <= 0) firstTouch = y;
            else lastTouch = y;

            if (event.getAction() == KeyEvent.ACTION_UP
                    && lastTouch - firstTouch >= v.getHeight() / 3) {
                hideBar();
            }
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == _2euroCard) {
            mDonateBar.setTitleRight(R.string._2euro);
            mDonateBar.setSummary(R.string.can_beer);
            productId = "kerneladiutordonate2";
        } else if (v == _5euroCard) {
            mDonateBar.setTitleRight(R.string._5euro);
            mDonateBar.setSummary(R.string.hot_meal);
            productId = "kerneladiutordonate5";
        } else if (v == _10euroCard) {
            mDonateBar.setTitleRight(R.string._10euro);
            mDonateBar.setSummary(R.string.movie_night);
            productId = "kerneladiutordonate10";
        } else if (v == _20euroCard) {
            mDonateBar.setTitleRight(R.string._20euro);
            mDonateBar.setSummary(R.string.tshirt);
            productId = "kerneladiutordonate20";
        }

        if (mDonateBar.getVisibility() == View.GONE) {
            mDonateBar.setVisibility(View.VISIBLE);
            mDonateBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_bottom));
        }

    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        bp.consumePurchase(productId);
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
    }

    @Override
    public void onBillingInitialized() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null) bp.release();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DONATE_BAR_VISIBLE, mDonateBar.getVisibility() == View.VISIBLE);
        outState.putCharSequence(DONATE_BAR_PRICE, mDonateBar.getTitleRight());
        outState.putCharSequence(DONATE_BAR_SUMMARY, mDonateBar.getSummary());
        if (productId != null) outState.putString(DONATE_PRODUCT_ID, productId);
        if (bp != null) bp.release();
    }

    @Override
    public boolean onBackPressed() {
        if (mDonateBar != null && mDonateBar.getVisibility() == View.VISIBLE) {
            hideBar();
            return true;
        }
        return false;
    }
}
