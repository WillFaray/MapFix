package com.example.ecoufrr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        OcorrenciaAdapter.OnOcorrenciaActionListener {

    private RecyclerView recyclerView;
    private OcorrenciaAdapter adapter;
    private OcorrenciaDatabase db;
    private FloatingActionButton fabAdicionar;
    private TextView tvClima;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes
        recyclerView = findViewById(R.id.recyclerViewOcorrencias);
        fabAdicionar = findViewById(R.id.fabAdicionar);
        tvClima = findViewById(R.id.tvClima);
        btnLogout = findViewById(R.id.btn_logout);
        db = new OcorrenciaDatabase(this);

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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Buscar clima na thread de background
        carregarClima();
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
                    String textoClima = String.format("🌡️ %s: %.1f°C\n%s\n💧 Umidade: %d%%\n💨 Vento: %.1f km/h",
                            clima.getCidade(),
                            clima.getTemperatura(),
                            clima.getDescricao(),
                            clima.getUmidade(),
                            clima.getVelocidadeVento());
                    tvClima.setText(textoClima);
                } else {
                    tvClima.setText("⚠️ Sem conexão com internet");
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarOcorrencias();
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
    }


    @Override
    public void onVerClick(Ocorrencia ocorrencia) {
        Intent intent = new Intent(MainActivity.this, VerOcorrenciaActivity.class);
        intent.putExtra("ocorrencia_id", ocorrencia.getId());
        startActivity(intent);
    }
}