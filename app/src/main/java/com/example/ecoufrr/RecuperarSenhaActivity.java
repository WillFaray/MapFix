package com.example.ecoufrr;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RecuperarSenhaActivity extends AppCompatActivity {

    private EditText etMatricula, etNovaSenha, etConfirmarNovaSenha;
    private Button btnResetarSenha;
    private TextView tvVoltarLogin;
    private OcorrenciaDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        db = new OcorrenciaDatabase(this);

        etMatricula = findViewById(R.id.et_recuperar_matricula);
        etNovaSenha = findViewById(R.id.et_nova_senha);
        etConfirmarNovaSenha = findViewById(R.id.et_confirmar_nova_senha);
        btnResetarSenha = findViewById(R.id.btn_resetar_senha);
        tvVoltarLogin = findViewById(R.id.tv_voltar_login_rec);

        btnResetarSenha.setOnClickListener(v -> {
            String matricula = etMatricula.getText().toString().trim();
            String novaSenha = etNovaSenha.getText().toString().trim();
            String confirmarNovaSenha = etConfirmarNovaSenha.getText().toString().trim();

            if (matricula.isEmpty() || novaSenha.isEmpty() || confirmarNovaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!novaSenha.equals(confirmarNovaSenha)) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!db.existeMatricula(matricula)) {
                Toast.makeText(this, "Matrícula não encontrada", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.atualizarSenha(matricula, novaSenha)) {
                Toast.makeText(this, "Senha redefinida com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erro ao redefinir senha", Toast.LENGTH_SHORT).show();
            }
        });

        tvVoltarLogin.setOnClickListener(v -> finish());
    }
}