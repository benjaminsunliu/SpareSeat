package com.example.spareseat;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

import android.widget.EditText;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

    @Rule
    public ActivityScenarioRule<SignInActivity> activityRule =
            new ActivityScenarioRule<>(SignInActivity.class);

    @Test
    public void keyElements_areDisplayed() {
        onView(withId(R.id.btnEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.btnPhone)).check(matches(isDisplayed()));
        onView(withId(R.id.tilEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.tilPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()));
    }

    @Test
    public void emptySubmit_staysOnSignInScreen() {
        onView(withId(R.id.btnSignIn)).perform(click());
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()));
    }

    @Test
    public void invalidEmail_staysOnSignInScreen() {
        onView(allOf(isDescendantOfA(withId(R.id.tilEmail)), isAssignableFrom(EditText.class)))
                .perform(replaceText("not-an-email"), closeSoftKeyboard());
        onView(allOf(isDescendantOfA(withId(R.id.tilPassword)), isAssignableFrom(EditText.class)))
                .perform(replaceText("password"), closeSoftKeyboard());
        onView(withId(R.id.btnSignIn)).perform(click());
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()));
    }

    @Test
    public void switchToPhoneMode_changesLabel() {
        onView(withId(R.id.btnPhone)).perform(click());
        onView(withId(R.id.labelEmail)).check(matches(withText("Phone Number")));
    }
}
