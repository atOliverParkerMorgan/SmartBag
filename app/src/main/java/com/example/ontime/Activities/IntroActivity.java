package com.example.ontime.Activities;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;


public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance(
                "Welcome to SmartBag",
                "Do you hate figuring out what to take out and put into your bag in order for it to be ready? If so SmartBag is the perfect app for you."
        ));
        addSlide(AppIntroFragment.newInstance(
                "...Let's get started!",
                "This is the last slide, I won't annoy you more :)"
        ));
    }

    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
