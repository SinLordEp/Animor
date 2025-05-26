package com.example.animor.Utils;

import android.util.Log;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import com.example.animor.*;
public class ApiRequests {
    private static final String TAG = "ApiRequests";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;

    public ApiRequests() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Envía el AppCheck Token y el Firebase Installation ID (FID) a tu backend
     * para validar la autenticidad del dispositivo.
     */
    public String sendFidDeviceToServer(String appCheckToken, String fid) {
        String url = "https://www.animor.es/auth/device-token";
        RequestBody formBody = new FormBody.Builder()
                .add("deviceFid", fid)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Firebase-AppCheck", appCheckToken)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String cuerpoRespuesta = response.body().string();
                Log.d(TAG, "Respuesta del servidor: " + cuerpoRespuesta);
                return cuerpoRespuesta;
            } else {
                Log.e(TAG, "Error en la solicitud: " + response.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en la solicitud: ", e);
        }
        return null;
    }

    /**
     * Envía el token de dispositivo al backend para autenticar al usuario en tu sistema.
     */
    public void sendUserToServer(String firebaseIdToken) {
        String url = "https://www.animor.es/auth/firebase-login";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", firebaseIdToken)
                .post(new FormBody.Builder().build()) // POST vacío
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String respuesta = response.body().string();
                Log.d(TAG, "Respuesta del servidor: " + respuesta);
            } else {
                Log.e(TAG, "Error en la solicitud: " + response.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al enviar usuario: ", e);
        }
    }
}