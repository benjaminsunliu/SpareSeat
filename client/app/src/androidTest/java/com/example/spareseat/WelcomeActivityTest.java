package com.example.spareseat;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {

    @Before
    public void clearSession() {
        SessionManager.clear(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    public void getStartedButton_isDisplayed() {
        ActivityScenario.launch(WelcomeActivity.class);
        onView(withId(R.id.btnGetStarted)).check(matches(isDisplayed()));
    }

    @Test
    public void clickGetStarted_opensSignInScreen() {
        ActivityScenario.launch(WelcomeActivity.class);
        onView(withId(R.id.btnGetStarted)).perform(click());
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()));
    }
}
