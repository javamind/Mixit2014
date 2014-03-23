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

import android.os.Bundle;
import com.ehret.mixit.R;
import com.ehret.mixit.fragment.PlanningHoraireFragment;

import java.util.Date;

/**
 * Classe regroupant les parties communes aux deux jours de planning
 */
public abstract class AbstractPlanningActivity extends AbstractActivity {
    /**
     * Fragment utilisé pour afficher les resultats des différents utilsiateurs
     */
    protected PlanningHoraireFragment planningFragment;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getIdMainLayout());

        //Recup objet declare dans la vue et gerer dynamiquement
        planningFragment = (PlanningHoraireFragment) getFragmentManager().findFragmentById(R.id.planningHoraireFragment);
    }

    /**
     * Recuperation des marques de la partie en cours
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Cette méthode permet d'accéder au fragment affichant les conférences liée à la plage sélectionnee
     * a l'ecran. Cette méthode peut etre appelee par le fragment planning pour deleguer au fragment
     * planningHoraireFragment
     */
    public void refreshPlanningHoraire(Date heure) {
        planningFragment.refreshPlanningHoraire(heure);
    }

    public abstract int getIdMainLayout();
}
