package com.congregator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
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

    private FirebaseAuth mAuth;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

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

        Button signupButton = findViewById(R.id.activity_signup_signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(SignupActivity.this);
                String email = emailInputEditText.getText().toString();
                String password = passwordInputEditText.getText().toString();
                String confirmPassword = confirmPasswordInputEditText.getText().toString();

                createAccount(email, password, confirmPassword);
            }
        });

        LinearLayout loginLayout = findViewById(R.id.activity_signup_login_linearlayout);
        loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        if (!email.endsWith("@ufl.edu")) {
            emailTextInputLayout.setError("must end with @ufl.edu");
        } else {
            emailTextInputLayout.setError(null);
            isEmailValid = true;
        }
        if (password.length() < 8) {
            passwordTextInputLayout.setError("must be at least 8 characters long");
        } else {
            passwordTextInputLayout.setError(null);
            isPasswordValid = true;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordTextInputLayout.setError("passwords must match");
        } else {
            confirmPasswordTextInputLayout.setError(null);
            isConfirmPasswordValid = true;
        }

        if (isEmailValid && isPasswordValid && isConfirmPasswordValid) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                new VerificationActivity().sendVerificationEmail(mAuth.getCurrentUser(), SignupActivity.this);

                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(SignupActivity.this, "Account successfully created.", Toast.LENGTH_SHORT).show();
                                Intent verificationActivityIntent = new Intent(SignupActivity.this, VerificationActivity.class);
                                startActivity(verificationActivityIntent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

}
