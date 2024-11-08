package com.NewBookiT.BookiT.activities;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.NewBookiT.BookiT.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends AppCompatActivity {

    // משתנה לסליידר תמונות
    ImageSlider imageSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_item);
        // הגדרת המסך למצב מלא
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // קישור הסליידר לקוד עם ה-ID
        imageSlider = findViewById(R.id.image_slider);
        final List<SlideModel> remoteimages = new ArrayList<>();

        // קריאה למסד נתונים של Firebase לקבלת התמונות
        FirebaseDatabase.getInstance().getReference().child("Books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren())
                            // הוספת תמונות לסליידר מתוך הנתונים שהתקבלו
                            remoteimages.add(new SlideModel(data.child("url").getValue(String.class), data.child("title").getValue(String.class), ScaleTypes.FIT));

                        // הצגת התמונות בסליידר
                        imageSlider.setImageList(remoteimages, ScaleTypes.FIT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // טיפול בשגיאות אפשריות בעת קריאה למסד נתונים
                    }
                });
    }
}
