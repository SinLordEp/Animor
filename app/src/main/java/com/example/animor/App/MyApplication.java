package com.example.animor.App;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.animor.Model.Species;
import com.example.animor.Model.Tag;
import com.example.animor.Utils.ApiRequests;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static final String PREFS_NAME = "userPrefs";
    public static final String KEY_DEVICE_TOKEN = "device_token";
    public static final String KEY_GOOGLE_SIGNED_IN = "google_signed_in";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_NAME = "user_name";

    private static String appCheckToken;
    private static String firebaseInstallationId;
    private static String deviceToken;
    private static ArrayList<Tag> tags;
    private static ArrayList<Species> species;
    private static String notificationToken;
    private static boolean isGoogleSignedIn = false;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);

        // Configurar Firebase AppCheck
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );

        // Siempre autenticar dispositivo (sin verificar estado previo)
        authenticateDevice();

        // Verificar estado de Google Sign In
        checkGoogleSignInState();
    }

    private void authenticateDevice() {
        Log.d(TAG, "Iniciando autenticación de dispositivo...");

        FirebaseAppCheck.getInstance().getToken(false)
                .addOnSuccessListener(tokenResult -> {
                    appCheckToken = tokenResult.getToken();
                    Log.d(TAG, "App Check Token obtenido");

                    FirebaseInstallations.getInstance().getId()
                            .addOnSuccessListener(fid -> {
                                firebaseInstallationId = fid;
                                Log.d(TAG, "Firebase Installation ID obtenido: " + fid);

                                // Llamar al servidor para autenticar dispositivo
                                performDeviceAuthentication();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error al obtener Firebase Installation ID", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener App Check Token", e);
                });
    }

    private void performDeviceAuthentication() {
        new Thread(() -> {
            try {
                ApiRequests api = new ApiRequests();
                ApiRequests.ApiResponse response = api.sendFidDeviceToServer(appCheckToken, firebaseInstallationId);

                // Guardar datos recibidos
                species = response.getSpecies();
                tags = response.getTags();
                deviceToken = response.getDeviceToken();

                Log.d(TAG, "Autenticación de dispositivo exitosa. Token: " + deviceToken);

                // Guardar en SharedPreferences
                saveDeviceData();

            } catch (Exception e) {
                Log.e(TAG, "Error en autenticación de dispositivo", e);
            }
        }).start();
    }

    private void saveDeviceData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_DEVICE_TOKEN, deviceToken)
                .apply();

        Log.d(TAG, "Datos de dispositivo guardados en SharedPreferences");
    }

    private void checkGoogleSignInState() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            isGoogleSignedIn = true;
            saveGoogleSignInState(currentUser.getEmail(), currentUser.getDisplayName(), true);
            Log.d(TAG, "Usuario Google ya autenticado: " + currentUser.getEmail());
        } else {
            isGoogleSignedIn = false;
            Log.d(TAG, "No hay usuario Google autenticado");
        }
    }

    public void saveGoogleSignInState(String email, String name, boolean signedIn) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(KEY_GOOGLE_SIGNED_IN, signedIn);

        if (signedIn && email != null && name != null) {
            editor.putString(KEY_USER_EMAIL, email);
            editor.putString(KEY_USER_NAME, name);
            Log.d(TAG, "Estado de Google Sign In guardado para: " + email);
        } else {
            editor.remove(KEY_USER_EMAIL);
            editor.remove(KEY_USER_NAME);
            Log.d(TAG, "Estado de Google Sign In limpiado");
        }

        editor.apply();
        isGoogleSignedIn = signedIn;
    }

    public void performCompleteLogout() {
        FirebaseAuth.getInstance().signOut();

        // Limpiar SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Resetear variables
        isGoogleSignedIn = false;
        deviceToken = null;

        Log.d(TAG, "Logout completo realizado");
    }

    // Getters estáticos
    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static String getDeviceToken() {
        return deviceToken;
    }

    public static ArrayList<Tag> getTags() {
        return tags;
    }

    public static ArrayList<Species> getSpecies() {
        return species;
    }

    public static boolean isGoogleSignedIn() {
        return isGoogleSignedIn;
    }

    public static String getNotificationToken() {
        return notificationToken;
    }

    public static void setNotificationToken(String token) {
        notificationToken = token;
    }
}