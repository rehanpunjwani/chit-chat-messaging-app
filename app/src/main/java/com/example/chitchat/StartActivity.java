package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class StartActivity extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN =123 ;
    private Button regBtn;
    private Button logBtn;
    private Button mGoogleSignBtn;
    private DatabaseReference mDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog mRegProgress;
    private FirebaseAuth mAuth;
    private Animation fromBottom, fromTop;
    private ImageView imageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRegProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        fromBottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromTop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        setContentView(R.layout.activity_start);
        regBtn = findViewById(R.id.start_reg_btn);
        logBtn = findViewById(R.id.start_reg_btn2);

        imageView = findViewById(R.id.imageView2);
        regBtn.setAnimation(fromBottom);
        logBtn.setAnimation(fromBottom);
        imageView.setAnimation(fromTop);



        mGoogleSignBtn = (Button) findViewById(R.id.reg_google);
        mGoogleSignBtn.setAnimation(fromBottom);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(regIntent);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET, Manifest.permission.CALL_PRIVILEGED, Manifest.permission.ACCESS_WIFI_STATE},
                1);

        mGoogleSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SignInGoogle();
            }
        });


    }
    private void sigin_with_google(FirebaseUser user) {
        String email = user.getEmail();
        String displayname = user.getDisplayName();
        String uid = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        String device_token = FirebaseInstanceId.getInstance().getToken();
        // String eDisplayName = aes.encrypt(display_name); //AES Tested with display name Successfully

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", displayname);
        userMap.put("status", getString(R.string.newStatus));
        userMap.put("image", "default");
        userMap.put("thumb_image", "default");
        userMap.put("device_token", device_token);

        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    mRegProgress.dismiss();
                    Intent mainIntent = new Intent(StartActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);


                } else {
                    Toast.makeText(StartActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        });


    }
    public void SignInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    mRegProgress.setTitle("Signing in With Google");
                    mRegProgress.setMessage("Please wait while we check your Credentials !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                mRegProgress.dismiss();
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                        progressBar.setVisibility(View.INVISIBLE);


                    Log.d("TAG", "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    sigin_with_google(user);


                } else {
//                        progressBar.setVisibility(View.INVISIBLE);

                    Log.w("TAG", "signInWithCredential:failure", task.getException());

                    Toast.makeText(StartActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {


                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(StartActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();

                }
                return;
            }

        }
    }


}
