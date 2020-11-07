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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kostya_zinoviev.aliexpress.Model.User;
import com.kostya_zinoviev.aliexpress.Prevalent.Prevalent;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputPhoneNumber, inputPassword;
    private CheckBox rememberMe;
    private TextView forgetPassword;
    private Button loginBtn;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private String parentDbName;
    private TextView adminPanel,notAdminPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        loginBtn.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        adminPanel.setOnClickListener(this);
        notAdminPanel.setOnClickListener(this);
    }

    private void init() {
        inputPhoneNumber = findViewById(R.id.login_phone_number);
        inputPassword = findViewById(R.id.login_password);
        rememberMe = findViewById(R.id.chekPass);
        forgetPassword = findViewById(R.id.forget_password);
        loginBtn = findViewById(R.id.loginBtn);
        firebaseDatabase = FirebaseDatabase.getInstance();
        parentDbName = "Users";
        adminPanel = findViewById(R.id.admin_panel);
        notAdminPanel = findViewById(R.id.not_admin_panel);

        Paper.init(LoginActivity.this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Log in to your account");
        progressDialog.setMessage("Please waiting...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);


    }

    private void loginUser() {

        String phoneNumber = inputPhoneNumber.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Write empty input!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            allowAccessToAccount(phoneNumber, password);
        }
    }

    private void allowAccessToAccount(final String phoneNumber, final String password) {
        if (rememberMe.isChecked()) {
            Paper.book().write(Prevalent.userPhoneKey, phoneNumber);
            Paper.book().write(Prevalent.userPasswordKey, password);
        }

        final DatabaseReference rootRef = firebaseDatabase.getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phoneNumber).exists()) {
                    //Если вход успешный,то мы получаем данные из FireBase в нашу модель user
                    //И через геттеры можем получить имя юзера пароль и номер телефона

                    User userData = dataSnapshot.child(parentDbName).child(String.valueOf(phoneNumber)).getValue(User.class);

                    //Get phone number user with getter's
                    //Если номер введенный в поле равен номеру из базы данных,то...
                    //Теперь проверяем на валидность пароль,веденный пользователем
                    if (String.valueOf(userData.getPhone()).equals(phoneNumber)) {
                        if (userData.getPassword().equals(password)) {
                                if (parentDbName.equals("Admins")){
                                    //Если все верно,то перекидываем админа на админ активность приложения
                                    Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                    Intent homeIntent = new Intent(LoginActivity.this, CategoryAdminActivity.class);
                                    startActivity(homeIntent);
                                } else if (parentDbName.equals("Users")){
                                    //Если все верно,то перекидываем юзера на основную активность приложения
                                    Toast.makeText(LoginActivity.this, "Loged in successfully..", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                    //Если логин проходит успешно,тогда мы присваиваем currentOnlineUser userData,теперь они ссылаются
                                    //На один и тот же обЪект,таким образом в HomeActivity navheader textView через гет метод присваиваем имя
                                    Prevalent.currentOnlineUser = userData;
                                    startActivity(homeIntent);
                                }
                        } else {
                            Toast.makeText(LoginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Неверный номер", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Account with this " + phoneNumber + " number do not exists", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.loginBtn:
                loginUser();
                break;

            case R.id.forget_password:

                break;
            case R.id.admin_panel:
                    loginBtn.setText("Login admin");
                    adminPanel.setVisibility(View.INVISIBLE);
                    notAdminPanel.setVisibility(View.VISIBLE);
                    parentDbName = "Admins";
                break;
            case R.id.not_admin_panel:
                loginBtn.setText("Login");
                adminPanel.setVisibility(View.VISIBLE);
                notAdminPanel.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
                break;
        }
    }
}
