package com.ehret.mixit.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.domain.people.Membre;
import com.ehret.mixit.domain.talk.Conference;
import com.ehret.mixit.domain.talk.Lightningtalk;
import com.ehret.mixit.domain.talk.Talk;
import com.ehret.mixit.model.ConferenceFacade;
import com.ehret.mixit.model.MembreFacade;
import com.ehret.mixit.adapter.ListMembreAdapter;
import com.ehret.mixit.adapter.ListTalkAdapter;
import com.ehret.mixit.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;

public class ParseListeActivity extends AbstractActivity {
    private final static String TAG = "ParseListeActivity";
    private TextView title;
    private ListView liste;
    private LinearLayout layout;
    private TextView descriptif;
    private String typeAppel;
    private String filterQuery;
    private Activity mActivity;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Handle with layout
        this.title = (TextView) findViewById(R.id.liste_title);
        this.liste = (ListView) findViewById(R.id.liste_content);
        this.layout = (LinearLayout) findViewById(R.id.liste_entete);
        this.descriptif = (TextView) findViewById(R.id.liste_descr);
        this.mActivity = this;

        SharedPreferences settings = getSharedPreferences(UIUtils.PREFS_TEMP_NAME, 0);

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            filterQuery = getIntent().getStringExtra(SearchManager.QUERY);
            typeAppel = settings.getString("typeAppel", TypeFile.staff.name());
        } else if (getIntent().getExtras() != null) {
            typeAppel = (String) getIntent().getExtras().getCharSequence(UIUtils.MESSAGE);
            //On sauvagarde dans les preferences le type pour le retrouver quand on rcoit l'intent de recherche
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("typeAppel", typeAppel);
            editor.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("TYPE_APPEL", typeAppel);
        outState.putString("FILTRE_REQ", filterQuery);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String myType = savedInstanceState.getString("TYPE_APPEL");
        if (myType != null) {
            typeAppel = myType;
        }
        String query = savedInstanceState.getString("FILTRE_REQ");
        if (query != null) {
            filterQuery = query;
        }
    }

    /**
     * Recuperation des marques de la partie en cours
     */
    @Override
    protected void onResume() {
        super.onResume();

        switch (TypeFile.getTypeFile(typeAppel)) {
            case members:
                handleFields(R.string.focus_membre, R.string.focus_membre_desc, R.color.violet1);
                afficherMembre(true);
                break;
            case staff:
                handleFields(R.string.focus_orga, R.string.focus_orga_desc, R.color.yellow1);
                afficherMembre(false);
                break;
            case sponsor:
                handleFields(R.string.focus_sponsor, R.string.focus_sponsor_desc, R.color.pink1);
                afficherMembre(false);
                break;
            case talks:
                handleFields(R.string.focus_talk, R.string.focus_talk_desc, R.color.blue1);
                afficherConference();
                break;
            case workshops:
                handleFields(R.string.focus_workshop, R.string.focus_workshop_desc, R.color.yellow2);
                afficherConference();
                break;
            case lightningtalks:
                handleFields(R.string.focus_lightningtalk, R.string.focus_lightningtalk_desc, R.color.blue2);
                afficherConference();
                break;
            case favorites:
                handleFields(R.string.focus_favorite, R.string.focus_favorite_desc, R.color.blue3);
                afficherConference();
                break;
            default:
                //Par defaut on affiche les speakers
                handleFields(R.string.focus_speaker, R.string.focus_speaker_desc, R.color.green1);
                afficherMembre(false);

        }

    }


    /**
     * Permet de lier les champs du layout a l'activite
     *
     * @param resMembre
     * @param resMembreDesc
     * @param resBackground
     */
    private void handleFields(int resMembre, int resMembreDesc, int resBackground) {
        title.setText(getText(resMembre));
        descriptif.setText(getText(resMembreDesc));
        layout.setBackgroundResource(resBackground);
    }

    /**
     * Afichage es conferences
     *
     * @param partial
     */
    private void afficherMembre(boolean partial) {
        liste.setClickable(true);
        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Membre membre = (Membre) liste.getItemAtPosition(position);
                Map<String, Object> parameters = new HashMap<String, Object>(2);
                parameters.put(UIUtils.MESSAGE, membre.getId());
                parameters.put(UIUtils.TYPE, typeAppel);
                UIUtils.startActivity(MembreActivity.class, mActivity, parameters);
            }
        });
        //On trie la liste retournée
        liste.setAdapter(new ListMembreAdapter(getBaseContext(), MembreFacade.getInstance().getMembres(getBaseContext(), typeAppel, filterQuery)));
    }

    /**
     * Affichage des confs
     */
    private void afficherConference() {
        liste.setClickable(true);
        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conference conf = (Conference) liste.getItemAtPosition(position);
                Map<String, Object> parameters = new HashMap<String, Object>(2);
                parameters.put(UIUtils.MESSAGE, conf.getId());
                if (conf instanceof Lightningtalk) {
                    parameters.put(UIUtils.TYPE, TypeFile.lightningtalks.name());
                } else if (conf instanceof Talk && ((Talk) conf).getFormat().equals("Workshop")) {
                    parameters.put(UIUtils.TYPE, TypeFile.workshops.name());
                } else {
                    parameters.put(UIUtils.TYPE, TypeFile.talks.name());
                }
                UIUtils.startActivity(TalkActivity.class, mActivity, parameters);
            }
        });
        switch (TypeFile.getTypeFile(typeAppel)) {
            case workshops:
                liste.setAdapter(new ListTalkAdapter(getBaseContext(), ConferenceFacade.getInstance().getWorkshops(getBaseContext(), filterQuery)));
                break;
            case talks:
                liste.setAdapter(new ListTalkAdapter(getBaseContext(), ConferenceFacade.getInstance().getTalks(getBaseContext(), filterQuery)));
                break;
            case lightningtalks:
                liste.setAdapter(new ListTalkAdapter(getBaseContext(), ConferenceFacade.getInstance().getLightningTalks(getBaseContext(), filterQuery)));
                break;
            default:
                liste.setAdapter(new ListTalkAdapter(getBaseContext(), ConferenceFacade.getInstance().getFavorites(getBaseContext(), filterQuery)));

        }
    }
}
