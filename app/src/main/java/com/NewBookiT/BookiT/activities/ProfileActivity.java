package com.NewBookiT.BookiT.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.NewBookiT.BookiT.adapters.AdapterPdfFavorite;
import com.NewBookiT.BookiT.databinding.ActivityProfileBinding;
import com.technifysoft.BookiT.models.ModelPdf;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    //view binding
    private ActivityProfileBinding binding;

    //firebase auth, for loading user data using user uid
    private FirebaseAuth firebaseAuth;
    //firebase current user
    private FirebaseUser firebaseUser;

    //arraylist to hold the books
    private ArrayList<ModelPdf> pdfArrayList;
    //adapter to set in recyclerview
    private AdapterPdfFavorite adapterPdfFavorite;

    //progress dialog
    private ProgressDialog progressDialog;

    private static final String TAG = "PROFILE_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // איפוס נתוני מידע על המשתמש
        binding.accountTypeTv.setText("N/A");
        binding.memberDateTv.setText("N/A");
        binding.favoriteBookCountTv.setText("N/A");
        binding.accountStatusTv.setText("N/A");

        // הגדרת FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        // קבלת המשתמש הנוכחי
        firebaseUser = firebaseAuth.getCurrentUser();

        // הגדרת תיבת הדו-שיח להתקדמות
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // טעינת מידע המשתמש והספרים המועדפים
        loadUserInfo();
        loadFavoriteBooks();

        // טיפול בלחיצה, מעבר לעריכת פרופיל
        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class));
            }
        });

        // טיפול בלחיצה, חזרה אחורה
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // טיפול בלחיצה, אימות משתמש אם טרם אומת
        binding.accountStatusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser.isEmailVerified()){
                    // כבר אומת
                    Toast.makeText(ProfileActivity.this, "Already verified...", Toast.LENGTH_SHORT).show();
                }
                else {
                    // לא אומת, הצגת תיבת דו-שיח לאימות
                    emailVerificationDialog();
                }
            }
        });

    }

    private void emailVerificationDialog() {
        // תיבת דו-שיח
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verify Email")
                .setMessage("Are you sure you want to send email verification instructions to your email "+firebaseUser.getEmail())
                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendEmailVerification();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void sendEmailVerification() {
        // הצגת תיבת הדו-שיח
        progressDialog.setMessage("Sending email verification instructions to your email "+firebaseUser.getEmail());
        progressDialog.show();

        // שליחת מייל לאימות
        firebaseUser.sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // שליחה בהצלחה
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Instructions sent, check your email "+firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // שליחה נכשלה
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserInfo(){
        Log.d(TAG, "loadUserInfo: Loading user info of user "+firebaseAuth.getUid());

        // בדיקת מצב אימות האימייל, לאחר אימות יש להתחבר מחדש כדי לראות שינויים
        if (firebaseUser.isEmailVerified()) {
            binding.accountStatusTv.setText("Verified");
        } else {
            binding.accountStatusTv.setText("Not Verified");
        }

        // הפנייה למסד נתונים כדי לקבל את פרטי המשתמש
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // קבלת כל המידע על המשתמש מה-snapshot
                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String userType = ""+snapshot.child("userType").getValue();

                        // עיצוב התאריך ל-dd/MM/yyyy
                        String formattedDate = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        // הצגת הנתונים בממשק המשתמש
                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);
                        binding.memberDateTv.setText(formattedDate);
                        binding.accountTypeTv.setText(userType);

                        // הצגת תמונת הפרופיל, באמצעות Glide
                        try {
                            Glide.with(ProfileActivity.this)
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_person_gray)
                                    .into(binding.profileIv);
                        } catch (Exception e){
                            Log.e(TAG, "onDataChange: ", e);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }

    public boolean isNameValid(String name) {
        return !TextUtils.isEmpty(name);
    }

    public boolean isUriValid(Uri uri) {
        return uri != null;
    }

    public boolean isEmailVerified() {
        return true;
    }


    private void loadFavoriteBooks(){
        // אתחול הרשימה
        pdfArrayList = new ArrayList<>();

        // טעינת הספרים המועדפים ממסד הנתונים
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // ניקוי הרשימה לפני ההוספה
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String bookId = ""+ds.child("bookId").getValue();

                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookId);

                            pdfArrayList.add(modelPdf);
                        }

                        // הצגת מספר הספרים המועדפים
                        binding.favoriteBookCountTv.setText(""+pdfArrayList.size());
                        // הגדרת האדפטר
                        adapterPdfFavorite = new AdapterPdfFavorite(ProfileActivity.this, pdfArrayList);
                        // הצגת האדפטר ב-RecyclerView
                        binding.booksRv.setAdapter(adapterPdfFavorite);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}