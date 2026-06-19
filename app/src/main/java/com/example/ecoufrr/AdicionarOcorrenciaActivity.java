package com.example.ecoufrr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdicionarOcorrenciaActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText etTitulo, etDescricao, etLocalizacao;
    private ImageView ivFoto;
    private Button btnTirarFoto, btnSalvar, btnCancelar;
    private Spinner spinnerStatus;
    private OcorrenciaDatabase db;
    private String caminhoFoto;
    private int ocorrenciaId = -1;
    private Ocorrencia ocorrenciaEditando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar);

        etTitulo = findViewById(R.id.etTitulo);
        etDescricao = findViewById(R.id.etDescricao);
        etLocalizacao = findViewById(R.id.etLocalizacao);
        ivFoto = findViewById(R.id.ivFoto);
        btnTirarFoto = findViewById(R.id.btnTirarFoto);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancelar = findViewById(R.id.btnCancelar);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        db = new OcorrenciaDatabase(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_opcoes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        ocorrenciaId = getIntent().getIntExtra("ocorrencia_id", -1);
        if (ocorrenciaId != -1) {
            ocorrenciaEditando = db.obterOcorrencia(ocorrenciaId);
            if (ocorrenciaEditando != null) {
                etTitulo.setText(ocorrenciaEditando.getTitulo());
                etDescricao.setText(ocorrenciaEditando.getDescricao());
                etLocalizacao.setText(ocorrenciaEditando.getLocalizacao());
                caminhoFoto = ocorrenciaEditando.getCaminhoFoto();
                if (caminhoFoto != null && !caminhoFoto.isEmpty()) {
                    ivFoto.setImageURI(Uri.parse("file://" + caminhoFoto));
                }
            }
        }

        btnTirarFoto.setOnClickListener(v -> abrirCamera());
        btnSalvar.setOnClickListener(v -> salvarOcorrencia());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void abrirCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Câmera não disponível", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    ivFoto.setImageBitmap(imageBitmap);
                    salvarFotoNoArmazenamento(imageBitmap);
                    Toast.makeText(this, "Foto capturada com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void salvarFotoNoArmazenamento(Bitmap bitmap) {
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (storageDir != null && !storageDir.exists()) {
                storageDir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File imageFile = new File(storageDir, "IMG_" + timestamp + ".jpg");

            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            caminhoFoto = imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao salvar foto", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarOcorrencia() {
        String titulo = etTitulo.getText().toString().trim();
        String descricao = etDescricao.getText().toString().trim();
        String localizacao = etLocalizacao.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

        if (titulo.isEmpty() || descricao.isEmpty() || localizacao.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (caminhoFoto == null) {
            caminhoFoto = "";
        }

        String dataCriacao = new SimpleDateFormat("dd/MM/yyyy HH:mm",
                Locale.getDefault()).format(new Date());

        if (ocorrenciaId != -1) {
            ocorrenciaEditando.setTitulo(titulo);
            ocorrenciaEditando.setDescricao(descricao);
            ocorrenciaEditando.setLocalizacao(localizacao);
            ocorrenciaEditando.setStatus(status);
            db.atualizarOcorrencia(ocorrenciaEditando);
        } else {
            Ocorrencia ocorrencia = new Ocorrencia(titulo, descricao, localizacao,
                    caminhoFoto, dataCriacao, status);
            db.adicionarOcorrencia(ocorrencia);
        }

        Toast.makeText(this, "Ocorrência salva!", Toast.LENGTH_SHORT).show();
        finish();
    }
}