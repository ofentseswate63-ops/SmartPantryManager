package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.graphics.Color;
import android.widget.Toast;

public class SuggestedRecipesActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private LinearLayout recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(Color.rgb(245, 247, 242));

        TextView title = new TextView(this);
        title.setText("Suggested Recipes");
        title.setTextSize(26);
        title.setTextColor(Color.rgb(36, 92, 69));
        layout.addView(title);

        Button backButton = new Button(this);
        backButton.setText("Back to Pantry");
        backButton.setOnClickListener(v -> finish());
        layout.addView(backButton);

        recipeList = new LinearLayout(this);
        recipeList.setOrientation(LinearLayout.VERTICAL);
        layout.addView(recipeList);

        setContentView(layout);
        loadRecipes();
    }

    private void loadRecipes() {
        recipeList.removeAllViews();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        java.util.HashMap<String, Integer> pantry =
                new java.util.HashMap<>();

        Cursor pantryCursor = db.rawQuery(
                "SELECT name, quantity FROM pantry_items", null);

        while (pantryCursor.moveToNext()) {
            String name = pantryCursor.getString(0)
                    .trim().toLowerCase();
            int quantity = pantryCursor.getInt(1);

            pantry.put(name, quantity);
        }
        pantryCursor.close();

        Cursor recipes = db.rawQuery(
                "SELECT id, name, steps FROM recipes ORDER BY name",
                null);

        int matchingRecipes = 0;

        while (recipes.moveToNext()) {
            int recipeId = recipes.getInt(0);
            String recipeName = recipes.getString(1);
            String steps = recipes.getString(2);

            Cursor ingredients = db.rawQuery(
                    "SELECT ingredient_name, required_quantity " +
                            "FROM recipe_ingredients WHERE recipe_id = ?",
                    new String[]{String.valueOf(recipeId)});

            boolean canMake = true;

            while (ingredients.moveToNext()) {
                String ingredient = ingredients.getString(0)
                        .trim().toLowerCase();
                int required = ingredients.getInt(1);

                Integer available = pantry.get(ingredient);

                if (available == null) {
                    String plural = ingredient.endsWith("s")
                            ? ingredient.substring(0,
                            ingredient.length() - 1)
                            : ingredient + "s";

                    available = pantry.get(plural);
                }

                if (available == null || available < required) {
                    canMake = false;
                    break;
                }
            }

            ingredients.close();

            if (canMake) {
                matchingRecipes++;

                TextView recipe = new TextView(this);
                recipe.setText("🍽 " + recipeName);
                recipe.setTextSize(19);
                recipe.setTextColor(Color.rgb(36, 92, 69));
                recipe.setPadding(12, 20, 12, 20);
                recipe.setBackgroundColor(Color.WHITE);

                recipe.setOnClickListener(v -> {
                    android.content.Intent intent =
                            new android.content.Intent(
                                    SuggestedRecipesActivity.this,
                                    RecipeDetailActivity.class);

                    intent.putExtra("recipeName", recipeName);
                    intent.putExtra("recipeSteps", steps);

                    startActivity(intent);
                });

                recipeList.addView(recipe);
            }
        }

        recipes.close();

        if (matchingRecipes == 0) {
            TextView empty = new TextView(this);
            empty.setText(
                    "No recipes match your pantry yet. " +
                            "Add the required ingredients and quantities.");
            empty.setTextSize(16);
            empty.setPadding(12, 20, 12, 20);
            recipeList.addView(empty);
        }
    }
}