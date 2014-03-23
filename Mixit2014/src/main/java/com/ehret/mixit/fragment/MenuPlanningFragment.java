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
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.ui.PlanningJ1Activity;
import com.ehret.mixit.ui.PlanningJ2Activity;
import com.ehret.mixit.utils.ButtonTableBuilder;
import com.ehret.mixit.utils.TableRowBuilder;
import com.ehret.mixit.utils.TextViewTableBuilder;
import com.ehret.mixit.utils.UIUtils;

/**
 * Fragment utilise sur la page daccueil pour afficher les talks
 */
public class MenuPlanningFragment extends Fragment {

    protected TableLayout menuTableLayout;
    protected TextView titleMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_planning, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dessinerMenu();
    }


    /**
     * Affiche les données à l'écran
     */
    protected void dessinerMenu() {
        Context context = getActivity().getBaseContext();

        //Mise a jour du titre
        if (titleMenu == null) {
            titleMenu = (TextView) getActivity().findViewById(R.id.menuFragmentTitle3);
        }
        titleMenu.setText(context.getText(R.string.description_planning));

        //deux tableaux juxtaposer
        //Un d'une colonne pour gérer l'heure
        if (menuTableLayout == null) {
            menuTableLayout = (TableLayout) getActivity().findViewById(R.id.menuFragmentTableLayout3);
        }
        menuTableLayout.removeAllViews();

        createMenu(R.color.grey_light, R.color.grey_light,
                context.getString(R.string.blank),
                context.getString(R.string.calendrier_avril), false, true, null, 1);
        createMenu(R.color.grey_light, android.R.color.white,
                context.getString(R.string.blank),
                context.getString(R.string.calendrier_24), false, false, null, 1);

        createMenu(R.color.grey_light, R.color.yellow3,
                "\n" + context.getString(R.string.calendrier_jeudi) + "\n",
                context.getString(R.string.calendrier_25) + "\n", false, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.startActivity(PlanningJ1Activity.class, getActivity());
            }
        }, 3);
        createMenu(R.color.grey_light, R.color.yellow3,
                "\n" + context.getString(R.string.calendrier_vendredi) + "\n",
                context.getString(R.string.calendrier_26) + "\n", false, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.startActivity(PlanningJ2Activity.class, getActivity());
            }
        }, 3);
        createMenu(R.color.grey_light, android.R.color.white,
                context.getString(R.string.blank),
                context.getString(R.string.calendrier_27), true, false, null, 1);

    }

    protected void createMenu(int color1, int color2, String nom1, String nom2,
                              boolean dernierligne, boolean upper, View.OnClickListener listener, int nbLigne) {
        TableRow tableRow = new TableRowBuilder().buildTableRow(getActivity())
                .addNbColonne(2)
                .addBackground(getResources().getColor(R.color.grey)).getView();

        TextView colorView = new TextViewTableBuilder()
                .buildView(getActivity())
                .addText(nom1)
                .addPadding(4, 0, 4)
                .addBackground(getResources().getColor(color1))
                .addNbLines(nbLigne)
                .addBold(true)
                .addNbMaxLines(nbLigne)
                .addTextColor(getResources().getColor(android.R.color.black))
                .getView();

        tableRow.addView(colorView);

        if(listener!=null){
            Button button = new ButtonTableBuilder()
                    .buildView(getActivity())
                    .addAlignement(Gravity.CENTER)
                    .addText(nom2)
                    .addBorders(true, true, dernierligne, true)
                    .addPadding(4, 0, 4)
                    .addNbLines(nbLigne)
                    .addNbMaxLines(nbLigne)
                    .addUpperCase()
                    .addBold(true)
                    .addBackground(getResources().getColor(color2))
                    .addTextColor(getResources().getColor(android.R.color.black))
                    .getView();
            button.setAllCaps(upper);
            button.setOnClickListener(listener);
            button.setBackgroundResource(R.drawable.button_yellow_background);
            tableRow.addView(button);
        }
        else{
            TextView textview = new TextViewTableBuilder()
                    .buildView(getActivity())
                    .addAlignement(Gravity.CENTER)
                    .addText(nom2)
                    .addBorders(true, true, dernierligne, true)
                    .addPadding(4, 0, 4)
                    .addNbLines(nbLigne)
                    .addNbMaxLines(nbLigne)
                    .addUpperCase()
                    .addBold(true)
                    .addBackground(getResources().getColor(color2))
                    .addTextColor(getResources().getColor(android.R.color.black))
                    .getView();
            textview.setAllCaps(upper);
            textview.setOnClickListener(listener);
            textview.setBackgroundResource(R.drawable.button_white_background);
            tableRow.addView(textview);
        }

        menuTableLayout.addView(tableRow, TableRowBuilder.getLayoutParams());
    }

}
