package com.NewBookiT.BookiT.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import com.NewBookiT.BookiT.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // קישור ישיר לאלמנטים בממשק המשתמש
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // הגדרת התצוגה של הממשק המשתמש דרך ה-view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // טיפול בלחיצה על כפתור הכניסה, פתיחת מסך הכניסה
        binding.loginBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));

        // טיפול בלחיצה על הטקסט להרשמה, פתיחת מסך ההרשמה
        binding.regTV2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }
}
