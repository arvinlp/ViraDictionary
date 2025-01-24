package com.alp.ir.viradictionary;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText searchField = findViewById(R.id.searchField);
        ListView listView = findViewById(R.id.listView);

        words = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, words);
        listView.setAdapter(adapter);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.checkAndCopyDatabase();
        SQLiteDatabase db = dbHelper.openDatabase();

        // بارگذاری همه کلمات در ابتدا
        loadWords(db, "");

        // جستجو هنگام تغییر متن
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // جستجو با متن وارد شده
                loadWords(db, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // متد برای بارگذاری کلمات از پایگاه داده
    private void loadWords(SQLiteDatabase db, String query) {
        words.clear();

        String sql = "SELECT fa, en FROM words";
        if (!query.isEmpty()) {
            sql += " WHERE fa LIKE ? OR en LIKE ?";
        }

        Cursor cursor = db.rawQuery(sql, query.isEmpty() ? null : new String[]{"%" + query + "%", "%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                String fa = cursor.getString(0);
                String en = cursor.getString(1);
                words.add(fa + " - " + en);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }
}