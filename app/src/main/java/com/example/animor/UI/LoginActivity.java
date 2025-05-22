package com.example.animor.UI;

import android.content.Context;
import android.content.Intent;
import android.credentials.GetCredentialRequest;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;

import java.security.SecureRandom;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String WEB_CLIENT_ID = "@string/web_client_id";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> signInLauncher;
    private ActivityResultLauncher<Intent> signOutLauncher;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configura Google Sign-In
        configureGoogleSignIn();

        // Configura los listeners
        findViewById(R.id.btn_google_sign_in).setOnClickListener(v -> checkNetworkAndSignIn());
        findViewById(R.id.textView2).setOnClickListener(v -> continueWithoutLogin());
    }

    // Genera un nonce seguro (ejemplo simplificado)
    private String generateNonce() {
        SecureRandom random = new SecureRandom();
        byte[] nonceBytes = new byte[16];
        random.nextBytes(nonceBytes);
        return Base64.encodeToString(nonceBytes, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }
    private void configureGoogleSignIn() {
        // Configura las opciones de inicio de sesión con el ID de cliente
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .setNonce(generateNonce())
                .build();
        Bundle data = new Bundle();
        CredentialOption credentialOption = CredentialOption.createFrom(googleIdOption);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            GetCredentialRequest gcr = new GetCredentialRequest.Builder(data)
                    .addCredentialOption(googleIdOption)
                    .build();
        }

        // Configura el ActivityResultLauncher para login
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        handleSignInIntent(result.getData());
                    } else {
                        handleSignInFailure(result.getResultCode());
                    }
                });
    }


    private void checkNetworkAndSignIn() {
        if (!isNetworkAvailable()) {
            showToast(getString(R.string.no_internet_connection));
            return;
        }
        signInWithGoogle();
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void handleSignInIntent(Intent data) {
        executor.execute(() -> {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = Tasks.await(task);

                if (account == null || account.getIdToken() == null) {
                    runOnUiThread(() -> showToast(getString(R.string.sign_in_failed)));
                    return;
                }

                // Guardar el token de acceso si es necesario
                String idToken = account.getIdToken();
                // Puedes guardar esto en SharedPreferences o enviarlo a tu backend

                processSuccessfulSignIn(account);

            } catch (Exception e) {
                handleSignInError(e);
            }
        });
    }

    private void processSuccessfulSignIn(GoogleSignInAccount account) {
        runOnUiThread(() -> {
            Log.d(TAG, "Sign-in successful. Email: " + account.getEmail());
            navigateToMain();
        });
    }

    private void handleSignInError(Exception e) {
        runOnUiThread(() -> {
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                String errorMessage = getErrorMessage(apiException);
                showToast(errorMessage);
                Log.e(TAG, "Sign-in failed. Code: " + apiException.getStatusCode(), e);
            } else {
                showToast(getString(R.string.sign_in_error));
                Log.e(TAG, "Sign-in failed", e);
            }
        });
    }

    private String getErrorMessage(ApiException e) {
        switch (e.getStatusCode()) {
            case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                return getString(R.string.sign_in_cancelled);
            case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                return getString(R.string.sign_in_failed);
            case GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS:
                return getString(R.string.sign_in_in_progress);
            default:
                return getString(R.string.unknown_error) + ": " + e.getStatusCode();
        }
    }

    private void handleSignInFailure(int resultCode) {
        if (resultCode == RESULT_CANCELED) {
            showToast(getString(R.string.sign_in_cancelled));
        } else {
            showToast(getString(R.string.sign_in_failed) + ". Code: " + resultCode);
        }
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