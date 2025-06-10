package com.example.animor.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.dto.TagDTO;
import com.example.animor.Model.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;


public class PreferenceUtils {
    private static Context context;
    private static String deviceToken = null;
    private static List<TagDTO> tagList = null;
    private static List<SpeciesDTO> speciesDTOList = null;
    private static User user = null;
    public static final String PREFS_NAME = "userPrefs";
    public static final String KEY_DEVICE_TOKEN = "device_token";
    public static final String KEY_TAG_LIST = "tag_list";
    public static final String KEY_SPECIES_LIST = "species_list";
    public static final String KEY_USER_MODEL = "user_model";
    public static final String KEY_GOOGLE_SIGNED_IN = "google_signed_in";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_NAME = "user_name";

    public static void init(Context appContext) {
        context = appContext.getApplicationContext();
    }
    public static void saveData(String key, String value) {
        Log.d("SP Util","Saving data Key: " + key + " Value: " + value);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(key, value)
                .apply();
    }

    private static void deleteData(String key){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(key).apply();
    }

    private static String getString(String key){
        Log.d("SP Util","Getting value Key: " + key);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        Log.d("SP Util", "Value: " + value);
        return value;
    }
    private static <T> T getEntity(String key, TypeReference<T> typeReference){
        String json = getString(key);
        return json.isEmpty() ? null : JacksonUtils.readEntity(json, typeReference);
    }
    private static <T> List<T> getList(String key, TypeReference<List<T>> typeReference) {
        String json = getString(key);
        return json.isEmpty() ? new ArrayList<>() : JacksonUtils.readEntities(json, typeReference);
    }

    public static List<TagDTO> getTagList() {
        if (tagList != null) {
            return tagList;
        }
        tagList = getList(KEY_TAG_LIST, new TypeReference<List<TagDTO>>() {});
        return tagList;
    }

    public static List<SpeciesDTO> getSpeciesList() {
        if (speciesDTOList != null) {
            return speciesDTOList;
        }
        speciesDTOList = getList(KEY_SPECIES_LIST, new TypeReference<List<SpeciesDTO>>() {});
        return speciesDTOList;
    }

    public static String getDeviceToken(){
        if(deviceToken != null){
            return deviceToken;
        }
        deviceToken = getString( KEY_DEVICE_TOKEN);
        return deviceToken;
    }

    public static User getUser(){
        if(user != null){
            return user;
        }
        user = getEntity(KEY_USER_MODEL, new TypeReference<User>() {});
        return user;
    }

    public static void removeUser(){
        user = null;
        deleteData(KEY_USER_MODEL);
    }
}