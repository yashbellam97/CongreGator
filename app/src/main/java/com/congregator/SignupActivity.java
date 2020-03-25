package com.congregator;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailInputEditText;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordInputEditText;
    private TextInputLayout confirmPasswordTextInputLayout;
    private TextInputEditText confirmPasswordInputEditText;
    private ProgressBar signupProgressBar;
    private Button signupButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailTextInputLayout = findViewById(R.id.activity_signup_email_textinputlayout);
        emailInputEditText = findViewById(R.id.activity_signup_email_textinputedittext);
        passwordTextInputLayout = findViewById(R.id.activity_signup_password_textinputlayout);
        passwordInputEditText = findViewById(R.id.activity_signup_password_textinputedittext);
        confirmPasswordTextInputLayout = findViewById(R.id.activity_signup_confirm_password_textinputlayout);
        confirmPasswordInputEditText = findViewById(R.id.activity_signup_confirm_password_textinputedittext);
        signupProgressBar = findViewById(R.id.signup_progressbar);

        signupButton = findViewById(R.id.activity_signup_signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable emailEditable = emailInputEditText.getText();
                String email = null;
                if (emailEditable != null) {
                    email = emailEditable.toString();
                }

                Editable passwordEditable = passwordInputEditText.getText();
                String password = null;
                if (passwordEditable != null) {
                    password = passwordEditable.toString();
                }

                Editable confirmPasswordEditable = confirmPasswordInputEditText.getText();
                String confirmPassword = null;
                if (confirmPasswordEditable != null) {
                    confirmPassword = confirmPasswordEditable.toString();
                }

                createAccount(email, password, confirmPassword);
            }
        });

        LinearLayout loginLayout = findViewById(R.id.activity_signup_login_linearlayout);
        loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(SignupActivity.this);
                Intent loginActivityIntent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(loginActivityIntent);
                finish();
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void createAccount(String email, String password, String confirmPassword) {
        boolean isEmailValid = false;
        boolean isPasswordValid = false;
        boolean isConfirmPasswordValid = false;

        if (!Utility.isValidEmail(email) || !email.endsWith("@ufl.edu")) {
            emailTextInputLayout.setError("Please enter a valid @ufl.edu email address");
        } else {
            emailTextInputLayout.setError(null);
            isEmailValid = true;
        }

        if (password.isEmpty() || password.length() < 8) {
            passwordTextInputLayout.setError("Password must be at least 8 characters long");
        } else {
            passwordTextInputLayout.setError(null);
            isPasswordValid = true;
        }

        if (!confirmPassword.equals(password)) {
            confirmPasswordTextInputLayout.setError("Passwords must match");
        } else {
            confirmPasswordTextInputLayout.setError(null);
            isConfirmPasswordValid = true;
        }

        if (isEmailValid && isPasswordValid && isConfirmPasswordValid) {
            Utility.hideKeyboard(SignupActivity.this);
            signupButton.setVisibility(View.INVISIBLE);
            signupProgressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                new VerificationActivity().sendVerificationEmail(mAuth.getCurrentUser(), SignupActivity.this);

                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(SignupActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                                Intent verificationActivityIntent = new Intent(SignupActivity.this, VerificationActivity.class);
                                startActivity(verificationActivityIntent);
                                finish();
                            } else {
                                signupProgressBar.setVisibility(View.INVISIBLE);
                                signupButton.setVisibility(View.VISIBLE);
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
