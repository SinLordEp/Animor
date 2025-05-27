package com.example.animor.Utils;

import android.content.Context;
import android.util.Log;

import com.example.animor.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import okhttp3.*;

public class ApiRequests {
    private static final String TAG = "ApiRequests";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;
    static String idToken;

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
                // Parsear JSON y extraer el campo "data"
                JSONObject json = new JSONObject(cuerpoRespuesta);
                if (json.has("data")) {
                    idToken = json.getString("data");
                    return idToken;
                } else {
                    Log.e(TAG, "No se encontró el campo 'data' en la respuesta");
                }
            } else {
                assert response.body() != null;
                Log.e(TAG, "Error en la solicitud de token para peticiones: " + response.code()
                        + " | Respuesta: " + response.body().string());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en la solicitud: ", e);
        }
        return null;
    }

    /**
     * Envía el token de Firebase al backend para autenticar al usuario.
     */
    public void sendUserToServer(String firebaseIdToken) {
        String url = "https://www.animor.es/auth/firebase-login";

        // Muestra el token por si quieres copiarlo para pruebas manuales (curl o Postman)
        Log.d(TAG, "Token que se enviará al servidor: " + firebaseIdToken);

        // Construye el cuerpo con el parámetro 'firebaseToken' que espera el backend
        RequestBody formBody = new FormBody.Builder()
                .add("firebaseToken", firebaseIdToken)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", idToken)
                .post(formBody)  // POST con el token como parámetro
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String respuesta = response.body().string();
                User user = new User();
                Log.d(TAG, "Respuesta del servidor: " + respuesta);
            } else {
                assert response.body() != null;
                Log.e(TAG, "Error en la solicitud de datos de usuario: " + response.code()
                        + " | Respuesta: " + response.body().string());
                //{"status":2002,"data":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ4MzcyOTQ0LCJleHAiOjE3NDg0NTkzNDR9.Kdqk_L15TH2PqbLCi0qOoBh__e3UAei0cVfoPfGCMvg","userName":"Zelawola","email":"mixolida36@gmail.com","photoUrl":"https://lh3.googleusercontent.com/a/ACg8ocK5rMgBRRnY4JxR9m0fOdqAdHWzJjr31gPgJmJvO7juru0c_HTE=s96-c","phone":null}}
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al enviar usuario: ", e);
        }

    }
}