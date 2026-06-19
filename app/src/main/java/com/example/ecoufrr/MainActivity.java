package com.example.ecoufrr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        OcorrenciaAdapter.OnOcorrenciaActionListener {

    private RecyclerView recyclerView;
    private OcorrenciaAdapter adapter;
    private OcorrenciaDatabase db;
    private FloatingActionButton fabAdicionar;
    private TextView tvClima;
    private Button btnLogout;
    
    private MapView map = null;
    private MyLocationNewOverlay locationOverlay;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuração do OSMDroid (importante ser antes do setContentView)
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);

        // Inicializar componentes
        recyclerView = findViewById(R.id.recyclerViewOcorrencias);
        fabAdicionar = findViewById(R.id.fabAdicionar);
        tvClima = findViewById(R.id.tvClima);
        btnLogout = findViewById(R.id.btn_logout);
        map = findViewById(R.id.mapview);
        
        db = new OcorrenciaDatabase(this);

        // Configurar Mapa
        configurarMapa();

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carregarOcorrencias();

        // Botão para adicionar
        fabAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdicionarOcorrenciaActivity.class);
            startActivity(intent);
        });

        // Botão de Logout
        btnLogout.setOnClickListener(v -> {
            // Limpar sessão ao sair
            SharedPreferences pref = getSharedPreferences("EcoUFRR_Prefs", Context.MODE_PRIVATE);
            pref.edit().putBoolean("isLoggedIn", false).apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Buscar clima na thread de background
        carregarClima();

        // Solicitar permissões de localização
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
    }

    private void configurarMapa() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(18.5);
        
        // Coordenadas centrais da UFRR (Conforme solicitado: 2.833339, -60.693798)
        GeoPoint ufrrPoint = new GeoPoint(2.833339, -60.693798);
        map.getController().setCenter(ufrrPoint);

        // Overlay de Localização Atual do Usuário
        this.locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        this.locationOverlay.enableMyLocation();
        this.locationOverlay.enableFollowLocation();
        map.getOverlays().add(this.locationOverlay);
        
        atualizarMarkersNoMapa();
    }

    private void atualizarMarkersNoMapa() {
        // Limpar markers de ocorrências anteriores, mantendo o overlay de localização
        map.getOverlays().removeIf(overlay -> overlay instanceof Marker);
        
        List<Ocorrencia> ocorrencias = db.obterTodasOcorrencias();
        for (Ocorrencia oc : ocorrencias) {
            try {
                // Tenta extrair coordenadas da string localizacao (formato esperado: "lat, lon")
                String loc = oc.getLocalizacao();
                if (loc != null && loc.contains(",")) {
                    String[] parts = loc.split(",");
                    double lat = Double.parseDouble(parts[0].trim());
                    double lon = Double.parseDouble(parts[1].trim());
                    
                    Marker marker = new Marker(map);
                    marker.setPosition(new GeoPoint(lat, lon));
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle(oc.getTitulo());
                    marker.setSnippet(oc.getDescricao() + "\nStatus: " + oc.getStatus());
                    
                    // Definir cor do pin baseado no status
                    int color;
                    String status = oc.getStatus();
                    if ("Resolvido".equalsIgnoreCase(status)) {
                        color = 0xFF00FF00; // Verde
                    } else if ("Em Análise".equalsIgnoreCase(status)) {
                        color = 0xFFFFA500; // Laranja
                    } else {
                        color = 0xFFFF0000; // Vermelho (Pendente)
                    }

                    // Tint do ícone padrão
                    Drawable icon = ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default);
                    if (icon != null) {
                        icon = DrawableCompat.wrap(icon.mutate());
                        DrawableCompat.setTint(icon, color);
                        marker.setIcon(icon);
                    }
                    
                    map.getOverlays().add(marker);
                }
            } catch (Exception ignored) {
                // Ignora se não conseguir converter para coordenadas
            }
        }
        map.invalidate(); 
    }

    private void carregarOcorrencias() {
        List<Ocorrencia> ocorrencias = db.obterTodasOcorrencias();
        adapter = new OcorrenciaAdapter(this, ocorrencias, this);
        recyclerView.setAdapter(adapter);
    }

    private void carregarClima() {
        new Thread(() -> {
            ClimaInfo clima = ClimaAPI.obterClimaBoaVista();
            new Handler(Looper.getMainLooper()).post(() -> {
                if (clima != null) {
                    String textoClima = String.format("🌡️ %s: %.1f°C | %s",
                            clima.getCidade(),
                            clima.getTemperatura(),
                            clima.getDescricao());
                    tvClima.setText(textoClima);
                } else {
                    tvClima.setText("⚠️ Sem conexão");
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume(); // Necessário para o ciclo de vida do OSMDroid
        carregarOcorrencias();
        atualizarMarkersNoMapa();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause(); // Necessário para o ciclo de vida do OSMDroid
    }

    @Override
    public void onEditarClick(Ocorrencia ocorrencia) {
        Intent intent = new Intent(MainActivity.this, AdicionarOcorrenciaActivity.class);
        intent.putExtra("ocorrencia_id", ocorrencia.getId());
        startActivity(intent);
    }

    @Override
    public void onDeletarClick(int id) {
        db.deletarOcorrencia(id);
        carregarOcorrencias();
        atualizarMarkersNoMapa();
    }

    @Override
    public void onVerClick(Ocorrencia ocorrencia) {
        Intent intent = new Intent(MainActivity.this, VerOcorrenciaActivity.class);
        intent.putExtra("ocorrencia_id", ocorrencia.getId());
        startActivity(intent);
        
        // Centraliza no mapa ao clicar em "Ver" nos detalhes
        try {
            String[] parts = ocorrencia.getLocalizacao().split(",");
            GeoPoint point = new GeoPoint(Double.parseDouble(parts[0].trim()), Double.parseDouble(parts[1].trim()));
            map.getController().animateTo(point);
            map.getController().setZoom(20.0);
        } catch (Exception ignored) {}
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationOverlay.enableMyLocation();
            }
        }
    }
}
