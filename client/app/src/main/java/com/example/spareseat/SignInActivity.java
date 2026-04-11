package com.example.spareseat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spareseat.api.ApiClient;
import com.example.spareseat.model.LoginRequest;
import com.example.spareseat.model.UserResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private static final int PHONE_MAX_DIGITS = 10;
    private static final String COLOR_SELECTED = "#89F336";
    private static final String COLOR_UNSELECTED_BG = "#F0F0F0";
    private static final String COLOR_UNSELECTED_TEXT = "#888888";
    private static final String COLOR_DARK = "#111111";

    private boolean isEmailMode = true;
    private Button btnEmail, btnPhone, btnSignIn;
    private TextView labelEmail, tvSignUpLink, tvError;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnEmail = findViewById(R.id.btnEmail);
        btnPhone = findViewById(R.id.btnPhone);
        btnSignIn = findViewById(R.id.btnSignIn);
        labelEmail = findViewById(R.id.labelEmail);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tvSignUpLink = findViewById(R.id.tvSignUpLink);
        tvError = findViewById(R.id.tvError);
        etEmail = (TextInputEditText) tilEmail.getEditText();
        etPassword = (TextInputEditText) tilPassword.getEditText();

        btnBack.setOnClickListener(v -> finish());
        tvSignUpLink.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        btnEmail.setOnClickListener(v -> setMode(true));
        btnPhone.setOnClickListener(v -> setMode(false));
        btnSignIn.setOnClickListener(v -> attemptLogin());
    }

    private void setMode(boolean emailMode) {
        isEmailMode = emailMode;
        hideError();
        if (emailMode) {
            btnEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(COLOR_SELECTED)));
            btnEmail.setTextColor(Color.parseColor(COLOR_DARK));
            btnPhone.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(COLOR_UNSELECTED_BG)));
            btnPhone.setTextColor(Color.parseColor(COLOR_UNSELECTED_TEXT));
            labelEmail.setText("Email Address");
            if (etEmail != null) {
                etEmail.setFilters(new InputFilter[]{});
                etEmail.setHint("bobbobby@example.com");
                etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            }
        } else {
            btnPhone.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(COLOR_SELECTED)));
            btnPhone.setTextColor(Color.parseColor(COLOR_DARK));
            btnEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(COLOR_UNSELECTED_BG)));
            btnEmail.setTextColor(Color.parseColor(COLOR_UNSELECTED_TEXT));
            labelEmail.setText("Phone Number");
            if (etEmail != null) {
                etEmail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PHONE_MAX_DIGITS)});
                etEmail.setHint("5550001234");
                etEmail.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        }
        if (etEmail != null) etEmail.setText("");
        tilEmail.setError(null);
    }

    private void attemptLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);
        hideError();

        String identifier = etEmail != null && etEmail.getText() != null
                ? etEmail.getText().toString().trim() : "";
        String password = etPassword != null && etPassword.getText() != null
                ? etPassword.getText().toString() : "";

        boolean valid = true;

        if (isEmailMode) {
            if (TextUtils.isEmpty(identifier)) {
                tilEmail.setError("Email is required");
                valid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                tilEmail.setError("Enter a valid email address");
                valid = false;
            }
        } else {
            if (TextUtils.isEmpty(identifier)) {
                tilEmail.setError("Phone number is required");
                valid = false;
            } else if (identifier.length() < PHONE_MAX_DIGITS) {
                tilEmail.setError("Phone number must be " + PHONE_MAX_DIGITS + " digits");
                valid = false;
            }
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            valid = false;
        }

        if (!valid) return;

        btnSignIn.setEnabled(false);
        btnSignIn.setText("Signing in...");

        LoginRequest request = isEmailMode
                ? new LoginRequest(identifier, null, password)
                : new LoginRequest(null, identifier, password);

        ApiClient.getService().login(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                btnSignIn.setEnabled(true);
                btnSignIn.setText("Sign In");
                if (response.isSuccessful()) {
                    UserResponse user = response.body();
                    SessionManager.save(SignInActivity.this, user);
                    Class<?> dest = "HOST".equals(user != null ? user.getRole() : "")
                            ? HostDashboardActivity.class : LoggedInActivity.class;
                    startActivity(new Intent(SignInActivity.this, dest));
                    finish();
                } else {
                    showError(parseErrorBody(response, "Incorrect email or password. Please try again."));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                btnSignIn.setEnabled(true);
                btnSignIn.setText("Sign In");
                showError("Unable to connect. Please check your internet connection.");
            }
        });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
        tvError.setText("");
    }

    private String parseErrorBody(Response<?> response, String fallback) {
        try {
            if (response.errorBody() == null) return fallback;
            String raw = response.errorBody().string();
            if (raw == null || raw.isEmpty()) return fallback;
            // Try to extract a human-readable message from JSON
            JSONObject json = new JSONObject(raw);
            if (json.has("message")) return json.getString("message");
            if (json.has("error")) return json.getString("error");
            return fallback;
        } catch (Exception e) {
            return fallback;
        }
    }
}
