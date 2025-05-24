package com.example.animor.UI;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.R;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.common.api.ApiException;
import com.google.firebase.installations.FirebaseInstallations;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    //private static final String WEB_CLIENT_ID = "@string/web_client_id";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth; // Instancia de Firebase Auth
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializa Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Configura los listeners
        findViewById(R.id.btn_google_sign_in).setOnClickListener(v -> signIn());
        findViewById(R.id.textView2).setOnClickListener(v -> continueWithoutLogin());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // <-- ¡Importante!
                .requestEmail() // Opcional: solicitar el email
                .build();
        // Crea un GoogleSignInClient con las opciones especificadas.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
// --- Inicio de la implementación de Firebase App Check ---

        // ✅ Inicializa Firebase (obligatorio)
        // Aunque Firebase suele inicializarse automáticamente, es una buena práctica
        // asegurarte de que lo está antes de usar App Check.
        FirebaseApp.initializeApp(this);

        // Activa Firebase App Check con Play Integrity como proveedor
        // Esto le dice a Firebase que use Play Integrity para verificar la autenticidad de tu app.
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
        );

        // Obtiene el App Check Token de forma asíncrona
        // Este token es una prueba de que la solicitud proviene de tu app legítima.
        FirebaseAppCheck.getInstance()
                .getToken(false) // false = permite usar token en caché, lo que mejora el rendimiento
                .addOnSuccessListener(tokenResult -> {
                    String appCheckToken = tokenResult.getToken(); // 🔐 Token que demuestra que la app es legítima
                    Log.d(TAG, "App Check Token: " + appCheckToken); // Para depuración

                    // Obtiene el ID de instalación de Firebase (FID), único para cada instalación
                    // El FID identifica de forma única la instalación de tu app en un dispositivo.
                    FirebaseInstallations.getInstance().getId()
                            .addOnSuccessListener(fid -> {
                                //  En este punto ya tienes el App Check Token y el FID
                                // Puedes enviar ambos al servidor para solicitar un device_token
                                // Esto es útil si tienes un backend propio que necesita verificar
                                // que las peticiones provienen de tu app genuina.
                                Log.d(TAG, "Firebase Installation ID (FID): " + fid); // Para depuración
                                sendDeviceInitRequest(fid, appCheckToken); // Se requiere implementar este método
                            });
                })
                .addOnFailureListener(e -> {
                    //  Manejo de error si falla obtener el App Check Token
                    // Es importante registrar estos errores para depuración.
                    Log.e(TAG, "Error al obtener App Check Token", e);
                    // Opcionalmente, podrías notificar al usuario o tomar alguna acción.
                });
        // --- Fin de la implementación de Firebase App Check ---
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // Dentro de tu MainActivity.java

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica si el resultado proviene de nuestra solicitud de Google Sign-In
        if (requestCode == RC_SIGN_IN) {
            // Obtiene el resultado de la tarea asincrónica
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Si el inicio de sesión de Google fue exitoso, autentica con Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GoogleSignIn", "firebaseAuthWithGoogle:" + account.getId()); // Log de depuración
                firebaseAuthWithGoogle(account.getIdToken()); // <-- Llama al método para autenticar con Firebase y consigue el token id de usuario
            } catch (ApiException e) {
                // Si falló el inicio de sesión de Google
                Log.w("GoogleSignIn", "Google sign in failed", e);
                // Puedes mostrar un mensaje al usuario (ej. Toast)
                Toast.makeText(this, "Error al iniciar sesión con Google.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Dentro de tu MainActivity.java

    private void firebaseAuthWithGoogle(String idToken) {
        // [START firebase_auth_with_google]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicio de sesión con Firebase exitoso
                            Log.d("FirebaseAuth", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            //uid de firebase
                            String userId = user.getUid();
                            //todo:añadir datos a bbdd
                            updateUI(user); // <-- Actualiza tu interfaz
                            startActivity(new Intent(LoginActivity.this, InicioActivity.class));

                        } else {
                            // Si falla el inicio de sesión con Firebase
                            Log.w("FirebaseAuth", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Error de autenticación.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null); // <-- Actualiza tu interfaz mostrando que no hay usuario logeado
                        }
                    }
                });
        // [END firebase_auth_with_google]
    }
    // Dentro de tu MainActivity.java

    @Override
    public void onStart() {
        super.onStart();
        // Verifica si el usuario ha iniciado sesión (no es nulo) y actualiza la UI
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    // Un método de ejemplo para actualizar la UI
    private void updateUI(FirebaseUser user) {
        // Aquí puedes cambiar qué se muestra en la pantalla:
        // Si user no es null, muestra la información del usuario y esconde el botón de login.
        // Si user es null, muestra el botón de login y esconde la información del usuario.
        if (user != null) {
            // Usuario logeado
            Log.d("UI", "Usuario logeado: " + user.getDisplayName());
            // Por ejemplo:
            // findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            // findViewById(R.id.signed_in_layout).setVisibility(View.VISIBLE);
            // ((TextView) findViewById(R.id.user_name_text)).setText(user.getDisplayName());
        } else {
            // Usuario no logeado
            Log.d("UI", "Usuario no logeado");
            // Por ejemplo:
            // findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            // findViewById(R.id.signed_in_layout).setVisibility(View.GONE);
        }
    }


    private void continueWithoutLogin() {
        startActivity(new Intent(LoginActivity.this, InicioActivity.class));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    // TODO: Implementar este método para enviar los datos a tu servidor si es necesario
    private void sendDeviceInitRequest(String fid, String appCheckToken) {
        // Aquí es donde harías una llamada a tu propio backend, si lo tienes.
        // Por ejemplo, usando Retrofit, Volley o HttpURLConnection.
        // Envía el FID y el appCheckToken a tu servidor para que este pueda
        // verificar la autenticidad de tu aplicación antes de otorgar un "device_token"
        // o realizar otras acciones sensibles.
        Log.d(TAG, "Enviando FID y App Check Token a tu servidor...");
    }
}