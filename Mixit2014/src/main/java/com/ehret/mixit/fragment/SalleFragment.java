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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.Salle;
import com.ehret.mixit.ui.Salle1Activity;
import com.ehret.mixit.ui.Salle2Activity;
import com.ehret.mixit.utils.TableRowBuilder;
import com.ehret.mixit.utils.TextViewTableBuilder;
import com.ehret.mixit.utils.UIUtils;


/**
 * Ce fragment permet d'afficher les tweets contenant le mot clé mix-it
 */
public class SalleFragment extends Fragment {

    protected TableLayout salleTableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_salle, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dessinerSalle();
    }

    protected void dessinerSalle() {
        //deux tableaux juxtaposer
        //Un d'une colonne pour gérer l'heure
        salleTableLayout = (TableLayout) getActivity().findViewById(R.id.salleTableLayout);
        salleTableLayout.removeAllViews();

        createSalle(Salle.SALLE1, Salle.SALLE2, false);
        createSalle(Salle.SALLE3, Salle.SALLE4, false);
        createSalle(Salle.SALLE5, Salle.SALLE6, false);
        createSalle(Salle.SALLE7, Salle.SALLE8, false);
        createSalle(Salle.SALLE9, Salle.INCONNU, false);
    }

    private void createSalle(Salle salle1, Salle salle2, boolean dernierligne) {
        TableRow tableRow = new TableRowBuilder().buildTableRow(getActivity())
                .addNbColonne(2)
                .addBackground(getResources().getColor(R.color.grey)).getView();
        createSalle(dernierligne, tableRow, salle1);
        createSalle(dernierligne, tableRow, salle2);
        salleTableLayout.addView(tableRow, TableRowBuilder.getLayoutParams());
    }

    private void createSalle(boolean dernierligne, TableRow tableRow, final Salle salle) {




        tableRow.addView(new TextViewTableBuilder()
                .buildView(getActivity())
                .addText(" ")
                .addBorders(true, false, dernierligne, true)
                .addPadding(4, 0, 4)
                .addBackground(getResources().getColor(salle.getColor()))
                .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal))
                .getView());

        TextView mySalle = new TextViewTableBuilder()
                .buildView(getActivity())
                .addAlignement(Gravity.CENTER)
                .addText(salle.getNom())
                .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal))
                .addBorders(true, true, dernierligne, true)
                .addPadding(4, 0, 4)
                .addBackground(getResources().getColor(android.R.color.white))
                .addTextColor(getResources().getColor(android.R.color.black))
                .getView();

        mySalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.startActivity(salle.getEtage()==0 ? Salle1Activity.class : Salle2Activity.class, getActivity());
            }
        });
        tableRow.addView(mySalle);
    }
}
