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

import com.ehret.mixit.R;
import com.ehret.mixit.domain.TypeFile;

/**
 * Fragment utilise sur la page daccueil pour afficher les talks
 */
public class MenuPeopleFragment extends AbstractMenuFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_menu_people;
    }

    @Override
    public int getIdTitle() {
        return R.id.menuFragmentTitle2;
    }

    @Override
    public int getIdTable() {
        return R.id.menuFragmentTableLayout2;
    }

    @Override
    public int getNameMenu() {
        return R.string.description_people;
    }

    @Override
    public void createElementsMenu() {
        createMenu(R.color.green1, getActivity().getString(R.string.description_speakers), false, TypeFile.speaker);
        createMenu(R.color.yellow1, getActivity().getString(R.string.description_staff), false,  TypeFile.staff);
        createMenu(R.color.pink1, getActivity().getString(R.string.description_sponsor), false, TypeFile.sponsor);
        createMenu(R.color.violet1, getActivity().getString(R.string.description_membres), true, TypeFile.members);
    }

}
