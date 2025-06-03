package com.example.animor.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.Animal;
import com.example.animor.Model.AnimalPhoto;
import com.example.animor.Model.Sex;
import com.example.animor.Model.Species;
import com.example.animor.Model.Tag;
import com.example.animor.Model.User;
import com.example.animor.UI.LoginActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    static String userToken="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzQ4ODgyNDI5LCJleHAiOjE3NDg5Njg4Mjl9.qV0Ow-dnHc3Nn249qQMAaYRyTThu7v8AlV_9db7CsGs";

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
    public void getSharedDeviceId(){
        deviceToken = MyApplication.getAppContext()
                .getSharedPreferences(MyApplication.PREFS_NAME, Context.MODE_PRIVATE)
                .getString(MyApplication.KEY_DEVICE_TOKEN, null);
        deviceToken = MyApplication.getAppContext()
                .getSharedPreferences(MyApplication.PREFS_NAME, Context.MODE_PRIVATE)
                .getString(MyApplication.KEY_DEVICE_TOKEN, null);
        System.out.println("DEVICE TOKEN DE SHAREDPREFERENCES:"+ deviceToken);
    }
    public ApiResponse sendFidDeviceToServer(String appCheckToken, String fid) {
        getSharedDeviceId();
        String url = "https://www.animor.es/auth/device-token";
        fidToken = fid;
        RequestBody formBody = new FormBody.Builder()
                .add("deviceFid", fid)
                .build();

        Log.d("Appchecktoken para postman", appCheckToken);
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

                JSONObject jsonResponse = new JSONObject(cuerpoRespuesta);
                JSONObject dataObject = jsonResponse.getJSONObject("data");

                // Procesar tags
                JSONArray tagsArray = dataObject.getJSONArray("tagDTOList");
                ArrayList<Tag> tags = new ArrayList<>();
                for (int i = 0; i < tagsArray.length(); i++) {
                    JSONObject tagObject = tagsArray.getJSONObject(i);
                    Tag tag = new Tag();
                    tag.setTagId(tagObject.getInt("tagId"));
                    tag.setTagName(tagObject.getString("tagName"));
                    tags.add(tag);
                }

                // Procesar species
                JSONArray speciesArray = dataObject.getJSONArray("speciesDTOList");
                ArrayList<Species> speciesList = new ArrayList<>();
                for (int i = 0; i < speciesArray.length(); i++) {
                    JSONObject speciesObject = speciesArray.getJSONObject(i);
                    Species species = new Species();
                    species.setSpeciesId(speciesObject.getInt("speciesId"));
                    species.setSpeciesName(speciesObject.getString("name"));
                    speciesList.add(species);
                }

                // Preparar respuesta
                ApiResponse apiResponse = new ApiResponse(speciesList, tags, deviceToken);
                return apiResponse;
            } else {
                assert response.body() != null;
                Log.e(TAG, "Error en la solicitud: " + response.code()
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
    public User sendUserToServer(String firebaseIdToken) {
        getSharedDeviceId();
        String url = "https://www.animor.es/auth/firebase-login";

        // Muestra el token por si quieres copiarlo para pruebas manuales (curl o Postman)
        Log.d(TAG, "Token que se enviará al servidor (FId): " + firebaseIdToken);

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
                Log.d("USER TOKEN ES ESTO", userToken);
                User user = new User();
                user.setUserToken(userToken);
                String userName = data.getString("userName");
                String email = data.getString("email");
                String userPhoto = data.getString("photoUrl");
                user.setUserFid(fidToken);
                user.setDeviceToken(deviceToken);
                user.setUserPhoto(userPhoto);
                user.setEmail(email);
                user.setUserName(userName);
                return user;
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
        getSharedDeviceId();
        String url = "https://www.animor.es/user/delete-account";

        Log.d(TAG, "Tokens que se enviarán al servidor: \n Device-token:" + deviceToken + "\n User token: "+ userToken);

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
        getSharedDeviceId();
        String url = "https://www.animor.es/animal/add-animal";
        RequestBody body = null;

        // 1. Crear el objeto JSON anidado
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Opcional: no incluir campos null

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
        getSharedDeviceId();
        String url = "https://www.animor.es/animalPhoto/add-photo?animalId=" + receivedAnimalId;
        RequestBody body = null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String json = objectMapper.writeValueAsString(animalPhoto);
            Log.d("REQUEST_JSON", json);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("X-Device-Token", deviceToken)
                    .post(requestBody)  // POST con el token como parámetro
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String respuesta = response.body().string();
                    Log.d(TAG, "Respuesta del servidor: " + respuesta);
                    //JSONObject jsonObject = new JSONObject(respuesta);
                    //JSONObject data = jsonObject.getJSONObject("data");
                    //return new User(data.getString("token"),fidToken, data.getString("userName"),data.getString("email"));
                } else {
                    assert response.body() != null;
                    Log.e(TAG, "Error guardando foto: " + response.code()
                            + " | Respuesta: " + response.body().string());
                }
            } catch (JsonProcessingException e) {
                System.out.println("Error mapeando animalPhoto");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al guardar foto: ", e);
        }

        //return null;
   }

    public ArrayList<Animal> askForMyAnimalsToDatabase() {
        getSharedDeviceId();
        String url = "https://www.animor.es/animal/my-animals";
        System.out.println("DEVICE TOKEN NULO: "+ deviceToken);
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .addHeader("X-User-Token", userToken)
                .get()
                .build();
        ArrayList<Animal> animals = null;
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String respuesta = response.body().string();
                Log.d(TAG, "Respuesta del servidor a la petición de animales: " + respuesta);
                JSONObject jsonResponse = new JSONObject(respuesta);
                JSONArray jsonArray = jsonResponse.getJSONArray("data");
                /*
                {
    "status": "AUTH_ERROR",
    "data": null
} if "data" = null "NO TIENES ANIMALES"
                 */
                animals = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = jsonArray.getJSONObject(i);
                        JSONArray arrayTags = jsonobject.getJSONArray("tagDTOList");
                        ArrayList<Tag> receivedTags = new ArrayList<>();
                        for (int e = 0; e < arrayTags.length(); e++) {
                            JSONObject jsonTags = arrayTags.getJSONObject(e);
                            Tag tag = new Tag();
                            tag.setTagId(jsonTags.getInt("tagId"));
                            tag.setTagName(jsonTags.getString("tagName"));
                            receivedTags.add(tag);
                        }
                        JSONArray arrayFotos = jsonobject.getJSONArray("animalPhotoList");
                        ArrayList<AnimalPhoto> receivedPhotos = new ArrayList<>();
                        for (int o = 0; o < arrayFotos.length(); o++) {
                            JSONObject jsonPhotos = arrayFotos.getJSONObject(o);
                            AnimalPhoto animalPhoto = new AnimalPhoto();
                            animalPhoto.setPhotoId(jsonPhotos.getInt("photoId"));
                            animalPhoto.setPhotoUrl(jsonPhotos.getString("photoUrl"));
                            animalPhoto.setIsCoverPhoto(jsonPhotos.getBoolean("isCoverPhoto"));
                            animalPhoto.setDisplayOrder(jsonPhotos.getInt("displayOrder"));
                            receivedPhotos.add(animalPhoto);
                        }
                        Animal animal = new Animal();
                        animal.setAnimalId(jsonobject.getLong("animalId"));
                        animal.setAnimalName(jsonobject.getString("animalName"));
                        animal.setMicrochipNumber(jsonobject.getString(("microchipNumber")));
                        animal.setSpeciesId(jsonobject.getInt("speciesId"));
                        animal.setBirthDate(LocalDate.parse(jsonobject.getString("birthDate")));
                        animal.setIsNeutered(jsonobject.getBoolean("isNeutered"));
                        String sex = jsonobject.getString("sex");
                        animal.setSex(Sex.fromString(sex));
                        animal.setIsBirthDateEstimated(jsonobject.getBoolean("isBirthDateEstimated"));
                        animal.setTags(receivedTags);
                        animal.setAnimalPhotoList(receivedPhotos);
                        animals.add(animal);
                    } catch (JSONException e) {
                        System.out.println("Error leyendo animal: "+ e.getMessage());                    }
                }

            } else {
                assert response.body() != null;
                Log.e(TAG, "Error assert recibiendo animales: " + response.code()
                        + " | Respuesta: " + response.body().string());
                //{"status":2002,"data":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ4MzcyOTQ0LCJleHAiOjE3NDg0NTkzNDR9.Kdqk_L15TH2PqbLCi0qOoBh__e3UAei0cVfoPfGCMvg","userName":"Zelawola","email":"mixolida36@gmail.com","photoUrl":"https://lh3.googleusercontent.com/a/ACg8ocK5rMgBRRnY4JxR9m0fOdqAdHWzJjr31gPgJmJvO7juru0c_HTE=s96-c","phone":null}}
            }
        } catch (Exception e) {
            Log.e(TAG, "Error recibiendo tags: ", e);
        }

        return animals;
    }
    public void deleteAnimal(long animalId) {
        getSharedDeviceId();
        String url = "https://www.animor.es/animal/delete-animal";
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Device-Token", deviceToken)
                .addHeader("animalId", String.valueOf(animalId))
                .delete()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String respuesta = response.body().string();
                Log.d(TAG, "Respuesta del servidor al borrado de animales: " + respuesta);
                JSONObject jsonResponse = new JSONObject(respuesta);
                if(jsonResponse.getString("Status").equals("ANIMAL_DELETE_SUCCESS")){
                    Log.d("ApiRequest - Delete animal", "Borrado exitoso");
                }
            } else {
                assert response.body() != null;
                Log.e(TAG, "Error assert recibiendo tags: " + response.code()
                        + " | Respuesta: " + response.body().string());
                //{"status":2002,"data":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ4MzcyOTQ0LCJleHAiOjE3NDg0NTkzNDR9.Kdqk_L15TH2PqbLCi0qOoBh__e3UAei0cVfoPfGCMvg","userName":"Zelawola","email":"mixolida36@gmail.com","photoUrl":"https://lh3.googleusercontent.com/a/ACg8ocK5rMgBRRnY4JxR9m0fOdqAdHWzJjr31gPgJmJvO7juru0c_HTE=s96-c","phone":null}}
            }
        } catch (IOException e) {
            System.out.println("Error de tipo in/out: "+ e.getMessage());
        } catch (JSONException e) {
            System.out.println("Error de json: "+ e.getMessage());        }
    }

    public Long addPhotoIntoDatabase(AnimalPhoto animalphoto) {
        getSharedDeviceId();
        String url = "https://www.animor.es/animalPhoto/add-photo";
        RequestBody body = null;

        // 1. Crear el objeto JSON anidado
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Opcional: no incluir campos null

            Log.d("apirequest", "animal que se va a enviar:"+ animalphoto.toString());
            String json = objectMapper.writeValueAsString(animalphoto);
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
    public class ApiResponse {
        private ArrayList<Species> species;
        private ArrayList<Tag> tags;
        private String deviceToken;

        public ApiResponse(ArrayList<Species> species, ArrayList<Tag> tags, String deviceToken) {
            this.species = species;
            this.tags = tags;
            this.deviceToken = deviceToken;
        }

        public ArrayList<Species> getSpecies() {
            return species;
        }

        public void setSpecies(ArrayList<Species> species) {
            this.species = species;
        }

        public ArrayList<Tag> getTags() {
            return tags;
        }

        public void setTags(ArrayList<Tag> tags) {
            this.tags = tags;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }
    }

}