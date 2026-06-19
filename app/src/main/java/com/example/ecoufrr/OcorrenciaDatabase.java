package com.example.ecoufrr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class OcorrenciaDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ecoufrr.db";
    private static final int DATABASE_VERSION = 2; // Incremented version
    private static final String TABLE_OCORRENCIAS = "ocorrencias";
    private static final String TABLE_USUARIOS = "usuarios";

    // Ocorrencias Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITULO = "titulo";
    private static final String COLUMN_DESCRICAO = "descricao";
    private static final String COLUMN_LOCALIZACAO = "localizacao";
    private static final String COLUMN_CAMINHO_FOTO = "caminho_foto";
    private static final String COLUMN_DATA = "data_criacao";
    private static final String COLUMN_STATUS = "status";

    // Usuarios Columns
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_MATRICULA = "matricula";
    private static final String COLUMN_SENHA = "senha";

    public OcorrenciaDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_OCORRENCIAS_TABLE = "CREATE TABLE " + TABLE_OCORRENCIAS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITULO + " TEXT, " +
                COLUMN_DESCRICAO + " TEXT, " +
                COLUMN_LOCALIZACAO + " TEXT, " +
                COLUMN_CAMINHO_FOTO + " TEXT, " +
                COLUMN_DATA + " TEXT, " +
                COLUMN_STATUS + " TEXT" +
                ")";
        db.execSQL(CREATE_OCORRENCIAS_TABLE);

        String CREATE_USUARIOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS + "(" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MATRICULA + " TEXT UNIQUE, " +
                COLUMN_SENHA + " TEXT" +
                ")";
        db.execSQL(CREATE_USUARIOS_TABLE);

        // Inserir usuário de teste
        ContentValues values = new ContentValues();
        values.put(COLUMN_MATRICULA, "2021005577");
        values.put(COLUMN_SENHA, "senha123");
        db.insert(TABLE_USUARIOS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String CREATE_USUARIOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS + "(" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MATRICULA + " TEXT UNIQUE, " +
                    COLUMN_SENHA + " TEXT" +
                    ")";
            db.execSQL(CREATE_USUARIOS_TABLE);

            // Inserir usuário de teste na atualização também
            ContentValues values = new ContentValues();
            values.put(COLUMN_MATRICULA, "2021005577");
            values.put(COLUMN_SENHA, "senha123");
            db.insert(TABLE_USUARIOS, null, values);
        }
    }

    // --- Métodos de Usuário ---

    public boolean validarUsuario(String matricula, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS, new String[]{COLUMN_USER_ID},
                COLUMN_MATRICULA + "=? AND " + COLUMN_SENHA + "=?",
                new String[]{matricula, senha}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public long adicionarUsuario(String matricula, String senha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MATRICULA, matricula);
        values.put(COLUMN_SENHA, senha);
        return db.insert(TABLE_USUARIOS, null, values);
    }

    public boolean atualizarSenha(String matricula, String novaSenha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENHA, novaSenha);
        int rows = db.update(TABLE_USUARIOS, values, COLUMN_MATRICULA + " = ?", new String[]{matricula});
        return rows > 0;
    }

    public boolean existeMatricula(String matricula) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS, new String[]{COLUMN_USER_ID},
                COLUMN_MATRICULA + "=?", new String[]{matricula}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // --- Métodos de Ocorrência (Mantidos) ---

    public long adicionarOcorrencia(Ocorrencia ocorrencia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, ocorrencia.getTitulo());
        values.put(COLUMN_DESCRICAO, ocorrencia.getDescricao());
        values.put(COLUMN_LOCALIZACAO, ocorrencia.getLocalizacao());
        values.put(COLUMN_CAMINHO_FOTO, ocorrencia.getCaminhoFoto());
        values.put(COLUMN_DATA, ocorrencia.getDataCriacao());
        values.put(COLUMN_STATUS, ocorrencia.getStatus());
        return db.insert(TABLE_OCORRENCIAS, null, values);
    }

    public List<Ocorrencia> obterTodasOcorrencias() {
        List<Ocorrencia> ocorrencias = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_OCORRENCIAS, null);

        if (cursor.moveToFirst()) {
            do {
                Ocorrencia ocorrencia = new Ocorrencia(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                );
                ocorrencia.setId(cursor.getInt(0));
                ocorrencias.add(ocorrencia);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ocorrencias;
    }

    public Ocorrencia obterOcorrencia(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_OCORRENCIAS + " WHERE id = " + id, null);
        Ocorrencia ocorrencia = null;

        if (cursor.moveToFirst()) {
            ocorrencia = new Ocorrencia(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
            ocorrencia.setId(cursor.getInt(0));
        }
        cursor.close();
        return ocorrencia;
    }

    public int atualizarOcorrencia(Ocorrencia ocorrencia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, ocorrencia.getTitulo());
        values.put(COLUMN_DESCRICAO, ocorrencia.getDescricao());
        values.put(COLUMN_LOCALIZACAO, ocorrencia.getLocalizacao());
        values.put(COLUMN_STATUS, ocorrencia.getStatus());
        return db.update(TABLE_OCORRENCIAS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(ocorrencia.getId())});
    }

    public void deletarOcorrencia(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OCORRENCIAS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }
}
