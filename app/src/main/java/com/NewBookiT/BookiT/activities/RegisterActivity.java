package com.NewBookiT.BookiT.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.NewBookiT.BookiT.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // קישור לתצוגה
    private ActivityRegisterBinding binding;

    // התחברות ל- Firebase
    private FirebaseAuth firebaseAuth;

    // חלון התקדמות
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // אתחול התחברות ל- Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // הגדרת חלון התקדמות
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // לטפל בלחיצה ולחזור
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // לטפל בלחיצה ולהתחיל ברישום
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    // משתנים לשמירת נתונים
    private String name = "", email = "", password = "";


    // פונקציה לבדיקת שם תקין
    public boolean isNameValid(String name) {
        return !TextUtils.isEmpty(name);
    }

    // פונקציה לבדיקת אימייל תקין
    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // פונקציה לבדיקת סיסמה תקינה (אורך)
    public boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    // פונקציה לבדיקת התאמת סיסמאות
    public boolean doPasswordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    private void validateData() {
        /* לפני יצירת החשבון, בואו נבצע תקינות נתונים */

        // קבלת הנתונים
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        String cPassword = binding.cPasswordEt.getText().toString().trim();

        // ביצוע תקינות נתונים
        if (TextUtils.isEmpty(name)){
            // אם רשום שם ריק, יש להזין שם
            Toast.makeText(this, "Enter you name...", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // אם כתובת האימייל לא הוזנה או התבנית שלה לא חוקית, אי אפשר להמשיך במקרה כזה
            Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            // אם רשום סיסמה ריקה, יש להזין סיסמה
            Toast.makeText(this, "Enter password...!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cPassword)){
            // אם רשומה אימות סיסמה ריקה, יש להזין אימות סיסמה
            Toast.makeText(this, "Confirm Password...!", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(cPassword)){
            // אם הסיסמה ואימות הסיסמה לא תואמים, אי אפשר להמשיך במקרה כזה, יש לוודא ששתי הסיסמאות תואמות
            Toast.makeText(this, "Password doesn't match...!", Toast.LENGTH_SHORT).show();
        }
        else {
            // כל הנתונים תקינים, יש להתחיל ביצירת החשבון
            createUserAccount();
        }
    }

    private void createUserAccount() {
        // הצגת התקדמות
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        // יצירת משתמש ב- Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // החשבון נוצר בהצלחה, עכשיו יש להוסיף למסד נתונים ריאלי של Firebase
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // יצירת החשבון נכשלה
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user info...");

        // זמן סימן
        long timestamp = System.currentTimeMillis();

        // קבלת המזהה הייחודי של המשתמש הנוכחי, מאחר והמשתמש נרשם כבר כעת אנו יכולים לקבל אותו
        String uid = firebaseAuth.getUid();

        // הגדרת הנתונים להוספה למסד הנתונים
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", "");//הוספת ריק, נעשה זאת מאוחר יותר
        hashMap.put("userType", "user"); //ערכים אפשריים הם user, admin:  ניצור מנהל באופן ידני במסד הנתונים הריאלי של Firebase על ידי שינוי ערך זה
        hashMap.put("timestamp", timestamp);

        // הגדרת הנתונים במסד הנתונים
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // הנתונים נוספו למסד הנתונים
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account created...", Toast.LENGTH_SHORT).show();
                        // מאחר והחשבון של המשתמש נוצר כעת נתחיל במסך הלוח של המשתמש
                        startActivity(new Intent(RegisterActivity.this, DashboardUsermainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // הנתונים לא נוספו למסד הנתונים
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
