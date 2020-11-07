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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE = 100;
    private String categoryName;
    private ImageView productImageView;
    private Button addProductBtn;
    private EditText inputProductName,inputProductDescription,inputProductPrice;
    private Uri imageUri;
    private String saveCurrentDate,saveCurrentTime;
    private String productRandomKey,downloadImageURL;
    private StorageReference productImagesRef;
    private String productName,productDescription,productPrice;
    private DatabaseReference productRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        init();

        Intent getIntent = getIntent();
        categoryName = getIntent.getStringExtra("category");

        addProductBtn.setOnClickListener(this);
        productImageView.setOnClickListener(this);
    }

    private void init() {
        addProductBtn = findViewById(R.id.add_new_product);
        inputProductName = findViewById(R.id.product_name);
        inputProductDescription= findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);
        productImageView = findViewById(R.id.select_product);
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding new product");
        progressDialog.setMessage("Please waiting...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product images");
    }

    @Override
    public void onClick(View v) {
        int id  = v.getId();

        switch (id){
            case R.id.add_new_product:
                validateProductData();
                break;
            case R.id.select_product:
                openGallery();
                break;
        }
    }

    private void validateProductData() {
         productName = inputProductName.getText().toString().trim();
         productDescription = inputProductDescription.getText().toString().trim();
         productPrice = inputProductPrice.getText().toString().trim();

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productDescription)
                || TextUtils.isEmpty(productPrice) || imageUri == null){
            Toast.makeText(this, "write field and download image!", Toast.LENGTH_SHORT).show();
        } else {
            //Если прошли все проверки,то загружаем информацию о продукте в FireBaseDataBase
            storeProductInformation();
        }

    }

    private void storeProductInformation() {
        progressDialog.show();
        Calendar calendar = Calendar.getInstance();

        //Получаем Дату
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MMMM-d-E", Locale.US);
        saveCurrentDate = currentDate.format(calendar.getTime());

        //Получаем время
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a",Locale.US);
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        //В путь filepath мы помещаем фотку,по uri
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageURL = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){

                            downloadImageURL = task.getResult().toString();

                            Toast.makeText(AdminActivity.this, "got the product url successfully", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDataBase();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDataBase() {
        HashMap<String,Object> productDataMap = new HashMap<>();
        productDataMap.put("productID",productRandomKey);
        productDataMap.put("date",saveCurrentDate);
        productDataMap.put("time",saveCurrentTime);
        productDataMap.put("name",productName);
        productDataMap.put("description",productDescription);
        productDataMap.put("price",productPrice);
        productDataMap.put("image",downloadImageURL);
        productDataMap.put("category",categoryName);

        //Сохраняем данные в категорию "Products" под категорию productRandomKey
        productRef.child(productRandomKey).updateChildren(productDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Intent homeIntent = new Intent(AdminActivity.this,HomeActivity.class);
                    startActivity(homeIntent);

                    progressDialog.dismiss();
                    Toast.makeText(AdminActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            productImageView.setImageURI(imageUri);
        }
    }

}
