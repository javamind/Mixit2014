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
package com.ehret.mixit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.Time;

import java.util.*;

/**
 * Classe utilitaire
 */
public class UIUtils {

    private static final Time sTime = new Time();
    public static final long CONFERENCE_START_MILLIS = parseTime("2014-04-29T08:30:00.000-07:00");
    public static final long CONFERENCE_END_MILLIS = parseTime("2014-04-30T18:30:00.000-07:00");
    public static final String MESSAGE = "message";
    public static final String TYPE = "type";
    public static final String PREFS_FAVORITES_NAME = "PrefFavorites";
    public static final String PREFS_TEMP_NAME = "PrefTemp";

    /**
     * Parse the given string as a RFC 3339 timestamp, returning the value as
     * milliseconds since the epoch.
     */
    private static long parseTime(String time) {
        sTime.parse3339(time);
        return sTime.toMillis(false);
    }

    /**
     * Verifie si la connectivite reseau est OK
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Ouvre une Intent
     *
     * @param activityClass
     * @param activity
     * @return
     */
    public static boolean startActivity(Class activityClass, Activity activity) {
        return startActivity(activityClass, activity, null);
    }


    /**
     * Ouvre une Intent
     *
     * @param activityClass
     * @param activity
     * @param parametres    on simplifie et on  ne prend en compte que les Longs et les Strings
     * @return
     */
    public static boolean startActivity(Class activityClass, Activity activity, Map<String, Object> parametres) {
        Intent i;
        i = new Intent(activity, activityClass);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (parametres != null) {
            for (String key : parametres.keySet()) {
                Object param = parametres.get(key);
                if (param != null) {
                    if (param instanceof Long) {
                        i.putExtra(key, ((Long) param).longValue());
                    } else {
                        i.putExtra(key, param.toString());
                    }
                }
            }
        }
        activity.startActivity(i);
        return true;
    }

    /**
     * @param activityClass
     * @param activity
     * @param id
     * @return
     */
    public static boolean startActivity(Class activityClass, Activity activity, long id) {
        Intent i;
        i = new Intent(activity, activityClass);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(MESSAGE, id);
        activity.startActivity(i);
        return true;
    }

    /**
     * Permet de filtrer les intents proposées à l'utilisateur
     *
     * @param activity
     * @param type
     * @param i
     * @return
     */
    public static boolean filterIntent(Activity activity, String type, Intent i) {
        List<ResolveInfo> resInfo = activity.getPackageManager().queryIntentActivities(i, 0);
        if (!resInfo.isEmpty()) {
            boolean found = false;
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type)) {
                    i.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            return found;
        }
        return false;
    }

    /**
     * Creation de la bonne date
     *
     * @param jour
     * @param heure
     * @param minute
     * @return
     */
    public static Date createPlageHoraire(int jour, int heure, int minute) {
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        calendar.set(2014, 3, jour, heure, minute , 0);
        return calendar.getTime();
    }
}
