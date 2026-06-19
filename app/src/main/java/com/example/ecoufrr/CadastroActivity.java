package com.example.ecoufrr;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {

    private EditText etMatricula, etSenha, etConfirmarSenha;
    private Button btnCadastrar;
    private TextView tvVoltarLogin;
    private OcorrenciaDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        db = new OcorrenciaDatabase(this);

        etMatricula = findViewById(R.id.et_cadastro_matricula);
        etSenha = findViewById(R.id.et_cadastro_senha);
        etConfirmarSenha = findViewById(R.id.et_confirmar_senha);
        btnCadastrar = findViewById(R.id.btn_cadastrar);
        tvVoltarLogin = findViewById(R.id.tv_voltar_login);

        btnCadastrar.setOnClickListener(v -> {
            String matricula = etMatricula.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();
            String confirmarSenha = etConfirmarSenha.getText().toString().trim();

            if (matricula.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!senha.equals(confirmarSenha)) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.existeMatricula(matricula)) {
                Toast.makeText(this, "Esta matrícula já está cadastrada", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = db.adicionarUsuario(matricula, senha);
            if (id > 0) {
                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erro ao realizar cadastro", Toast.LENGTH_SHORT).show();
            }
        });

        tvVoltarLogin.setOnClickListener(v -> finish());
    }
}