package com.ehret.mixit.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.ehret.mixit.R;

public class Salle1Activity extends AbstractActivity {
    private ImageView image;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salle0);

        image = (ImageView) findViewById(R.id.image_salle0);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
