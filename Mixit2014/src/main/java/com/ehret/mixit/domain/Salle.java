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
package com.ehret.mixit.domain;

import com.ehret.mixit.R;

/**
 * Created with IntelliJ IDEA.
 * User: EHRET_G
 * Date: 04/04/13
 * Time: 21:27
 * To change this template use File | Settings | File Templates.
 */
public enum Salle {
    SALLE1("Gosling", R.color.salle1, R.drawable.salle1_background),
    SALLE2("Eich", R.color.salle2, R.drawable.salle2_background),
    SALLE3("Nonaka", R.color.salle3, R.drawable.salle3_background),
    SALLE4("Dijkstra", R.color.salle4, R.drawable.salle4_background),
    SALLE5("Turing", R.color.salle5, R.drawable.salle5_background),
    INCONNU("Inconnue", R.color.grey, 0);

    private String nom;
    private int color;
    private int drawable;


    private Salle(String nom, int color,int drawable) {
        this.nom = nom;
        this.color = color;
        this.drawable=drawable;
    }

    public String getNom() {
        return nom;
    }

    public int getColor() {
        return color;
    }
    public int getDrawable() {
        return drawable;
    }
    public static Salle getSalle(String nom) {
        if (nom != null) {
            for (Salle salle : values()) {
                if (salle.getNom().equals(nom)) {
                    return salle;
                }
            }
        }
        return INCONNU;
    }
}
