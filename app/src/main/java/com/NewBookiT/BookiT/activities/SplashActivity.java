package com.NewBookiT.BookiT.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.NewBookiT.BookiT.R;

public class SplashActivity extends AppCompatActivity {

    // אימות Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // אתחול אימות Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // התחל מסך ראשי אחרי 2 שניות
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        },2000); // 2000 משמעותו 2 שניות
    }

    private void checkUser() {
        // לקבל את המשתמש הנוכחי, אם מחובר
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            // משתמש לא מחובר
            // התחל מסך ראשי
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
             // סיום פעילות זו
        }
        else {
            // המשתמש מחובר - לבדוק את סוג המשתמש, כמו שנעשה במסך התחברות
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            // לקבל את סוג המשתמש
                            String userType = ""+snapshot.child("userType").getValue();
                            // לבדוק את סוג המשתמש
                            if (userType.equals("user")){
                                // זהו משתמש רגיל, לפתוח לוח המחוונים של המשתמש
                                startActivity(new Intent(SplashActivity.this, DashboardUsermainActivity.class));
                            }
                            else if (userType.equals("admin")){
                                // זהו מנהל, לפתוח לוח המחוונים של המנהל
                                startActivity(new Intent(SplashActivity.this, HomescreenActivity.class));
                            }
                            else if (userType.equals("lib")){
                                // זהו ספרן, לפתוח לוח המחוונים של הספרן
                                startActivity(new Intent(SplashActivity.this, DashboardLibActivity.class));
                            }
                            else{
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });

        }
    }
}
