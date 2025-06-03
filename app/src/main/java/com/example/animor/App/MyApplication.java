package com.example.animor.App;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.example.animor.Model.Species;
import com.example.animor.Model.Tag;
import com.example.animor.Utils.ApiRequests;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    private static String appCheckToken;
    private static String firebaseInstallationId;
    private static String deviceToken;
    private static ArrayList<Tag> tags;
    private static ArrayList<Species> species;
    private static String notificationToken;
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );

        // Obtiene App Check Token y FID para tu servidor
        firebaseAppCheck.getToken(false)
                .addOnSuccessListener(tokenResult -> {
                    appCheckToken = tokenResult.getToken();
                    Log.d(TAG, "App Check Token: " + appCheckToken);

                    FirebaseInstallations.getInstance().getId()
                            .addOnSuccessListener(fid -> {
                                firebaseInstallationId = fid;
                                Log.d(TAG, "Firebase Installation ID (FID): " + fid);

                                // Enviar a servidor en un hilo aparte
                                new Thread(() -> {
                                    ApiRequests api = new ApiRequests();
                                    // Estos datos se inicializan en MyApplication.onCreate()
                                    // Asegurarse de que se accede a ellos solo después de completarse la inicialización

                                    deviceToken = api.sendFidDeviceToServer(appCheckToken, fid);
                                    species = api.askForSpeciesToDatabase();
                                    tags = api.askForTagsToDatabase();

                                    Log.d(TAG, "DEVICE-TOKEN: " + deviceToken);

                                    if (deviceToken == null) {
                                        // No hay contexto aquí para Toast, puedes manejarlo distinto
                                        Log.e(TAG, "No se recibió respuesta del servidor de autenticación");
                                        // Opcional: terminar app o manejar error
                                    }
                                }).start();
                            });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al obtener App Check Token", e));
    }

    // Getters estáticos para que otras clases puedan acceder a los tokens
    public static String getAppCheckToken() {
        return appCheckToken;
    }

    public static String getFirebaseInstallationId() {
        return firebaseInstallationId;
    }
    public String getDeviceToken() {
        return deviceToken;
    }

    public static ArrayList<Tag> getTags() {
        return tags;
    }

    public static ArrayList<Species> getSpecies() {
        return species;
    }
    public static String getNotificationToken() {
        return notificationToken;
    }
}
