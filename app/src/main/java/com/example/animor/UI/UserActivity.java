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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.dto.UserDTO;
import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.NavigationHelper;
import com.example.animor.Utils.PreferenceUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class UserActivity extends AppCompatActivity {

    // Views
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;
    TextView nombreUsuario;
    TextView emailUsuario;
    LinearLayout layoutNoLogin;
    TableRow dataRow;
    Button btnIniciarSesion;
    ImageView imgUsuario;
    BottomNavigationView bottomNavigationView;

    // Navigation y Auth
    private NavigationHelper navigationHelper;
    private GoogleSignInClient mGoogleSignInClient;

    // Datos de usuario
    private String nombre;
    private String photo;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Inicializar GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
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
        dataRow = findViewById(R.id.tableanimal);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    private void setupNavigation() {
        // CORREGIR: Configurar NavigationHelper DESPUÉS de inicializar las vistas
        navigationHelper = NavigationHelper.create(this, NavigationHelper.ActivityType.USER);
        navigationHelper.setupBottomNavigation(bottomNavigationView);

        // Drawer navigation
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

        // Login button
        btnIniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateUI() {
        // CORREGIR: Verificar si el usuario existe antes de acceder a sus propiedades
        UserDTO user = PreferenceUtils.getUser();

        if (user == null) {
            Log.d("UserActivity", "Usuario no encontrado");
            showLoginLayout();
            return;
        }

        // Usuario existe, obtener datos
        nombre = user.getUserName();
        email = user.getEmail();
        photo = user.getUserPhoto();

        Log.d("UserActivity", "nombre: " + nombre);
        Log.d("UserActivity", "email: " + email);

        // Verificar si los datos son válidos
        if (nombre == null || nombre.isEmpty()) {
            showLoginLayout();
        } else {
            showProfileLayout();
        }
    }

    private void showLoginLayout() {
        dataRow.setVisibility(View.GONE);
        layoutNoLogin.setVisibility(View.VISIBLE);

        // Limpiar los campos de texto para evitar mostrar datos antiguos
        nombreUsuario.setText("");
        emailUsuario.setText("");

        Log.d("UserActivity", "Mostrando layout de login");
    }

    private void showProfileLayout() {
        layoutNoLogin.setVisibility(View.GONE);
        dataRow.setVisibility(View.VISIBLE);

        // CORREGIR: Verificar valores antes de usarlos
        nombreUsuario.setText(nombre != null ? nombre : "Usuario");
        emailUsuario.setText(email != null ? email : "Sin email");

        // Cargar imagen de forma segura
        if (photo != null && !photo.isEmpty()) {
            Picasso.get()
                    .load(photo)
                    .placeholder(R.drawable.gatoinicio)
                    .error(R.drawable.gatoinicio)
                    .into(imgUsuario);
        } else {
            imgUsuario.setImageResource(R.drawable.gatoinicio);
        }

        Log.d("UserActivity", "Mostrando layout de perfil");
    }

    public void performGoogleLogout() {
        // PRIMERO: Limpiar datos locales
        PreferenceUtils.removeUser();

        // SEGUNDO: Actualizar UI inmediatamente
        showLoginLayout();

        // TERCERO: Cerrar sesión de Google y Firebase en segundo plano
        MyApplication.executor.execute(() -> {
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

                    // Actualizar UI final
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                        // NO navegar automáticamente, dejar al usuario en UserActivity
                        updateUI(); // Refrescar UI
                    });
                });

            } catch (Exception e) {
                Log.e("UserActivity", "Error durante el signOut: " + e.getMessage());

                runOnUiThread(() -> {
                    Toast.makeText(this, "Error cerrando sesión", Toast.LENGTH_SHORT).show();
                    updateUI(); // Refrescar UI aunque haya error
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar UI cada vez que se regresa a esta activity
        updateUI();
        Log.d("UserActivity", "onResume() - UI actualizada");
    }
}