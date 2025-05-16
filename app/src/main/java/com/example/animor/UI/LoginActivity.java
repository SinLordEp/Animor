package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView tvContinueWithoutLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Asegúrate de que el nombre del layout coincida

        // Configurar el cliente de inicio de sesión de Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configurar el botón de inicio de sesión de Google
        com.google.android.gms.common.SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(com.google.android.gms.common.SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(view -> signIn());

        // Configurar el texto para continuar sin iniciar sesión
        tvContinueWithoutLogin = findViewById(R.id.textView2);
        tvContinueWithoutLogin.setOnClickListener(v -> continueWithoutLogin());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si el usuario ya ha iniciado sesión
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            updateUI(account);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado devuelto al iniciar el Intent de GoogleSignInClient.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Inicio de sesión exitoso, actualizar la UI con la información del usuario
            updateUI(account);
        } catch (ApiException e) {
            // El código de estado de ApiException contiene el código de error detallado
            Toast.makeText(this, "Error al iniciar sesión: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // Navegar a la pantalla principal
            startActivity(new Intent(this, InicioActivity.class));
            finish();
        } else {
            // El usuario no ha iniciado sesión
            Toast.makeText(this, "Por favor inicia sesión", Toast.LENGTH_SHORT).show();
        }
    }

    private void continueWithoutLogin() {
        // Navegar a la pantalla principal sin iniciar sesión
        startActivity(new Intent(this, InicioActivity.class));
        finish();
    }
}