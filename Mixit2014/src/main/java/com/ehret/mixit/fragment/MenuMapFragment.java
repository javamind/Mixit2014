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

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.utils.UIUtils;

/**
 * Fragment utilise sur la page daccueil pour afficher les talks
 */
public class MenuMapFragment extends Fragment {
    protected TextView mapText;
    protected TextView mapDescView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_gmap, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Mise a jour du titre
        if (mapText == null) {
            mapText = (TextView) getActivity().findViewById(R.id.mapTextView);
            mapText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                    mapIntent.setData(Uri.parse("geo:45.78375369999999,4.869024799999999?z=14&q=CPE+Lyon,+43+Boulevard+du+11+novembre,+69616+Villeurbanne"));
                    UIUtils.filterIntent(getActivity(), "maps", mapIntent);
                    startActivity(Intent.createChooser(mapIntent, "Venir à Mix-IT"));
                }
            });
        }

    }


}
