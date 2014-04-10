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
package com.ehret.mixit.model;

import android.content.Context;
import android.util.Log;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.domain.people.Membre;
import com.ehret.mixit.domain.talk.Conference;
import com.ehret.mixit.domain.talk.Lightningtalk;
import com.ehret.mixit.domain.talk.Talk;
import com.ehret.mixit.utils.FileUtils;
import com.ehret.mixit.utils.UIUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Le but de ce fichier est de s'interfacer avec le fichier Json gerant les
 * différentes conf et lightning talks.
 */
public class ConferenceFacade {
    /**
     * Factory Json
     */
    private JsonFactory jsonFactory;
    /**
     * Objetc mapper permettant de faire le binding entre le JSON et les objets
     */
    private ObjectMapper objectMapper;
    /**
     * Instance du singleton
     */
    private static ConferenceFacade membreFacade;

    private final static String TAG = "ConferenceFacade";
    /**
     * Liste des talks statique pour ne pas la recharger à chaque appel
     */
    private static Map<Long, Talk> talks = new HashMap<Long, Talk>();
    /**
     * Liste des lightning talk statique pour ne pas la recharger à chaque appel
     */
    private static Map<Long, Lightningtalk> lightningtalks = new HashMap<Long, Lightningtalk>();

    /**
     * Events du calendrier qui ne sont pas envoyés par Mixit
     */
    private static Map<Long, Talk> talksSpeciaux = new HashMap<Long, Talk>();

    /**
     * Permet de vider le cache de données hormis les events speciaux
     */
    public void viderCache() {
        talks.clear();
        lightningtalks.clear();
    }

    /**
     * Constructeur prive car singleton
     */
    private ConferenceFacade() {
        //Creation des objets Jakkson
        this.jsonFactory = new JsonFactory();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retourne le singleton
     *
     * @return
     */
    public static ConferenceFacade getInstance() {
        if (membreFacade == null) {
            membreFacade = new ConferenceFacade();
        }
        return membreFacade;
    }

    /**
     * Permet de recuperer la liste des confs mises en favoris
     *
     * @param context
     * @return
     */
    public List<Conference> getFavorites(Context context, String filtre) {
        List<Conference> conferences = new ArrayList<Conference>();

        //La premiere étape consiste a reconstitue la liste
        Set<String> keys = context.getSharedPreferences(UIUtils.PREFS_FAVORITES_NAME, 0).getAll().keySet();
        for (String key : keys) {
            if (key != null) {
                //On regarde d'abord dans les confs
                Conference conf = getTalk(context, Long.valueOf(key));
                if (conf != null) {
                    conferences.add(conf);
                } else {
                    //On regarde dans les ligthning talks
                    conf = getLightningtalk(context, Long.valueOf(key));
                    if (conf != null) {
                        conferences.add(conf);
                    }
                }
            }
        }
        return Ordering.from(getComparatorDate()).sortedCopy(filtrerConferenceParDate(filtrerConference(conferences, filtre)));
    }

    /**
     * Permet de recuperer la liste des talks
     *
     * @param context
     * @return
     */
    public List<Talk> getTalks(Context context, String filtre) {
        return Ordering.from(getComparatorDate()).compound(getComparatorConference())
                .sortedCopy(filtrerTalk(getTalkAndWorkshops(context), TypeFile.talks, filtre));
    }

    /**
     * Permet de recuperer la liste des talks
     *
     * @param context
     * @return
     */
    public List<Lightningtalk> getLightningTalks(Context context, String filtre) {
        return Ordering.from(getComparatorDate()).compound(getComparatorConference())
                .sortedCopy(filtrerLightningTalk(getLightningtalks(context), filtre));
    }

    /**
     * Permet de recuperer la liste des talks
     *
     * @param context
     * @return
     */
    public List<Talk> getWorkshops(Context context, String filtre) {
        return Ordering.from(getComparatorDate()).compound(getComparatorConference())
                .sortedCopy(filtrerTalk(getTalkAndWorkshops(context), TypeFile.workshops, filtre));
    }

    /**
     * Cette méthode cherche les talks sur cette période
     *
     * @param date
     * @return
     */
    public List<Conference> getConferenceSurPlageHoraire(Date date, Context context) {
        List<Conference> confs = new ArrayList<Conference>();
        //On recupere les talks
        Collection<Talk> talks = getTalkAndWorkshops(context).values();

        //On decale la date de 1 minute pour ne pas avoir de souci de comparaison
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 2);
        Date dateComparee = calendar.getTime();

        for (Talk talk : talks) {
            if (talk.getStart() != null && talk.getEnd() != null){
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                cal.setTime(talk.getEnd());
                if(dateComparee.before(cal.getTime())){
                    cal.setTime(talk.getStart());
                    if(dateComparee.after(cal.getTime())){
                        confs.add(talk);
                    }
                }
            }
        }

        //On ajoute ls events particuliers
        Collection<Talk> talkSpeciaux = getEventsSpeciaux(context).values();
        for (Talk talk : talkSpeciaux) {
            if (talk.getStart() != null && talk.getEnd() != null && (dateComparee.before(talk.getEnd()) && dateComparee.after(talk.getStart()))) {
                confs.add(talk);
            }
        }
        return confs;
    }

