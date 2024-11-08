package com.NewBookiT.BookiT.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.NewBookiT.BookiT.MyApplication;
import com.NewBookiT.BookiT.R;
import com.NewBookiT.BookiT.adapters.AdapterComment;
import com.NewBookiT.BookiT.databinding.ActivityPdfDetailBinding;
import com.NewBookiT.BookiT.databinding.DialogCommentAddBinding;
import com.technifysoft.BookiT.models.ModelComment;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfDetailActivity extends AppCompatActivity {

    // הגדרת תיבת הטקסט ושאר המאפיינים של הפעילות
    private ActivityPdfDetailBinding binding;

    // מזהה הספר, מתקבל מהintent
    String bookId, bookTitle, bookUrl;


    // האם הספר ברשימת המועדפים שלי
    boolean isInMyFavorite = false;

    private FirebaseAuth firebaseAuth;

    // תגית לdebugging
    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";

    // תיבת ההתקדמות
    private ProgressDialog progressDialog;

    // רשימת התגובות
    private ArrayList<ModelComment> commentArrayList;

    // מתאם להצגת תגובות ב-RecyclerView
    private AdapterComment adapterComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // קבלת הנתונים מintent כמו bookId
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");

        // בתחילה הסתרת כפתור ההורדה, כי נצטרך את כתובת הספר שנטען מאוחר יותר בפונקציה loadBookDetails();
        binding.downloadBookBtn.setVisibility(View.GONE);

        // התחלת תיבת ההתקדמות
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            checkIsFavorite();
        }

        // טעינת פרטי הספר והתגובות
        loadBookDetails();
        loadComments();
        // הגדלת מספר הצפיות בספר בכל פעם שהדף מתחיל
        MyApplication.incrementBookViewCount(bookId);

        // התנהגות לחיצה על כפתור "חזור"
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // התנהגות לחיצה על כפתור "קרא"
        binding.readBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PdfDetailActivity.this, PdfViewActivity.class);
                intent1.putExtra("bookId", bookId);
                startActivity(intent1);
            }
        });

        // התנהגות לחיצה על כפתור "הורד"
        binding.downloadBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_DOWNLOAD, "onClick: Checking permission");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    MyApplication.downloadBook(PdfDetailActivity.this, "" + bookId, "" + bookTitle, "" + bookUrl);
                } else {
                    if (ContextCompat.checkSelfPermission(PdfDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG_DOWNLOAD, "onClick: Permission already granted, can download book");
                        MyApplication.downloadBook(PdfDetailActivity.this, "" + bookId, "" + bookTitle, "" + bookUrl);
                    } else {
                        Log.d(TAG_DOWNLOAD, "onClick: Permission was not granted, request permission...");
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }

            }
        });

        // התנהגות לחיצה על כפתור הוספת/הסרת מועדפים
        binding.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(PdfDetailActivity.this, "You're not logged in", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (isInMyFavorite){
                        // במועדפים, להסר מהמועדפים
                        MyApplication.removeFromFavorite(PdfDetailActivity.this, bookId);
                    }
                    else {
                        // לא במועדפים, להוסיף למועדפים
                        MyApplication.addToFavorite(PdfDetailActivity.this, bookId);
                    }
                }
            }
        });


        binding.emailLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailToGal();
            }
        });

        // התנהגות לחיצה על כפתור הוספת תגובה
        binding.addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*דרישות: משתמש חייב להיות מחובר כדי להוסיף תגובה*/
                if (firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(PdfDetailActivity.this, "You're not logged in...", Toast.LENGTH_SHORT).show();
                }
                else {
                    addCommentDialog();
                }
            }
        });
    }

    // טעינת התגובות ממסד הנתונים
    private void loadComments() {
        // איתחול של הרשימה לפני הוספת הנתונים
        commentArrayList = new ArrayList<>();

        // נתיב לקריאת התגובות ממסד הנתונים
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // ניקוי הרשימה לפני קריאת הנתונים
                        commentArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            // קריאת הנתונים כמודל, שם המשתנים במודל חייבים להיות תואמים לשמות במסד הנתונים
                            ModelComment model = ds.getValue(ModelComment.class);
                            // הוספת הנתונים לרשימה
                            commentArrayList.add(model);
                        }
                        // יצירת המתאם
                        adapterComment = new AdapterComment(PdfDetailActivity.this, commentArrayList);
                        // הצבת המתאם בRecyclerView
                        binding.commentsRv.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String comment = "";

    //unit test
    public boolean isStringValid(String str) {
        return str != null && !TextUtils.isEmpty(str);
    }

    public boolean isUriValid(Uri uri) {
        return uri != null;
    }

    public boolean isListNotEmpty(ArrayList<?> list) {
        return list != null && !list.isEmpty();
    }


    // הצגת תיבת דיאלוג להוספת תגובה
    private void addCommentDialog() {
        // יצירת תיבת דיאלוג באמצעות view binding
        DialogCommentAddBinding commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this));

        // הגדרת בנאי ליצירת חלון הדיאלוג
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(commentAddBinding.getRoot());

        // יצירת חלון הדיאלוג והצגתו
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // התנהגות לחיצה על כפתור "חזור"
        commentAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        // התנהגות לחיצה על כפתור "שלח"
        commentAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // קבלת הנתונים מהטופס
                comment = commentAddBinding.commentEt.getText().toString().trim();
                // אימות הנתונים
                if (TextUtils.isEmpty(comment)){
                    Toast.makeText(PdfDetailActivity.this, "Enter your comment...", Toast.LENGTH_SHORT).show();
                }
                else {
                    alertDialog.dismiss();
                    addComment();
                }
            }
        });
    }

    // הוספת התגובה למסד הנתונים
    private void addComment() {
        // הצגת תיבת ההתקדמות
        progressDialog.setMessage("Adding comment...");
        progressDialog.show();

        // תאריך עבור מזהה התגובה והזמן שלה
        String timestamp = ""+System.currentTimeMillis();

        // הגדרת הנתונים להוספה למסד הנתונים
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+timestamp);
        hashMap.put("bookId", ""+bookId);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("comment", ""+comment);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        // נתיב להוספת התגובה למסד הנתונים
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PdfDetailActivity.this, "Comment Added...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // כשיש שגיאה בהוספת התגובה
                        progressDialog.dismiss();
                        Toast.makeText(PdfDetailActivity.this, "Failed to add comment due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // בקשת הרשאת כתיבה לאחסון
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted){
                    Log.d(TAG_DOWNLOAD, "Permission Granted");
                    MyApplication.downloadBook(this, ""+bookId, ""+bookTitle, ""+bookUrl);
                }
                else {
                    Log.d(TAG_DOWNLOAD, "Permission was denied...: ");
                    Toast.makeText(this, "Permission was denied...", Toast.LENGTH_SHORT).show();
                }
            });

    // טעינת פרטי הספר ממסד הנתונים
    private void loadBookDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        // קבלת הנתונים
                        bookTitle = ""+snapshot.child("title").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        String downloadsCount = ""+snapshot.child("downloadsCount").getValue();
                        bookUrl = ""+snapshot.child("url").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();

                        // יצירת תאריך
                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        // טעינת קטגוריה
                        MyApplication.loadCategory(
                                ""+categoryId,
                                binding.categoryTv
                        );
                        // טעינת הספר מכתובת ה-URL
                        MyApplication.loadPdfFromUrlSinglePage(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.pdfView,
                                binding.progressBar,
                                binding.pagesTv
                        );
                        // טעינת גודל הקובץ
                        MyApplication.loadPdfSize(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.sizeTv
                        );

                        // הצבת הנתונים
                        binding.titleTv.setText(bookTitle);
                        binding.descriptionTv.setText(description);
                        binding.viewsTv.setText(viewsCount.replace("null", "N/A"));
                        binding.downloadsTv.setText(downloadsCount.replace("null", "N/A"));
                        binding.dateTv.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }

    // בדיקה האם הספר נמצא ברשימת המועדפים של המשתמש
    private void checkIsFavorite(){
        // בדיקה אם הספר נמצא ברשימת המועדפים של המשתמש
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Favorites").child(bookId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists(); // אמת אם קיים, שקר אם לא קיים
                        if (isInMyFavorite){
                            // אם קיים במועדפים
                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_white, 0, 0);
                            binding.favoriteBtn.setText("הסר מהמועדפים");
                        }
                        else {
                            // אם לא קיים במועדפים
                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_border_white, 0, 0);
                            binding.favoriteBtn.setText("הוסף למועדפים");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void sendEmailToGal() {
        // קריאת נתוני המשתמש
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "אתה לא מחובר למשתמש", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    // קריאת נתוני המשתמש
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    String username = userSnapshot.child("name").getValue(String.class);

                    // קריאת נתוני הספר
                    DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("Books").child(bookId);
                    bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot bookSnapshot) {
                            if (bookSnapshot.exists()) {
                                // קריאת נתוני הספר
                                String bookTitle = bookSnapshot.child("title").getValue(String.class);
                                String bookDescription = bookSnapshot.child("description").getValue(String.class);

                                // בניית תוכן ההודעה
                                String subject = "בקשה לספר: " + bookTitle;
                                String message = "המשתמש " + username + " (" + userEmail + ") + מעוניין בספר:\n" + "כותרת: " + bookTitle + "\n" + "תיאור: " + bookDescription;

                                // שליחת הודעת הדוא"ל ישירות למייל שנקבע
                                sendEmail("noamkadosh4444@gmail.com", subject, message);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // התעלמות מהשגיאה או טיפול בה
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // התעלמות מהשגיאה או טיפול בה
            }
        });
    }

    // פונקציה לשליחת הודעת דוא"ל
// פונקציה לשליחת הודעת דוא"ל
    private void sendEmail(String recipientEmail, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { recipientEmail });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
    }


}
