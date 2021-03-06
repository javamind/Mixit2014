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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.JsonFile;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.domain.people.Membre;
import com.ehret.mixit.model.ConferenceFacade;
import com.ehret.mixit.model.MembreFacade;
import com.ehret.mixit.model.Synchronizer;
import com.ehret.mixit.utils.FileUtils;
import com.ehret.mixit.utils.UIUtils;
import com.github.rjeschke.txtmark.Processor;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Activity permettant d'afficher les informations sur une personne participant à Mix-IT
 */
public class MembreActivity extends AbstractActivity {

    private TextView temp;
    private Long id;
    private String type;
    private TextView membreTitle;
    private ImageView profileImage;
    private ImageView logoImage;
    private TextView membreUserName;
    private TextView personDesciptif;
    private TextView personShortDesciptif;
    private TextView membreEntreprise;
    private ProgressDialog progressDialog;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membre);

        this.membreTitle = (TextView) findViewById(R.id.membre_title);
        this.membreUserName = (TextView) findViewById(R.id.membre_user_name);
        this.personDesciptif = (TextView) findViewById(R.id.membre_desciptif);
        this.personShortDesciptif = (TextView) findViewById(R.id.membre_shortdesciptif);
        this.membreEntreprise = (TextView) findViewById(R.id.membre_entreprise);
        this.profileImage = (ImageView) findViewById(R.id.membre_image);
        this.logoImage = (ImageView) findViewById(R.id.membre_logo);

        if (getIntent().getExtras() != null) {
            id = getIntent().getExtras().getLong(UIUtils.MESSAGE);
            type = getIntent().getExtras().getString(UIUtils.TYPE);
        }
    }

    /**
     * Recuperation des marques de la partie en cours
     */
    @Override
    protected void onResume() {
        super.onResume();

        //On commence par recuperer le Membre que l'on sohaite afficher
        Membre membre = MembreFacade.getInstance().getMembre(getBaseContext(), type, id);
        TypeFile typeFile = TypeFile.getTypeFile(type);
        switch (typeFile) {
            case staff:
                this.membreTitle.setText(getText(R.string.membre_staff) + " \n");
                this.membreTitle.setBackgroundResource(R.color.yellow1);
                break;
            case members:
                this.membreTitle.setText(getText(R.string.membre_membre) + " \n");
                this.membreTitle.setBackgroundResource(R.color.violet1);
                break;
            case sponsor:
                this.membreTitle.setText(getText(R.string.membre_sponsor) + " \n");
                this.membreTitle.setBackgroundResource(R.color.pink1);
                break;
            default:
                this.membreTitle.setText(getText(R.string.membre_speaker) + " \n");
                this.membreTitle.setBackgroundResource(R.color.green1);
        }
        this.membreTitle.setLines(2);
        this.membreUserName.setText(membre.getCompleteName());
        this.membreEntreprise.setText(membre.getCompany());
        this.personDesciptif.setText(Html.fromHtml(Processor.process(membre.getLongdesc().trim())), TextView.BufferType.SPANNABLE);
        this.personShortDesciptif.setText(Html.fromHtml(Processor.process(membre.getShortdesc().trim())));

        Bitmap image = null;
        //Si on est un sponsor on affiche le logo
        if(membre.getLevel()!=null && membre.getLevel().length()>0){
            image = FileUtils.getImageLogo(getBaseContext(), membre);
            profileImage.setImageBitmap(image);
            logoImage.setImageBitmap(image);
            logoImage.setVisibility(View.VISIBLE);
        }
        else{
            logoImage.setVisibility(View.INVISIBLE);
        }
        if (image == null) {
            //Recuperation de l'mage liee au profil
            image = FileUtils.getImageProfile(getBaseContext(), membre);
            if (image == null) {
                profileImage.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.person_image_empty));
            }
        }
        if(image!=null){
            profileImage.setImageBitmap(image);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("ID_MEMBRE", id);
        outState.putString("TYPE_MEMBRE", type);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Long myId = savedInstanceState.getLong("ID_MEMBRE");
        if (myId != null) {
            id = myId;
        }
        String myType = savedInstanceState.getString("TYPE_MEMBRE");
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
        MenuItem item = menu.findItem(R.id.menu_profile);
        if(type==null || !TypeFile.members.equals(TypeFile.getTypeFile(type))){
            item.setVisible(false);
        }
        else{
            item.setVisible(true);
        }
        return retour;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_profile) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.description_link_user))
                    .setPositiveButton(R.string.dial_oui, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            appelerSynchronizer();
                        }
                    })
                    .setNeutralButton(R.string.dial_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //On ne fait rien
                        }
                    });
            builder.create();
            builder.show();

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Lancement de la synchro
     */
    protected void appelerSynchronizer() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCancelable(true);
        int nbMax = 1;
        progressDialog.setMax(nbMax);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(getResources().getString(R.string.description_link_user_confirm));
        progressDialog.show();
        SynchronizeMixitAsync synchronizeMixitAsync = new SynchronizeMixitAsync();
        synchronizeMixitAsync.execute(TypeFile.favorites);
    }


    /**
     * Lance en asynchrone la recuperation des fichiers
     */
    private class SynchronizeMixitAsync extends AsyncTask<TypeFile, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressStatus = 0;
        }

        @Override
        protected Void doInBackground(TypeFile... params) {
            try {
                if (Synchronizer.downloadJsonFile(getBaseContext(), String.format(JsonFile.FileFavorites.getUrl(),id), JsonFile.FileFavorites.getType())) {
                    //Si OK on met a jour les favoris en ecrasant ceux existant
                    ConferenceFacade.getInstance().setFavorites(getBaseContext(), true);
                }
                publishProgress(progressStatus++);
            } catch (RuntimeException e) {
                Log.w("DialogSynchronizeFragment", "Impossible de synchroniser", e);
            }


            return null;
        }

        /**
         * This callback method is invoked when publishProgress()
         * method is called
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(progressStatus);
        }

        /**
         * This callback method is invoked when the background function
         * doInBackground() is executed completely
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try{
                progressDialog.dismiss();
            }
            catch (IllegalArgumentException e){
                //Si la vue n'est plus attachée (changement d'orientation on évite de faire planter)
                Log.w("AbstractActivity", "Erreur à la fin du chargement lors de la notification de la vue");

            }
        }
    }
}
