package com.example.animor.Utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.animor.Model.dto.UserDTO;
import com.example.animor.R;
import com.example.animor.UI.CreateActivity;
import com.example.animor.UI.FavActivity;
import com.example.animor.UI.InicioActivity;
import com.example.animor.UI.ShowActivity;
import com.example.animor.UI.UserActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHelper {

    // Enum para identificar las activities
    public enum ActivityType {
        HOME,           // Activity principal/inicio
        FAVORITES,      // FavActivity
        CREATE,         // CreateActivity (listings)
        SHOW,          // ShowActivity (animals)
        USER           // UserActivity
    }

    private Activity activity;
    private ActivityType currentActivityType;

    public NavigationHelper(Activity activity, ActivityType currentActivityType) {
        this.activity = activity;
        this.currentActivityType = currentActivityType;
    }

    /**
     * Configura la navegación del BottomNavigationView
     * @param bottomNavigationView El BottomNavigationView a configurar
     */
    public void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        if (bottomNavigationView == null) {
            Log.e("NavigationHelper", "BottomNavigationView es null");
            return;
        }

        // Marcar el item actual como seleccionado
        markCurrentItem(bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            Log.d("NavigationHelper", "Item seleccionado: " + id + ", Activity actual: " + currentActivityType);

            if (id == R.id.nav_inicio) {
                return handleHomeNavigation();
            } else if (id == R.id.nav_favs) {
                return handleFavoritesNavigation();
            } else if (id == R.id.nav_listing) {
                return handleListingNavigation();
            } else if (id == R.id.nav_animals) {
                return handleAnimalsNavigation();
            } else if (id == R.id.nav_user) {
                return handleUserNavigation();
            }
            return false;
        });
    }

    /**
     * Marca el item actual como seleccionado en el bottom navigation
     */
    private void markCurrentItem(BottomNavigationView bottomNavigationView) {
        try {
            switch (currentActivityType) {
                case HOME:
                    bottomNavigationView.setSelectedItemId(R.id.nav_inicio);
                    break;
                case FAVORITES:
                    bottomNavigationView.setSelectedItemId(R.id.nav_favs);
                    break;
                case CREATE:
                    bottomNavigationView.setSelectedItemId(R.id.nav_listing);
                    break;
                case SHOW:
                    bottomNavigationView.setSelectedItemId(R.id.nav_animals);
                    break;
                case USER:
                    bottomNavigationView.setSelectedItemId(R.id.nav_user);
                    break;
            }
        } catch (Exception e) {
            Log.e("NavigationHelper", "Error marcando item actual", e);
        }
    }

    private boolean handleHomeNavigation() {
        Log.d("NavigationHelper", "handleHomeNavigation - currentType: " + currentActivityType);

        // Si ya estamos en HOME, no navegar
        if (currentActivityType == ActivityType.HOME) {
            return true;
        }
        try {
            Intent intent = new Intent(activity, InicioActivity.class);
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e("NavigationHelper", "Error navegando a FavActivity", e);
            return false;

        }
    }

    private boolean handleFavoritesNavigation() {
        Log.d("NavigationHelper", "handleFavoritesNavigation - currentType: " + currentActivityType);

        // Si ya estamos en FAVORITES, no navegar
        if (currentActivityType == ActivityType.FAVORITES) {
            return true;
        }

        // Verificar si el usuario está logueado
        if (!isUserLogged()) {
            navigateToLogin();
            return true;
        }

        // Navegar a FavActivity
        try {
            Intent intent = new Intent(activity, FavActivity.class);
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e("NavigationHelper", "Error navegando a FavActivity", e);
            return false;
        }
    }

    private boolean handleListingNavigation() {
        Log.d("NavigationHelper", "handleListingNavigation - currentType: " + currentActivityType);

        // Si ya estamos en CREATE, no navegar
        if (currentActivityType == ActivityType.CREATE) {
            return true;
        }

        // Verificar si el usuario está logueado
        if (!isUserLogged()) {
            navigateToLogin();
            return true;
        }

        // Navegar a CreateActivity
        try {
            Intent intent = new Intent(activity, CreateActivity.class);
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e("NavigationHelper", "Error navegando a CreateActivity", e);
            return false;
        }
    }

    private boolean handleAnimalsNavigation() {
        Log.d("NavigationHelper", "handleAnimalsNavigation - currentType: " + currentActivityType);

        // Si ya estamos en SHOW, no navegar
        if (currentActivityType == ActivityType.SHOW) {
            return true;
        }

        // Verificar si el usuario está logueado
        if (!isUserLogged()) {
            navigateToLogin();
            return true;
        }

        // Navegar a ShowActivity
        try {
            Intent intent = new Intent(activity, ShowActivity.class);
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e("NavigationHelper", "Error navegando a ShowActivity", e);
            return false;
        }
    }

    private boolean handleUserNavigation() {
        Log.d("NavigationHelper", "handleUserNavigation - currentType: " + currentActivityType);

        // Si ya estamos en USER, no navegar
        if (currentActivityType == ActivityType.USER) {
            return true;
        }

        // Navegar a UserActivity (siempre permitido)
        try {
            Intent intent = new Intent(activity, UserActivity.class);
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e("NavigationHelper", "Error navegando a UserActivity", e);
            return false;
        }
    }

    /**
     * Verifica si el usuario está logueado
     * @return true si está logueado, false si no
     */
    private boolean isUserLogged() {
        try {
            UserDTO user = PreferenceUtils.getUser();

            if (user == null) {
                Log.d("NavigationHelper", "Usuario es null - NO logueado");
                return false;
            }

            // Verificar que tenga datos básicos
            boolean isLogged = user.getUserName() != null && !user.getUserName().isEmpty();

            Log.d("NavigationHelper", "Usuario: " + user.getUserName() + " - Logueado: " + isLogged);
            return isLogged;

        } catch (Exception e) {
            Log.e("NavigationHelper", "Error verificando usuario logueado", e);
            return false;
        }
    }

    /**
     * Navega a la pantalla de login/user
     */
    private void navigateToLogin() {
        Log.d("NavigationHelper", "Navegando a UserActivity (no logueado)");
        try {
            Intent intent = new Intent(activity, UserActivity.class);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e("NavigationHelper", "Error navegando a login", e);
        }
    }

    /**
     * Método estático para crear el helper fácilmente
     */
    public static NavigationHelper create(Activity activity, ActivityType activityType) {
        return new NavigationHelper(activity, activityType);
    }
}