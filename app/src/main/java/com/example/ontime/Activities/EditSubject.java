package com.example.ontime.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.ontime.R;

import java.util.Objects;

public class EditSubject extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = Objects.requireNonNull(this.getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE));
        boolean darkModeOn = preferences.getBoolean("Mode", false);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_subject_edit);
        final String subject = (String) getIntent().getSerializableExtra("subjectName");
        TextView title = findViewById(R.id.subjectText);
        title.setText(subject);

        // create a view holder for this layout
        final EditItem.ViewHolder viewHolder = new EditItem.ViewHolder();
        viewHolder.addedItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));


    }
}