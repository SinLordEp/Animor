package com.example.animor.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;


import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;
    TextView nombreUsuario;
    TextView emailUsuario;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String nombre = prefs.getString("nombreUsuario", "No logueado");
        String email = prefs.getString("email", "No logueado");
        String deviceToken = prefs.getString("device-token", "No logueado");
        Log.d("nombre:", nombre);
        Log.d("email:", email);

        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnMenu = findViewById(R.id.btn_menu);
        nombreUsuario = findViewById(R.id.textViewNombreUsuario);
        nombreUsuario.setText(String.valueOf(prefs.getString("nombreUsuario", null)));
        emailUsuario = findViewById(R.id.textViewEmailUsuario);
        emailUsuario.setText(String.valueOf(prefs.getString("email", null)));
        LinearLayout layoutNoLogin = findViewById(R.id.layoutNoLogin);
        TableRow dataRow = findViewById(R.id.dataRow);
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
// Lógica: ¿hay datos de usuario?
        if (nombre.equals("No logueado")|| email.equals("No logueado")) {
            // NO hay datos: mostrar el botón
            layoutNoLogin.setVisibility(View.VISIBLE);
            dataRow.setVisibility(View.GONE);

            btnIniciarSesion.setOnClickListener(v -> {
                // Redirige a LoginActivity
                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
        // SÍ hay datos: mostrar perfil
        layoutNoLogin.setVisibility(View.GONE);
            dataRow.setVisibility(View.VISIBLE);

            nombreUsuario.setText(nombre);
            emailUsuario.setText(email);
    }

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                signOutFromGoogle(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear(); // Borra todas las claves y valores de este archivo
                editor.apply();
                layoutNoLogin.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (id == R.id.nav_delete) {
                ApiRequests api = new ApiRequests();
                api.deleteAccount(this); // Este método maneja todo (eliminar, cerrar sesión, redirigir, etc.)
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;

        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_user);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(UserActivity.this, InicioActivity.class));
                return true;
            } else if (id == R.id.nav_favs) {
                startActivity(new Intent(UserActivity.this, FavActivity.class));
                return true;
            } else if (id == R.id.nav_listing) {
                startActivity(new Intent(UserActivity.this, CreateActivity.class));
                return true;
            } else if (id == R.id.nav_user) {
                return true;
            } else if (id == R.id.nav_animals) {
                startActivity(new Intent(UserActivity.this, ShowActivity.class));
                return true;
            }
            return false;
        });
    }
    public void signOutFromGoogle(Activity activity) {
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