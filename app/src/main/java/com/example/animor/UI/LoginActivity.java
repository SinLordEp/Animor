package com.example.animor.UI;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.R;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.security.SecureRandom;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String WEB_CLIENT_ID = "@string/web_client_id";
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
}