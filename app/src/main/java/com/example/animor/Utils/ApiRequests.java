package com.example.animor.Utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiRequests {
    //http://79.116.77.132:8080
    private static final okhttp3.MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;

    public ApiRequests() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS) // Timeout para la conexión
                .readTimeout(15, TimeUnit.SECONDS)    // Timeout para la lectura
                .writeTimeout(15, TimeUnit.SECONDS)   // Timeout para escritura
                .build();
    }

    public String sendJsonDeviceToServer(String appCheckToken, String fid) {
        String url = "https://www.animor.es/auth/device-token";
        RequestBody formBody = new FormBody.Builder()
                .add("deviceFid", fid)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Firebase-AppCheck", appCheckToken)
                .post(formBody)
                .build();
        // Ejecuta la solicitud y recibe la respuesta
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String cuerpoRespuesta = response.body().string();
                Log.d("ApiRequest",cuerpoRespuesta);
                System.out.println("Respuesta del servidor: " + cuerpoRespuesta);
                return cuerpoRespuesta;
            } else {
                System.out.println("Error en la solicitud: " + response.code());
            }
        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }
        return null;
    }
    public void sendUserToServer(String cuerpoRespuesta) {
        String url = "https://www.animor.es/auth/firebase-login";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", cuerpoRespuesta) // Opcional: si necesitas App Check
                .post(new FormBody.Builder().build()) // Cuerpo vacío (POST requiere body, aunque no lo uses)
                .build();

        // Ejecuta la solicitud y recibe la respuesta
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String respuesta = response.body().string();
                Log.d("ApiRequest",cuerpoRespuesta);
                System.out.println("Respuesta del servidor: " + cuerpoRespuesta);
            } else {
                System.out.println("Error en la solicitud: " + response.code());
            }
        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }
    }
}