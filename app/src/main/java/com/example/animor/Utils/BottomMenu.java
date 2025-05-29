package com.example.animor.Utils;

import android.app.Activity;
import android.content.Intent;

import com.example.animor.R;
import com.example.animor.UI.FavActivity;
import com.example.animor.UI.ShowActivity;
import com.example.animor.UI.UserActivity;
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
                } else if (id == R.id.nav_listing) {
                    activity.startActivity(new Intent(activity, RegistryActivity.class));
                    return true;
                } else if (id == R.id.nav_user) {
                    activity.startActivity(new Intent(activity, UserActivity.class));
                    return true;
                } else if (id == R.id.nav_animals) {
                    activity.startActivity(new Intent(activity, ShowActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

}
