package com.congregator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailInputEditText;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordInputEditText;

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
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailTextInputLayout = findViewById(R.id.activity_login_email_textinputlayout);
        emailInputEditText = findViewById(R.id.activity_login_email_textinputedittext);
        passwordTextInputLayout = findViewById(R.id.activity_login_password_textinputlayout);
        passwordInputEditText = findViewById(R.id.activity_login_password_textinputedittext);

        Button loginButton = findViewById(R.id.activity_login_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(LoginActivity.this);
                String email = emailInputEditText.getText().toString();
                String password = passwordInputEditText.getText().toString();

                signIn(email, password);
            }
        });

        LinearLayout signupLayout = findViewById(R.id.activity_login_signup_linearlayout);
        signupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupActivityIntent = new Intent(view.getContext(), SignupActivity.class);
                startActivity(signupActivityIntent);
                finish();
            }
        });
    }

    private void signIn(String email, String password) {
        boolean isEmailValid = false;
        boolean isPasswordValid = false;

        if (email.isEmpty()) {
            emailTextInputLayout.setError("required");
        } else {
            emailTextInputLayout.setError(null);
            isEmailValid = true;
        }

        if (password.isEmpty()) {
            passwordTextInputLayout.setError("required");
        } else {
            passwordTextInputLayout.setError(null);
            isPasswordValid = true;
        }

        if (isEmailValid && isPasswordValid) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(mainActivityIntent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
