package com.example.animor.Utils;

import android.app.Activity;
import android.content.Intent;

import com.example.animor.Model.dto.UserDTO;
import com.example.animor.R;
import com.example.animor.UI.CreateActivity;
import com.example.animor.UI.FavActivity;
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
        // Marcar el item actual como seleccionado
        markCurrentItem(bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

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
    }

    private boolean handleHomeNavigation() {
        // Si ya estamos en HOME, no navegar
        if (currentActivityType == ActivityType.HOME) {
            return true;
        }

        // Navegar a la activity de inicio
        // Asume que tienes una MainActivity o similar
        // Intent intent = new Intent(activity, MainActivity.class);
        // activity.startActivity(intent);

        // Por ahora, solo retornar true si no tienes activity principal definida
        return true;
    }

    private boolean handleFavoritesNavigation() {
        // Si ya estamos en FAVORITES, no navegar
        if (currentActivityType == ActivityType.FAVORITES) {
            return true;
        }

        // Verificar si el usuario está logueado
        if (isUserLogged()) {
            navigateToLogin();
            return true;
        }

        // Navegar a FavActivity
        Intent intent = new Intent(activity, FavActivity.class);
        activity.startActivity(intent);
        return true;
    }

    private boolean handleListingNavigation() {
        // Si ya estamos en CREATE, no navegar
        if (currentActivityType == ActivityType.CREATE) {
            return true;
        }

        // Verificar si el usuario está logueado
        if (isUserLogged()) {
            navigateToLogin();
            return true;
        }

        // Navegar a CreateActivity
        Intent intent = new Intent(activity, CreateActivity.class);
        activity.startActivity(intent);
        return true;
    }

    private boolean handleAnimalsNavigation() {
        // Si ya estamos en SHOW, no navegar
        if (currentActivityType == ActivityType.SHOW) {
            return true;
        }

        // Verificar si el usuario está logueado
        if (isUserLogged()) {
            navigateToLogin();
            return true;
        }

        // Navegar a ShowActivity
        Intent intent = new Intent(activity, ShowActivity.class);
        activity.startActivity(intent);
        return true;
    }

    private boolean handleUserNavigation() {
        // Si ya estamos en USER, no navegar
        if (currentActivityType == ActivityType.USER) {
            return true;
        }

        // Navegar a UserActivity (siempre permitido)
        Intent intent = new Intent(activity, UserActivity.class);
        activity.startActivity(intent);
        return true;
    }

    /**
     * Verifica si el usuario está logueado
     * @return true si está logueado, false si no
     */
    private boolean isUserLogged() {
        UserDTO user = PreferenceUtils.getUser();
        return user != null;
    }

    /**
     * Navega a la pantalla de login/user
     */
    private void navigateToLogin() {
        Intent intent = new Intent(activity, UserActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Método estático para crear el helper fácilmente
     */
    public static NavigationHelper create(Activity activity, ActivityType activityType) {
        return new NavigationHelper(activity, activityType);
    }
}