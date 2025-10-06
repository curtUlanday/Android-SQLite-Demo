package com.example.androidsqlitedemo;

import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.androidsqlitedemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseHelper db;
    private SimpleCursorAdapter adapter;
    private Cursor cursor; // keep reference so we can swap/close cleanly

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(this);

        setupList();
        setupButtons();
        loadData(); // initial load
    }

    private void setupList() {
        // Map DB columns to built-in layout fields (simple_list_item_2)
        String[] from = new String[] {
                DatabaseHelper.COL_NAME,
                DatabaseHelper.COL_COURSE
        };
        int[] to = new int[] {
                android.R.id.text1,
                android.R.id.text2
        };

        cursor = db.getAllStudents();
        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                from,
                to,
                0
        );
        binding.listView.setAdapter(adapter);
    }

    private void setupButtons() {
        binding.btnAdd.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String course = binding.etCourse.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = db.insertStudent(name, course);
            if (id > 0) {
                Toast.makeText(this, "Inserted id=" + id, Toast.LENGTH_SHORT).show();
                binding.etName.setText("");
                binding.etCourse.setText("");
                loadData();
            } else {
                Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnRefresh.setOnClickListener(v -> loadData());
    }

    private void loadData() {
        Cursor newCursor = db.getAllStudents();
        adapter.changeCursor(newCursor);
        // close old cursor AFTER swapping
        if (cursor != null && !cursor.isClosed()) cursor.close();
        cursor = newCursor;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null && !cursor.isClosed()) cursor.close();
    }
}