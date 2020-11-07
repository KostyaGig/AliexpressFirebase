package com.kostya_zinoviev.aliexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountBtn;
    private EditText inputName,inputPhoneNumber,inputPassword;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountBtn = findViewById(R.id.registerBtn);
        inputName = findViewById(R.id.register_user_name);
        inputPhoneNumber = findViewById(R.id.register_phone_number);
        inputPassword = findViewById(R.id.register_password);
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }
    private void createAccount(){
        String name = inputName.getText().toString().trim();
        String phoneNumber = inputPhoneNumber.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Write empty input!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Create Account");
            progressDialog.setMessage("Please waiting..");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            validatePhoneNumber(name,phoneNumber,password);
        }
    }

    private void validatePhoneNumber(final String nameInput, final String phoneNumber, final String password) {
        final DatabaseReference rootRef = firebaseDatabase.getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Если ветки users с подветкой phoneNumber не существует данных под это веткой,то регаем юзера
                //Иначе говрим,что пользователь с таким номером уже существует!
                if (!(dataSnapshot.child("Users").child(phoneNumber).exists())){
                    HashMap<String,Object>  userDataMap = new HashMap<>();
                    userDataMap.put("phone",phoneNumber);
                    userDataMap.put("name",nameInput);
                    userDataMap.put("password",password);
                    rootRef.child("Users").child(phoneNumber).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Your Account was created!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(loginIntent);
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Error!Please try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "This " + phoneNumber + " already exists!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again with phone number", Toast.LENGTH_SHORT).show();

                    Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
