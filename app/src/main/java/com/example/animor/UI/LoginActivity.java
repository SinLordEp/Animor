package com.example.animor.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.dto.UserDTO;
import com.example.animor.Model.entity.User;
import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.JacksonUtils;
import com.example.animor.Utils.PreferenceUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configurar botones
        findViewById(R.id.btn_google_sign_in).setOnClickListener(v -> signInWithGoogle());
        findViewById(R.id.textView2).setOnClickListener(v -> continueWithoutGoogleLogin());
    }

    private void signInWithGoogle() {
        Log.d(TAG, "Iniciando Google Sign In...");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "Google Sign In exitoso para: " + account.getEmail());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e(TAG, "Error en Google Sign In", e);
                Toast.makeText(this, "Error al iniciar sesión con Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Log.d(TAG, "Firebase Auth exitoso para: " + firebaseUser.getEmail());

                            // Obtener el token de Firebase
                            firebaseUser.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                if (tokenTask.isSuccessful()) {
                                    String firebaseIdToken = tokenTask.getResult().getToken();

                                    // Enviar datos del usuario al servidor
                                    sendUserDataToServer(firebaseIdToken, firebaseUser);
                                } else {
                                    Log.e(TAG, "Error al obtener Firebase ID Token", tokenTask.getException());
                                    Toast.makeText(this, "Error al obtener el token de usuario", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Log.e(TAG, "Error en Firebase Auth", task.getException());
                        Toast.makeText(this, "Error de autenticación con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendUserDataToServer(String firebaseIdToken, FirebaseUser firebaseUser) {
        MyApplication.executor.execute(() -> {
            try {
                Log.d(TAG, "Enviando datos del usuario al servidor...");

                ApiRequests api = new ApiRequests();
                try {
                    UserDTO userDTO = api.sendUserToServer(firebaseIdToken);
                    saveUserData(userDTO);

                    // Navegar a la siguiente actividad
                    runOnUiThread(this::navigateToMainActivity);

                } catch (Exception e) {
                    Log.e(TAG, "Error al enviar usuario: ", e);

                    // Mostrar toast y cerrar app en el hilo principal
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Servidor no conectado", Toast.LENGTH_LONG).show();

                        // Dar tiempo para que se muestre el toast antes de cerrar
                        new android.os.Handler().postDelayed(() -> {
                            finishAndRemoveTask(); // Mejor que System.exit(0)
                        }, 2000); // 2 segundos delay
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error general al enviar datos del usuario al servidor", e);
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Error al guardar datos del usuario", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void saveUserData(UserDTO userDTO) {
        User user = User.toEntity(userDTO);
        String userJson = JacksonUtils.entityToJson(user);
        PreferenceUtils.saveData(PreferenceUtils.KEY_USER_MODEL, userJson);
        Log.d(TAG, "Datos del usuario guardados en SharedPreferences");
    }

    private void continueWithoutGoogleLogin() {
        Log.d(TAG, "Continuando sin Google Sign In...");
        navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si el usuario ya está autenticado con Google
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Usuario ya autenticado: " + currentUser.getEmail());
        }
    }
}