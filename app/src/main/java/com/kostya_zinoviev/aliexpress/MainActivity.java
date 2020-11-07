package com.kostya_zinoviev.aliexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kostya_zinoviev.aliexpress.Model.User;
import com.kostya_zinoviev.aliexpress.Prevalent.Prevalent;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button loginBtn,registrationBtn;
    private String parentDbName = "Users";
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        loginBtn.setOnClickListener(this);
        registrationBtn.setOnClickListener(this);

        //Проверка checkbox rememberMe

        String getUserPhoneWithKey = Paper.book().read(Prevalent.userPhoneKey);
        String getUserPasswordWithKey = Paper.book().read(Prevalent.userPasswordKey);

        //Проверяем наши номера и пароли на пустоту и "" и если все хорошо,то...
        if (getUserPhoneWithKey != "" && getUserPasswordWithKey != ""){

            if (!TextUtils.isEmpty(getUserPhoneWithKey) && !TextUtils.isEmpty(getUserPasswordWithKey))
            {
                progressDialog.show();
                allowAccess(getUserPhoneWithKey,getUserPasswordWithKey);
            }
        }

    }

    private void allowAccess(final String getUserPhoneWithKey, final String getUserPasswordWithKey) {
        final DatabaseReference rootRef = firebaseDatabase.getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(getUserPhoneWithKey).exists()){
                    //Если вход успешный,то мы получаем данные из FireBase в нашу модель user
                    //И через геттеры можем получить имя юзера пароль и номер телефона

                    User userData = dataSnapshot.child(parentDbName).child(getUserPhoneWithKey).getValue(User.class);

                    //Get phone number user with getter's
                    //Если номер введенный в поле равен номеру из базы данных,то...
                    //Теперь проверяем на валидность пароль,веденный пользователем
                    if (userData.getPhone().equals(getUserPhoneWithKey)){
                        if (userData.getPassword().equals(getUserPasswordWithKey)){
                            //Если все верно,то перекидываем юзера на основную активность приложения
                            Toast.makeText(MainActivity.this, "Loged in successfully..", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            Intent homeIntent = new Intent(MainActivity.this,HomeActivity.class);
                            //Чтобы при уничтожении активити мы могли войти в приложение и при запомнить меня = true оно сразу перебросит в HomeActivity
                            Prevalent.currentOnlineUser = userData;
                            startActivity(homeIntent);

                        } else {
                            Toast.makeText(MainActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Неверный номер", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Account with this " + getUserPhoneWithKey + " number do not exists", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        loginBtn = findViewById(R.id.loginBtn);
        registrationBtn = findViewById(R.id.registrationBtn);

        firebaseDatabase = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login Account");
        progressDialog.setMessage("Please waiting..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        Paper.init(MainActivity.this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
           case R.id.loginBtn:
                Intent loginActivity = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(loginActivity);
            break;
            case R.id.registrationBtn:
                Intent registerActivity = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(registerActivity);
                break;
        }
    }
}
