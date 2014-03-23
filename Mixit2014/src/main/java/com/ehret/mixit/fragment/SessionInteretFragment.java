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
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.domain.people.Interet;
import com.ehret.mixit.domain.people.Link;
import com.ehret.mixit.domain.people.Membre;
import com.ehret.mixit.domain.talk.Conference;
import com.ehret.mixit.model.ConferenceFacade;
import com.ehret.mixit.model.MembreFacade;
import com.ehret.mixit.utils.TextViewTableBuilder;
import com.ehret.mixit.utils.UIUtils;


/**
 * Ce fragment permet d'afficher les différents links qu'une des personnes référenceés
 * sous Mixit a partage
 */
public class SessionInteretFragment extends Fragment {
    public static final String TAG = "SessionInteretFragment";
    private ViewGroup mRootView;
    private LayoutInflater mInflater;
    private String typeSession;
    private Long idSession;
    private TextView link_text;
    private LinearLayout linearLayoutRoot;

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

        //On affiche les liens que si on a recuperer des choses
        if (conference.getInterests() != null && !conference.getInterests().isEmpty()) {
            linearLayoutRoot = (LinearLayout) mInflater.inflate(R.layout.fragment_linear, mRootView, false);
            //On vide les éléments
            linearLayoutRoot.removeAllViews();

            linearLayoutRoot.addView(new TextViewTableBuilder()
                    .buildView(getActivity())
                    .addText(getString(R.string.description_interet))
                    .addPadding(0, 10, 4)
                    .addBold(true)
                    .addUpperCase()
                    .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal_title))
                    .addTextColor(getResources().getColor(R.color.black))
                    .getView());

            StringBuffer interets = new StringBuffer();
            for (final Long iidInteret : conference.getInterests()) {
                Interet interet = MembreFacade.getInstance().getInteret(getActivity(), iidInteret);
                if (interet != null) {
                    if (interets.length() > 0) {
                        interets.append(", ");
                    }
                    interets.append(interet.getName());
                }
            }
            TextView text = new TextViewTableBuilder()
                    .buildView(getActivity())
                    .addText(interets.toString())
                    .addPadding(4, 10, 4)
                    .addSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.text_size_cal))
                    .addTextColor(getResources().getColor(R.color.black))
                    .getView();
            text.setSingleLine(false);
            linearLayoutRoot.addView(text);
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
