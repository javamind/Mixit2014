package com.ehret.mixit.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.ehret.mixit.R;

public class Salle2Activity extends AbstractActivity {
    private ImageView image;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salle1);

        image = (ImageView) findViewById(R.id.image_salle1);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