    /**
     * Création de tous les events qui ne sont pas fournis par l'interface Mixit
     *
     * @return
     */
    public Map<Long, Talk> getEventsSpeciaux(Context context) {
        if (talksSpeciaux.isEmpty()) {

            Talk event = null;
            event = createTalkHorsConf( context.getString(R.string.calendrier_accueillg), 90000);
            event.setStart(UIUtils.createPlageHoraire(29, 8, 30));
            event.setEnd(UIUtils.createPlageHoraire(29, 9, 0));
            talksSpeciaux.put(event.getId(), event);
            event = createTalkHorsConf(context.getString(R.string.calendrier_orgalg), 90002);
            event.setStart(UIUtils.createPlageHoraire(29, 9, 0));
            event.setEnd(UIUtils.createPlageHoraire(29, 9, 15));
            talksSpeciaux.put(event.getId(), event);

            event = createTalkHorsConf(context.getString(R.string.calendrier_presseslg), 90003);
            event.setStart(UIUtils.createPlageHoraire(29, 13, 30));
            event.setEnd(UIUtils.createPlageHoraire(29, 13, 40));
            talksSpeciaux.put(event.getId(), event);
            event = createTalkHorsConf(context.getString(R.string.calendrier_accueillg), 90005);
            event.setStart(UIUtils.createPlageHoraire(30, 8, 30));
            event.setEnd(UIUtils.createPlageHoraire(30, 9, 0));
            talksSpeciaux.put(event.getId(), event);
            event = createTalkHorsConf(context.getString(R.string.calendrier_orgalg), 90006);
            event.setStart(UIUtils.createPlageHoraire(30, 9, 0));
            event.setEnd(UIUtils.createPlageHoraire(30, 9, 15));
            talksSpeciaux.put(event.getId(), event);
            event = createTalkHorsConf(context.getString(R.string.calendrier_presseslg), 90007);
            event.setStart(UIUtils.createPlageHoraire(30, 13, 30));
            event.setEnd(UIUtils.createPlageHoraire(30, 13, 40));
            talksSpeciaux.put(event.getId(), event);

            Talk repas = null;
            repas = createTalkHorsConf(context.getString(R.string.calendrier_repas), 80000);
            repas.setStart(UIUtils.createPlageHoraire(29, 12, 0));
            repas.setEnd(UIUtils.createPlageHoraire(29, 13, 0));
            talksSpeciaux.put(repas.getId(), repas);
            repas = createTalkHorsConf(context.getString(R.string.calendrier_repas), 80002);
            repas.setStart(UIUtils.createPlageHoraire(30, 12, 0));
            repas.setEnd(UIUtils.createPlageHoraire(30, 13, 0));
            talksSpeciaux.put(repas.getId(), repas);

            Talk pause = null;
            pause = createTalkHorsConf(context.getString(R.string.calendrier_pause), 70000);
            pause.setStart(UIUtils.createPlageHoraire(29, 10, 50));
            pause.setEnd(UIUtils.createPlageHoraire(29, 11, 10));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf(context.getString(R.string.calendrier_pause), 70001);
            pause.setStart(UIUtils.createPlageHoraire(29, 14, 30));
            pause.setEnd(UIUtils.createPlageHoraire(29, 14, 50));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf(context.getString(R.string.calendrier_pause), 70002);
            pause.setStart(UIUtils.createPlageHoraire(29, 9, 40));
            pause.setEnd(UIUtils.createPlageHoraire(29, 10, 0));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf(context.getString(R.string.calendrier_pause), 70003);
            pause.setStart(UIUtils.createPlageHoraire(29, 15, 40));
            pause.setEnd(UIUtils.createPlageHoraire(29, 16, 0));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf(context.getString(R.string.calendrier_pause), 70004);
            pause.setStart(UIUtils.createPlageHoraire(29, 16, 50));
            pause.setEnd(UIUtils.createPlageHoraire(29, 17, 10));
            talksSpeciaux.put(pause.getId(), pause);

            pause = createTalkHorsConf(context.getString(R.string.calendrier_pause), 70006);
            pause.setStart(UIUtils.createPlageHoraire(30, 9, 40));
            pause.setEnd(UIUtils.createPlageHoraire(30, 10, 0));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf(context.getString(R.string.calendrier_pause), 70005);
            pause.setStart(UIUtils.createPlageHoraire(30, 10, 50));
            pause.setEnd(UIUtils.createPlageHoraire(30, 11, 10));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf(context.getString(R.string.calendrier_pause), 70007);
            pause.setStart(UIUtils.createPlageHoraire(30, 14, 30));
            pause.setEnd(UIUtils.createPlageHoraire(30, 14, 50));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf(context.getString(R.string.calendrier_pause), 70008);
            pause.setStart(UIUtils.createPlageHoraire(30, 15, 40));
            pause.setEnd(UIUtils.createPlageHoraire(30, 16, 0));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf(context.getString(R.string.calendrier_pause), 70009);
            pause.setStart(UIUtils.createPlageHoraire(30, 16, 50));
            pause.setEnd(UIUtils.createPlageHoraire(30, 17, 10));
            talksSpeciaux.put(pause.getId(), pause);


            Talk lit = null;
            lit = createTalkHorsConf(context.getString(R.string.calendrier_ligthning), 100000);
            lit.setStart(UIUtils.createPlageHoraire(29, 13, 0));
            lit.setEnd(UIUtils.createPlageHoraire(29, 13, 30));
            talksSpeciaux.put(lit.getId(), lit);
            lit = createTalkHorsConf(context.getString(R.string.calendrier_ligthning), 100001);
            lit.setStart(UIUtils.createPlageHoraire(30, 13, 0));
            lit.setEnd(UIUtils.createPlageHoraire(30, 13, 30));
            talksSpeciaux.put(lit.getId(), lit);
            lit = createTalkHorsConf(context.getString(R.string.calendrier_cloture), 100002);
            lit.setStart(UIUtils.createPlageHoraire(30, 18, 0));
            lit.setEnd(UIUtils.createPlageHoraire(30, 18, 10));
            talksSpeciaux.put(lit.getId(), lit);
            lit = createTalkHorsConf(context.getString(R.string.calendrier_partie), 100002);
            lit.setStart(UIUtils.createPlageHoraire(29, 19, 0));
            lit.setEnd(UIUtils.createPlageHoraire(29, 20, 0));
            talksSpeciaux.put(lit.getId(), lit);

        }
        return talksSpeciaux;
    }

