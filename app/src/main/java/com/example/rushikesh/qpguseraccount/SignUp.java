package com.example.rushikesh.qpguseraccount;

import android.app.ProgressDialog;
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

public class SignUp extends AppCompatActivity implements View.OnClickListener{

    EditText editTextEmail,editTextPassword;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.textViewLogin).setOnClickListener(this);
        findViewById(R.id.buttonSignUp).setOnClickListener(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);


        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSignUp:
                registerUser();
                break;

            case R.id.textViewLogin:
                finish();
                Intent intent = new Intent(SignUp.this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

        }

    }

    private void registerUser() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            editTextEmail.setError("Enter vaild email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.length()<6){

            editTextPassword.setError("Minimum length should be 6");
            editTextPassword.requestFocus();
            return;
        }
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.cancel();
                if (task.isSuccessful()){
                    finish();
                    startActivity(new Intent(SignUp.this,Home.class));
                    Toast.makeText(SignUp.this,"User register successfully",Toast.LENGTH_LONG).show();
                }else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(SignUp.this,"You are already register",Toast.LENGTH_LONG).show();

                    }else {
                        Toast.makeText(SignUp.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                    }

                }
            }
        });
    }

}
