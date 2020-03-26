package com.congregator;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailInputEditText;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordInputEditText;
    private ProgressBar loginProgressBar;
    private Button loginButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailTextInputLayout = findViewById(R.id.activity_login_email_textinputlayout);
        emailInputEditText = findViewById(R.id.activity_login_email_textinputedittext);
        passwordTextInputLayout = findViewById(R.id.activity_login_password_textinputlayout);
        passwordInputEditText = findViewById(R.id.activity_login_password_textinputedittext);
        loginProgressBar = findViewById(R.id.login_progressbar);

        loginButton = findViewById(R.id.activity_login_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
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

                signIn(email, password);
            }
        });

        LinearLayout signupLayout = findViewById(R.id.activity_login_signup_linearlayout);
        signupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(LoginActivity.this);
                Intent signupActivityIntent = new Intent(view.getContext(), SignupActivity.class);
                startActivity(signupActivityIntent);
                finish();
            }
        });

        TextView forgotPasswordTextView = findViewById(R.id.activity_login_forgot_password_textview);
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(LoginActivity.this);
                startActivity(getIntent());
                finish();
                Intent forgotPasswordIntent = new Intent(view.getContext(), ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
            }
        });
    }

    private void signIn(String email, String password) {
        boolean isEmailValid = false;
        boolean isPasswordValid = false;

        if (Utility.isEmailInvalid(email)) {
            emailTextInputLayout.setError(getText(R.string.invalid_email_error_message));
        } else {
            emailTextInputLayout.setError(null);
            isEmailValid = true;
        }

        if (password.isEmpty()) {
            passwordTextInputLayout.setError(getText(R.string.required_error_message));
        } else {
            passwordTextInputLayout.setError(null);
            isPasswordValid = true;
        }

        if (isEmailValid && isPasswordValid) {
            Utility.hideKeyboard(LoginActivity.this);
            loginButton.setVisibility(View.INVISIBLE);
            loginProgressBar.setVisibility(View.VISIBLE);

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
                                loginProgressBar.setVisibility(View.INVISIBLE);
                                loginButton.setVisibility(View.VISIBLE);

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException invalidUserException) {
                                    Toast.makeText(LoginActivity.this, R.string.no_account_with_email_error_message, Toast.LENGTH_LONG).show();
                                } catch (FirebaseAuthInvalidCredentialsException invalidCredentialsException) {
                                    Toast.makeText(LoginActivity.this, R.string.password_incorrect_error_message, Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(LoginActivity.this, R.string.login_error_message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }
}
