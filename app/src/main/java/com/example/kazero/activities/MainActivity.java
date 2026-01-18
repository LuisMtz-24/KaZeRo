package com.example.kazero.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kazero.R;
import com.example.kazero.database.MySQLConnection;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
        // En MainActivity, agregar temporalmente en onCreate():
        new Thread(() -> {
            boolean connected = MySQLConnection.testConnection();
            runOnUiThread(() -> {
                Toast.makeText(this,
                        connected ? "Conexión exitosa" : " Error de conexión",
                        Toast.LENGTH_LONG).show();
            });
        }).start();
    }

    }
