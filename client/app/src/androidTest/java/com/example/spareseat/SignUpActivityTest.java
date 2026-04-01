package com.example.spareseat;

import android.widget.EditText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {

    @Rule
    public ActivityScenarioRule<SignUpActivity> activityRule =
            new ActivityScenarioRule<>(SignUpActivity.class);

    private void typeInField(int tilId, String text) {
        onView(allOf(isDescendantOfA(withId(tilId)), isAssignableFrom(EditText.class)))
                .perform(replaceText(text), closeSoftKeyboard());
    }

    @Test
    public void keyElements_areDisplayed() {
        onView(withId(R.id.tilFullName)).check(matches(isDisplayed()));
        onView(withId(R.id.tilEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.tilPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.tilConfirmPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnRoleClient)).check(matches(isDisplayed()));
        onView(withId(R.id.btnRoleHost)).check(matches(isDisplayed()));
        onView(withId(R.id.btnCreateAccount)).check(matches(isDisplayed()));
    }

    @Test
    public void shortPassword_staysOnSignUpScreen() {
        typeInField(R.id.tilFullName, "Test User");
        typeInField(R.id.tilEmail, "test@example.com");
        typeInField(R.id.tilPassword, "Ab1");
        typeInField(R.id.tilConfirmPassword, "Ab1");
        onView(withId(R.id.btnCreateAccount)).perform(scrollTo(), click());
        onView(withId(R.id.btnCreateAccount)).check(matches(isDisplayed()));
    }

    @Test
    public void noUppercasePassword_staysOnSignUpScreen() {
        typeInField(R.id.tilFullName, "Test User");
        typeInField(R.id.tilEmail, "test@example.com");
        typeInField(R.id.tilPassword, "password1");
        typeInField(R.id.tilConfirmPassword, "password1");
        onView(withId(R.id.btnCreateAccount)).perform(scrollTo(), click());
        onView(withId(R.id.btnCreateAccount)).check(matches(isDisplayed()));
    }

    @Test
    public void passwordMismatch_staysOnSignUpScreen() {
        typeInField(R.id.tilFullName, "Test User");
        typeInField(R.id.tilEmail, "test@example.com");
        typeInField(R.id.tilPassword, "Password1");
        typeInField(R.id.tilConfirmPassword, "Password2");
        onView(withId(R.id.btnCreateAccount)).perform(scrollTo(), click());
        onView(withId(R.id.btnCreateAccount)).check(matches(isDisplayed()));
    }
}
