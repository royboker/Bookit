package com.NewBookiT.BookiT.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.NewBookiT.BookiT.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    // קישור ישיר לאלמנטים בממשק המשתמש
    private ActivityLoginBinding binding;

    // אותנטיקציה של Firebase
    private FirebaseAuth firebaseAuth;

    // דיאלוג התקדמות
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // אתחול אותנטיקציה של Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // הגדרה ואתחול של דיאלוג התקדמות
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // טיפול בלחיצה, מעבר למסך הרשמה
        binding.noAccountTv.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        // טיפול בלחיצה, התחלת תהליך הכניסה
        binding.loginBtn.setOnClickListener(v -> validateData());

        // טיפול בלחיצה, מעבר למסך שחזור סיסמה
        binding.forgotTv.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }

    // פונקציה לבדיקת אימייל תקין
    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // פונקציה לבדיקת סיסמה תקינה
    public boolean isPasswordValid(String password) {
        return password.length() >= 6; // לדוגמה: בדיקת אורך מינימלי
    }

    private String email = "", password = "";

    private void validateData() {
        // קבלת הנתונים
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();

        // אימות הנתונים
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password...!", Toast.LENGTH_SHORT).show();
        }
        else {
            // הנתונים מאומתים, התחלת תהליך הכניסה
            loginUser();
        }
    }

    private void loginUser() {
        // הצגת התקדמות
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        // כניסה למשתמש
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> checkUser())
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkUser() {
        progressDialog.setMessage("Checking User...");

        // בדיקה אם המשתמש הוא משתמש או מנהל
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // בדיקה במסד הנתונים
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        // קבלת סוג המשתמש
                        String userType = ""+snapshot.child("userType").getValue();
                        // בדיקת סוג המשתמש ומעבר למסך המתאים
                        switch (userType) {
                            case "user":
                                startActivity(new Intent(LoginActivity.this, DashboardUsermainActivity.class));
                                finish();
                                break;
                            case "admin":
                                startActivity(new Intent(LoginActivity.this, HomescreenActivity.class));
                                finish();
                                break;
                            case "lib":
                                startActivity(new Intent(LoginActivity.this, DashboardLibActivity.class));
                                finish();
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }
}
