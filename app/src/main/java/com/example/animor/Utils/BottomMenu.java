package com.example.animor.Utils;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.animor.R;
import com.example.animor.UI.FavActivity;
import com.example.animor.UI.InicioActivity;
import com.example.animor.UI.MyAnimalsActivity;
import com.example.animor.UI.ProfileActivity;
import com.example.animor.UI.RegistryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomMenu {
    public class NavigationHelper {
        public void setupBottomNavigation(BottomNavigationView bottomNavigationView, Activity activity) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    return true;
                } else if (id == R.id.nav_favs) {
                    activity.startActivity(new Intent(activity, FavActivity.class));
                    return true;
                } else if (id == R.id.registrar) {
                    activity.startActivity(new Intent(activity, RegistryActivity.class));
                    return true;
                } else if (id == R.id.nav_user) {
                    activity.startActivity(new Intent(activity, ProfileActivity.class));
                    return true;
                } else if (id == R.id.nav_animals) {
                    activity.startActivity(new Intent(activity, MyAnimalsActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

}
