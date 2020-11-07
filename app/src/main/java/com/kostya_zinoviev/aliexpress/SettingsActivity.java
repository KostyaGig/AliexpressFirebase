package com.kostya_zinoviev.aliexpress;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.kostya_zinoviev.aliexpress.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropOverlayView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private ImageView profileImage;
    private EditText nameEd,phoneEd,addressEd;
    private TextView profileChane,closeText,saveText;

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePictureReference;
    private StorageTask storageTask;
    private String cheker = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();

        userInfoDisplay(profileImage,nameEd,phoneEd,addressEd);

        closeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cheker.equals("clicked")){
                    userInfoSaved();
                } else {
                    updateOnlyUserInfo();
                }
            }
        });
        profileChane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cheker = "cliсked";
                //Наша библиотека по обрезанию фото
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //получение фото
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Error try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();

        }
    }

    private void updateOnlyUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMap = new HashMap<>();

        userMap.put("name",nameEd.getText().toString().trim());
        userMap.put("address",addressEd.getText().toString().trim());
        userMap.put("phone",phoneEd.getText().toString().trim());
        reference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile info updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userInfoSaved() {

        String name = nameEd.getText().toString().trim();
        String address = addressEd.getText().toString().trim();
        String phone = phoneEd.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Write is empty field!", Toast.LENGTH_SHORT).show();
        } else if (cheker.equals("clicked")){
            uploadImage();
        }

    }

    private void uploadImage() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Log in to your account");
        progressDialog.setMessage("Please waiting...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileRef = storageProfilePictureReference.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            storageTask = fileRef.putFile(imageUri);

            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                                HashMap<String,Object> userMap = new HashMap<>();
                                userMap.put("name",nameEd.getText().toString().trim());
                                userMap.put("address",addressEd.getText().toString().trim());
                                userMap.put("phone",phoneEd.getText().toString().trim());
                                userMap.put("image",myUrl);

                                reference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(SettingsActivity.this,MainActivity.class));
                                Toast.makeText(SettingsActivity.this, "Profile info updated successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Error update info user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }

    }


    private void userInfoDisplay(final ImageView profileImage, final EditText nameEd, final EditText phoneEd, final EditText addressEd) {
       //Происходит обновление данных
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Если существует ветка Users / numberPhone,то
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImage);
                        nameEd.setText(name);
                        phoneEd.setText(phone);
                        addressEd.setText(address);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        profileImage = findViewById(R.id.profile);
        nameEd = findViewById(R.id.edName);
        phoneEd = findViewById(R.id.edPhoneNumber);
        addressEd = findViewById(R.id.edAddress);
        profileChane = findViewById(R.id.profile_change);
        saveText = findViewById(R.id.update_settings);
        closeText = findViewById(R.id.close_settings);

        storageProfilePictureReference = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        progressDialog = new ProgressDialog(this);
    }
}
