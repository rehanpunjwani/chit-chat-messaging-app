package com.example.chitchat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class AppIntroActivity extends AppIntro {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_app_intro);

        addSlide(AppIntroFragment.newInstance("Welcome to Chit Chat App", "Lets Discover new World of Chatting ",
                R.drawable.one, ContextCompat.getColor(getApplicationContext(), R.color.first_color)));
        addSlide(AppIntroFragment.newInstance("All Features in one App", "You can make Friends,Public Groups, Audio Call, Send Files and Much More",
                R.drawable.two, ContextCompat.getColor(getApplicationContext(), R.color.second_color)));
        addSlide(AppIntroFragment.newInstance("Get Started", "Enjoy ",
                R.drawable.three, ContextCompat.getColor(getApplicationContext(), R.color.third_color)));
        setFadeAnimation();

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences !=null){
            boolean checkShared = sharedPreferences.getBoolean("checkState",false);
            if(checkShared == true){
                startActivity(new Intent(AppIntroActivity.this,MainActivity.class));
                finish();
            }
        }
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        editor.putBoolean("checkState",true).commit();
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        editor.putBoolean("checkState",false).commit();
        finish();
    }
}