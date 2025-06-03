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
    public static final String KEY_DEVICE_AUTHENTICATED = "device_authenticated";
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
    private static boolean isDeviceAuthenticated = false;
    private static boolean isGoogleSignedIn = false;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseApp.initializeApp(this);

        // 1. Verificar token existente primero
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        deviceToken = prefs.getString(KEY_DEVICE_TOKEN, null);

        if (deviceToken != null) {
            Log.d(TAG, "Token de dispositivo encontrado en SharedPreferences: " + deviceToken);
            isDeviceAuthenticated = true;
            loadSavedData();
        }

        // 2. Configurar Firebase AppCheck (necesario siempre para otras funciones)
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );

        // 3. Verificar estados de autenticación
        checkGoogleSignInState();

        // 4. Solo autenticar dispositivo si no tenemos token
        if (deviceToken == null) {
            initializeFirebaseTokens();
        }
    }

    private void loadSavedData() {
        // Cargar datos adicionales si es necesario
        new Thread(() -> {
            // Implementación para cargar datos persistentes
        }).start();
    }

    private void checkGoogleSignInState() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        isGoogleSignedIn = currentUser != null;

        if (isGoogleSignedIn) {
            saveGoogleSignInState(currentUser.getEmail(), currentUser.getDisplayName(), true);
            Log.d(TAG, "Usuario ya logueado: " + currentUser.getEmail());
        } else if (prefs.getBoolean(KEY_GOOGLE_SIGNED_IN, false)) {
            clearGoogleSignInState();
            Log.d(TAG, "Limpiando estado de Google Sign In inconsistente");
        }
    }

    private void initializeFirebaseTokens() {
        FirebaseAppCheck.getInstance().getToken(false)
                .addOnSuccessListener(tokenResult -> {
                    appCheckToken = tokenResult.getToken();
                    Log.d(TAG, "App Check Token obtenido");

                    FirebaseInstallations.getInstance().getId()
                            .addOnSuccessListener(fid -> {
                                firebaseInstallationId = fid;
                                authenticateDevice();
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error al obtener FID", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al obtener App Check Token", e));
    }

    private void authenticateDevice() {
        new Thread(() -> {
            ApiRequests api = new ApiRequests();
            ApiRequests.ApiResponse response = api.sendFidDeviceToServer(appCheckToken, firebaseInstallationId);
            species = response.getSpecies();
            tags = response.getTags();
            deviceToken = response.getDeviceToken();
            Log.d(TAG, "DeviceToken recibido del servidor: " + deviceToken);
        }).start();
    }
    private void saveDeviceAuthenticationState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_DEVICE_AUTHENTICATED, true)
                .putString(KEY_DEVICE_TOKEN, deviceToken)
                .apply();
    }

    public void saveGoogleSignInState(String email, String name, boolean signedIn) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_GOOGLE_SIGNED_IN, signedIn);

        if (signedIn) {
            editor.putString(KEY_USER_EMAIL, email)
                    .putString(KEY_USER_NAME, name);
        } else {
            editor.remove(KEY_USER_EMAIL)
                    .remove(KEY_USER_NAME);
        }
        editor.apply();
        isGoogleSignedIn = signedIn;
    }

    private void clearGoogleSignInState() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putBoolean(KEY_GOOGLE_SIGNED_IN, false)
                .remove(KEY_USER_EMAIL)
                .remove(KEY_USER_NAME)
                .apply();
    }

    public void clearDeviceAuthenticationState() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putBoolean(KEY_DEVICE_AUTHENTICATED, false)
                .remove(KEY_DEVICE_TOKEN)
                .apply();
        isDeviceAuthenticated = false;
        deviceToken = null;
    }

    public void performCompleteLogout() {
        FirebaseAuth.getInstance().signOut();
        clearGoogleSignInState();
        clearDeviceAuthenticationState();
    }

    // Getters
    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
    public static String getDeviceToken() { return deviceToken; }
    public static ArrayList<Tag> getTags() { return tags; }
    public static ArrayList<Species> getSpecies() { return species; }
    public static boolean isDeviceAuthenticated() { return isDeviceAuthenticated; }
    public static boolean isGoogleSignedIn() { return isGoogleSignedIn; }
}