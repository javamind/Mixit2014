/*
 * Copyright 2014 Guillaume EHRET
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
package com.ehret.mixit.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.utils.UIUtils;


/**
 * Ce fragment permet d'afficher d'afficher un compteur de jour avant l'event
 * puis une fois que c'est passé un message pour l'année prochaine
 */
public class WhatsOnFragment extends Fragment {
    private Handler mHandler = new Handler();

    private TextView mCountdownTextView;
    private ViewGroup mRootView;
    private LayoutInflater mInflater;

    private static final int ANNOUNCEMENTS_LOADER_ID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_whats_on, container);
        refresh();
        return mRootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler.removeCallbacks(mCountdownRunnable);
    }

    private void refresh() {
        mHandler.removeCallbacks(mCountdownRunnable);
        mRootView.removeAllViews();

        final long currentTimeMillis = System.currentTimeMillis();

        // Show Loading... and load the view corresponding to the current state
        if (currentTimeMillis < UIUtils.CONFERENCE_START_MILLIS) {
            setupBefore();
        } else if (currentTimeMillis > UIUtils.CONFERENCE_END_MILLIS) {
            setupAfter();
        }
    }

    private void setupBefore() {
        // Before conference, show countdown.
        mCountdownTextView = (TextView) mInflater.inflate(R.layout.whats_on_countdown, mRootView, false);
        mRootView.addView(mCountdownTextView);
        mHandler.post(mCountdownRunnable);
    }

    private void setupAfter() {
        // After conference, show canned text.
        mInflater.inflate(R.layout.whats_on_thank_you, mRootView, true);
    }


    /**
     * Event that updates countdown timer. Posts itself again to
     * {@link #mHandler} to continue updating time.
     */
    private final Runnable mCountdownRunnable = new Runnable() {
        public void run() {
            int remainingSec = (int) Math.max(0, (UIUtils.CONFERENCE_START_MILLIS - System.currentTimeMillis()) / 1000);
            final boolean conferenceStarted = remainingSec == 0;

            if (conferenceStarted) {
                // Conference started while in countdown mode, switch modes and
                // bail on future countdown updates.
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        refresh();
                    }
                }, 100);
                return;
            }

            final int secs = remainingSec % 86400;
            final int days = remainingSec / 86400;
            final String str;
            if (days == 0) {
                str = getResources().getString(
                        R.string.whats_on_countdown_title_0,
                        DateUtils.formatElapsedTime(secs));
            } else {
                str = getResources().getQuantityString(
                        R.plurals.whats_on_countdown_title, days, days,
                        DateUtils.formatElapsedTime(secs));
            }
            mCountdownTextView.setText(str);

            // Repost ourselves to keep updating countdown
            mHandler.postDelayed(mCountdownRunnable, 1000);
        }
    };


}
