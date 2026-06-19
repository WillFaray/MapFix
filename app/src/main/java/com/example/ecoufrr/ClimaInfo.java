package com.example.ecoufrr;

public class ClimaInfo {
    private String cidade;
    private double temperatura;
    private String descricao;
    private int umidade;
    private double velocidadeVento;

    public ClimaInfo(String cidade, double temperatura, String descricao,
                     int umidade, double velocidadeVento) {
        this.cidade = cidade;
        this.temperatura = temperatura;
        this.descricao = descricao;
        this.umidade = umidade;
        this.velocidadeVento = velocidadeVento;
    }

    public String getCidade() { return cidade; }
    public double getTemperatura() { return temperatura; }
    public String getDescricao() { return descricao; }
    public int getUmidade() { return umidade; }
    public double getVelocidadeVento() { return velocidadeVento; }
}