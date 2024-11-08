package com.NewBookiT.BookiT.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.NewBookiT.BookiT.databinding.ActivityPdfAddBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {

    // קישור ישיר לאלמנטים בממשק המשתמש
    private ActivityPdfAddBinding binding;

    // אותנטיקציה של Firebase
    private FirebaseAuth firebaseAuth;

    // דיאלוג התקדמות
    private ProgressDialog progressDialog;

    // ArrayList לשמירת קטגוריות ה-PDF
    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;

    // URI של ה-PDF שנבחר
    private Uri pdfUri = null;

    // קוד בחירת PDF
    private static final int PDF_PICK_CODE = 1000;

    // תגית לדיבאגינג
    private static final String TAG = "ADD_PDF_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // אתחול Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfCategories();

        // הגדרה ואתחול של דיאלוג התקדמות
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // טיפול בלחיצה, חזרה לפעילות הקודמת
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // טיפול בלחיצה, צרף PDF
        binding.attachBtn.setOnClickListener(v -> pdfPickIntent());

        // טיפול בלחיצה, בחר קטגוריה
        binding.categoryTv.setOnClickListener(v -> categoryPickDialog());

        // טיפול בלחיצה, העלה PDF
        binding.submitBtn.setOnClickListener(v -> validateData());
    }

    private String title = "", description = "";

    //unit test
    public boolean isTitleValid(String title) {
        return !TextUtils.isEmpty(title);
    }

    public boolean isDescriptionValid(String description) {
        return !TextUtils.isEmpty(description);
    }

    public boolean isSelectedCategoryValid(String category) {
        return !TextUtils.isEmpty(category);
    }

    public boolean isPdfSelected(Uri pdfUri) {
        return pdfUri != null;
    }

    private void validateData() {
        // שלב 1: אימות נתונים

        // קבלת נתונים
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();

        // אימות נתונים
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, "Pick Category...", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(this, "Pick Pdf...", Toast.LENGTH_SHORT).show();
        } else {
            // כל הנתונים תקפים, ניתן להעלות
            uploadPdfToStorage();
        }
    }

    private void uploadPdfToStorage() {
        // שלב 2: העלאת ה-PDF לאחסון של Firebase

        // הצגת התקדמות
        progressDialog.setMessage("Uploading Pdf...");
        progressDialog.show();

        // זמן
        long timestamp = System.currentTimeMillis();

        // נתיב ה-PDF באחסון של Firebase
        String filePathAndName = "Books/" + timestamp;
        // הפניית אחסון
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // השגת URL של ה-PDF
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    String uploadedPdfUrl = "" + uriTask.getResult();

                    // העלאה למסד הנתונים של Firebase
                    uploadPdfInfoToDb(uploadedPdfUrl, timestamp);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(PdfAddActivity.this, "PDF upload failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadPdfInfoToDb(String uploadedPdfUrl, long timestamp) {
        // שלב 3: העלאת מידע ה-PDF למסד הנתונים של Firebase

        progressDialog.setMessage("Uploading pdf info...");

        String uid = firebaseAuth.getUid();

        // הגדרת נתונים להעלאה
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + uid);
        hashMap.put("id", "" + timestamp);
        hashMap.put("title", "" + title);
        hashMap.put("description", "" + description);
        hashMap.put("categoryId", "" + selectedCategoryId);
        hashMap.put("url", "" + uploadedPdfUrl);
        hashMap.put("timestamp", timestamp);
        hashMap.put("viewsCount", 0);
        hashMap.put("downloadsCount", 0);

        // הפנייה למסד הנתונים: DB > Books
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(PdfAddActivity.this, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(PdfAddActivity.this, "Failed to upload to db due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadPdfCategories() {
        // טעינת קטגוריות ה-PDF
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();

        // הפנייה למסד הנתונים לטעינת קטגוריות
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear(); // ניקוי לפני הוספת נתונים
                categoryIdArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // קבלת id ושם הקטגוריה
                    String categoryId = "" + ds.child("id").getValue();
                    String categoryTitle = "" + ds.child("category").getValue();

                    // הוספה לרשימות המתאימות
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // קטגוריה שנבחרה ושם הקטגוריה
    private String selectedCategoryId, selectedCategoryTitle;

    private void categoryPickDialog() {
        // דיאלוג בחירת קטגוריה

        // המרת הרשימה למערך
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i < categoryTitleArrayList.size(); i++) {
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        // דיאלוג
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // בחירה בקטגוריה
                        selectedCategoryTitle = categoryTitleArrayList.get(which);
                        selectedCategoryId = categoryIdArrayList.get(which);
                        // עדכון TextView של הקטגוריה
                        binding.categoryTv.setText(selectedCategoryTitle);
                    }
                })
                .show();
    }

    private void pdfPickIntent() {
        // כוונה לבחירת PDF

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PDF_PICK_CODE) {
                // PDF נבחר
                pdfUri = data.getData();
            }
        } else {
            Toast.makeText(this, "cancelled picking pdf", Toast.LENGTH_SHORT).show();
        }
    }
}