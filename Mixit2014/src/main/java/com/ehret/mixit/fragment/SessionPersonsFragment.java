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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.domain.people.Membre;
import com.ehret.mixit.domain.talk.Conference;
import com.ehret.mixit.model.ConferenceFacade;
import com.ehret.mixit.model.MembreFacade;
import com.ehret.mixit.ui.MembreActivity;
import com.ehret.mixit.utils.FileUtils;
import com.ehret.mixit.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Ce fragment permet d'afficher les sessions d'un user
 */
public class SessionPersonsFragment extends Fragment {
    public static final String TAG = "SessionPersonsFragment";
    private ViewGroup mRootView;
    private LayoutInflater mInflater;
    private String typeSession;
    private Long idSession;
    private LinearLayout linearLayoutRoot;
    private TextView userName;
    private TextView descriptif;
    private TextView level;
    private ImageView profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_list_tweets, container);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity().getIntent().getExtras() != null) {
            idSession = getActivity().getIntent().getExtras().getLong(UIUtils.MESSAGE);
            typeSession = getActivity().getIntent().getExtras().getString(UIUtils.TYPE);
        } else {
            //On gere le cas ou on tourne l'écran en restorant les états de la vue
            idSession = savedInstanceState.getLong("ID_SESSION");
            ;
            typeSession = savedInstanceState.getString("TYPE_SESSION");
        }
        Conference conference = null;
        //On recupere la session concernee
        if (TypeFile.lightningtalks.name().equals(typeSession)) {
            conference = ConferenceFacade.getInstance().getLightningtalk(getActivity(), idSession);
        } else {
            conference = ConferenceFacade.getInstance().getTalk(getActivity(), idSession);
        }

        List<Membre> speakers = new ArrayList<Membre>();
        for (Long id : conference.getSpeakers()) {
            Membre membre = MembreFacade.getInstance().getMembre(getActivity(), TypeFile.members.name(), id);
            if (membre != null) {
                speakers.add(membre);
            }
        }

        //On affiche les liens que si on a recuperer des choses
        if (!speakers.isEmpty()) {
            //On utilisait auparavant une liste pour afficher ces éléments dans la page mais cette liste
            //empêche d'avoir un ScrollView englobant pour toute la page. Nous utilisons donc un tableau
            linearLayoutRoot = (LinearLayout) mInflater.inflate(R.layout.fragment_linear, mRootView, false);

            //On vide les éléments
            linearLayoutRoot.removeAllViews();

            //On ajoute un table layout
            TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            TableLayout tableLayout = new TableLayout(getActivity().getBaseContext());
            tableLayout.setLayoutParams(tableParams);

            for (final Membre membre : speakers) {
                RelativeLayout row = (RelativeLayout) mInflater.inflate(R.layout.person_item, null);
                row.setBackgroundResource(R.drawable.row_transparent_background);

                //Dans lequel nous allons ajouter le contenu que nous faisons mappé dans
                userName = (TextView) row.findViewById(R.id.person_user_name);
                descriptif = (TextView) row.findViewById(R.id.person_shortdesciptif);
                level = (TextView) row.findViewById(R.id.person_level);
                profileImage = (ImageView) row.findViewById(R.id.person_user_image);

                userName.setText(membre.getCompleteName());

                if (membre.getShortdesc() != null) {
                    descriptif.setText(membre.getShortdesc().trim());
                }

                if (membre.getLevel() != null && !membre.getLevel().isEmpty()) {
                    level.setText("[" + membre.getLevel().trim() + "]");
                }

                //Recuperation de l'mage liee au profil
                Bitmap image = FileUtils.getImageProfile(getActivity(), membre);
                if (image == null) {
                    profileImage.setImageDrawable(getResources().getDrawable(R.drawable.person_image_empty));
                } else {
                    //On regarde dans les images embarquees
                    profileImage.setImageBitmap(image);
                }

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> parameters = new HashMap<String, Object>(2);
                        parameters.put(UIUtils.MESSAGE, membre.getId());
                        parameters.put(UIUtils.TYPE, TypeFile.speaker);
                        UIUtils.startActivity(MembreActivity.class, getActivity(), parameters);
                    }
                });

                tableLayout.addView(row);
            }

            linearLayoutRoot.addView(tableLayout);
            mRootView.addView(linearLayoutRoot);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("ID_SESSION", idSession);
        outState.putString("TYPE_SESSION", typeSession);
    }

}