    private Talk createTalkHorsConf(String titleFormat, long id) {
        Talk repas = null;
        repas = new Talk();
        repas.setFormat(titleFormat);
        repas.setTitle(titleFormat);
        repas.setId(id);
        return repas;
    }



    /**
     * Permet de recuperer la liste des talks
     *
     * @param context
     * @return
     */
    private Map<Long, Talk> getTalkAndWorkshops(Context context) {
        if (talks.isEmpty()) {
            InputStream is = null;
            List<Talk> talkListe = null;
            JsonParser jp = null;
            try {
                //On regarde si fichier telecharge
                File myFile = FileUtils.getFileJson(context, TypeFile.talks);
                if (myFile == null) {
                    //On prend celui inclut dans l'archive
                    is = FileUtils.getRawFileJson(context, TypeFile.talks);
                } else {
                    is = new FileInputStream(myFile);
                }
                jp = this.jsonFactory.createJsonParser(is);
                talkListe = this.objectMapper.readValue(jp, new TypeReference<List<Talk>>() {
                });
                //On transforme la liste en Map
                for (Talk m : talkListe) {
                    talks.put(m.getId(), m);
                }
            } catch (IOException e) {
                Log.e(TAG, "Erreur lors de la recuperation des talks", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Impossible de fermer le fichier des talks", e);
                    }
                }
            }
        }
        return talks;
    }

    /**
     * Permet de recuperer la liste des talks
     *
     * @param context
     * @return
     */
    private Map<Long, Lightningtalk> getLightningtalks(Context context) {
        if (lightningtalks.isEmpty()) {
            InputStream is = null;
            List<Lightningtalk> talkListe = null;
            JsonParser jp = null;
            try {
                //On regarde si fichier telecharge
                File myFile = FileUtils.getFileJson(context, TypeFile.lightningtalks);
                if (myFile == null) {
                    //On prend celui inclut dans l'archive
                    is = FileUtils.getRawFileJson(context, TypeFile.lightningtalks);
                } else {
                    is = new FileInputStream(myFile);
                }
                jp = this.jsonFactory.createJsonParser(is);
                talkListe = this.objectMapper.readValue(jp, new TypeReference<List<Lightningtalk>>() {
                });
                //On transforme la liste en Map
                for (Lightningtalk m : talkListe) {
                    lightningtalks.put(m.getId(), m);
                }
            } catch (IOException e) {
                Log.e(TAG, "Erreur lors de la recuperation des lightning talks", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Impossible de fermer le fichier des lightnings talks", e);
                    }
                }
            }
        }
        return lightningtalks;
    }

    /**
     * @param context
     * @param key
     * @return
     */
    public Talk getTalk(Context context, Long key) {
        return getTalkAndWorkshops(context).get(key);
    }

    /**
     * @param context
     * @param key
     * @return
     */
    public Talk getWorkshop(Context context, Long key) {
        return getTalkAndWorkshops(context).get(key);
    }

    /**
     * @param context
     * @param key
     * @return
     */
    public Lightningtalk getLightningtalk(Context context, Long key) {
        return getLightningtalks(context).get(key);
    }

    /**
     * Filtre la liste des talks ou des workshops
     *
     * @param talks
     * @param type
     * @return
     */
    private List<Talk> filtrerTalk(Map<Long, Talk> talks, final TypeFile type, final String filtre) {
        return FluentIterable.from(talks.values()).filter(new Predicate<Talk>() {
            @Override
            public boolean apply(Talk input) {
                boolean retenu = false;
                if (type.equals(TypeFile.workshops)) {
                    retenu = "Workshop".equals(((Talk) input).getFormat());
                } else {
                    retenu = !"Workshop".equals(((Talk) input).getFormat());
                }
                if (retenu) {
                    return (filtre == null ||
                            (input.getTitle() != null && input.getTitle().toLowerCase().contains(filtre.toLowerCase())) ||
                            (input.getSummary() != null && input.getSummary().toLowerCase().contains(filtre.toLowerCase())));
                }
                return false;
            }
        }).toList();
    }

    /**
     * Filtre la liste des talks ou des workshops
     *
     * @param talks
     * @param filtre
     * @return
     */
    private List<Lightningtalk> filtrerLightningTalk(Map<Long, Lightningtalk> talks, final String filtre) {
        return FluentIterable.from(talks.values()).filter(new Predicate<Lightningtalk>() {
            @Override
            public boolean apply(Lightningtalk input) {
                return (filtre == null ||
                        (input.getTitle() != null && input.getTitle().toLowerCase().contains(filtre.toLowerCase())) ||
                        (input.getSummary() != null && input.getSummary().toLowerCase().contains(filtre.toLowerCase())));
            }
        }).toList();
    }

    /**
     * Filtre la liste des talks ou des workshops
     *
     * @param talks
     * @param filtre
     * @return
     */
    private List<Conference> filtrerConference(List<Conference> talks, final String filtre) {
        return FluentIterable.from(talks).filter(new Predicate<Conference>() {
            @Override
            public boolean apply(Conference input) {
                return (filtre == null ||
                        (input.getTitle() != null && input.getTitle().toLowerCase().contains(filtre.toLowerCase())) ||
                        (input.getSummary() != null && input.getSummary().toLowerCase().contains(filtre.toLowerCase())));
            }
        }).toList();
    }

    /**
     * Filtre la liste des favoris pour qu'ils disparaissent le jour de la conference
     *
     * @param talks
     * @return
     */
    private List<Conference> filtrerConferenceParDate(List<Conference> talks) {
        return FluentIterable.from(talks).filter(new Predicate<Conference>() {
            @Override
            public boolean apply(Conference input) {
                //On verifie la date, si on est avant ou après la conf on garde tout
                if(System.currentTimeMillis()>UIUtils.CONFERENCE_START_MILLIS &&
                        System.currentTimeMillis()<UIUtils.CONFERENCE_END_MILLIS){
                    //Si on est dedans on ne garde que les favoris qui ne sont pas passés
                    if(input.getEnd()!=null && input.getEnd().getTime()<System.currentTimeMillis()){
                        return false;
                    }
                    return true;
                }
                else{
                    return true;
                }
            }
        }).toList();
    }

    /**
     * Renvoie le comparator permettant de trier des conf
     *
     * @return
     */
    private <T extends Conference> Comparator<T> getComparatorDate() {
        return new Comparator<T>() {
            @Override
            public int compare(T m1, T m2) {
                if (m1.getStart() == null && m2.getStart() == null)
                    return 0;
                if (m1.getStart() == null)
                    return -1;
                if (m2.getStart() == null)
                    return 1;
                return m1.getStart().compareTo(m2.getStart());
            }
        };
    }

    /**
     * Renvoie le comparator permettant de trier des conf
     *
     * @return
     */
    private <T extends Conference> Comparator<T> getComparatorConference() {
        return new Comparator<T>() {
            @Override
            public int compare(T m1, T m2) {
                return m1.getTitle().compareTo(m2.getTitle());
            }
        };
    }

    /**
     * Renvoi la liste des membres attachés à une session
     *
     * @param membre
     * @return
     */
    public List<Conference> getSessionMembre(Membre membre, Context context) {
        List<Conference> sessions = new ArrayList<Conference>();

        //On recherche les talks
        List<Talk> listetalks = Lists.newArrayList(getTalkAndWorkshops(context).values());
        for (Talk t : listetalks) {
            for (Long idS : t.getSpeakers()) {
                if (Long.valueOf(membre.getId()).equals(idS)) {
                    sessions.add(t);
                }
            }
        }
        List<Lightningtalk> listelt = Lists.newArrayList(getLightningtalks(context).values());
        for (Lightningtalk t : listelt) {
            for (Long idS : t.getSpeakers()) {
                if (Long.valueOf(membre.getId()).equals(idS)) {
                    sessions.add(t);
                }
            }
        }

        return sessions;
    }
}
