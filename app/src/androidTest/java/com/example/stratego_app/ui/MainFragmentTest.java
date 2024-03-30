package com.example.stratego_app.ui;

import androidx.test.ext.junit.rules.ActivityScenarioRule;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import com.example.stratego_app.R;


@RunWith(AndroidJUnit4.class)
public class MainFragmentTest {

    /*
    MainActivity: launch before each test and tearDown afterwards.
    App logic: to display MainFragment upon launch
     */
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testSettingsButton() {
        onView(ViewMatchers.withId(R.id.settings))
                .check(matches(isDisplayed()))
                .perform(click());
    }
    @Test
    public void testStartGameButton() {
        onView(ViewMatchers.withId(R.id.startGame))
                .check(matches(isDisplayed()))
                .perform(click());
    }




}
