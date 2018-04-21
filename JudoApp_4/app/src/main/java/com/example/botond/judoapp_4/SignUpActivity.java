package com.example.botond.judoapp_4;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    final Context context=this;
    EditText editTextEmail,editTextUsername,editTextPassword;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail=findViewById(R.id.input_email);
        editTextUsername=findViewById(R.id.input_name);
        editTextPassword=findViewById(R.id.input_password);
        progressBar=(ProgressBar)findViewById(R.id.progressbarSignUp);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }

    private  void registerUser(){
        final String email=editTextEmail.getText().toString().trim();
        String username=editTextUsername.getText().toString().trim();
        final String password=editTextPassword.getText().toString().trim();

        if(!validateInput(email,password)){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(SignUpActivity.this, "User registered succesfully!",
                                    Toast.LENGTH_SHORT).show();

                            loginUser(email,password);

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(SignUpActivity.this, "Email address already registered!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "Registration failed!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void loginUser(String email, String password){
        finish();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Incorrect email or password!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInput(String email, String password){
        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return false;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return false;
        }

        if(password.length()<6){
            editTextPassword.setError("Minimum lenght of password should be 6");
            editTextPassword.requestFocus();
            return false;
        }
        return true;
    }
}
