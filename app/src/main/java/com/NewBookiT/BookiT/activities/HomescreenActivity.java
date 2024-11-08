package com.NewBookiT.BookiT.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.NewBookiT.BookiT.R;
import com.NewBookiT.BookiT.Terms_ofUseActivity;


public class HomescreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // הגדרת משתנים לרכיבי הממשק
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;
    TextView textViewToUpdateS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);


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



        // טיפול בבחירה מתוך התפריט התחתון
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_exit:
                        firebaseAuth.signOut();
                        checkUser();
                        break;
                    case R.id.bottom_short:
                        startActivity(new Intent(HomescreenActivity.this, Terms_ofUseActivity.class));
                        break;
                    case R.id.bottom_subscription:
                        startActivity(new Intent(HomescreenActivity.this, dashboard_userActivity.class));
                        break;
                    case R.id.bottom_home:
                        startActivity(new Intent(HomescreenActivity.this, HomescreenActivity.class));
                        break;
                }
                return false;
            }
        });




        fragmentManager = getSupportFragmentManager();
        openFragment(new HomeFragment());

        // כפתור פעולה מהירה לפרופיל
        fab.setOnClickListener(v -> {
            startActivity(new Intent(HomescreenActivity.this, ProfileActivity.class));
            Toast.makeText(HomescreenActivity.this, "Profile", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_adminNav:
                startActivity(new Intent(HomescreenActivity.this, DashboardAdminActivity.class));
                break;
            case R.id.nav_adminNav1:
                startActivity(new Intent(HomescreenActivity.this, CategoryAddActivity.class));
                break;
            case R.id.nav_adminNav2:
                startActivity(new Intent(HomescreenActivity.this, PdfAddActivity.class));
                break;
            case R.id.nav_adminNav3:
                startActivity(new Intent(HomescreenActivity.this, Terms_ofUseActivity.class));
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}