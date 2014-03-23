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
package com.ehret.mixit.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.Salle;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.domain.talk.Conference;
import com.ehret.mixit.domain.talk.Lightningtalk;
import com.ehret.mixit.domain.talk.Talk;
import com.ehret.mixit.model.ConferenceFacade;
import com.ehret.mixit.utils.UIUtils;
import com.github.rjeschke.txtmark.Processor;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Activité permettant d'afficher les informations sur un talk
 */
public class TalkActivity extends AbstractActivity {

    private ImageView image;
    private TextView title;
    private TextView horaire;
    private TextView level;
    private TextView levelTitle;
    private TextView name;
    private TextView summary;
    private TextView descriptif;
    private Button salle;
    private Long id;
    private String type;
    private ImageView imageFavorite;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        this.title = (TextView) findViewById(R.id.talk_title);
        this.image = (ImageView) findViewById(R.id.talk_image);
        this.imageFavorite = (ImageView) findViewById(R.id.talk_image_favorite);
        this.horaire = (TextView) findViewById(R.id.talk_horaire);
        this.level = (TextView) findViewById(R.id.talk_level);
        this.levelTitle = (TextView) findViewById(R.id.talk_level_title);
        this.name = (TextView) findViewById(R.id.talk_name);
        this.summary = (TextView) findViewById(R.id.talk_summary);
        this.descriptif = (TextView) findViewById(R.id.talk_desciptif);
        this.salle = (Button) findViewById(R.id.talk_salle);

        if (getIntent().getExtras() != null) {
            long id = getIntent().getExtras().getLong(UIUtils.MESSAGE);
            type = getIntent().getExtras().getString(UIUtils.TYPE);

            //On stocke l'ID dans la preference pour qu'on puisse le retrouvé quand l'utilisateur car dans ses operations
            //la vue ne passe pas dans les fonctions de sauvegarde/restauration de l'état
            SharedPreferences settings = getSharedPreferences(UIUtils.PREFS_TEMP_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("idTalk", id);
            editor.commit();

            Conference conference = null;
            if (TypeFile.lightningtalks.name().equals(type)) {
                conference = ConferenceFacade.getInstance().getLightningtalk(getBaseContext(), id);
                title.setText(getText(R.string.calendrier_ligthning) + " \n");
                title.setBackgroundResource(R.color.blue2);
                image.setImageDrawable(getResources().getDrawable(R.drawable.lightning));
            } else if (TypeFile.workshops.name().equals(type)) {
                conference = ConferenceFacade.getInstance().getTalk(getBaseContext(), id);
                title.setText(getText(R.string.calendrier_atelier) + " \n");
                title.setBackgroundResource(R.color.yellow2);
                image.setImageDrawable(getResources().getDrawable(R.drawable.workshop));
            } else {
                conference = ConferenceFacade.getInstance().getTalk(getBaseContext(), id);
                title.setText(getText(R.string.calendrier_conf) + " \n");
                title.setBackgroundResource(R.color.blue1);
                image.setImageDrawable(getResources().getDrawable(R.drawable.talk));
            }
            title.setLines(2);

            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
            if (conference.getStart() != null && conference.getEnd() != null) {
                horaire.setText(String.format(getResources().getString(R.string.periode),
                        sdf.format(conference.getStart()),
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(conference.getStart()),
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(conference.getEnd())
                ));
            } else {
                horaire.setText(getResources().getString(R.string.pasdate));

            }
            if (conference instanceof Talk) {
                levelTitle.setText(getString(R.string.description_niveau));
                level.setText("[" + ((Talk) conference).getLevel() + "]");
            }
            else if(conference instanceof Lightningtalk){
                levelTitle.setText(getString(R.string.description_votant));
                level.setText(""+((Lightningtalk) conference).getNbVotes());
            }
            name.setText(conference.getTitle());
            summary.setText(Html.fromHtml(conference.getSummary().trim()));
           
            descriptif.setText(Html.fromHtml(Processor.process(conference.getDescription()).trim()), TextView.BufferType.SPANNABLE);
            Salle room = Salle.INCONNU;
            if (conference instanceof Talk) {
                room = Salle.getSalle(((Talk) conference).getRoom());
            }
            final Activity talkActivity = this;
            if (Salle.INCONNU != room) {
                salle.setText(String.format(getString(R.string.Salle), room.getNom()));
                if(room.getDrawable()!=0){
                    salle.setBackgroundResource(room.getDrawable());
                }
                else{
                    salle.setBackgroundColor(getBaseContext().getResources().getColor(room.getColor()));
                }
                salle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIUtils.startActivity(SalleActivity.class, talkActivity);
                    }
                });
            }
        }
    }

    /**
     * Recuperation des marques de la partie en cours
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (id != null) {
            outState.putLong("ID_TALK", id);
            outState.putString("TYPE_TALK", type);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Long myId = savedInstanceState.getLong("ID_TALK");
        if (myId != null) {
            id = myId;
        }
        String myType = savedInstanceState.getString("TYPE_TALK");
        if (myType != null) {
            type = myType;
        }
    }

    /**
     * Adaptation du menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean retour = super.onCreateOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_favorites);
        //Dans le cadre de cet écran on doit savoir si l'activité fait partie des favoris
        //de l'utilisateur
        updateMenuItem(item, isTalkFavorite());
        return retour;
    }

    private void updateMenuItem(MenuItem item, boolean trouve) {
        if (trouve) {
            //On affiche bouton pour l'enlever
            item.setTitle(R.string.description_favorite_del);
            item.setIcon(getResources().getDrawable(R.drawable.ic_action_del_event));
            imageFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_important));
        }
        else {
            //On affiche bouton pour l'ajouter
            item.setTitle(R.string.description_favorite_add);
            item.setIcon(getResources().getDrawable(R.drawable.ic_action_add_event));
            imageFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_not_important));
        }
    }


    /**
     * Verifie si l'activité st dans les favoris
     *
     * @return
     */
    private boolean isTalkFavorite() {
        boolean trouve = false;
        SharedPreferences settings = getSharedPreferences(UIUtils.PREFS_FAVORITES_NAME, 0);
        for (String key : settings.getAll().keySet()) {
            if (key.equals(String.valueOf(id))) {
                trouve = true;
                break;
            }
        }
        return trouve;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_favorites) {
            //On recupere id
            SharedPreferences settings = getSharedPreferences(UIUtils.PREFS_TEMP_NAME, 0);
            id = settings.getLong("idTalk", 0L);
            if (id != null && id > 0) {
                //On sauvegarde le choix de l'utilsateur
                settings = getSharedPreferences(UIUtils.PREFS_FAVORITES_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (isTalkFavorite()) {
                    //S'il l'est et on qu'on a cliquer sur le bouton on supprime
                    editor.remove(String.valueOf(id));
                    updateMenuItem(item, false);

                } else {
                    editor.putBoolean(String.valueOf(id), Boolean.TRUE);
                    updateMenuItem(item, true);
                }
                editor.commit();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
