package com.NewBookiT.BookiT.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.NewBookiT.BookiT.R;

public class ErrorActivity extends AppCompatActivity {

    // כפתור לדיווח על בעיה
    Button btnReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // הפיכת האפליקציה למתאימה לשולי המסך
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_error);

        // קישור הכפתור מהממשק למשתנה
        btnReport = findViewById(R.id.sendReportButton);

        // הגדרת מאזין לכפתור
        btnReport.setOnClickListener(view -> {
            // פעולה שתתבצע בלחיצה - דיווח על בעיה
            reportProblem();
        });
    }

    // unit test
    // פונקציית עזר לבדיקת תקינות כתובת אימייל
    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // פונקציה לדיווח על בעיות
    private void reportProblem() {
        // כתובת האימייל לדיווח
        String email = "Gal-ta@windowslive.com";
        // נושא ההודעה
        String subject = "Reporting for app " + getString(R.string.app_name);

        // יצירת כוונה עם הפרטים הנדרשים לשליחת אימייל
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // רק יישומי אימייל יכולים לטפל בכוונה זו
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email}); // הוספת כתובת האימייל
        intent.putExtra(Intent.EXTRA_SUBJECT, subject); // הוספת הנושא
        intent.putExtra(Intent.EXTRA_TEXT, ""); // הודעה ריקה

        // הצגת בורר אימייל למשתמש
        startActivity(Intent.createChooser(intent, "Choose an email client:"));
    }
}
