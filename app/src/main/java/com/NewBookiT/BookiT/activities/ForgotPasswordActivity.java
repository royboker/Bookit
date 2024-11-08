package com.NewBookiT.BookiT.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.NewBookiT.BookiT.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    // קישורים לממשק המשתמש
    private ActivityForgotPasswordBinding binding;

    // אותנטיקציה של Firebase
    private FirebaseAuth firebaseAuth;

    // דיאלוג התקדמות
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // אתחול Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // הגדרה ואתחול של דיאלוג התקדמות
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("המתן בבקשה");
        progressDialog.setCanceledOnTouchOutside(false);

        // טיפול בלחיצה, חזרה אחורה
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // טיפול בלחיצה, התחלת שחזור סיסמה
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }
    // פונקציה לבדיקת אימייל תקין
    // הפכנו אותה לpublic כדי לאפשר בדיקה
    public boolean isEmailValid(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    // פונקציה לבדיקת תקינות סיסמה
    public boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    // פונקציה לשליחת הודעת איפוס סיסמה
    // מוגדרת כpublic כדי לאפשר Mocking ובדיקה
    public void sendPasswordResetEmail(String email) {
        // הגדרת התקדמות ושליחת אימייל
        progressDialog.setMessage("שולח הוראות לשחזור סיסמה " + email);
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this, "הוראות לאיפוס סיסמה נשלחו אליך " + email, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this, "השליחה נכשלה עקב " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private String email = "";
    private void validateData() {
        // קבלת הנתונים, כלומר האימייל
        email = binding.emailEt.getText().toString().trim();

        // אימות הנתונים, צריך להיות מלא ובפורמט חוקי
        if (email.isEmpty()){
            Toast.makeText(this, "הזן אימייל...", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "פורמט דוא\"ל לא חוקי...", Toast.LENGTH_SHORT).show();
        }
        else {
            recoverPassword();
        }
    }

    private void recoverPassword() {
        // הצגת התקדמות
        progressDialog.setMessage("שולח הוראות לשחזור סיסמה  " + email);
        progressDialog.show();

        // התחלת השחזור
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // הודעת הצלחה
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "הוראות לאיפוס סיסמה נשלחו אליך " + email, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // הודעת כישלון
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "השליחה נכשלה עקב " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
