package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class RecipeDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String recipeName = getIntent().getStringExtra("recipeName");
        String recipeSteps = getIntent().getStringExtra("recipeSteps");

        if (recipeName == null) {
            recipeName = "Recipe Details";
        }

        if (recipeSteps == null) {
            recipeSteps = "No preparation steps available.";
        }

        ScrollView scrollView = new ScrollView(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.rgb(245, 247, 242));

        TextView title = new TextView(this);
        title.setText(recipeName);
        title.setTextSize(28);
        title.setTextColor(Color.rgb(36, 92, 69));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 24);
        layout.addView(title);

        TextView heading = new TextView(this);
        heading.setText("Preparation Instructions");
        heading.setTextSize(20);
        heading.setTextColor(Color.BLACK);
        heading.setPadding(0, 8, 0, 16);
        layout.addView(heading);

        TextView steps = new TextView(this);
        steps.setText(recipeSteps);
        steps.setTextSize(18);
        steps.setTextColor(Color.DKGRAY);
        steps.setLineSpacing(8, 1.0f);
        layout.addView(steps);

        Button backButton = new Button(this);
        backButton.setText("Back to Recipes");
        backButton.setOnClickListener(v -> finish());
        layout.addView(backButton);

        scrollView.addView(layout);
        setContentView(scrollView);
    }
}