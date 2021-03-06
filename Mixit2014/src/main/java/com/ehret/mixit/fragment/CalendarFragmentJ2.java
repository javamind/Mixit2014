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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.utils.UIUtils;


/**
 * Planning de la première journée
 */
public class CalendarFragmentJ2 extends AbstractCalendarFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setConteneur((LinearLayout) inflater.inflate(R.layout.fragment_calendar2, container, false));
        return getConteneur();
    }

    protected void dessinerCalendrier() {
        addViewHeure();
        addViewQuartHeure();

        addViewEventCommun(0,6," ", null,R.drawable.button_empty_background);
        addViewEventCommun(6, 6, getResources().getString(R.string.calendrier_accueil), UIUtils.createPlageHoraire(30, 8, 30),R.drawable.button_pause_background);
        addViewEventCommun(12,3,getResources().getString(R.string.calendrier_orga), UIUtils.createPlageHoraire(30, 9, 0),R.drawable.button_pause_background);

        addViewEventCommun(15, 5, getResources().getString(R.string.calendrier_keynote), UIUtils.createPlageHoraire(30, 9, 15),R.drawable.button_ligtalk_background);
        addViewEventCommun(20, 4, getResources().getString(R.string.calendrier_pause), UIUtils.createPlageHoraire(30, 9, 40), R.drawable.button_pause_background);

        addViewTalk(24, 10, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(30, 10, 0));
        addViewTalk(34, 4, getResources().getString(R.string.calendrier_pause), false, R.drawable.button_pause_background, UIUtils.createPlageHoraire(30, 10, 50));
        addViewTalk(38, 10, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(30, 11, 10));
        addViewWorkshop(24, 24, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(30, 10, 0));


        addViewTalk(48, 12,getResources().getString(R.string.calendrier_repas), false, R.drawable.button_pause_background, UIUtils.createPlageHoraire(30, 12, 0));
        addViewWorkshop(48, 6, getResources().getString(R.string.calendrier_repas), false, R.drawable.button_pause_background, UIUtils.createPlageHoraire(30, 12, 0));

        addViewWorkshop(54, 24, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(30, 12, 30));

        addViewTalk(60, 6, getString(R.string.calendrier_ligthning_small), false, R.drawable.button_ligtalk_background, UIUtils.createPlageHoraire(30, 13, 0));
        addViewTalk(66, 2, getString(R.string.calendrier_presses), false, R.drawable.button_pause_background, UIUtils.createPlageHoraire(30, 13, 30));
        addViewTalk(68, 10, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(30, 13, 40));

        addViewEventCommun(78, 4, getResources().getString(R.string.calendrier_pause), UIUtils.createPlageHoraire(30, 14, 30),R.drawable.button_pause_background);

        addViewWorkshop(82  , 24, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(30, 14, 50));
        addViewTalk(82, 10, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(30, 14, 50));
        addViewTalk(92, 4, getString(R.string.calendrier_pause), false, R.drawable.button_pause_background, UIUtils.createPlageHoraire(30, 15, 40));
        addViewTalk(96, 10, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(30, 16, 0));

        addViewEventCommun(106, 4, getResources().getString(R.string.calendrier_pause), UIUtils.createPlageHoraire(30, 16, 50),R.drawable.button_pause_background);
        addViewEventCommun(110, 5, getResources().getString(R.string.calendrier_keynote), UIUtils.createPlageHoraire(30, 17, 10),R.drawable.button_ligtalk_background);
        addViewEventCommun(115, 5, getResources().getString(R.string.calendrier_keynote), UIUtils.createPlageHoraire(30, 17, 35),R.drawable.button_ligtalk_background);
        addViewEventCommun(120, 2, getResources().getString(R.string.calendrier_cloture), UIUtils.createPlageHoraire(30, 18, 0),R.drawable.button_ligtalk_background);

        addViewEventCommun(122,22," ", null,R.drawable.button_empty_background);

//        calendarGrid = (GridLayout) getActivity().findViewById(R.id.planningGrid);
//        calendarGrid.removeAllViews();
//        calendarGrid.setColumnCount(4);
//        calendarGrid.setRowCount(52);
//
//
//        //On peut fusionner les colonnes 1 et 2 pour un event
//        GridLayout.Spec colFusionne = GridLayout.spec(2,2, GridLayout.FILL);
//        GridLayout.Spec col1 = GridLayout.spec(0, GridLayout.FILL);
//        GridLayout.Spec col2 = GridLayout.spec(1, GridLayout.FILL);
//        GridLayout.Spec col3 = GridLayout.spec(2, GridLayout.FILL);
//        GridLayout.Spec col4 = GridLayout.spec(3, GridLayout.FILL);
//
//        TextView textView = null;
//
//        addViewHeure();
//        addViewQuartHeure();
//
//        addViewEventCommun(0, 3, getResources().getString(R.string.calendrier_accueil), UIUtils.createPlageHoraire(26, 8, 0),R.drawable.button_pause_background);
//        addViewEventCommun(3,1,getResources().getString(R.string.calendrier_orga), UIUtils.createPlageHoraire(26, 8, 45),R.drawable.button_pause_background);
//        addViewEventCommun(4,2,getResources().getString(R.string.calendrier_keynote), UIUtils.createPlageHoraire(26, 9, 0),R.drawable.button_ligtalk_background);
//        addViewEventCommun(6,1,getResources().getString(R.string.calendrier_presses), UIUtils.createPlageHoraire(26, 9, 20),R.drawable.button_pause_background);
//        addViewTalk(7, 4, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(26, 9, 45));
//        addViewEventPalge1(11, 2, getString(R.string.calendrier_pause), false, UIUtils.createPlageHoraire(26, 10, 45));
//        addViewWorkshop(7, 6, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(26, 9, 45));
//        addViewTalk(13, 4, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(26, 11, 15));
//        addViewWorkshop(13, 6, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(26, 11, 15));
//        addViewEventPalge1(17, 2, getString(R.string.calendrier_repas), false, UIUtils.createPlageHoraire(26, 12, 15));
//        addViewEventCommun(19, 1, getString(R.string.calendrier_repas), UIUtils.createPlageHoraire(26, 12, 45),R.drawable.button_pause_background);
//
//        addViewWorkshop(20, 6, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(26, 13, 0));
//        addViewTalk(20, 2, getString(R.string.calendrier_ligthning_small), false, R.drawable.button_ligtalk_background, UIUtils.createPlageHoraire(26, 13, 0));
//        addViewTalk(22, 4, getResources().getString(R.string.calendrier_conf_small), false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(26, 13, 30));
//        addViewWorkshop(26, 6, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(26, 14, 30));
//        addViewEventPalge1(26, 2, getString(R.string.calendrier_pause), false, UIUtils.createPlageHoraire(26, 14, 30));
//
//        addViewTalk(28,4, getResources().getString(R.string.calendrier_conf_small),false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(26, 15, 0));
//        addViewWorkshop(32, 8, getResources().getString(R.string.calendrier_atelier), false, UIUtils.createPlageHoraire(26, 14, 30));
//        addViewEventPalge1(32, 2, getString(R.string.calendrier_pause), false, UIUtils.createPlageHoraire(26, 14, 30));
//        addViewTalk(34,6, getResources().getString(R.string.calendrier_conf_small),false, R.drawable.button_talk_background, UIUtils.createPlageHoraire(26, 15, 0));
//
//        addViewEventCommun(40,2,getResources().getString(R.string.calendrier_keynote), UIUtils.createPlageHoraire(26, 18, 0),R.drawable.button_ligtalk_background);
//        addViewEventCommun(42, 6, " ", null,R.drawable.button_pause_background);
    }


}
