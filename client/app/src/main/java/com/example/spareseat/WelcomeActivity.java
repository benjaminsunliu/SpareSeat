package com.example.spareseat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionManager.isLoggedIn(this)) {
            Class<?> dest = "HOST".equals(SessionManager.getUserRole(this))
                    ? HostDashboardActivity.class : LoggedInActivity.class;
            startActivity(new Intent(this, dest));
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(v ->
                startActivity(new Intent(this, SignInActivity.class)));
    }
}
