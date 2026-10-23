package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_ITEMS = "pantry_items";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_CATEGORY = "category";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY_DATE = "expiry_date";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " INTEGER NOT NULL, " +
                COL_CATEGORY + " TEXT NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL DEFAULT 'pieces', " +
                COL_EXPIRY_DATE + " TEXT)";
        db.execSQL(createTable);
        db.execSQL("CREATE TABLE IF NOT EXISTS recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "steps TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "required_quantity INTEGER NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id))");
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "steps TEXT NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "recipe_id INTEGER NOT NULL, " +
                    "ingredient_name TEXT NOT NULL, " +
                    "required_quantity INTEGER NOT NULL, " +
                    "FOREIGN KEY(recipe_id) REFERENCES recipes(id))");
        }

        if (oldVersion < 3) {
            seedAdditionalRecipes(db);
        }

        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_ITEMS +
                    " ADD COLUMN " + COL_UNIT +
                    " TEXT NOT NULL DEFAULT 'pieces'");

            db.execSQL("ALTER TABLE " + TABLE_ITEMS +
                    " ADD COLUMN " + COL_EXPIRY_DATE + " TEXT");
        }
    }

    public boolean addItem(String name, int quantity, String category,
                           String unit, String expiryDate) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_CATEGORY, category);
        values.put(COL_UNIT, unit);
        values.put(COL_EXPIRY_DATE, expiryDate);

        long result = db.insert(TABLE_ITEMS, null, values);
        return result != -1;
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                TABLE_ITEMS,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " ASC"
        );
    }

    public boolean updateQuantity(int id, int quantity) {
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY, quantity);

        int rows = getWritableDatabase().update(
                TABLE_ITEMS,
                values,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        return rows > 0;
    }

    public boolean deleteItem(int id) {
        int rows = getWritableDatabase().delete(
                TABLE_ITEMS,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        return rows > 0;
    }
    private void seedRecipes(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM recipes", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count > 0) {
            return;
        }

        addRecipe(db, "Tomato Rice",
                "1. Cook the rice. 2. Chop and cook the tomatoes. " +
                        "3. Mix together and season to taste.",
                new String[]{"rice", "tomato"}, new int[]{1, 2});

        addRecipe(db, "Egg Fried Rice",
                "1. Cook the rice. 2. Scramble the eggs. " +
                        "3. Mix the rice and eggs in a pan and cook thoroughly.",
                new String[]{"rice", "egg"}, new int[]{1, 2});

        addRecipe(db, "Vegetable Rice",
                "1. Cook the rice. 2. Chop and cook the carrots and onions. " +
                        "3. Mix everything together.",
                new String[]{"rice", "carrot", "onion"}, new int[]{1, 1, 1});

        addRecipe(db, "Tomato Omelette",
                "1. Beat the eggs. 2. Chop the tomatoes. " +
                        "3. Mix and cook in a frying pan.",
                new String[]{"egg", "tomato"}, new int[]{2, 1});

        addRecipe(db, "Mashed Potatoes",
                "1. Peel and boil the potatoes. 2. Mash until smooth. " +
                        "3. Add seasoning to taste.",
                new String[]{"potato"}, new int[]{3});

        addRecipe(db, "Carrot Salad",
                "1. Wash and grate the carrots. " +
                        "2. Mix with your preferred dressing.",
                new String[]{"carrot"}, new int[]{2});

        addRecipe(db, "Onion Omelette",
                "1. Beat the eggs. 2. Chop the onion. " +
                        "3. Cook both together in a frying pan.",
                new String[]{"egg", "onion"}, new int[]{2, 1});

        addRecipe(db, "Potato and Egg Hash",
                "1. Dice and cook the potatoes. 2. Add the eggs. " +
                        "3. Cook thoroughly and season.",
                new String[]{"potato", "egg"}, new int[]{2, 2});

        addRecipe(db, "Tomato Potato Stew",
                "1. Chop the potatoes and tomatoes. " +
                        "2. Simmer with water and seasoning until tender.",
                new String[]{"potato", "tomato"}, new int[]{2, 2});

        addRecipe(db, "Carrot Rice",
                "1. Grate the carrots. 2. Cook the rice. " +
                        "3. Mix and cook together until tender.",
                new String[]{"rice", "carrot"}, new int[]{1, 1});

        addRecipe(db, "Tomato Egg Scramble",
                "1. Chop the tomatoes. 2. Beat the eggs. " +
                        "3. Cook together in a frying pan.",
                new String[]{"tomato", "egg"}, new int[]{1, 2});

        addRecipe(db, "Potato and Carrot Mash",
                "1. Peel and boil the potatoes and carrots. " +
                        "2. Drain and mash together.",
                new String[]{"potato", "carrot"}, new int[]{2, 1});

        addRecipe(db, "Tomato Onion Rice",
                "1. Cook the rice. 2. Chop and cook the tomato and onion. " +
                        "3. Mix everything together.",
                new String[]{"rice", "tomato", "onion"}, new int[]{1, 1, 1});

        addRecipe(db, "Vegetable Egg Scramble",
                "1. Beat the eggs. 2. Chop the carrots and onions. " +
                        "3. Cook the vegetables and eggs thoroughly.",
                new String[]{"egg", "carrot", "onion"}, new int[]{2, 1, 1});

        addRecipe(db, "Potato Vegetable Bowl",
                "1. Boil the potatoes and carrots. " +
                        "2. Chop the onion. 3. Combine and season.",
                new String[]{"potato", "carrot", "onion"},
                new int[]{2, 1, 1});
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps,
                           String[] ingredients, int[] quantities) {
        ContentValues recipe = new ContentValues();
        recipe.put("name", name);
        recipe.put("steps", steps);

        long recipeId = db.insert("recipes", null, recipe);

        for (int i = 0; i < ingredients.length; i++) {
            addRecipeIngredient(db, recipeId, ingredients[i], quantities[i]);
        }
    }

    private void addRecipeIngredient(SQLiteDatabase db, long recipeId,
                                     String name, int quantity) {
        ContentValues ingredient = new ContentValues();
        ingredient.put("recipe_id", recipeId);
        ingredient.put("ingredient_name", name);
        ingredient.put("required_quantity", quantity);
        db.insert("recipe_ingredients", null, ingredient);
    }

    private void seedAdditionalRecipes(SQLiteDatabase db) {
        addRecipe(db, "Vegetable Rice",
                "1. Cook rice. 2. Cook chopped carrots and onions. " +
                        "3. Mix everything together.",
                new String[]{"rice", "carrot", "onion"},
                new int[]{1, 1, 1});

        addRecipe(db, "Tomato Omelette",
                "1. Beat eggs. 2. Chop tomatoes. " +
                        "3. Cook together in a frying pan.",
                new String[]{"egg", "tomato"}, new int[]{2, 1});

        addRecipe(db, "Mashed Potatoes",
                "1. Boil potatoes until soft. 2. Drain and mash. " +
                        "3. Season to taste.",
                new String[]{"potato"}, new int[]{3});

        addRecipe(db, "Carrot Salad",
                "1. Wash and grate carrots. 2. Add your preferred dressing.",
                new String[]{"carrot"}, new int[]{2});

        addRecipe(db, "Onion Omelette",
                "1. Beat eggs. 2. Chop onion. " +
                        "3. Cook together in a frying pan.",
                new String[]{"egg", "onion"}, new int[]{2, 1});

        addRecipe(db, "Potato and Egg Hash",
                "1. Dice and cook potatoes. 2. Add eggs. " +
                        "3. Cook thoroughly.",
                new String[]{"potato", "egg"}, new int[]{2, 2});

        addRecipe(db, "Tomato Potato Stew",
                "1. Chop potatoes and tomatoes. " +
                        "2. Simmer with water and seasoning until tender.",
                new String[]{"potato", "tomato"}, new int[]{2, 2});

        addRecipe(db, "Carrot Rice",
                "1. Grate carrots. 2. Cook rice. " +
                        "3. Mix and cook together.",
                new String[]{"rice", "carrot"}, new int[]{1, 1});

        addRecipe(db, "Tomato Egg Scramble",
                "1. Chop tomatoes. 2. Beat eggs. " +
                        "3. Cook together thoroughly.",
                new String[]{"tomato", "egg"}, new int[]{1, 2});

        addRecipe(db, "Potato and Carrot Mash",
                "1. Boil potatoes and carrots. 2. Drain and mash together.",
                new String[]{"potato", "carrot"}, new int[]{2, 1});

        addRecipe(db, "Tomato Onion Rice",
                "1. Cook rice. 2. Cook chopped tomato and onion. " +
                        "3. Mix together.",
                new String[]{"rice", "tomato", "onion"},
                new int[]{1, 1, 1});

        addRecipe(db, "Vegetable Egg Scramble",
                "1. Beat eggs. 2. Chop carrots and onions. " +
                        "3. Cook everything thoroughly.",
                new String[]{"egg", "carrot", "onion"},
                new int[]{2, 1, 1});

        addRecipe(db, "Potato Vegetable Bowl",
                "1. Boil potatoes and carrots. 2. Chop onion. " +
                        "3. Combine and season.",
                new String[]{"potato", "carrot", "onion"},
                new int[]{2, 1, 1});
    }



}
