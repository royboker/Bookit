package com.NewBookiT.BookiT.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.NewBookiT.BookiT.R;
import com.NewBookiT.BookiT.databinding.ActivityHomescreenBinding;
import com.NewBookiT.BookiT.databinding.ActivityLoginBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    ImageSlider imageSlider;
    View view;

    // משתנה להצגת השעה
    private TextView textViewTime;

    // מטפל בזמנים
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // מגדיר את הטקסט להיות השעה הנוכחית
            String currentTime = "היי, הספריה תיהיה סגורה היום!  " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            // מוסיף שעות פתיחה של הספריה
            String openingHours = "שעות פתיחה: א-ה 08:00-20:00, ו' 08:00-14:00";

            // מוסיף מלל של דף הבית
            String homepageText = "ברוכים הבאים לספריה המרכזית שלנו! אנו מציעים מגוון רחב של ספרים ושירותים לקהל הרחב. תן לנו לשרת אותך בכיף!";

            // מחבר את הטקסטים לטקסט יחיד
            String finalText = currentTime + "\n\n" + openingHours + "\n\n" + homepageText;

            // מקבל את אורך הטקסט של השעות הפתיחה ושל מלל דף הבית
            int openingHoursLength = openingHours.length();
            int homepageTextLength = homepageText.length();

            // מחלק את הטקסט לחלקים
            String firstPart = finalText.substring(0, finalText.length());
            String secondPart = homepageText;

            // מחבר את החלקים כדי ליצור את הטקסט הסופי
            String finalTextWithAlignment = firstPart ;

            textViewTime.setText(finalTextWithAlignment);

            // מתזמן לעדכון שוב בעוד שנייה
            handler.postDelayed(this, 1000);
        }
    };




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // טוען את הממשק
        view = inflater.inflate(R.layout.recycler_item, container, false);
        imageSlider = (ImageSlider)view.findViewById(R.id.image_slider);
        loadImages();
        textViewTime = view.findViewById(R.id.textViewToUpdate);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // מתחיל בפעולת עדכון השעה
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // עצירת פעולת העדכון כאשר הפרגמנט נהרס
        handler.removeCallbacks(runnable);
    }

    private void loadImages() {
        final List<SlideModel> remoteimages = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String imageUrl = null;
                            if (data.child("image").getValue() != null) {
                                imageUrl = data.child("image").getValue().toString().replace("url:", "");
                            }
                            String pdfUrl = data.child("url").getValue().toString();
                            String title = data.child("title").getValue().toString();

                            // Check if imageUrl is null before using it
                            if (imageUrl != null) {
                                remoteimages.add(new SlideModel(imageUrl, title, ScaleTypes.FIT));
                            }
                        }
                        imageSlider.setImageList(remoteimages, ScaleTypes.FIT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // טיפול בשגיאות אפשריות
                    }
                });
    }

}
