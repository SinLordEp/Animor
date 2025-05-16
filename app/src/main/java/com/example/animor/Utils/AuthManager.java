package com.example.animor.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.animor.R;
import com.example.animor.UI.InicioActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class AuthManager {
    private final Context context;
    private final SharedPreferences prefs;

    public AuthManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
    }

    // Verifica si el usuario tiene acceso válido (invitado o Google)
    public boolean hasValidAccess() {
        return isGuestUser() || isGoogleUser();
    }

    // Comprueba si es usuario de Google
    public boolean isGoogleUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        return account != null;
    }

    // Comprueba si es usuario invitado
    public boolean isGuestUser() {
        return prefs.getBoolean("is_guest", false);
    }

    // Establece modo invitado
    public void setGuestMode(boolean enable) {
        prefs.edit().putBoolean("is_guest", enable).apply();
    }

    // Cierra sesión (ambos tipos)
    public void logout() {
        if (isGoogleUser()) {
            GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
        }
        setGuestMode(false);
    }
}