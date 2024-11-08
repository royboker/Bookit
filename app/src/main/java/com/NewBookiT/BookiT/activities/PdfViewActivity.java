package com.NewBookiT.BookiT.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.NewBookiT.BookiT.Constants;
import com.NewBookiT.BookiT.databinding.ActivityPdfViewBinding;

public class PdfViewActivity extends AppCompatActivity {

    // קישור ישויות הממשק
    private ActivityPdfViewBinding binding;

    private String bookId;

    private static final String TAG = "PDF_VIEW_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // קבלת ה-ID של הספר מה-Intent שהועבר
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        Log.d(TAG, "onCreate: BookId: " + bookId);

        // טעינת פרטי הספר
        loadBookDetails();

        // טיפול בלחיצה, חזרה אחורה
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Get Pdf URL from db...");
        // יצירת הפנייה ל-Database כדי לקבל את פרטי הספר, למשל לקבל את כתובת ה-URL של הספר באמצעות ה-ID שלו
        // שלב (1) קבלת כתובת ה-URL של הספר באמצעות ה-ID
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // קבלת כתובת ה-URL של הספר
                        String pdfUrl = "" + snapshot.child("url").getValue();
                        Log.d(TAG, "onDataChange: PDF URL: " + pdfUrl);

                        // שלב (2) טעינת ה-PDF באמצעות כתובת ה-URL מאחסון Firebase
                        loadBookFromUrl(pdfUrl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadBookFromUrl(String pdfUrl) {
        Log.d(TAG, "loadBookFromUrl: Get PDF from storage");
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // טעינת ה-PDF באמצעות הבתים שהתקבלו
                        binding.pdfView.fromBytes(bytes)
                                .swipeHorizontal(false) // false לגלילה אנכית, true להחלפת דפים אופקית
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        // עדכון הדף הנוכחי והסך הכולל של הדפים בכותרת המשנה של הכלים
                                        int currentPage = (page + 1); // +1 מכיוון שהדף מתחיל מ-0
                                        binding.toolbarSubtitleTv.setText(currentPage + "/" + pageCount); // לדוגמה, 3/290
                                        Log.d(TAG, "onPageChanged: " + currentPage + "/" + pageCount);
                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        // טיפול בשגיאה בעת טעינת ה-PDF
                                        Log.d(TAG, "onError: " + t.getMessage());
                                        Toast.makeText(PdfViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        // טיפול בשגיאה בדף ספציפי
                                        Log.d(TAG, "onPageError: " + t.getMessage());
                                        Toast.makeText(PdfViewActivity.this, "Error on page " + page + " " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .load();

                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // כשלון בטעינת הספר
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
