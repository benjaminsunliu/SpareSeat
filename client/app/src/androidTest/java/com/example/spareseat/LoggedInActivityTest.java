package com.example.spareseat;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoggedInActivityTest {

    @Rule
    public ActivityScenarioRule<LoggedInActivity> activityRule =
            new ActivityScenarioRule<>(LoggedInActivity.class);

    @Test
    public void searchBar_isDisplayed() {
        onView(withId(R.id.etSearch)).check(matches(isDisplayed()));
    }

    @Test
    public void filterDropdowns_areDisplayed() {
        onView(withId(R.id.actvLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.actvCategory)).check(matches(isDisplayed()));
        onView(withId(R.id.actvDate)).check(matches(isDisplayed()));
    }

    @Test
    public void eventCountLabel_isDisplayed() {
        onView(withId(R.id.tvEventCount)).check(matches(isDisplayed()));
    }
}
