package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    Button btn_logout;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        Toolbar toolbar = findViewById(R.id.toolbar);  // Añádelo en el layout también si no existe

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                signOutFromGoogle();  // Tu método de logout
                return true;
            }
            return false;
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(v -> signOutFromGoogle());
        bottomNavigationView.setSelectedItemId(R.id.nav_favs);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_inicio) {
                    startActivity(new Intent(ProfileActivity.this, InicioActivity.class));
                    return true;
                } else if (id == R.id.nav_favs) {
                    return true;
                } else if (id == R.id.registrar) {
                    startActivity(new Intent(ProfileActivity.this, RegistryActivity.class));
                    return true;
                } else if (id == R.id.nav_user) {
                    startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                    return true;
                } else if (id == R.id.nav_animals) {
                    startActivity(new Intent(ProfileActivity.this, MyAnimalsActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
    private void signOutFromGoogle() {
        new Thread(() -> {

            mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("ProfileActivity", "Sesión de Google cerrada correctamente.");
                        FirebaseAuth.getInstance().signOut();  // También cerrar sesión de Firebase

                        // Redirigir al LoginActivity
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("ProfileActivity", "Error al cerrar sesión de Google.");
                    }
                });
        }).start();
    }

}
