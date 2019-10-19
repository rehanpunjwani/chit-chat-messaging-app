package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mResetEmail;
    private Button mResetBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog mForgetProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        mToolbar = (Toolbar) findViewById(R.id.forgot_pass_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reset Password");
        mForgetProgress = new ProgressDialog(this);


        mResetEmail = findViewById(R.id.forgot_text_email);
        mResetBtn = findViewById(R.id.forgot_reset);

        mAuth = FirebaseAuth.getInstance();

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mResetEmail.getText().toString();

                if(email.isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this,"Email Field can't be Empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    mForgetProgress.setTitle("Sending Email");
                    mForgetProgress.setMessage("Please wait while we check your Email.");
                    mForgetProgress.setCanceledOnTouchOutside(false);
                    mForgetProgress.show();

                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mForgetProgress.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this,"Please Check your Email to Reset Password",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);

                            }
                            else {
                                mForgetProgress.hide();
                                String error = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this,error,Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });


    }
}
