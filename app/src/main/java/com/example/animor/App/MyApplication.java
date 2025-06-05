package com.example.animor.App;

import static com.example.animor.Utils.PreferenceUtils.KEY_DEVICE_TOKEN;
import static com.example.animor.Utils.PreferenceUtils.KEY_SPECIES_LIST;
import static com.example.animor.Utils.PreferenceUtils.KEY_TAG_LIST;

import android.app.Application;
import android.util.Log;

import com.example.animor.Model.StartupResource;
import com.example.animor.Model.dto.UserDTO;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.JacksonUtils;
import com.example.animor.Utils.PreferenceUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {

    public static final int NUM_CORES = Runtime.getRuntime().availableProcessors();
    public static final int POOL_SIZE = Math.max(2, Math.min(NUM_CORES + 1, 4));
    public static final ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
    private static final String TAG = "MyApplication";

    //private static String appCheckToken;
    private static String firebaseInstallationId;
    private static String deviceToken;
    private static String notificationToken;

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceUtils.init(getApplicationContext());
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
        String[] appCheckTokenAndDeviceFid = new String[2];
        final boolean[] tokenOk = {false};
        final boolean[] idOk = {false};

        Runnable tryContinue = () -> {
            if (tokenOk[0] && idOk[0]) {
                Log.d("MyApplication", "Obtenidos ambos datos, procediendo a autenticar");
                performDeviceAuthentication(appCheckTokenAndDeviceFid);
            }
        };

        FirebaseAppCheck.getInstance().getToken(false)
                .addOnSuccessListener(tokenResult -> {
                    String appCheckToken = tokenResult.getToken();
                    if(!appCheckToken.isEmpty()){
                        appCheckTokenAndDeviceFid[0] = appCheckToken;
                        tokenOk[0] = true;
                        Log.e(TAG, "Appcheck token accomplished");
                    }else{
                        Log.e(TAG, "Error getting appcheck token, is empty");
                    }
                    tryContinue.run();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al obtener App Check Token", e));

        FirebaseInstallations.getInstance().getId()
                .addOnSuccessListener(deviceFid -> {
                    if(!deviceFid.isEmpty()){
                        appCheckTokenAndDeviceFid[1] = deviceFid;
                        idOk[0] = true;
                        Log.e(TAG, "Device fid fetched: " + deviceFid);
                    }else{
                        Log.e(TAG, "Error getting Device fid, is empty");
                    }
                    tryContinue.run();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al obtener Firebase Installation ID", e));
    }

    private void performDeviceAuthentication(String[] appCheckTokenAndDeviceFid) {
        executor.execute(()->{
            try {
                ApiRequests api = new ApiRequests();
                Log.d("MyApplication","Ejecutando autenticación con el servidor");
                StartupResource startupResource = api.sendFidDeviceToServer(appCheckTokenAndDeviceFid[0], appCheckTokenAndDeviceFid[1]);
                //lectura de SP
                //species = PreferenceUtils.getSpeciesList();
               // tags = PreferenceUtils.getTagList();
                // Guardar datos recibidos
                deviceToken = startupResource.getDeviceToken();
                Log.d(TAG, "Autenticación de dispositivo exitosa. Token: " + deviceToken);
                // Guardar en SharedPreferences
                String tagListJson = JacksonUtils.entityToJson(startupResource.getTags());
                String speciesListJson = JacksonUtils.entityToJson(startupResource.getSpecies());

                PreferenceUtils.saveData(KEY_TAG_LIST, tagListJson);
                PreferenceUtils.saveData(KEY_SPECIES_LIST, speciesListJson);
                PreferenceUtils.saveData(KEY_DEVICE_TOKEN, deviceToken);
            } catch (Exception e) {
                Log.e(TAG, "Error en autenticación de dispositivo", e);
            }
        });
    }

    private void checkGoogleSignInState() {
        UserDTO currentUser = PreferenceUtils.getUser();
        if (currentUser != null) {
            Log.d(TAG, "Usuario Google ya autenticado: " + currentUser.getEmail());
        } else {
            Log.d(TAG, "No hay usuario Google autenticado");
        }
    }
    public void performCompleteLogout() {
        FirebaseAuth.getInstance().signOut();

        // Limpiar SharedPreferences
        PreferenceUtils.removeUser();
        // Resetear variables
        deviceToken = null;
        Log.d(TAG, "Logout completo realizado");
    }

    // Getters estáticos
    public static String getNotificationToken() {
        return notificationToken;
    }

    public static void setNotificationToken(String token) {
        notificationToken = token;
    }
}