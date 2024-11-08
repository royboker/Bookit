package com.NewBookiT.BookiT.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.NewBookiT.BookiT.adapters.AdapterPdfAdmin;
import com.NewBookiT.BookiT.databinding.ActivityPdfListAdminBinding;
import com.technifysoft.BookiT.models.ModelPdf;

import java.util.ArrayList;

public class PdfListAdminActivity extends AppCompatActivity {

    // הגדרת ViewBinding
    private ActivityPdfListAdminBinding binding;

    // ArrayList לשמירת רשימת נתונים מסוג ModelPdf
    private ArrayList<ModelPdf> pdfArrayList;
    // הגדרת המתאם (Adapter)
    private AdapterPdfAdmin adapterPdfAdmin;

    private String categoryId, categoryTitle;

    private static final String TAG = "PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // טעינת ה-view באמצעות ViewBinding
        binding = ActivityPdfListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // קבלת הנתונים מה-Intent
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        // הגדרת כותרת הקטגוריה של ה-PDF
        binding.subTitleTv.setText(categoryTitle);

        // טעינת רשימת ה-PDF
        loadPdfList();

        // חיפוש
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // חיפוש בזמן אמת עם כל הקלדה של המשתמש
                try {
                    adapterPdfAdmin.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // טיפול בלחיצה, חזרה לפעילות הקודמת
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    // טעינת רשימת ה-PDF
    private void loadPdfList() {
        // אתחול הרשימה לפני הוספת נתונים
        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        // שאילתא ל-Firebase לפי categoryId
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        pdfArrayList.clear(); // ניקוי הרשימה לפני הוספת נתונים חדשים
                        for (DataSnapshot ds: snapshot.getChildren()){
                            // קבלת הנתונים
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            // הוספת הנתונים לרשימה
                            pdfArrayList.add(model);

                            Log.d(TAG, "onDataChange: "+model.getId()+" "+model.getTitle());
                        }
                        // הגדרת המתאם עם הנתונים החדשים
                        adapterPdfAdmin = new AdapterPdfAdmin(PdfListAdminActivity.this, pdfArrayList);
                        // חיבור המתאם ל-RecyclerView
                        binding.bookRv.setAdapter(adapterPdfAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {
                        // טיפול בשגיאה
                    }
                });
    }

    public boolean isSearchQueryValid(String query) {
        return query != null && !query.isEmpty();
    }

    public boolean isPdfListLoaded(ArrayList<ModelPdf> pdfList) {
        return pdfList != null && !pdfList.isEmpty();
    }


}