package com.example.animor.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.animor.Model.Animal;
import com.example.animor.Model.User;
import com.example.animor.UI.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

public class ApiRequests {
    private static final String TAG = "ApiRequests";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;
    static String deviceToken;
    static String fidToken;
    static String userToken;

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
        fidToken = fid;
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
                Log.d("USER-TOKEN DEL SERVIDOR BUENO", cuerpoRespuesta.toString());
                if (json.has("data")) {
                    deviceToken = json.getString("data");
                    return deviceToken;
                } else {
                    Log.e(TAG, "No se encontró el campo 'data' en la respuesta");
                }
            } else {
                assert response.body() != null;
                Log.e(TAG, "Error en la solicitud de token para peticiones: " + response.code()
                        + " | Respuesta: " + response.body().string());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en la solicitud: es posible que el servidor no esté conectado", e);
        }
        return null;
    }

    /**
     * Envía el token de Firebase al backend para autenticar al usuario.
     */
    public User sendUserToServer(String firebaseIdToken) {
        String url = "https://www.animor.es/auth/firebase-login";

        // Muestra el token por si quieres copiarlo para pruebas manuales (curl o Postman)
        Log.d(TAG, "Token que se enviará al servidor (userToken): " + firebaseIdToken);

        // Construye el cuerpo con el parámetro 'firebaseToken' que espera el backend
        RequestBody formBody = new FormBody.Builder()
                .add("firebaseToken", firebaseIdToken)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .post(formBody)  // POST con el token como parámetro
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String respuesta = response.body().string();
                Log.d(TAG, "Respuesta del servidor: " + respuesta);
                JSONObject jsonObject = new JSONObject(respuesta);
                JSONObject data = jsonObject.getJSONObject("data");
                userToken = data.getString("token");
                return new User(data.getString("token"),fidToken, data.getString("userName"),data.getString("email"));
            } else {
                assert response.body() != null;
                Log.e(TAG, "Error en la solicitud de datos de usuario: " + response.code()
                        + " | Respuesta: " + response.body().string());
                //{"status":2002,"data":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ4MzcyOTQ0LCJleHAiOjE3NDg0NTkzNDR9.Kdqk_L15TH2PqbLCi0qOoBh__e3UAei0cVfoPfGCMvg","userName":"Zelawola","email":"mixolida36@gmail.com","photoUrl":"https://lh3.googleusercontent.com/a/ACg8ocK5rMgBRRnY4JxR9m0fOdqAdHWzJjr31gPgJmJvO7juru0c_HTE=s96-c","phone":null}}
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al enviar usuario: ", e);
        }

        return null;
    }
    public void deleteAccount(Activity activity) {
        String url = "https://www.animor.es/user/delete-account";

        Log.d(TAG, "Tokens que se enviarán al servidor: \n Device-token:" + deviceToken + "\n User token: "+ userToken);

        if (userToken == null || userToken.trim().isEmpty()){
            Log.e(TAG, "tokenId es nulo o vacío");
            return;
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .addHeader("X-User-Token", userToken)
                .delete()
                .build();
        Log.d("PETICIÓN ENVIADA", request.toString());

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String respuesta = response.body().string();
                    Log.d(TAG, "Respuesta del servidor: " + respuesta);

                    activity.runOnUiThread(() -> {
                        // Limpiar datos locales
                        SharedPreferences prefs = activity.getSharedPreferences("userPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();

                        // Redirigir a LoginActivity
                        Toast.makeText(activity, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    });

                } else {
                    assert response.body() != null;
                    Log.e(TAG, "Error en la solicitud de borrar cuenta: " + response.code()
                            + " | Respuesta: " + response.body().string());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al borrar cuenta: ", e);
            }
        }).start();
    }
    public void addAnimalIntoDatabase(Animal animal) {

        String url = "https://www.animor.es/animal/add-animal";
        // 1. Crear el objeto JSON anidado
        JSONObject animalJson = new JSONObject();
        try {
            animalJson.put("animal_name", animal.getName());
            animalJson.put("species_id", animal.getSpeciesId());
            animalJson.put("birth_date", animal.getBirthDate());
            animalJson.put("is_birth_date_estimated", animal.getIsBirthDateEstimated());
            animalJson.put("sex", animal.getSex());
            animalJson.put("size", animal.getSize());
            animalJson.put("animal_description", animal.getAnimalDescription());
            animalJson.put("is_neutered", animal.getIsNeutered());
            animalJson.put("microchip_number", animal.getMicrochipNumber());
            animalJson.put("is_adopted", animal.getIsAdopted());
        } catch (JSONException e) {
            System.out.println("Error de tipo json: "+e.getMessage());
        }


        // 2. Envolver en un JSON superior con clave "animal"
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("animal", animalJson);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // 3. Crear el RequestBody en formato JSON
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(requestBodyJson.toString(), JSON);


        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .addHeader("X-User-Token", userToken)
                .post(body)
                .build();

        Log.d(TAG, "PETICIÓN ENVIADA: " + request.toString());

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String respuesta = response.body().string();
                Log.d(TAG, "Respuesta del servidor al enviar animal: " + respuesta);
                JSONObject jsonObject = new JSONObject(respuesta);
                //JSONObject data = jsonObject.getJSONObject("data");
            } else {
                assert response.body() != null;
                Log.e(TAG, "Error guardando animal en el servidor: " + response.code()
                        + " | Respuesta: " + response.body().string());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar animal: ", e);
        }

    }
    public List askForSpeciesToDatabase() {

        String url = "https://www.animor.es/animal/species/all";

        return java.util.Collections.emptyList();
    }
}