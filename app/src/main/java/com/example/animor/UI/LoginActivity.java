// LoginActivity.java
package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.*;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.*;
import com.google.firebase.installations.FirebaseInstallations;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializa Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Configura Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // App Check con Debug (para desarrollo)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        // Obtiene App Check Token y FID para tu servidor
        FirebaseAppCheck.getInstance().getToken(false)
                .addOnSuccessListener(tokenResult -> {
                    String appCheckToken = tokenResult.getToken();
                    Log.d(TAG, "App Check Token: " + appCheckToken);
                    FirebaseInstallations.getInstance().getId()
                            .addOnSuccessListener(fid -> {
                                Log.d(TAG, "Firebase Installation ID (FID): " + fid);
                                new Thread(() -> {
                                    ApiRequests api = new ApiRequests();
                                    String respuesta = api.sendFidDeviceToServer(appCheckToken, fid);
                                    if (respuesta == null) {
                                        runOnUiThread(() -> Toast.makeText(this, "No se recibió respuesta del servidor de autenticación", Toast.LENGTH_LONG).show());
                                        System.exit(0);
                                    }
                                }).start();
                            });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al obtener App Check Token", e));

        // Botones
        findViewById(R.id.btn_google_sign_in).setOnClickListener(v -> signIn());
        findViewById(R.id.textView2).setOnClickListener(v -> continueWithoutLogin());
    }

    private void signIn() {
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
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Log.e(TAG, "Error al obtener GoogleSignInAccount", e);
                Toast.makeText(this, "Error al iniciar sesión con Google.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                if (tokenTask.isSuccessful()) {
                                    String firebaseIdToken = tokenTask.getResult().getToken();
                                    Log.d(TAG, "Firebase ID Token: " + firebaseIdToken);

                                    new Thread(() -> {
                                        ApiRequests api = new ApiRequests();
                                        api.sendUserToServer(firebaseIdToken);
                                    }).start();

                                    updateUI(user);
                                    startActivity(new Intent(LoginActivity.this, InicioActivity.class));
                                } else {
                                    Log.e(TAG, "Error al obtener Firebase ID Token", tokenTask.getException());
                                    Toast.makeText(LoginActivity.this, "Error al obtener el token de usuario.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Log.w(TAG, "Error de autenticación", task.getException());
                        Toast.makeText(this, "Error de autenticación.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI(mAuth.getCurrentUser());
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d(TAG, "Usuario logeado: " + user.getDisplayName());
        } else {
            Log.d(TAG, "Usuario no logeado");
        }
    }

    private void continueWithoutLogin() {
        startActivity(new Intent(LoginActivity.this, InicioActivity.class));
    }
}
