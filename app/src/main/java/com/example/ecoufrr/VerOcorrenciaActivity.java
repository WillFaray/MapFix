package com.example.ecoufrr;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class VerOcorrenciaActivity extends AppCompatActivity {

    private TextView tvVerTitulo, tvVerLocalizacao, tvVerDescricao, tvVerStatus, tvVerData;
    private ImageView ivVerFoto;
    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_ocorrencia);

        tvVerTitulo = findViewById(R.id.tvVerTitulo);
        tvVerLocalizacao = findViewById(R.id.tvVerLocalizacao);
        tvVerDescricao = findViewById(R.id.tvVerDescricao);
        tvVerStatus = findViewById(R.id.tvVerStatus);
        tvVerData = findViewById(R.id.tvVerData);
        ivVerFoto = findViewById(R.id.ivVerFoto);
        btnVoltar = findViewById(R.id.btnVoltar);

        // Receber os dados da ocorrência
        int ocorrenciaId = getIntent().getIntExtra("ocorrencia_id", -1);

        if (ocorrenciaId != -1) {
            OcorrenciaDatabase db = new OcorrenciaDatabase(this);
            Ocorrencia ocorrencia = db.obterOcorrencia(ocorrenciaId);

            if (ocorrencia != null) {
                tvVerTitulo.setText(ocorrencia.getTitulo());
                tvVerLocalizacao.setText(ocorrencia.getLocalizacao());
                tvVerDescricao.setText(ocorrencia.getDescricao());
                tvVerStatus.setText(ocorrencia.getStatus());
                tvVerData.setText(ocorrencia.getDataCriacao());

                // Carregar a foto
                if (ocorrencia.getCaminhoFoto() != null && !ocorrencia.getCaminhoFoto().isEmpty()) {
                    ivVerFoto.setImageURI(Uri.parse("file://" + ocorrencia.getCaminhoFoto()));
                }
            }
        }

        btnVoltar.setOnClickListener(v -> finish());
    }
}