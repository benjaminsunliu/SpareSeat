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
import com.example.spareseat.model.UserRegistrationRequest;
import com.example.spareseat.model.UserResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final int PHONE_MAX_DIGITS = 10;
    private static final String COLOR_SELECTED = "#89F336";
    private static final String COLOR_UNSELECTED_BG = "#F0F0F0";
    private static final String COLOR_UNSELECTED_TEXT = "#888888";
    private static final String COLOR_DARK = "#111111";

    private boolean isEmailMode = true;
    private boolean isClientRole = true;
    private Button btnEmail, btnPhone, btnCreateAccount, btnRoleClient, btnRoleHost;
    private TextView labelEmail, tvSignInLink, tvError;
    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnEmail = findViewById(R.id.btnEmail);
        btnPhone = findViewById(R.id.btnPhone);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        labelEmail = findViewById(R.id.labelEmail);
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tvSignInLink = findViewById(R.id.tvSignInLink);
        tvError = findViewById(R.id.tvError);
        etFullName = (TextInputEditText) tilFullName.getEditText();
        etEmail = (TextInputEditText) tilEmail.getEditText();
        etPassword = (TextInputEditText) tilPassword.getEditText();
        etConfirmPassword = (TextInputEditText) tilConfirmPassword.getEditText();

        btnRoleClient = findViewById(R.id.btnRoleClient);
        btnRoleHost = findViewById(R.id.btnRoleHost);

        btnBack.setOnClickListener(v -> finish());
        tvSignInLink.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });
        btnEmail.setOnClickListener(v -> setMode(true));
        btnPhone.setOnClickListener(v -> setMode(false));
        btnRoleClient.setOnClickListener(v -> setRole(true));
        btnRoleHost.setOnClickListener(v -> setRole(false));
        btnCreateAccount.setOnClickListener(v -> attemptRegister());
    }

    private void setRole(boolean clientRole) {
        isClientRole = clientRole;
        if (clientRole) {
            btnRoleClient.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(COLOR_SELECTED)));
            btnRoleClient.setTextColor(Color.parseColor(COLOR_DARK));
            btnRoleHost.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(COLOR_UNSELECTED_BG)));
            btnRoleHost.setTextColor(Color.parseColor(COLOR_UNSELECTED_TEXT));
        } else {
            btnRoleHost.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(COLOR_SELECTED)));
            btnRoleHost.setTextColor(Color.parseColor(COLOR_DARK));
            btnRoleClient.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(COLOR_UNSELECTED_BG)));
            btnRoleClient.setTextColor(Color.parseColor(COLOR_UNSELECTED_TEXT));
        }
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

    private void attemptRegister() {
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        hideError();

        String name = etFullName != null && etFullName.getText() != null
                ? etFullName.getText().toString().trim() : "";
        String identifier = etEmail != null && etEmail.getText() != null
                ? etEmail.getText().toString().trim() : "";
        String password = etPassword != null && etPassword.getText() != null
                ? etPassword.getText().toString() : "";
        String confirm = etConfirmPassword != null && etConfirmPassword.getText() != null
                ? etConfirmPassword.getText().toString() : "";

        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            tilFullName.setError("Full name is required");
            valid = false;
        }

        valid = validateRegistrationInput(identifier, password, confirm);

        if (!valid) {
            return;
        }

        btnCreateAccount.setEnabled(false);
        btnCreateAccount.setText("Creating account...");

        String role = isClientRole ? "CUSTOMER" : "HOST";
        UserRegistrationRequest request = isEmailMode
                ? new UserRegistrationRequest(name, identifier, null, password, role)
                : new UserRegistrationRequest(name, null, identifier, password, role);

        ApiClient.getService().register(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                btnCreateAccount.setEnabled(true);
                btnCreateAccount.setText("Create Account");
                if (response.isSuccessful()) {
                    SessionManager.save(SignUpActivity.this, response.body());
                    startActivity(new Intent(SignUpActivity.this, LoggedInActivity.class));
                    finish();
                } else {
                    showError(parseErrorBody(response, "Registration failed. Please try again."));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                btnCreateAccount.setEnabled(true);
                btnCreateAccount.setText("Create Account");
                showError("Unable to connect. Please check your internet connection.");
            }
        });
    }

    // Extracted validation logic to make it easier to unit test `attemptRegister()`.
    // TODO: Add unit tests covering all validation branches and error messages.
    boolean validateRegistrationInput(String identifier, String password, String confirm) {
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

        String passwordError = getPasswordError(password);
        if (passwordError != null) {
            tilPassword.setError(passwordError);
            valid = false;
        }

        if (valid && !confirm.equals(password)) {
            tilConfirmPassword.setError("Passwords do not match");
            valid = false;
        }

        return valid;
    }

    static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) return "Password is required";
        if (password.length() < 8) return "Password must be at least 8 characters";
        if (!password.matches(".*[A-Z].*")) return "Password must contain at least one uppercase letter";
        if (!password.matches(".*[0-9].*")) return "Password must contain at least one number";
        return null;
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
            JSONObject json = new JSONObject(raw);
            if (json.has("message")) return json.getString("message");
            if (json.has("error")) return json.getString("error");
            return fallback;
        } catch (Exception e) {
            return fallback;
        }
    }
}
