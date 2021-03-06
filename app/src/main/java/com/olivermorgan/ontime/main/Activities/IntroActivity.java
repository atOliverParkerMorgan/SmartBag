package com.olivermorgan.ontime.main.Activities;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;


import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.olivermorgan.ontime.main.R;


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
                "Schedule", "You can share your schedule with your class mates, thanks to the unique code smart bag gives you. You can also login into your Bakaláři account.",
                R.drawable.ic_tutorial_schedule
        ));
    }

    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("buttonName", getText(R.string.LATER).toString());
        startActivity(i);
        finish();
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("buttonName", getText(R.string.LATER).toString());
        startActivity(i);
        finish();
    }
}
