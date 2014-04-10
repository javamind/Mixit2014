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
import android.util.TypedValue;
import android.view.*;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.ui.AbstractPlanningActivity;
import com.ehret.mixit.utils.ButtonGridBuilder;
import com.ehret.mixit.utils.TextViewGridBuilder;
import com.ehret.mixit.utils.UIUtils;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Planning de la première journée
 */
public class CalendarFragmentJ1 extends AbstractCalendarFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setConteneur((LinearLayout) inflater.inflate(R.layout.fragment_calendar2, container, false));
        return getConteneur();
    }

    protected void dessinerCalendrier() {
        addViewHeure();
        addViewQuartHeure();

        addViewEventCommun(0,6," ", null,R.drawable.button_empty_background);
        addViewEventCommun(6, 6, getResources().getString(R.string.calendrier_accueil), UIUtils.createPlageHoraire(29, 8, 30),R.drawable.button_pause_background);
        addViewEventCommun(12,3,getResources().getString(R.string.calendrier_orga), UIUtils.createPlageHoraire(29, 9, 0),R.drawable.button_pause_background);

        addViewEventCommun(15, 5, getResources().getString(R.string.calendrier_keynote), UIUtils.createPlageHoraire(29, 9, 15),R.drawable.button_ligtalk_background);
        addViewEventCommun(20, 4, getResources().getString(R.string.calendrier_pause), UIUtils.createPlageHoraire(29, 9, 40), R.drawable.button_pause_background);

        addViewTalk(24, 10, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(29, 10, 0));
        addViewTalk(34, 4, getResources().getString(R.string.calendrier_pause), false, R.drawable.button_pause_background, UIUtils.createPlageHoraire(29, 10, 50));
        addViewTalk(38, 10, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(29, 11, 10));
        addViewWorkshop(24, 24, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(29, 10, 0));


        addViewTalk(48, 12,getResources().getString(R.string.calendrier_repas), false, R.drawable.button_pause_background, UIUtils.createPlageHoraire(29, 12, 0));
        addViewWorkshop(48, 6, getResources().getString(R.string.calendrier_repas), false, R.drawable.button_pause_background, UIUtils.createPlageHoraire(29, 12, 0));

        addViewWorkshop(54, 24, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(29, 12, 30));

        addViewTalk(60, 6, getString(R.string.calendrier_ligthning_small), false, R.drawable.button_ligtalk_background, UIUtils.createPlageHoraire(29, 13, 0));
        addViewTalk(66, 2, getString(R.string.calendrier_presses), false, R.drawable.button_pause_background, UIUtils.createPlageHoraire(29, 13, 30));
        addViewTalk(68, 10, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(29, 13, 40));

        addViewEventCommun(78, 4, getResources().getString(R.string.calendrier_pause), UIUtils.createPlageHoraire(29, 14, 30),R.drawable.button_pause_background);

        addViewWorkshop(82  , 24, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(29, 14, 50));
        addViewTalk(82, 10, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(29, 14, 50));
        addViewTalk(92, 4, getString(R.string.calendrier_pause), false, R.drawable.button_pause_background, UIUtils.createPlageHoraire(29, 15, 40));
        addViewTalk(96, 10, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(29, 16, 0));

        addViewEventCommun(106, 4, getResources().getString(R.string.calendrier_pause), UIUtils.createPlageHoraire(29, 16, 50),R.drawable.button_pause_background);
        addViewEventCommun(110, 5, getResources().getString(R.string.calendrier_keynote), UIUtils.createPlageHoraire(29, 17, 10),R.drawable.button_ligtalk_background);
        addViewEventCommun(115, 5, getResources().getString(R.string.calendrier_keynote), UIUtils.createPlageHoraire(29, 17, 35),R.drawable.button_ligtalk_background);

        addViewEventCommun(120,12," ", null,R.drawable.button_empty_background);
        addViewEventCommun(132, 12, getResources().getString(R.string.calendrier_partie), UIUtils.createPlageHoraire(29, 19, 0),R.drawable.button_ligtalk_background);

    }


}
