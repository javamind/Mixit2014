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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.JsonFile;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.domain.people.Membre;
import com.ehret.mixit.fragment.DialogAboutFragment;
import com.ehret.mixit.model.ConferenceFacade;
import com.ehret.mixit.model.MembreFacade;
import com.ehret.mixit.model.Synchronizer;
import com.ehret.mixit.utils.FileUtils;
import com.ehret.mixit.utils.UIUtils;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe mère de toutes nos activités
 */
public abstract class AbstractActivity extends Activity {
    private int progressStatus = 0;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Association du bon fichier de menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        //Le menu refresh est masque pour les activities qui n'en ont pas besoin
        if (!(this instanceof SocialActivity)) {
            menu.removeItem(R.id.menu_refresh);
        }
        if (!(this instanceof ParseListeActivity)) {
            menu.removeItem(R.id.menu_search);
        } else {
            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        }
        if (!(this instanceof TalkActivity)) {
            menu.removeItem(R.id.menu_favorites);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                return UIUtils.startActivity(MainActivity.class, this);
            case R.id.menu_about:
                DialogAboutFragment dial = new DialogAboutFragment();
                dial.show(getFragmentManager(), getResources().getString(R.string.about_titre));
                return true;
            case R.id.menu_refresh:
                refresh();
                return true;
            case R.id.menu_compose_google:
                sendMessage(SendSocial.plus);
                return true;
            case R.id.menu_compose_twitter:
                sendMessage(SendSocial.twitter);
                return true;
            case R.id.menu_sync_membre:
                chargementDonnees(TypeFile.members);
                return true;
            case R.id.menu_sync_speaker:
                chargementDonnees(TypeFile.speaker);
                return true;
            case R.id.menu_sync_talk:
                chargementDonnees(TypeFile.talks);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Affichage d'un message pour savoir quelle données récupérer
     * @param type
     */
    protected void chargementDonnees(final TypeFile type) {
        if (UIUtils.isNetworkAvailable(getBaseContext())) {
            if (FileUtils.isExternalStorageWritable()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(type == TypeFile.members ? R.string.dial_message_membre : (type == TypeFile.talks ? R.string.dial_message_talk : R.string.dial_message)))
                        .setPositiveButton(R.string.dial_oui, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                appelerSynchronizer(type, true  );
                            }
                        })
                        .setNeutralButton(R.string.dial_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //On ne fait rien
                            }
                        });
                builder.create();
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), getText(R.string.sync_erreur), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getBaseContext(), getText(R.string.sync_erreur_reseau), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Lancement de la synchro
     * @param type
     */
    protected void appelerSynchronizer(TypeFile type, boolean display) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCancelable(true);
        int nbMax = 0;
        if(type.equals(TypeFile.speaker)){
            nbMax = MembreFacade.getInstance().getMembres(getBaseContext(), TypeFile.speaker.name(), null).size() +
                    MembreFacade.getInstance().getMembres(getBaseContext(), TypeFile.sponsor.name(), null).size() +
                    MembreFacade.getInstance().getMembres(getBaseContext(), TypeFile.staff.name(), null).size()+5;
        }
        else if (type.equals(TypeFile.members)){
            nbMax = MembreFacade.getInstance().getMembres(getBaseContext(), TypeFile.members.name(), null).size();
        }
        else{
            nbMax = 3;
        }
        progressDialog.setMax(nbMax);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(getResources().getString(R.string.sync_message));
        if(display){
            progressDialog.show();
        }
        SynchronizeMixitAsync synchronizeMixitAsync = new SynchronizeMixitAsync();
        synchronizeMixitAsync.execute(type);
    }

    /**
     * Template methode pouvant être surchargée par les écrans pour gérer le refresh
     */
    public void refresh() {
    }


    private enum SendSocial {twitter, plus}

    /**
     * Permet d'envoyer un message en filtrant les intents
     *
     * @param type
     */
    private void sendMessage(SendSocial type) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.hastag));
        if (!UIUtils.filterIntent(this, type.name(), i)) {
            Toast.makeText(getBaseContext(), SendSocial.plus.equals(type) ? R.string.description_no_google : R.string.description_no_twitter, Toast.LENGTH_SHORT).show();
        }
        startActivity(Intent.createChooser(i, "Share URL"));
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
            TypeFile type = params[0];
            List<JsonFile> jsonToSync = null;
            //En fonction de la demande on télécharge tel ou tel fichier
            if(type.equals(TypeFile.speaker)){
                jsonToSync = Lists.newArrayList(JsonFile.FileSpeaker, JsonFile.FileInterest, JsonFile.FileSponsor, JsonFile.FileStaff);
            }
            else if (type.equals(TypeFile.members)){
                jsonToSync = Lists.newArrayList(JsonFile.FileMembers, JsonFile.FileInterest);
            }
            else{
                jsonToSync = Lists.newArrayList(JsonFile.FileLightningTalks, JsonFile.FileInterest, JsonFile.FileTalks);
            }

            for (JsonFile json : jsonToSync) {
                try {
                    if (!Synchronizer.downloadJsonFile(getBaseContext(), json.getUrl(), json.getType())) {
                        //Si une erreur de chargement on sort
                        break;
                    }
                    publishProgress(progressStatus++);
                } catch (RuntimeException e) {
                    Log.w("DialogSynchronizeFragment", "Impossible de synchroniser", e);
                }
            }

            //Une fois finie on supprime le cache
            if(type.equals(TypeFile.speaker)){
                MembreFacade.getInstance().viderCacheSpeakerStaffSponsor();
            }
            else if (type.equals(TypeFile.members)){
                MembreFacade.getInstance().viderCacheMembres();
            }
            else{
                ConferenceFacade.getInstance().viderCache();
            }

            if (type.equals(TypeFile.talks)) {
                return null;
            }

            //L'action d'après consiste à charger les images
            List<Membre> membres = null;

            if (type.equals(TypeFile.members)) {
                membres = MembreFacade.getInstance().getMembres(getBaseContext(), TypeFile.members.name(), null);
            }
            else if (type.equals(TypeFile.speaker)) {
                membres = MembreFacade.getInstance().getMembres(getBaseContext(), TypeFile.staff.name(), null);
                membres.addAll(MembreFacade.getInstance().getMembres(getBaseContext(), TypeFile.speaker.name(), null));
            }

            for (Membre membre : membres) {
                if (membre.getUrlimage() != null) {
                    Synchronizer.downloadImage(getBaseContext(), membre.getUrlimage(), "membre" + membre.getId());
                    publishProgress(progressStatus++);
                }
            }
            //Pour les sponsors on s'interesse au logo
            if (type.equals(TypeFile.speaker)) {
                for (Membre membre : MembreFacade.getInstance().getMembres(getBaseContext(), TypeFile.sponsor.name(), null)) {
                    if (membre.getLogo() != null) {
                        Synchronizer.downloadImage(getBaseContext(), membre.getLogo(), "membre" + membre.getId());
                        publishProgress(progressStatus++);
                    }
                }
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
