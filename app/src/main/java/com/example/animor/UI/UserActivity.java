package com.example.animor.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.example.animor.App.MyApplication;
import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;
    TextView nombreUsuario;
    TextView emailUsuario;
    LinearLayout layoutNoLogin;
    TableRow dataRow;
    Button btnIniciarSesion;
    ImageView imgUsuario;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Inicializar GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Asegúrate de tener este string
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initializeViews();
        setupNavigation();
        updateUI();
    }

    private void initializeViews() {
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnMenu = findViewById(R.id.btn_menu);
        nombreUsuario = findViewById(R.id.textViewNombreUsuario);
        emailUsuario = findViewById(R.id.textViewEmailUsuario);
        imgUsuario = findViewById(R.id.imgUser);
        layoutNoLogin = findViewById(R.id.layoutNoLogin);
        dataRow = findViewById(R.id.dataTable);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
    }

    private void setupNavigation() {
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_edit) {
                performGoogleLogout();
            } else if (id == R.id.nav_delete) {
                ApiRequests api = new ApiRequests();
                api.deleteAccount(this);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        btnIniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
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

    private void updateUI() {
        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String nombre = prefs.getString("nombreUsuario", "No logueado");
        String email = prefs.getString("email", "No logueado");
        String deviceToken = prefs.getString("device-token", "No logueado");

        Log.d("UserActivity", "nombre: " + nombre);
        Log.d("UserActivity", "email: " + email);

        // Lógica: ¿hay datos de usuario válidos?
        if (nombre.equals("No logueado") || email.equals("No logueado") ||
                nombre.isEmpty() || email.isEmpty() ||
                nombre.equals("null") || email.equals("null")) {

            // NO hay datos válidos: mostrar el botón de login
            showLoginLayout();
        } else {
            // SÍ hay datos válidos: mostrar perfil
            showProfileLayout(nombre, email);
        }
    }

    private void showLoginLayout() {
        layoutNoLogin.setVisibility(View.VISIBLE);
        dataRow.setVisibility(View.GONE);

        // Limpiar los campos de texto para evitar mostrar datos antiguos
        nombreUsuario.setText("");
        emailUsuario.setText("");
    }

    private void showProfileLayout(String nombre, String email) {
        layoutNoLogin.setVisibility(View.GONE);
        dataRow.setVisibility(View.VISIBLE);

        nombreUsuario.setText(nombre);
//        emailUsuario.setText(email);
//        Picasso.get()
//                .load()
//                .placeholder(R.drawable.gatoinicio)
//                .error(R.drawable.gatoinicio)
//                .into(imgAnimal);
//
    }

    public void performGoogleLogout() {
        MyApplication app = (MyApplication) getApplication();

        // Actualizar UI inmediatamente
        showLoginLayout();

        // Limpiar solo Google Sign In (mantener autenticación de dispositivo)
        app.saveGoogleSignInState("", "", false);

        // Cerrar sesión de Google y Firebase en segundo plano
        new Thread(() -> {
            try {
                // Cerrar sesión de Firebase
                FirebaseAuth.getInstance().signOut();

                // Cerrar sesión de Google
                mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("UserActivity", "Sesión de Google cerrada correctamente.");
                    } else {
                        Log.e("UserActivity", "Error al cerrar sesión de Google: " + task.getException());
                    }

                    // Navegar a LoginActivity DESPUÉS de limpiar todo
                    runOnUiThread(() -> {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                });

            } catch (Exception e) {
                Log.e("UserActivity", "Error durante el signOut: " + e.getMessage());

                // En caso de error, navegar de todos modos
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }).start();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
    }

    // Si necesitas logout COMPLETO (incluyendo dispositivo):
    public void performCompleteLogout() {
        MyApplication app = (MyApplication) getApplication();

        // Actualizar UI inmediatamente
        showLoginLayout();

        // Limpiar TODO - usar el método de MyApplication
        app.performCompleteLogout();

        // También limpiar tus SharedPreferences personales si es necesario
        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Cerrar sesión de Google en segundo plano
        new Thread(() -> {
            try {
                mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                    runOnUiThread(() -> {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                });
            } catch (Exception e) {
                Log.e("UserActivity", "Error durante el signOut: " + e.getMessage());
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }).start();

        Toast.makeText(this, "Sesión cerrada completamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar UI cada vez que se regresa a esta activity
        updateUI();
    }
}