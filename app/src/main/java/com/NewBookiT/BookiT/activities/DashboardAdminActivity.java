package com.NewBookiT.BookiT.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.NewBookiT.BookiT.adapters.AdapterCategory;
import com.NewBookiT.BookiT.databinding.ActivityDashboardAdminBinding;
import com.technifysoft.BookiT.models.ModelCategory;

import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {

    // קישורים לממשק המשתמש
    private ActivityDashboardAdminBinding binding;

    // מופע לניהול התחברות Firebase
    private FirebaseAuth firebaseAuth;

    // רשימה לשמירת הקטגוריות
    private ArrayList<ModelCategory> categoryArrayList;
    // מתאם
    private AdapterCategory adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // אתחול Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategories();


        // מאזין לשינויים בתיבת הטקסט, חיפוש
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // מתבצע כאשר המשתמש מקליד כל אות
                try {
                    adapterCategory.getFilter().filter(s);
                }
                catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // טיפול בלחיצה, התנתקות
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        // טיפול בלחיצה, מעבר למסך הוספת קטגוריה
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this, CategoryAddActivity.class));
            }
        });

        // טיפול בלחיצה, מעבר למסך הוספת PDF
        binding.addPdfFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this, PdfAddActivity.class));
            }
        });

        // טיפול בלחיצה, פתיחת פרופיל
        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAdminActivity.this, ProfileActivity.class));
            }
        });
    }

    public static boolean isEmailValid(String email) {
        return email != null && !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password != null && !password.isEmpty() && password.length() >= 6;
    }


    private void loadCategories() {
        // אתחול רשימת הקטגוריות
        categoryArrayList = new ArrayList<>();

        // קבלת כל הקטגוריות מ-Firebase > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ניקוי הרשימה לפני הוספת נתונים לתוכה
                categoryArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    // קבלת הנתונים
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    // הוספה לרשימה
                    categoryArrayList.add(model);
                }
                // הגדרת המתאם
                adapterCategory = new AdapterCategory(DashboardAdminActivity.this, categoryArrayList);
                // הגדרת המתאם ל-recyclerview
                binding.categoriesRv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        // קבלת המשתמש הנוכחי
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser==null){
            // לא מחובר, מעבר למסך הראשי
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else {
            // מחובר, קבלת פרטי המשתמש
            String email = firebaseUser.getEmail();
            // הצגה ב-textview של סרגל הכלים
            binding.subTitleTv.setText(email);
        }
    }
}
