package com.congregator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NewWalkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_walk);

        Toolbar newWalkToolbar = findViewById(R.id.activity_new_walk_app_bar);
        setSupportActionBar(newWalkToolbar);
    }
}
