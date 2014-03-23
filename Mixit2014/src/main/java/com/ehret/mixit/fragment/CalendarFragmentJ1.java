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

         addViewEventCommun(0, 3, getResources().getString(R.string.calendrier_accueil), UIUtils.createPlageHoraire(25, 8, 0),R.drawable.button_pause_background);
        addViewEventCommun(3,1,getResources().getString(R.string.calendrier_orga), UIUtils.createPlageHoraire(25, 8, 45),R.drawable.button_pause_background);
        addViewEventCommun(4,2,getResources().getString(R.string.calendrier_keynote), UIUtils.createPlageHoraire(25, 9, 0),R.drawable.button_ligtalk_background);
        addViewEventCommun(6,1,getResources().getString(R.string.calendrier_presses), UIUtils.createPlageHoraire(25, 9, 20),R.drawable.button_pause_background);
        addViewTalk(7, 4, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(25, 9, 45));
        addViewEventPalge1(11, 2, getString(R.string.calendrier_pause), false, UIUtils.createPlageHoraire(25, 10, 45));
        addViewWorkshop(7, 6, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(25, 9, 45));
        addViewTalk(13, 4, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(25, 11, 15));
        addViewWorkshop(13, 6, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(25, 11, 15));
        addViewEventPalge1(17, 2, getString(R.string.calendrier_repas), false, UIUtils.createPlageHoraire(25, 12, 15));
        addViewEventCommun(19, 1, getString(R.string.calendrier_repas), UIUtils.createPlageHoraire(25, 12, 45),R.drawable.button_pause_background);
        addViewWorkshop(20, 6, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(25, 13, 0));
        addViewTalk(20, 2, getString(R.string.calendrier_ligthning_small), false, R.drawable.button_ligtalk_background, UIUtils.createPlageHoraire(25, 13, 0));
        addViewTalk(22, 4, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(25, 13, 30));
        addViewWorkshop(26, 6, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(25, 14, 30));
        addViewEventPalge1(26, 2, getString(R.string.calendrier_pause), false, UIUtils.createPlageHoraire(25, 14, 30));
        addViewTalk(28,4, getResources().getString(R.string.calendrier_conf_small),false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(25, 15, 0));
        addViewWorkshop(32, 12, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(25, 14, 30));
        addViewEventPalge1(32, 2, getString(R.string.calendrier_pause), false, UIUtils.createPlageHoraire(25, 16, 0));
        addViewTalk(34,4, getResources().getString(R.string.calendrier_conf_small),false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(25, 16, 30));
        addViewEventPalge1(38, 2, getString(R.string.calendrier_pause), false, UIUtils.createPlageHoraire(25, 17, 30));
        addViewTalk(40,4, getResources().getString(R.string.calendrier_conf_small),false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(25, 18, 0));
        addViewEventCommun(44,4," ", null,R.drawable.button_pause_background);
    }


}
