package com.NewBookiT.BookiT.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.NewBookiT.BookiT.R;
import com.NewBookiT.BookiT.databinding.ActivityProfileEditBinding;

import java.util.HashMap;

public class ProfileEditActivity extends AppCompatActivity {

    // קישור ישויות הממשק עם View Binding
    private ActivityProfileEditBinding binding;

    // הגדרת FirebaseAuth, לקבלת/עדכון נתוני משתמש באמצעות uid
    private FirebaseAuth firebaseAuth;

    // תיבת דו-שיח להתקדמות
    private ProgressDialog progressDialog;

    private static final String TAG = "PROFILE_EDIT_TAG";

    private Uri imageUri = null;

    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // הגדרת תיבת הדו-שיח להתקדמות
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false); // אל תבטל בלחיצה מחוץ לתיבת הדו-שיח

        // הגדרת FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        // טיפול בלחיצה, חזרה אחורה
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // טיפול בלחיצה, בחירת תמונה
        binding.profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageAttachMenu();
            }
        });

        // טיפול בלחיצה, עדכון הפרופיל
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

    }

    // unit test
    public boolean isNameValid(String name) {
        return !TextUtils.isEmpty(name);
    }

    public boolean isImageUriValid(Uri uri) {
        return uri != null;
    }


    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading user info of user " + firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // קבלת כל מידע המשתמש מה-snapshot
                        String email = "" + snapshot.child("email").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        // הצגת הנתונים בממשק המשתמש
                        binding.nameEt.setText(name);

                        // הצגת התמונה באמצעות Glide
                        Glide.with(ProfileEditActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person_gray)
                                .into(binding.profileIv);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void validateData() {
        // איסוף הנתונים
        name = binding.nameEt.getText().toString().trim();

        // אימות הנתונים
        if (TextUtils.isEmpty(name)) {
            // אם לא הוזן שם
            Toast.makeText(this, "Enter name...", Toast.LENGTH_SHORT).show();
        } else {
            // שם הוזן
            if (imageUri == null) {
                // עדכון ללא תמונה
                updateProfile("");
            } else {
                // עדכון עם תמונה
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        Log.d(TAG, "uploadImage: Uploading profile image...");
        progressDialog.setMessage("Updating profile image");
        progressDialog.show();

        // נתיב ושם התמונה, שימוש ב-uid להחלפת התמונה הקודמת
        String filePathAndName = "ProfileImages/" + firebaseAuth.getUid();

        // הפנייה לאחסון
        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Profile image uploaded");
                        Log.d(TAG, "onSuccess: Getting url of uploaded image");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedImageUrl = "" + uriTask.getResult();

                        Log.d(TAG, "onSuccess: Uploaded Image URL: " + uploadedImageUrl);

                        updateProfile(uploadedImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to upload image due to " + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Failed to upload image due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfile(String imageUrl) {
        Log.d(TAG, "updateProfile: Updating user profile");
        progressDialog.setMessage("Updating user profile...");
        progressDialog.show();

        // הכנת הנתונים לעדכון במסד הנתונים
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "" + name);
        if (imageUri != null) {
            hashMap.put("profileImage", "" + imageUrl);
        }

        // עדכון הנתונים במסד הנתונים
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Profile updated...");
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Profile updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to update db due to " + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Failed to update db due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImageAttachMenu() {
        // הכנת תפריט קופצני
        PopupMenu popupMenu = new PopupMenu(this, binding.profileIv);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

        popupMenu.show();

        // טיפול בלחיצות על פריטי התפריט
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // קבלת מזהה הפריט שנלחץ
                int which = item.getItemId();
                if (which == 0) {
                    // בחירת תמונה מהמצלמה
                    pickImageCamera();
                } else if (which == 1) {
                    // בחירת תמונה מהגלריה
                    pickImageGallery();
                }

                return false;
            }
        });
    }

    private void pickImageCamera() {
        // Intent לבחירת תמונה מהמצלמה
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick"); // כותרת התמונה
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);

    }

    private void pickImageGallery() {
        // Intent לבחירת תמונה מהגלריה
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // טיפול בתוצאה של ה-Intent של המצלמה
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked From Camera " + imageUri);

                        try {
                            // הצגת התמונה באמצעות Glide
                            Glide.with(ProfileEditActivity.this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_person_gray)
                                    .into(binding.profileIv);
                        } catch (Exception e) {
                            Log.e(TAG, "onActivityResult: ", e);
                        }
                    } else {
                        Toast.makeText(ProfileEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // טיפול בתוצאה של ה-Intent של הגלריה
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: Picked From Gallery " + imageUri);

                        try {
                            // הצגת התמונה באמצעות Glide
                            Glide.with(ProfileEditActivity.this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_person_gray)
                                    .into(binding.profileIv);
                        } catch (Exception e) {
                            Log.e(TAG, "onActivityResult: ", e);
                        }
                    } else {
                        Toast.makeText(ProfileEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

}
