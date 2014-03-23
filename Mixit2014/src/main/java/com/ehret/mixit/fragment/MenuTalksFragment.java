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

import android.content.Context;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.TypeFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment utilise sur la page daccueil pour afficher les talks
 */
public class MenuTalksFragment extends AbstractMenuFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_menu_talk;
    }

    @Override
    public int getIdTitle() {
        return R.id.menuFragmentTitle1;
    }

    @Override
    public int getIdTable() {
        return R.id.menuFragmentTableLayout1;
    }

    @Override
    public int getNameMenu() {
        return R.string.description_sessions;
    }

    @Override
    public void createElementsMenu() {
        Context context = getActivity().getBaseContext();
        final Map<String, Object> parameters = new HashMap<String, Object>(6);

        createMenu(R.color.blue3, context.getString(R.string.description_favorite), false,TypeFile.favorites);
        createMenu(R.color.blue1, context.getString(R.string.description_talk), false,TypeFile.talks);
        createMenu(R.color.yellow2, context.getString(R.string.description_workshop), false, TypeFile.workshops);
        createMenu(R.color.blue2, context.getString(R.string.description_ligtningtalk),true, TypeFile.lightningtalks);

    }

}
