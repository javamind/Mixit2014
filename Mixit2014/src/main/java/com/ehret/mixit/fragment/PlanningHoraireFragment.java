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
import android.content.res.Resources;
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
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.domain.people.Membre;
import com.ehret.mixit.domain.talk.Conference;
import com.ehret.mixit.domain.talk.Lightningtalk;
import com.ehret.mixit.domain.talk.Talk;
import com.ehret.mixit.model.ConferenceFacade;
import com.ehret.mixit.model.MembreFacade;
import com.ehret.mixit.ui.ParseListeActivity;
import com.ehret.mixit.ui.PlanningJ2Activity;
import com.ehret.mixit.ui.TalkActivity;
import com.ehret.mixit.utils.TableRowBuilder;
import com.ehret.mixit.utils.TextViewTableBuilder;
import com.ehret.mixit.utils.UIUtils;

import java.text.DateFormat;
import java.util.*;


/**
 * Planning sur une plage horaire en particulier
 */
public class PlanningHoraireFragment extends Fragment {

    protected TableLayout planningHoraireTableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planning_horaire, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Par defaut on affiche la premiere session de la journee de 9H
        if (getActivity() instanceof PlanningJ2Activity) {
            refreshPlanningHoraire(UIUtils.createPlageHoraire(25, 9, 0));
        } else {
            refreshPlanningHoraire(UIUtils.createPlageHoraire(26, 9, 0));
        }
    }


    /**
     * Permet d'afficher le planning lie a la plage selectionnee
     *
     * @param heure
     */
    public void refreshPlanningHoraire(Date heure) {
        //deux tableaux juxtaposer
        //Un d'une colonne pour gérer l'heure
        planningHoraireTableLayout = (TableLayout) getActivity().findViewById(R.id.planningHoraireTableLayout);
        planningHoraireTableLayout.removeAllViews();

        Calendar c = Calendar.getInstance(Locale.FRANCE);
        c.setTime(heure);
        c.add(Calendar.MINUTE, 30);
        Date heureFin = c.getTime();


        //On affiche le planning 30min par 30min
        TableRow tableRow = createTableRow();

        tableRow.addView(new TextViewTableBuilder()
                .buildView(getActivity())
                .addText(String.format(getString(R.string.calendrier_planninga),DateFormat.getTimeInstance(DateFormat.SHORT).format(heure)))
                .addAlignement(Gravity.CENTER)
                .addBorders(true, true, false, true)
                .addPadding(4, 0, 4)
                .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal_title))
                .addSpan(2)
                .addNbLines(2)
                .addBold(true)
                .addTextColor(R.color.black)
                .addBackground(getResources().getColor(R.color.blue))
                .addBackgroundDrawable(R.drawable.planning_horaire_background)
                .getView());
        planningHoraireTableLayout.addView(tableRow, TableRowBuilder.getLayoutParams());

        List<Conference> confs = ConferenceFacade.getInstance().getConferenceSurPlageHoraire(heure, getActivity());

        int size = confs.size();
        createPlage(confs, size, 1, getActivity().getResources());
        createPlage(confs, size, 2, getActivity().getResources());
        createPlage(confs, size, 3, getActivity().getResources());
        createPlage(confs, size, 4, getActivity().getResources());
        createPlage(confs, size, 5, getActivity().getResources());

    }



    private void createPlage(List<Conference> confs, int size, int index, Resources resource) {
        if (size >= index) {
            Conference c = confs.get(index - 1);

            Salle salle = Salle.INCONNU;
            if (c instanceof Talk) {
                salle = Salle.getSalle(((Talk) c).getRoom());
            }
            char code = ((Talk) c).getFormat().charAt(0);
            createPlanningSalle("(" + code + ") " + c.getTitle(), salle.getColor(), c);

            StringBuffer buf = new StringBuffer();
            if (c.getSpeakers() != null) {
                for (Long id : c.getSpeakers()) {
                    Membre m = MembreFacade.getInstance().getMembre(getActivity(), TypeFile.speaker.name(), id);
                    if (m.getCompleteName() != null) {
                        if (!buf.toString().equals("")) {
                            buf.append(", ");
                        }
                        buf.append(m.getCompleteName());
                    }

                }
            }
            createPresentateurSalle(true, buf.toString(), salle.getColor(), c);

        }

    }

    private String getHeureStr(float heure) {
        float difference = heure - ((int) heure);
        if (difference == 0) {
            return (int) heure + "H";
        } else {
            return (int) heure + "H" + ((int) (difference * 10) * 6);
        }
    }

    /**
     * Creation d'une ligne
     *
     * @return
     */
    private TableRow createTableRow() {
        return new TableRowBuilder().buildTableRow(getActivity())
                .addNbColonne(2)
                .addBackground(getResources().getColor(R.color.grey)).getView();
    }

    /**
     * Creation du planning salle
     *
     * @param nom
     * @param color
     */
    private void createPlanningSalle(String nom, int color, final Conference conf) {
        TableRow tableRow = createTableRow();
        addEventOnTableRow(conf, tableRow);
        TextView textView = new TextViewTableBuilder()
                .buildView(getActivity())
                .addText(" \n ")
                .addNbLines(2)
                .addNbMaxLines(2)
                .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal))
                .addBackground(getResources().getColor(color))
                .getView();
        tableRow.addView(textView);

        TextView button = new TextViewTableBuilder()
                .buildView(getActivity())
                .addAlignement(Gravity.CENTER)
                .addText(nom + " \n ")
                .addBorders(true, true, false, true)
                .addPadding(8, 8, 4)
                .addBold(true)
                .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal))
                .addNbLines(2)
                .addNbMaxLines(2)
                .addTextColor(getResources().getColor(android.R.color.black))
                .getView();
        button.setBackgroundResource(R.drawable.button_white_background);

        //textView.setMaxWidth(tableRow.getWidth()-4);
        tableRow.addView(button);
        planningHoraireTableLayout.addView(tableRow, TableRowBuilder.getLayoutParams());
    }

    /**
     * Ajoute un event pour zoomer sur le detail d'une plage horaire
     *
     * @param conf
     * @param tableRow
     */
    private void addEventOnTableRow(final Conference conf, TableRow tableRow) {
        final Map<String, Object> parameters = new HashMap<String, Object>(6);

        //En fonction du type de talk nous ne faisons pas la même chose
        if (conf instanceof Lightningtalk) {
            //Pour la les lightning on affiche la liste complete
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parameters.put(UIUtils.MESSAGE, TypeFile.lightningtalks.name());
                    UIUtils.startActivity(ParseListeActivity.class, getActivity(), parameters);
                }
            });
        } else {
            //Pour les talks on ne retient que les talks et workshop
            char code = ((Talk) conf).getFormat().charAt(0);
            if (code == 'T' || code == 'W' || code == 'K') {
                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> parameters = new HashMap<String, Object>(2);
                        parameters.put(UIUtils.MESSAGE, conf.getId());
                        parameters.put(UIUtils.TYPE, ((Talk) conf).getFormat().charAt(0) == 'W' ? TypeFile.workshops.name() :
                                TypeFile.talks.name());
                        UIUtils.startActivity(TalkActivity.class, getActivity(), parameters);
                    }
                });
            }
            else if(code == 'L'){
                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> parameters = new HashMap<String, Object>(6);
                        parameters.put(UIUtils.MESSAGE, TypeFile.lightningtalks);
                        UIUtils.startActivity(ParseListeActivity.class, getActivity(), parameters);
                    }
                });
            }
        }
    }

    /**
     * Ajout presentateur
     *
     * @param dernierligne
     * @param nom
     * @param color
     */
    private void createPresentateurSalle(boolean dernierligne, String nom, int color, final Conference conf) {
        TableRow tableRow = createTableRow();
        addEventOnTableRow(conf, tableRow);
        tableRow.addView(new TextViewTableBuilder()
                .buildView(getActivity())
                .addText(" ")
                .addBackground(getResources().getColor(color))
                .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal))
                .getView());
        TextView button = new TextViewTableBuilder()
                .buildView(getActivity())
                .addAlignement(Gravity.CENTER)
                .addText(nom)
                .addBorders(true, true, dernierligne, false)
                .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal))
                .addPadding(8, 8, 4)
                .addBackground(getResources().getColor(android.R.color.white))
                .addTextColor(getResources().getColor(R.color.grey_dark))
                .getView();
        button.setBackgroundResource(R.drawable.button_white_background);
        tableRow.addView(button);

        planningHoraireTableLayout.addView(tableRow, TableRowBuilder.getLayoutParams());
    }
}