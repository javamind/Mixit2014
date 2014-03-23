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

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Date;


/**
 * Planning de la première journée
 */
public abstract class AbstractCalendarFragment extends Fragment {
    protected GridLayout calendarGrid;
    private LinearLayout conteneur;

    public LinearLayout getConteneur() {
        return conteneur;
    }

    public void setConteneur(LinearLayout conteneur) {
        this.conteneur = conteneur;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        calendarGrid = (GridLayout) getActivity().findViewById(R.id.planningGrid);
        calendarGrid.removeAllViews();
        calendarGrid.setColumnCount(4);
        calendarGrid.setRowCount(48);
        calendarGrid.setUseDefaultMargins(true);
        calendarGrid.setBackgroundColor(getResources().getColor(R.color.black));
        dessinerCalendrier();

        //On rearange la largeur des colonnes
        ViewTreeObserver vto = calendarGrid.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout(){
                Double calcul = (calendarGrid.getWidth()- ((double)calendarGrid.getWidth())/4)*0.51;
                for( int i=0; i< calendarGrid.getChildCount();i++){
                    View view = calendarGrid.getChildAt(i);
                    if(view instanceof Button){
                        ((Button) view).setWidth(calcul.intValue());
                    }
                    else if(view instanceof TextView){
                        //On ne regarde que les colonnes fusionnées
                        if(((GridLayout.LayoutParams)view.getLayoutParams()).columnSpec.equals(GridLayout.spec(2,2, GridLayout.FILL))){
                            ((TextView) view).setWidth(calcul.intValue()*2);
                        }
                    }
                }
                ViewTreeObserver obs = calendarGrid.getViewTreeObserver();
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeGlobalOnLayoutListener(this);
                } else {
                    obs.removeOnGlobalLayoutListener(this);
                }
            }
        });
    }


    protected abstract void dessinerCalendrier();

    /**
     * Ajout de la colonne heure
     */
    protected void addViewHeure(){
        for(int i=0 ; i<12 ; i++){
            //Heure sur 4 lignes
            TextView textView = new TextViewGridBuilder()
                    .buildView(getActivity())
                    .addAlignement(Gravity.CENTER)
                    .addText(String.valueOf(i+8) +"H")
                    .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal))
                    .addBackgroundDrawable(R.drawable.calendar_title_background)
                    .addTextColor(getResources().getColor(android.R.color.black))
                    .getView();
            setLayoutAndBorder(textView,
                    new GridLayout.LayoutParams(GridLayout.spec(i * 4, 4, GridLayout.FILL), GridLayout.spec(0, GridLayout.FILL)),
                    i == 11, true, true, false,0);
            calendarGrid.addView(textView);
        }
    }

    private void setLayoutAndBorder(TextView textView, GridLayout.LayoutParams params, boolean borderBottom, boolean borderTop,
                                    boolean borderLeft, boolean borderRight, int hauteurcalculee){
        params.bottomMargin=borderBottom?1:0;
        params.leftMargin=borderLeft?1:0;
        params.rightMargin=borderRight?1:0;
        params.topMargin=borderTop?1:0;
        textView.setLayoutParams(params);
        float facteur = 0;
        switch (hauteurcalculee){
            case 1:
                facteur = 2;
                break;
            case 2 :
            case 3 :
                facteur = 1.5f;
                break;
            case 6 :
                facteur = 2;
                break;
            case 99 :
                facteur = 0.9f;
                break;
            default:
                    facteur = 1;

        }
        textView.getLayoutParams().height=
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getInteger(R.integer.text_size_cal)* facteur,
                        getResources().getDisplayMetrics() );
    }
    /**
     * Ajout de la colonne des quarts d'heure
     */
    protected void addViewQuartHeure(){
        for(int i=0 ; i<48 ; i++){
            //Quart d'heure affiche juste un repère
            TextView textView = new TextViewGridBuilder()
                    .buildView(getActivity())
                    //.addPadding(0, 0, 0)
                    .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal_mini))
                    .addBackgroundDrawable(R.drawable.calendar_title_background)
                    .addTextColor(getResources().getColor(android.R.color.black))
                    .getView();
            setLayoutAndBorder(textView,
                    new GridLayout.LayoutParams(GridLayout.spec(i,GridLayout.FILL), GridLayout.spec(1, GridLayout.FILL)),
                    i==47, true, true,false,99);
            calendarGrid.addView(textView);
        }
    }

    /**
     * Ajoute un moment commun
     * @param row
     * @param temps
     * @param text
     * @param heure
     */
    protected void addViewEventCommun(int row, int temps, String text, final Date heure, int background){
        Button textView =  getButton(text,false,background, heure);
        setLayoutAndBorder(textView,
                new GridLayout.LayoutParams(GridLayout.spec(row,temps, GridLayout.FILL), GridLayout.spec(2,2, GridLayout.FILL)),
                row>=42, true, true,true,temps);
        calendarGrid.addView(textView);
    }


    /**
     * Ajoute une conf
     * @param row
     * @param temps
     * @param text
     * @param title
     * @param background
     * @param heure
     */
    protected void addViewTalk(int row, int temps, String text, boolean title, int background, final Date heure){
        Button textView = getButton(text, title, background, heure);
        setLayoutAndBorder(textView,
                new GridLayout.LayoutParams(GridLayout.spec(row, temps, GridLayout.FILL), GridLayout.spec(2, GridLayout.FILL)),
                false, true, true, false,temps);

        calendarGrid.addView(textView);

    }

    /**
     * Ajoute un event entre les talks
     * @param row
     * @param temps
     * @param text
     * @param title
     * @param heure
     */
    protected void addViewEventPalge1(int row, int temps, String text, boolean title, final Date heure){
        Button textView = getButton(text, title, R.drawable.button_pause_background, heure);
        setLayoutAndBorder(textView,
                new GridLayout.LayoutParams(GridLayout.spec(row, temps, GridLayout.FILL), GridLayout.spec(2, GridLayout.FILL)),
                false, true, true,false,temps);
        textView.setContentDescription(text);
        calendarGrid.addView(textView);
    }

    /**
     * Ajoute un atlier
     * @param row
     * @param temps
     * @param text
     * @param title
     * @param heure
     */
    protected void addViewWorkshop(int row, int temps, String text , boolean title, final Date heure){
        Button textView = getButton(text, title, R.drawable.button_workshop_background, heure);
        setLayoutAndBorder(textView,
                new GridLayout.LayoutParams(GridLayout.spec(row, temps, GridLayout.FILL), GridLayout.spec(3, GridLayout.FILL)),
                false, true, true,true,temps);
        calendarGrid.addView(textView);
    }

    /**
     * Ajoute un champ clickable
     * @param text
     * @param title
     * @param background
     * @param heure
     * @return
     */
    protected Button getButton(String text, boolean title, int background, final Date heure) {
         Button textView =  new ButtonGridBuilder()
                .buildView(getActivity())
                .addText(text)
                .addAlignement(Gravity.CENTER)
                .addBold(true)
                .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal))
                .addBackgroundDrawable(background)
                .addTextColor(getResources().getColor(android.R.color.black))
                .getView();

        if(title){
            textView.setAllCaps(true);
            textView.setBackgroundResource(R.drawable.calendar_title_background);
        }
        if(heure !=null){
            //Sur un clic on va faire un zoom sur une session
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof AbstractPlanningActivity) {
                        ((AbstractPlanningActivity) getActivity()).refreshPlanningHoraire(heure);
                    }
                }
            });
        }
        return textView;
    }

}
