package com.example.ecoufrr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etMatricula, etSenha;
    private Button btnLogin;
    private TextView tvIrCadastro, tvEsqueciSenha;
    private OcorrenciaDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new OcorrenciaDatabase(this);

        etMatricula = findViewById(R.id.et_matricula);
        etSenha = findViewById(R.id.et_senha);
        btnLogin = findViewById(R.id.btn_login);
        tvIrCadastro = findViewById(R.id.tv_ir_cadastro);
        tvEsqueciSenha = findViewById(R.id.tv_esqueci_senha);

        btnLogin.setOnClickListener(v -> {
            String matricula = etMatricula.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();

            if (matricula.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.validarUsuario(matricula, senha)) {
                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Matrícula ou senha incorretos", Toast.LENGTH_SHORT).show();
            }
        });

        tvIrCadastro.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
        });

        tvEsqueciSenha.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RecuperarSenhaActivity.class));
        });
    }
}