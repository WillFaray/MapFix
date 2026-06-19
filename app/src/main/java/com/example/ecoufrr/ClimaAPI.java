package com.example.ecoufrr;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

public class ClimaAPI {
    // Coordenadas de Boa Vista, RR: -2.8235, -60.6758
    private static final String API_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=-2.8235&longitude=-60.6758&current=temperature_2m,weather_code,relative_humidity_2m,weather_code,wind_speed_10m&temperature_unit=celsius&weather_code=wmo";

    public static ClimaInfo obterClimaBoaVista() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(API_URL)
                    .build();

            Response response = client.newCall(request).execute();

            Log.d("ClimaAPI", "Código resposta: " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                String jsonData = response.body().string();
                Log.d("ClimaAPI", "Resposta: " + jsonData);

                JsonObject json = new Gson().fromJson(jsonData, JsonObject.class);

                if (json != null && json.has("current")) {
                    JsonObject current = json.getAsJsonObject("current");

                    double temperatura = current.get("temperature_2m").getAsDouble();
                    int umidade = current.get("relative_humidity_2m").getAsInt();
                    double vento = current.get("wind_speed_10m").getAsDouble();
                    int codigoTempo = current.get("weather_code").getAsInt();

                    String descricao = traduzirCodigoTempo(codigoTempo);

                    Log.d("ClimaAPI", "✅ Clima carregado: Boa Vista " + temperatura + "°C");

                    return new ClimaInfo("Boa Vista, RR", temperatura, descricao, umidade, vento);
                } else {
                    Log.e("ClimaAPI", "❌ JSON não contém 'current'");
                }
            } else {
                Log.e("ClimaAPI", "❌ Resposta falhou: " + response.code());
            }
        } catch (Exception e) {
            Log.e("ClimaAPI", "❌ Erro: " + e.getMessage(), e);
            e.printStackTrace();
        }

        Log.w("ClimaAPI", "⚠️ Retornando null");
        return null;
    }

    private static String traduzirCodigoTempo(int codigo) {
        switch (codigo) {
            case 0: return "Céu limpo";
            case 1:
            case 2: return "Parcialmente nublado";
            case 3: return "Nublado";
            case 45:
            case 48: return "Névoa";
            case 51:
            case 53:
            case 55: return "Chuva leve";
            case 61:
            case 63:
            case 65: return "Chuva";
            case 71:
            case 73:
            case 75: return "Neve";
            case 77: return "Neve granulada";
            case 80:
            case 81:
            case 82: return "Chuva forte";
            case 85:
            case 86: return "Neve e chuva";
            case 95:
            case 96:
            case 99: return "Tempestade";
            default: return "Desconhecido";
        }
    }
}