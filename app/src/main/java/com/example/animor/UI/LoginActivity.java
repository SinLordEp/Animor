package com.example.animor.UI;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.R;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> signInLauncher;
    private static final String WEB_CLIENT_ID = "804843988448-3u7337ptfpn4221ahnrvlp371tmhac04.apps.googleusercontent.com";
    private final Executor executor = Executors.newSingleThreadExecutor();
    //String scope = "audience:server:client_id:804843988448-3u7337ptfpn4221ahnrvlp371tmhac04.apps.googleusercontent.com";
    //String token = GoogleAuthUtil.getToken(this, client.getAccountName(), scope);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configura Google Sign-In (esto es seguro en el hilo principal)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(WEB_CLIENT_ID)
                .requestServerAuthCode(WEB_CLIENT_ID, false)
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configura los listeners
        findViewById(R.id.btn_google_sign_in).setOnClickListener(v -> checkNetworkAndSignIn());
        findViewById(R.id.textView2).setOnClickListener(v -> continueWithoutLogin());

        // Configura el ActivityResultLauncher
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Result code: " + result.getResultCode());
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            handleSignInIntent(result.getData());
                        } else {
                            Log.w(TAG, "Intent data is null");
                            showToast("Error: datos de inicio sesión no recibidos");
                        }
                    } else {
                        Log.w(TAG, "Sign-in failed. Result code: " + result.getResultCode());
                        showToast("Error en inicio de sesión. Código: " + result.getResultCode());
                    }
                });
    }

    private void checkNetworkAndSignIn() {
        executor.execute(() -> {
            try {
                // Verifica la conexión en segundo plano
                if (!isNetworkAvailable()) {
                    runOnUiThread(() -> showToast("No hay conexión a internet"));
                    return;
                }

                // Obtiene el intent en segundo plano
                Intent signInIntent = googleSignInClient.getSignInIntent();

                // Lanza la actividad en el hilo principal
                runOnUiThread(() -> signInLauncher.launch(signInIntent));
            } catch (Exception e) {
                Log.e(TAG, "Error en checkNetworkAndSignIn", e);
                runOnUiThread(() -> showToast("Error al iniciar sesión"));
            }
        });
    }

    private void handleSignInIntent(Intent data) {
        executor.execute(() -> {
            try {
                // Procesa el resultado en segundo plano
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = Tasks.await(task);

                // Navega en el hilo principal
                runOnUiThread(() -> {
                    Log.d(TAG, "Sign-in successful: " + account.getEmail());
                    navigateToMain();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error en handleSignInIntent", e);
                runOnUiThread(() -> {
                    if (e instanceof ApiException) {
                        handleApiException((ApiException) e);
                    } else {
                        showToast("Error al procesar el inicio de sesión");
                    }
                });
            }
        });
    }

    private void handleApiException(ApiException e) {
        String errorMessage = "Error al iniciar sesión: ";
        switch (e.getStatusCode()) {
            case 7: // NETWORK_ERROR
                errorMessage += "Problema de conexión";
                break;
            case 12501: // SIGN_IN_CANCELLED
                errorMessage += "Cancelado por el usuario";
                break;
            default:
                errorMessage += "Código de error: " + e.getStatusCode();
        }
        showToast(errorMessage);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void continueWithoutLogin() {
        navigateToMain();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}