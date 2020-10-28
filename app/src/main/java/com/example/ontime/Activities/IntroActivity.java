package com.example.ontime.Activities;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.ontime.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;


public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // You can customize your parallax parameters in the constructors.
        setTransformer(new AppIntroPageTransformerType.Parallax(
                1.0,
                1.0,
                1.0
        ));
        addSlide(AppIntroFragment.newInstance(
                "Welcome to SmartBag",
                "Do you hate figuring out what to take out and put into your bag in order for it to be ready?",
                R.drawable.ic_firstpagetutorial

        ));
        addSlide(AppIntroFragment.newInstance(
                "What is it for?", "SmartBag is made primarily for school. But it can be used for other things.",
                R.drawable.ic_tutorialschool
        ));

        addSlide(AppIntroFragment.newInstance(
                "How does it work?", "It keeps track of what is in your bag and then tells you what to take out or put in depending on your schedule.",
                R.drawable.ic_work
        ));

        addSlide(AppIntroFragment.newInstance(
                "Schedule", "You can share your schedule with your class mates. Thanks to the unique code smart bag gives you.",
                R.drawable.ic_tutorial_schedule
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
