package com.alp.ir.viradictionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> words;
    private ArrayList<String[]> wordDetails; // لیست برای ذخیره جزئیات هر کلمه

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = findViewById(R.id.listView);

        words = new ArrayList<>();
        wordDetails = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, words);
        listView.setAdapter(adapter);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.checkAndCopyDatabase();
        SQLiteDatabase db = dbHelper.openDatabase();

        // بارگذاری همه کلمات در ابتدا
        loadWords(db, "");


        // تنظیم کلیک روی آیتم‌های لیست
        listView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            // گرفتن اطلاعات کلمه انتخابی
            String[] details = wordDetails.get(position);
            String enWord = details[1];

            // ارسال اطلاعات به WordDetailsActivity
            Intent intent = new Intent(MainActivity.this, WordDetailsActivity.class);
            intent.putExtra("en_word", enWord);
            startActivity(intent);
        });
    }

    // متد برای بارگذاری کلمات از پایگاه داده
    private void loadWords(SQLiteDatabase db, String query) {
        words.clear();
        wordDetails.clear();

        String sql = "SELECT fa, en FROM words";
        if (!query.isEmpty()) {
            sql += " WHERE fa LIKE ? OR en LIKE ?";
        }
        sql += " GROUP BY en ";// گروه بندی کلمات یکسان

        Cursor cursor = db.rawQuery(sql, query.isEmpty() ? null : new String[]{"%" + query + "%", "%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                String fa = cursor.getString(0);
                String en = cursor.getString(1);
                words.add(en);
                wordDetails.add(new String[]{fa, en});
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // متصل کردن منو به Toolbar
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // پیدا کردن آیتم جستجو
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem aboutItem = menu.findItem(R.id.action_about);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // مدیریت رویداد جستجو
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // اعمال جستجو هنگام تایید
                loadWords(new DatabaseHelper(MainActivity.this).openDatabase(), query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // اعمال جستجو هنگام تایپ
                loadWords(new DatabaseHelper(MainActivity.this).openDatabase(), newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            // نمایش دیالوگ "درباره ما"
            showAboutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about_us);
        builder.setMessage(R.string.about_desc);
        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/iranyekan.ttf");

        TextView title = dialog.findViewById(android.R.id.title);
        if (title != null) {
            title.setTypeface(typeface);
        }

        TextView message = dialog.findViewById(android.R.id.message);
        if (message != null) {
            message.setTypeface(typeface);
        }
    }
}
