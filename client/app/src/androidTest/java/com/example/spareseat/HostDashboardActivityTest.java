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
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class HostDashboardActivityTest {

    @Before
    public void clearSession() {
        SessionManager.clear(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    public void keyElements_areDisplayed() {
        ActivityScenario.launch(HostDashboardActivity.class);
        onView(withId(R.id.tvGreeting)).check(matches(isDisplayed()));
        onView(withId(R.id.btnLogout)).check(matches(isDisplayed()));
        onView(withId(R.id.btnCreateEvent)).check(matches(isDisplayed()));
        onView(withId(R.id.tvEventCount)).check(matches(isDisplayed()));
    }

    @Test
    public void greeting_showsDefaultWhenNoSession() {
        ActivityScenario.launch(HostDashboardActivity.class);
        onView(withId(R.id.tvGreeting)).check(matches(withText("Hi, there!")));
    }

    @Test
    public void emptyState_isShownWhenNoSession() {
        ActivityScenario.launch(HostDashboardActivity.class);
        onView(withId(R.id.llEmpty)).check(matches(isDisplayed()));
    }

    @Test
    public void emptyStateMessage_mentionsLogin() {
        ActivityScenario.launch(HostDashboardActivity.class);
        onView(withId(R.id.tvEmpty)).check(matches(withText(containsString("log in"))));
    }

    @Test
    public void clickCreateEvent_opensFormDialog() {
        ActivityScenario.launch(HostDashboardActivity.class);
        onView(withId(R.id.btnCreateEvent)).perform(click());
        onView(withId(R.id.tilTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tilDate)).check(matches(isDisplayed()));
        onView(withId(R.id.tilLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.tilCapacity)).check(matches(isDisplayed()));
    }

    @Test
    public void clickLogout_navigatesToWelcomeScreen() {
        ActivityScenario.launch(HostDashboardActivity.class);
        onView(withId(R.id.btnLogout)).perform(click());
        onView(withId(R.id.btnGetStarted)).check(matches(isDisplayed()));
    }
}
