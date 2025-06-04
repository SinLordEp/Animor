package com.example.animor.Utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.StartupResource;
import com.example.animor.Model.dto.AnimalDTO;
import com.example.animor.Model.dto.PhotoDTO;
import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.dto.TagDTO;
import com.example.animor.Model.dto.UserDTO;
import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.Photo;
import com.example.animor.Model.request.AnimalRequest;
import com.example.animor.Model.request.PhotoRequest;
import com.example.animor.UI.LoginActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiRequests {
    private static final String TAG = "ApiRequests";
    private static final MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;
    static String deviceToken;
    static String deviceFid;
    static String userToken="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzQ4ODgyNDI5LCJleHAiOjE3NDg5Njg4Mjl9.qV0Ow-dnHc3Nn249qQMAaYRyTThu7v8AlV_9db7CsGs";

    public ApiRequests() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        deviceToken = PreferenceUtils.getDeviceToken();
        System.out.println("DEVICE TOKEN DE SHAREDPREFERENCES:"+ deviceToken);
    }

    private static String getResponseBody(Response response){
        if(response.body() == null){
            throw new RuntimeException("Response body is empty");
        }
        try {
            return response.body().string();
        }catch (IOException e) {
            throw new RuntimeException("Response body cannot be read as String");
        }
    }
    @SuppressWarnings("unchecked")
    private static <T> T getJsonObjectFromResponseBody(Response response, String param, Class<T> objectClass) {
        String body = getResponseBody(response);
        try {
            JSONObject jsonResponse = new JSONObject(body);
            if(objectClass == JSONObject.class){
                return (T) jsonResponse.getJSONObject(param);
            }else if(objectClass == String.class){
                return (T) jsonResponse.getString(param);
            }
            throw new IllegalArgumentException("Not supported class while converting json response body");
        } catch (JSONException e) {
            throw new RuntimeException("Json format is invalid");
        }
    }
    private static JSONObject getDataFromResponseBody(Response response){
        return getJsonObjectFromResponseBody(response, "data", JSONObject.class);
    }
    private static String getStatusFromResponseBody(Response response){
        return getJsonObjectFromResponseBody(response, "status", String.class);
    }
    public StartupResource sendFidDeviceToServer(String appCheckToken, String deviceFid) {
        String url = "https://www.animor.es/auth/device-token";

        ApiRequests.deviceFid = deviceFid;
        RequestBody formBody = new FormBody.Builder()
                .build();
        Log.d("Appchecktoken para postman", appCheckToken);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Firebase-AppCheck", appCheckToken)
                .addHeader("X-Device-Fid", deviceFid)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject dataObject = getDataFromResponseBody(response);
                // Procesar tags
                JSONArray tagsArray = dataObject.getJSONArray("tagDTOList");
                List<TagDTO> tagList = JacksonUtils.readEntities(tagsArray.toString(), new TypeReference<>() {});

                // Procesar species
                JSONArray speciesArray = dataObject.getJSONArray("speciesDTOList");
                List<SpeciesDTO> speciesDTOList = JacksonUtils.readEntities(speciesArray.toString(), new TypeReference<>() {});

                // Preparar respuesta
                return new StartupResource(speciesDTOList, tagList, deviceToken);
            } else {
                Log.e(TAG, "Respuesta no exitosa recibiendo device-token, tags y species: " + getStatusFromResponseBody(response)
                        + " | Respuesta: " + getDataFromResponseBody(response));
            }
        } catch (IOException e) {
            Log.e(TAG, "Error de tipo in/out: ", e);
            System.out.println(e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado en la ejecución: ", e);
        }
        return null;
    }
    /**
     * Envía el token de Firebase al backend para autenticar al usuario.
     */
    public UserDTO sendUserToServer(String firebaseIdToken) {
        String url = "https://www.animor.es/auth/firebase-login";

        // Muestra el token por si quieres copiarlo para pruebas manuales (curl o Postman)
        Log.d(TAG, "Token que se enviará al servidor (FId): " + firebaseIdToken);
        Log.d(TAG, "Token que se enviará al servidor (deviceToken): " + deviceToken);


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
            Log.d(TAG, "Respuesta del servidor a petición de user data: " + getStatusFromResponseBody(response));
            if (response.isSuccessful()) {
                JSONObject data = getDataFromResponseBody(response);
                UserDTO user = JacksonUtils.readEntity(data.toString(), new TypeReference<>(){});
                if(user == null){
                    throw new RuntimeException("User is null");
                }
                Log.d("USER TOKEN ES ESTO", user.getUserToken());
                return user;
            }
                //{"status":2002,"data":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ4MzcyOTQ0LCJleHAiOjE3NDg0NTkzNDR9.Kdqk_L15TH2PqbLCi0qOoBh__e3UAei0cVfoPfGCMvg","userName":"Zelawola","email":"mixolida36@gmail.com","photoUrl":"https://lh3.googleusercontent.com/a/ACg8ocK5rMgBRRnY4JxR9m0fOdqAdHWzJjr31gPgJmJvO7juru0c_HTE=s96-c","phone":null}}
        } catch (Exception e) {
            Log.e(TAG, "Error al enviar usuario: ", e);
        }
        return null;
    }
    public void deleteAccount(Activity activity) {
        String url = "https://www.animor.es/user/delete-account";

        Log.d(TAG, "Tokens que se enviarán al servidor: \n Device-token:" + deviceToken + "\n User token: "+ userToken);

        userToken = PreferenceUtils.getUser().getUserToken();
        if (userToken == null || userToken.trim().isEmpty()){
            Log.e(TAG, "tokenId y/o userToken es nulo o vacío");
            return;
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .addHeader("X-User-Token", userToken)
                .delete()
                .build();
        Log.d("PETICIÓN ENVIADA", request.toString());

        MyApplication.executor.execute(() ->{
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Respuesta del servidor: " + getStatusFromResponseBody(response));
                    activity.runOnUiThread(() -> {
                        // Limpiar datos locales
                        PreferenceUtils.removeUser();

                        // Redirigir a LoginActivity
                        Toast.makeText(activity, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    });
                } else {
                    Log.e(TAG, "Error en la solicitud de borrar cuenta: " + getStatusFromResponseBody(response)
                            + " | Respuesta: " + getDataFromResponseBody(response));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al borrar cuenta: ", e);
            }
        });
    }
    public Long addAnimalIntoDatabase(AnimalRequest animal) {
        String url = "https://www.animor.es/animal/add-animal";
        RequestBody body = null;

        // 1. Crear el objeto JSON anidado
        //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Opcional: no incluir campos null
        Log.d("apirequest", "animal que se va a enviar:"+ animal.getAnimalName()+","+animal.getAnimalDescription()+","+animal.getSex());
        String json = JacksonUtils.entityToJson(animal);
        // 3. Crear el RequestBody en formato JSON
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .addHeader("X-User-Token", userToken)
                .post(body)
                .build();
        Log.d(TAG, "PETICIÓN ENVIADA: " + request);

        try (Response response = client.newCall(request).execute()) {
            String responseBody = getResponseBody(response);
            if (response.isSuccessful()) {
                Log.d(TAG, "Respuesta del servidor al enviar animal: " + responseBody);
                JSONObject jsonResponse = new JSONObject(responseBody);
                long idAnimal = jsonResponse.getLong("data");
                Log.d(TAG, "ID del animal creado: " + idAnimal);
                return idAnimal;
            } else {
                Log.e(TAG, "Error guardando animal en el servidor: " + getStatusFromResponseBody(response)
                        + " | Respuesta: " + getDataFromResponseBody(response));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar animal: ", e);
        }
        return null;

    }

    public void addPhotoIntoDatabase(Long receivedAnimalId, PhotoRequest photoRequest) {
        HttpUrl url = HttpUrl.parse("https://www.animor.es/animalPhoto/add-photo");
        if (url == null) {
            throw new IllegalArgumentException("Invalid URL");
        }
        url.newBuilder()
                .addQueryParameter("animalId", String.valueOf(receivedAnimalId))
                .build();
        try {
            String json = JacksonUtils.entityToJson(photoRequest);
            Log.d("REQUEST_JSON", json);
            RequestBody requestBody = RequestBody.create(json, mediaType);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("X-Device-Token", deviceToken)
                    .post(requestBody)  // POST con el token como parámetro
                    .build();
            try (Response response = client.newCall(request).execute()) {
                Log.d(TAG, "Respuesta del servidor: " + getStatusFromResponseBody(response));
            } catch (JsonProcessingException e) {
                System.out.println("Error mapeando animalPhoto");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al guardar foto: ", e);
        }

        //return null;
    }

    public List<Animal> getMyAnimalsFromServer() {
        String url = "https://www.animor.es/animal/my-animals";
        System.out.println("DEVICE TOKEN NULO: "+ deviceToken);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .addHeader("X-User-Token", userToken)
                .get()
                .build();
        List<AnimalDTO> animalDTOList;
        List<Animal> animalList = new ArrayList<>();
        try (Response response = client.newCall(request).execute()) {
            Log.d(TAG, "Respuesta del servidor a la petición de animales: " + getResponseBody(response));
            if (response.isSuccessful()) {
                JSONObject jsonObject = getDataFromResponseBody(response);
                JSONArray jsonArray = jsonObject.getJSONArray("animalDTOList");
                animalDTOList = JacksonUtils.readEntities(jsonArray.toString(), new TypeReference<>() {});
                animalList = Animal.fromDTOList(animalDTOList);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error recibiendo tags: ", e);
        }
        return animalList;
    }
    public void deleteAnimal(long animalId) {
        HttpUrl url = HttpUrl.parse("https://www.animor.es/animal/delete-animal");
        if(url == null){
            throw new IllegalArgumentException("URL is not valid");
        }
        url.newBuilder()
                .addQueryParameter("animalID", String.valueOf(animalId))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .delete()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String status = getStatusFromResponseBody(response);
                if("ANIMAL_DELETE_SUCCESS".equals(status)){
                    Log.d("ApiRequest - Delete animal", "Borrado exitoso");
                }
            } else {
                Log.e(TAG, "Error assert recibiendo tags: " + getStatusFromResponseBody(response)
                        + " | Respuesta: " + getResponseBody(response));
                //{"status":2002,"data":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ4MzcyOTQ0LCJleHAiOjE3NDg0NTkzNDR9.Kdqk_L15TH2PqbLCi0qOoBh__e3UAei0cVfoPfGCMvg","userName":"Zelawola","email":"mixolida36@gmail.com","photoUrl":"https://lh3.googleusercontent.com/a/ACg8ocK5rMgBRRnY4JxR9m0fOdqAdHWzJjr31gPgJmJvO7juru0c_HTE=s96-c","phone":null}}
            }
        } catch (IOException e) {
            System.out.println("Error de tipo in/out: "+ e.getMessage());
        }
    }

    public void addPhotoIntoDatabase(Photo animalphoto) {
        String url = "https://www.animor.es/animalPhoto/add-photo";
        RequestBody body;

        // 1. Crear el objeto JSON anidado
        PhotoDTO photoDTO = PhotoDTO.fromEntity(animalphoto);
        Log.d("apirequest", "animal que se va a enviar:"+ animalphoto.toString());
        String json = JacksonUtils.entityToJson(photoDTO);
        Log.d("REQUEST_JSON", json);
        // 3. Crear el RequestBody en formato JSON
        body = RequestBody.create(json, mediaType);
        Log.d("api add animal", body.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .addHeader("X-User-Token", userToken)
                .post(body)
                .build();
        Log.d(TAG, "PETICIÓN ENVIADA: " + request);

        try (Response response = client.newCall(request).execute()) {
            String status = getStatusFromResponseBody(response);
            Log.d(TAG, "Respuesta del servidor al enviar animal: " + status);
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar animal: ", e);
        }
    }
}