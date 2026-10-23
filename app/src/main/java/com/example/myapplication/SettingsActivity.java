package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setBackgroundColor(Color.rgb(245, 247, 242));

        TextView title = new TextView(this);
        title.setText("Settings & Profile");
        title.setTextSize(26);
        title.setTextColor(Color.rgb(36, 92, 69));
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView description = new TextView(this);
        description.setText(
                "Smart Pantry Manager\n\n" +
                        "Manage your pantry and discover recipes " +
                        "using ingredients you already have.\n\n" +
                        "Storage: SQLite Database\n" +
                        "App version: 1.0\n\n" +
                        "Profile: Pantry User"
        );
        description.setTextSize(17);
        description.setTextColor(Color.DKGRAY);
        description.setPadding(0, 32, 0, 32);
        layout.addView(description);

        Button backButton = new Button(this);
        backButton.setText("Back to Pantry");
        backButton.setOnClickListener(v -> finish());
        layout.addView(backButton);

        setContentView(layout);
    }
}