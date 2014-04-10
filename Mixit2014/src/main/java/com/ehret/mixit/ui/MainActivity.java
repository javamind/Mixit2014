package com.ehret.mixit.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.ui.AbstractActivity;

public class MainActivity extends AbstractActivity {

    /**
     * Nom du fichier
     */
    private final static String FILE_SAV = "Mixit2014";
    /**
     * Id de la manche en cours
     */
    private final static String KEY_FIRST_TIME = "first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = this.getSharedPreferences(FILE_SAV, 0);
        Boolean test = settings.getBoolean(KEY_FIRST_TIME, true);
        if(test){
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(KEY_FIRST_TIME, false);
            super.appelerSynchronizer(TypeFile.speaker, true);
            //super.appelerSynchronizer(TypeFile.talks, false);
            editor.commit();
        }

    }


}
