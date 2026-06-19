package com.example.ecoufrr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 1. Carregamento de Dados (Pre-load em background)
        preloadData();

        // 2. Checagem de Permissões
        if (hasPermissions()) {
            proceedToNextScreen();
        } else {
            requestPermissions();
        }
    }

    private void preloadData() {
        new Thread(() -> {
            // "Esquenta" o banco de dados e a instância da API
            new OcorrenciaDatabase(this).getReadableDatabase();
            ClimaAPI.obterClimaBoaVista();
        }).start();
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Mesmo se a permissão for negada, prosseguimos para o app (o mapa lidará com isso)
        proceedToNextScreen();
    }

    private void proceedToNextScreen() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 3. Verificação de Sessão (Login Automático)
            SharedPreferences pref = getSharedPreferences("EcoUFRR_Prefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = pref.getBoolean("isLoggedIn", false);

            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000); // 2 segundos de exibição para Branding
    }
}