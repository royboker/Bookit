package com.NewBookiT.BookiT.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.NewBookiT.BookiT.R;
import com.NewBookiT.BookiT.Terms_ofUseActivity;

public class DashboardLibActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    // רכיבי ממשק המשתמש והאותנטיקציה של Firebase
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_lib);

        // אתחול Firebase Auth ורכיבי הממשק
        firebaseAuth = FirebaseAuth.getInstance();
        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackground(null);

        // טיפול בבחירות מהתפריט התחתון
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.bottom_exit:
                        firebaseAuth.signOut();
                        checkUser();
                        break;
                    case R.id.bottom_short:
                        startActivity(new Intent(DashboardLibActivity.this, Terms_ofUseActivity.class));
                        break;
                    case R.id.bottom_subscription:
                        startActivity(new Intent(DashboardLibActivity.this, dashboard_userActivity.class));
                        break;
                    case R.id.bottom_home:
                        startActivity(new Intent(DashboardLibActivity.this, DashboardUsermainActivity.class));
                        break;
                }
                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();
        openFragment(new HomeFragment());

        // כפתור פעולה מהירה לפתיחת הפרופיל
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardLibActivity.this, ProfileActivity.class));
                Toast.makeText(DashboardLibActivity.this, "Profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // בדיקת מצב המשתמש
    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    // טיפול בבחירות מתפריט הניווט
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.nav_addBook:
                startActivity(new Intent(DashboardLibActivity.this, PdfAddActivity.class));
                break;
            case R.id.nav_LibNav1:
                startActivity(new Intent(DashboardLibActivity.this, dashboard_userActivity.class));
                break;
            case R.id.nav_LibNav2:
                startActivity(new Intent(DashboardLibActivity.this, Terms_ofUseActivity.class));
            case R.id.nav_LibNav3:
                startActivity(new Intent(DashboardLibActivity.this, CategoryAddActivity.class));
                break;
            case R.id.nav_LibNav4:
                startActivity(new Intent(DashboardLibActivity.this, DashboardAdminActivity.class));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // טיפול בלחיצה על כפתור החזור
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // פתיחת פרגמנט במסך
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
