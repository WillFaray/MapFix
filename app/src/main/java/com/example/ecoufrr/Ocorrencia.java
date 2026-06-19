package com.example.ecoufrr;

public class Ocorrencia {
    private int id;
    private String titulo;
    private String descricao;
    private String localizacao;
    private String caminhoFoto;
    private String dataCriacao;
    private String status;

    public Ocorrencia(String titulo, String descricao, String localizacao,
                      String caminhoFoto, String dataCriacao, String status) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.caminhoFoto = caminhoFoto;
        this.dataCriacao = dataCriacao;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getLocalizacao() { return localizacao; }
    public String getCaminhoFoto() { return caminhoFoto; }
    public String getDataCriacao() { return dataCriacao; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public void setCaminhoFoto(String caminhoFoto) { this.caminhoFoto = caminhoFoto; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }
    public void setStatus(String status) { this.status = status; }
}