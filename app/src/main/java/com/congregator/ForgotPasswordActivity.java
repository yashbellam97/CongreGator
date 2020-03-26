package com.congregator;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailInputEditText;
    private ProgressBar forgotPasswordProgressBar;
    private Button sendEmailButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        emailTextInputLayout = findViewById(R.id.forgot_password_email_textinputlayout);
        emailInputEditText = findViewById(R.id.forgot_password_email_textinputedittext);
        forgotPasswordProgressBar = findViewById(R.id.forgot_password_progressbar);

        sendEmailButton = findViewById(R.id.forgot_password_send_email_button);
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable emailEditable = emailInputEditText.getText();
                String email = null;
                if (emailEditable != null) {
                    email = emailEditable.toString();
                }

                userExists(email);
            }
        });
    }

    private void userExists(final String email) {
        boolean isEmailValid = false;

        if (!Utility.isValidEmail(email)) {
            emailTextInputLayout.setError("Please enter a valid email address");
        } else {
            emailTextInputLayout.setError(null);
            isEmailValid = true;
        }

        if (isEmailValid) {
            Utility.hideKeyboard(ForgotPasswordActivity.this);
            sendEmailButton.setVisibility(View.INVISIBLE);
            forgotPasswordProgressBar.setVisibility(View.VISIBLE);

            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            SignInMethodQueryResult queryResult = task.getResult();
                            if (queryResult != null) {
                                List signInMethods = queryResult.getSignInMethods();
                                boolean userExists = signInMethods != null && !signInMethods.isEmpty();

                                if (userExists) {
                                    sendPasswordResetEmail(email);
                                } else {
                                    forgotPasswordProgressBar.setVisibility(View.INVISIBLE);
                                    sendEmailButton.setVisibility(View.VISIBLE);
                                    Toast.makeText(ForgotPasswordActivity.this, "Error: The email you entered doesn't match any account", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
    }
}