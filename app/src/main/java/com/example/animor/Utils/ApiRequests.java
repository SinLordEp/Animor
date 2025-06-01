package com.example.animor.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.animor.Model.Animal;
import com.example.animor.Model.AnimalPhoto;
import com.example.animor.Model.Tag;
import com.example.animor.Model.User;
import com.example.animor.UI.LoginActivity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
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
    Context context;

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
    public Long addAnimalIntoDatabase(Animal animal) {

        String url = "https://www.animor.es/animal/add-animal";
        RequestBody body = null;

        // 1. Crear el objeto JSON anidado
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Opcional: no incluir campos null

            Log.d("apirequest", "animal que se va a enviar:"+ animal.getAnimalName()+","+animal.getAnimalId()+","+animal.getAnimalDescription()+","+animal.getSex());
            String json = objectMapper.writeValueAsString(animal);
            Log.d("REQUEST_JSON", json);
            // 3. Crear el RequestBody en formato JSON
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            body = RequestBody.create(json, JSON);
        } catch (JsonProcessingException e) {
            System.out.println("Error procesando json: "+e.getMessage());
            return null;
        }
        Log.d("api add animal", body.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .addHeader("X-User-Token", userToken)
                .post(body)
                .build();

        Log.d(TAG, "PETICIÓN ENVIADA: " + request);

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String respuesta = response.body().string();
                Log.d(TAG, "Respuesta del servidor al enviar animal: " + respuesta);
                JSONObject jsonResponse = new JSONObject(respuesta);
                long idAnimal = jsonResponse.getLong("data");
                Log.d(TAG, "ID del animal creado: " + idAnimal);
                return idAnimal;

            } else {
                assert response.body() != null;
                Log.e(TAG, "Error guardando animal en el servidor: " + response.code()
                        + " | Respuesta: " + response.body().string());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar animal: ", e);
        }
        return null;

    }

    public void addPhotoIntoDatabase(Long receivedAnimalId, AnimalPhoto animalPhoto) {
        String url = "https://www.animor.es/animal-photo/add-photo";
        RequestBody formBody = new FormBody.Builder()
                .add("animalId", String.valueOf(receivedAnimalId))
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
                //return new User(data.getString("token"),fidToken, data.getString("userName"),data.getString("email"));
            } else {
                assert response.body() != null;
                Log.e(TAG, "Error guardando foto: " + response.code()
                        + " | Respuesta: " + response.body().string());
                //{"status":2002,"data":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ4MzcyOTQ0LCJleHAiOjE3NDg0NTkzNDR9.Kdqk_L15TH2PqbLCi0qOoBh__e3UAei0cVfoPfGCMvg","userName":"Zelawola","email":"mixolida36@gmail.com","photoUrl":"https://lh3.googleusercontent.com/a/ACg8ocK5rMgBRRnY4JxR9m0fOdqAdHWzJjr31gPgJmJvO7juru0c_HTE=s96-c","phone":null}}
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al enviar usuario: ", e);
        }

        //return null;
   }
    public ArrayList<Tag> askForTagsToDatabase() {
        String url = "https://www.animor.es/tag/all";
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .get()
                .build();
        ArrayList<Tag> tags = null;
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String respuesta = response.body().string();
                Log.d(TAG, "Respuesta del servidor: " + respuesta);
                JSONObject jsonResponse = new JSONObject(respuesta);
                JSONArray jsonArray = jsonResponse.getJSONArray("data");
                tags = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(i);
                    Tag tag = new Tag();
                    tag.setTagName(jsonobject.getString("tagName"));
                    tags.add(tag);
                }
                //return new User(data.getString("token"),fidToken, data.getString("userName"),data.getString("email"));
            } else {
                assert response.body() != null;
                Log.e(TAG, "Error assert recibiendo tags: " + response.code()
                        + " | Respuesta: " + response.body().string());
                //{"status":2002,"data":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ4MzcyOTQ0LCJleHAiOjE3NDg0NTkzNDR9.Kdqk_L15TH2PqbLCi0qOoBh__e3UAei0cVfoPfGCMvg","userName":"Zelawola","email":"mixolida36@gmail.com","photoUrl":"https://lh3.googleusercontent.com/a/ACg8ocK5rMgBRRnY4JxR9m0fOdqAdHWzJjr31gPgJmJvO7juru0c_HTE=s96-c","phone":null}}
            }
        } catch (Exception e) {
            Log.e(TAG, "Error recibiendo tags: ", e);
        }

        return tags;
    }

    public ArrayList<Tag> askForSpeciesToDatabase() {
        String url = "https://www.animor.es/tag/all";
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .get()
                .build();
        ArrayList<Tag> tags = null;
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String respuesta = response.body().string();
                Log.d(TAG, "Respuesta del servidor: " + respuesta);
                JSONObject jsonResponse = new JSONObject(respuesta);
                JSONArray jsonArray = jsonResponse.getJSONArray("data");
                tags = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(i);
                    Tag tag = new Tag();
                    tag.setTagName(jsonobject.getString("tagName"));
                    tags.add(tag);
                }
                //return new User(data.getString("token"),fidToken, data.getString("userName"),data.getString("email"));
            } else {
                assert response.body() != null;
                Log.e(TAG, "Error assert recibiendo tags: " + response.code()
                        + " | Respuesta: " + response.body().string());
                //{"status":2002,"data":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ4MzcyOTQ0LCJleHAiOjE3NDg0NTkzNDR9.Kdqk_L15TH2PqbLCi0qOoBh__e3UAei0cVfoPfGCMvg","userName":"Zelawola","email":"mixolida36@gmail.com","photoUrl":"https://lh3.googleusercontent.com/a/ACg8ocK5rMgBRRnY4JxR9m0fOdqAdHWzJjr31gPgJmJvO7juru0c_HTE=s96-c","phone":null}}
            }
        } catch (Exception e) {
            Log.e(TAG, "Error recibiendo tags: ", e);
        }

        return tags;
    }


}