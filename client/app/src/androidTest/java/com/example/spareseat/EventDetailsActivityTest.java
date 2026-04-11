package com.example.spareseat;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.spareseat.model.EventResponse;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EventDetailsActivityTest {

    @Test
    public void eventDetails_areDisplayed() {
        Context context = ApplicationProvider.getApplicationContext();
        EventResponse event = new EventResponse(
                12L,
                5L,
                "Jazz Night",
                "An evening of live music and small plates.",
                "2026-05-16",
                "Montreal",
                30,
                18,
                "Music"
        );

        try (ActivityScenario<EventDetailsActivity> scenario =
                     ActivityScenario.launch(EventDetailsActivity.createIntent(context, event))) {
            onView(withId(R.id.tvEventTitle)).check(matches(withText("Jazz Night")));
            onView(withId(R.id.tvEventDescription))
                    .check(matches(withText("An evening of live music and small plates.")));
            onView(withId(R.id.etQuantity)).check(matches(isDisplayed()));
            onView(withId(R.id.btnReserve)).check(matches(isDisplayed()));
        }
    }
}
