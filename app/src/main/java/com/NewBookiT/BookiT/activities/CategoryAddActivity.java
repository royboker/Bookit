package com.NewBookiT.BookiT.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.NewBookiT.BookiT.databinding.ActivityCategoryAddBinding;

import java.util.HashMap;

public class CategoryAddActivity extends AppCompatActivity {

    // קישורים לספרייה של ממשק המשתמש
    private ActivityCategoryAddBinding binding;

    // מופע של ניהול הזדהות של Firebase
    private FirebaseAuth firebaseAuth;

    // דיאלוג התקדמות להצגה במהלך פעולות ארוכות
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // יצירת קשר בין המחלקה לפריטי הממשק
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // אתחול Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // קונפיגורציה של דיאלוג התקדמות
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // טיפול בלחיצה, חזרה למסך הקודם
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // טיפול בלחיצה, התחלת העלאת קטגוריה
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    // unit test
    // פונקציה לבדיקת קטגוריה תקינה
    public boolean isCategoryValid(String category) {
        return !TextUtils.isEmpty(category); // בודק אם השם אינו ריק
    }

    // משתנה לשמירת שם הקטגוריה
    private String category = "";

    private void validateData() {
        /* לפני הוספה יש לוודא את הנתונים */

        // קבלת הנתונים
        category = binding.categoryEt.getText().toString().trim();
        // אימות שהשדה אינו ריק
        if (TextUtils.isEmpty(category)){
            Toast.makeText(this, "נא להכניס לקטגוריה...!", Toast.LENGTH_SHORT).show();
        }
        else {
            addCategoryFirebase();
        }
    }

    private void addCategoryFirebase() {
        // הצגת הודעת התקדמות
        progressDialog.setMessage("מוסיף קטגוריה...");
        progressDialog.show();

        // קבלת חותמת זמן
        long timestamp = System.currentTimeMillis();

        // הכנה של המידע להוספה במסד נתונים של Firebase
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+timestamp);
        hashMap.put("category", ""+category);
        hashMap.put("timestamp", timestamp);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        // הוספה למסד הנתונים של Firebase... Root Database > Categories > categoryId > category info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // הוספת הקטגוריה הסתיימה בהצלחה
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this, "הקטגוריה נוספה בהצלחה...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // הוספת הקטגוריה נכשלה
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}