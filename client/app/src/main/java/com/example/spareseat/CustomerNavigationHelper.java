package com.example.spareseat;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public final class CustomerNavigationHelper {

    private CustomerNavigationHelper() {
    }

    public static void setup(AppCompatActivity activity,
                             BottomNavigationView bottomNavigationView,
                             int selectedItemId) {
        syncSelection(bottomNavigationView, selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == selectedItemId) {
                return true;
            }

            Intent intent;
            if (item.getItemId() == R.id.navBrowse) {
                intent = new Intent(activity, LoggedInActivity.class);
            } else if (item.getItemId() == R.id.navReservations) {
                intent = new Intent(activity, MyReservationsActivity.class);
            } else {
                return false;
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
            return true;
        });
    }

    public static void syncSelection(BottomNavigationView bottomNavigationView, int selectedItemId) {
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }
}
