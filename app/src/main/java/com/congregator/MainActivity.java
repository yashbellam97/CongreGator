package com.congregator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FloatingActionButton newWalkFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainToolbar = findViewById(R.id.activity_main_app_bar);
        setSupportActionBar(mainToolbar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (!currentUser.isEmailVerified()) {
                Toast.makeText(this, "Email not verified.", Toast.LENGTH_LONG).show();
                Intent verificationIntent = new Intent(this, VerificationActivity.class);
                startActivity(verificationIntent);
                finish();
            } else updateUI(currentUser);
        } else {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        newWalkFab = findViewById(R.id.new_walk_fab);
        newWalkFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newWalkIntent = new Intent(MainActivity.this, NewWalkActivity.class);
                startActivity(newWalkIntent);
            }
        });
    }
}
