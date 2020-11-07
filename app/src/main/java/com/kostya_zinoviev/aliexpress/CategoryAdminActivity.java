package com.kostya_zinoviev.aliexpress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CategoryAdminActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView shirts,sports,dress,sweathers,glasses,purses_bags,heats,shoess,headphoness,laptops,watches,mobiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_admin);
        init();

        shirts.setOnClickListener(this);
        dress.setOnClickListener(this);
        sports.setOnClickListener(this);
        sweathers.setOnClickListener(this);
        glasses.setOnClickListener(this);
        purses_bags.setOnClickListener(this);
        heats.setOnClickListener(this);
        shoess.setOnClickListener(this);
        headphoness.setOnClickListener(this);
        laptops.setOnClickListener(this);
        watches.setOnClickListener(this);
        mobiles.setOnClickListener(this);
    }

    private void init() {
        shirts = findViewById(R.id.t_shirts);
        dress = findViewById(R.id.t_dress);
        sweathers = findViewById(R.id.t_sweathers);
        glasses = findViewById(R.id.t_glasses);
        purses_bags = findViewById(R.id.t_purses_bags);
        headphoness = findViewById(R.id.t_headphoness);
        laptops = findViewById(R.id.t_laptops);
        watches = findViewById(R.id.t_watches);
        mobiles = findViewById(R.id.t_mobiles);
        heats = findViewById(R.id.t_heats);
        shoess = findViewById(R.id.t_shoess);
        sports = findViewById(R.id.t_sports);
    }

    @Override
    public void onClick(View v) {

        int id  = v.getId();
        Intent intent = null;
        switch (id){
            case R.id.t_shirts:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","tShirts");
                break;

            case R.id.t_sports:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","Sports tShirts");
                break;

            case R.id.t_dress:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","Female Dress");
                break;

            case R.id.t_sweathers:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","Sweathers");
                break;

            case R.id.t_glasses:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","Glasses");
                break;

            case R.id.t_purses_bags:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","Purses Bags");
                break;

            case R.id.t_heats:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","Heats");
                break;

            case R.id.t_shoess:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","Shoes");
                break;

            case R.id.t_headphoness:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","HeadPhones");
                break;

            case R.id.t_laptops:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","Laptops");
                break;

            case R.id.t_watches:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","Whatches");
                break;

            case R.id.t_mobiles:
                intent = new Intent(CategoryAdminActivity.this,AdminActivity.class);
                intent.putExtra("category","Mobiles");
                break;
        }
        startActivity(intent);
    }
}
