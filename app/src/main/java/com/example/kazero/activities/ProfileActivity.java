package com.example.kazero.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.example.kazero.R;
import com.example.kazero.database.DatabaseHelper;
import com.example.kazero.models.User;
import com.example.kazero.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextInputEditText etEmail, etAddress, etPostalCode;
    private Button btnSave, btnLogout;
    private SessionManager sessionManager;
    private User currentUser;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupToolbar();

        sessionManager = new SessionManager(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        loadUserData();
        setupListeners();
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tvUsername);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPostalCode = findViewById(R.id.etPostalCode);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.profile_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> updateProfile());

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar Sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> logout())
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void loadUserData() {
        int userId = sessionManager.getUserId();

        executorService.execute(() -> {
            User user = DatabaseHelper.getUserById(userId);

            mainHandler.post(() -> {
                if (user != null) {
                    currentUser = user;
                    displayUserData();
                } else {
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void displayUserData() {
        tvUsername.setText(currentUser.getUsername());
        etEmail.setText(currentUser.getEmail());
        etAddress.setText(currentUser.getAddress());
        etPostalCode.setText(currentUser.getPostalCode());
    }

    private void updateProfile() {
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String postalCode = etPostalCode.getText().toString().trim();

        if (email.isEmpty() || address.isEmpty() || postalCode.isEmpty()) {
            Toast.makeText(this, R.string.fields_required, Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Guardando...");

        currentUser.setEmail(email);
        currentUser.setAddress(address);
        currentUser.setPostalCode(postalCode);

        executorService.execute(() -> {
            boolean success = DatabaseHelper.updateUser(currentUser);

            mainHandler.post(() -> {
                btnSave.setEnabled(true);
                btnSave.setText(R.string.save_changes);

                if (success) {
                    Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}