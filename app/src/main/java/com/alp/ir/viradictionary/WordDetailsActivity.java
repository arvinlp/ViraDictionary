package com.alp.ir.viradictionary;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class WordDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details);

        // پیکربندی Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // فعال کردن دکمه بازگشت
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // گرفتن اطلاعات از Intent
        String enWord = getIntent().getStringExtra("en_word");

        // اتصال به پایگاه داده
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.checkAndCopyDatabase();
        SQLiteDatabase db = dbHelper.openDatabase();

        // اجرای کوئری برای بازیابی اطلاعات بیشتر
        String sql = "SELECT fa, en FROM words WHERE en = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{enWord});

        StringBuilder faWord = new StringBuilder();
        if (cursor.moveToFirst()) {
            boolean flag = false;
            do {
                if(flag) faWord.append(" - "); // فاصله بین دو معنا
                faWord.append(cursor.getString(0)); // مقدار ستون fa
                flag = true;
            }while (cursor.moveToNext());
        }
        cursor.close();

        // نمایش اطلاعات در TextView ها
        TextView faTextView = findViewById(R.id.faTextView);
        TextView enTextView = findViewById(R.id.enTextView);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/iranyekan.ttf");
        faTextView.setTypeface(typeface);
        enTextView.setTypeface(typeface);



        if (faWord != null) {
            faTextView.setText(faWord.toString());
            enTextView.setText(enWord);
        } else {
            faTextView.setText("کلمه‌ای پیدا نشد.");
            enTextView.setText(enWord);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // برگشت به صفحه قبلی
        onBackPressed();
        return true;
    }
}
