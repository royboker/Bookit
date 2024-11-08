package com.NewBookiT.BookiT.activities;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.NewBookiT.BookiT.R;
import com.NewBookiT.BookiT.Terms_ofUseActivity;

public class DashboardUsermainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    // הגדרת משתנים לרכיבי הממשק ואותנטיקציה
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_usermain);

        // אתחול Firebase Auth וקישור רכיבי הממשק
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

        // טיפול באירועים של התפריט התחתון
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_exit:
                        firebaseAuth.signOut();
                        checkUser();
                        break;
                    case R.id.bottom_short:
                        startActivity(new Intent(DashboardUsermainActivity.this, Terms_ofUseActivity.class));
                        break;
                    case R.id.bottom_subscription:
                        startActivity(new Intent(DashboardUsermainActivity.this, dashboard_userActivity.class));
                        break;
                    case R.id.bottom_home:
                        startActivity(new Intent(DashboardUsermainActivity.this, DashboardUsermainActivity.class));
                        finish();
                        break;
                }
                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();
        openFragment(new HomeFragment());

        // כפתור פעולה מהירה לפרופיל
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardUsermainActivity.this, ProfileActivity.class));
                Toast.makeText(DashboardUsermainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
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

    // טיפול באירועים של תפריט הניווט
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_trending:
                startActivity(new Intent(DashboardUsermainActivity.this, Terms_ofUseActivity.class));
                break;
            case R.id.nav_searchBook:
//                Toast.makeText(this, "חיפוש ספרים", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardUsermainActivity.this, dashboard_userActivity.class));
                break;
            case R.id.nav_Reporting:
                    reportProblem();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void reportProblem() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "אתה לא מחובר למשתמש", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userEmail = snapshot.child("email").getValue(String.class);
                    String username = snapshot.child("name").getValue(String.class);
                // נושא ההודעה
                String subject =  "דיווח תקלות ";
                String message =" שלום " + username + "\n"+ "איזה תקלה נתקלת באפלקציה?";
                sendEmail("noamkadosh4444@gmail.com", subject, message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendEmail(String recipientEmail, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { recipientEmail });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
    }




    // טיפול בלחיצת חזור
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
