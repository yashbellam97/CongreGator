package com.congregator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class VerificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView verificationStatusTextview;
    private Button resendVerificationEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        verificationStatusTextview = findViewById(R.id.verification_status_textview);
        resendVerificationEmailButton = findViewById(R.id.resend_verification_email_button);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.reload();

        if (!currentUser.isEmailVerified())
            Toast.makeText(VerificationActivity.this, "Email unverified.", Toast.LENGTH_LONG).show();
        else {
            Intent mainActivityIntent = new Intent(VerificationActivity.this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }

        String userEmail = null;
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        }
        String statusText = getString(R.string.verification_email_sent_to_address_message, userEmail);
        verificationStatusTextview.setText(statusText);

        resendVerificationEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentUser.isEmailVerified()) {
                    sendVerificationEmail(currentUser, VerificationActivity.this);
                } else {
                    Intent mainActivityIntent = new Intent(VerificationActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                }
            }
        });
    }

    public void sendVerificationEmail(FirebaseUser user, final Context context) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, R.string.verification_email_sent_message, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, R.string.verification_email_sending_failed_message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

}