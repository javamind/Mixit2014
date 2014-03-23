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
import android.view.*;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.ui.ParseListeActivity;
import com.ehret.mixit.utils.ButtonTableBuilder;
import com.ehret.mixit.utils.TableRowBuilder;
import com.ehret.mixit.utils.TextViewTableBuilder;
import com.ehret.mixit.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment utilise sur la page daccueil pour afficher les talks
 */
public abstract class AbstractMenuFragment extends Fragment {
    protected TableLayout menuTableLayout;
    protected TextView titleMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
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
            titleMenu = (TextView) getActivity().findViewById(getIdTitle());
        }
        titleMenu.setText(context.getText(getNameMenu()));

        //deux tableaux juxtaposer
        //Un d'une colonne pour gérer l'heure
        if (menuTableLayout == null) {
            menuTableLayout = (TableLayout) getActivity().findViewById(getIdTable());
        }
        menuTableLayout.removeAllViews();

        createElementsMenu();
    }

    public abstract int getLayout();

    public abstract int getIdTitle();

    public abstract int getIdTable();

    /**
     * Template methode pour que les classes filles indiquent le nom du menu
     *
     * @return
     */
    public abstract int getNameMenu();

    /**
     * * Template methode pour que les classes filles composent les lignes du menu
     */
    public abstract void createElementsMenu();

    /**
     * Créé une ligne dans le tableau afichant les données
     *
     * @param color
     * @param nom
     * @param dernierligne
     */
    protected void createMenu(int color, String nom, boolean dernierligne, final TypeFile typeFile) {
        TableRow tableRow = new TableRowBuilder().buildTableRow(getActivity())
                .addNbColonne(2)
                .addBackground(getResources().getColor(R.color.grey)).getView();

        Button colorView = new ButtonTableBuilder()
                .buildView(getActivity())
                .addText(" ")
                .addBackground(getResources().getColor(color))
                .addBold(true)
                .addPadding(0, 0, 0)
                .addNbLines(2)
                .addNbMaxLines(2)
                .getView();
        colorView.setMinimumWidth(0);
        colorView.setWidth(35);
        tableRow.addView(colorView);

        Button button = new ButtonTableBuilder()
                .buildView(getActivity())
                .addAlignement(Gravity.CENTER)
                .addText(nom )
                .addBorders(true, true, dernierligne, true)
                .addPadding(4, 0, 4)
                .addNbLines(2)
                .addNbMaxLines(2)
                .addBold(true)
                .addTextColor(getResources().getColor(android.R.color.black))
                .getView();
        button.setClickable(true);
        button.setBackgroundResource(R.drawable.button_white_background);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.setBackgroundColor(getResources().getColor(R.color.yellow1));
                Map<String, Object> parameters = new HashMap<String, Object>(6);
                parameters.put(UIUtils.MESSAGE, typeFile.name());
                UIUtils.startActivity(ParseListeActivity.class, getActivity(), parameters);
                //v.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });
        tableRow.addView(button);

        menuTableLayout.addView(tableRow, TableRowBuilder.getLayoutParams());
    }

}
