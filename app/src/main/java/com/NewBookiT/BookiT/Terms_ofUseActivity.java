package com.NewBookiT.BookiT;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.NewBookiT.BookiT.databinding.ActivityTermsOfUseBinding;

public class Terms_ofUseActivity extends AppCompatActivity {

    private ActivityTermsOfUseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsOfUseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // הוספת כותרת לפעילות תנאי השימוש
        setTitle("תנאי שימוש");

        // הגדרת התוכן של תנאי השימוש
        String termsOfUseContent = "ברוך הבא לאפליקצייתנו! בהמשך לשימוש באפליקציה, המשתמש מסכים לתנאים הבאים:\n" +
                "\n" +
                "1. השימוש באפליקציה כפוף לתנאי השימוש ולמדיניות הפרטיות שלנו. המשתמש חייב לקרוא ולהבין את התנאים והמדיניות לפני שימושו באפליקציה.\n" +
                "\n" +
                "2. המשתמש מאשר כי הוא מגיש את פרטיו בהתאם למדיניות הפרטיות שלנו.\n" +
                "\n" +
                "3. המשתמש לא יעשה שימוש לא חוקי או לא מותר באפליקציה.\n" +
                "\n" +
                "4. המשתמש אחראי לכל הפעילות שביצע באפליקציה באמצעות חשבונו.\n" +
                "\n" +
                "5. אין להעתיק, לשכפל, לשדרג, למכור, להעביר או לשכור את האפליקציה או כל חלק ממנה ללא אישור מראש מאיתנו.\n" +
                "\n" +
                "6. המשתמש מסכים שאנו רשאים להשעות או לבטל את הגישה שלו לאפליקציה בכל עת, ללא התראה מראש או כל סיבה מוצדקת.\n" +
                "\n" +
                "7. אין להשתמש באפליקציה באופן שישפיע באופן שלילי על פעילות האפליקציה או על המשתמשים האחרים.\n" +
                "\n" +
                "8. כל הפעולות של המשתמש באפליקציה יהיו תחת אחריותו הבלעדית.\n" +
                "\n" +
                "תודה שבחרתם להשתמש באפליקצייתנו!" ;
        binding.termsOfUseContent.setText(termsOfUseContent);

        // הוספת האזנה לכפתור חזרה
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
