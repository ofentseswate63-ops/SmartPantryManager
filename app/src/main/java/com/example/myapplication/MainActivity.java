package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText itemNameInput;
    private EditText quantityInput;
    private EditText categoryInput;
    private EditText unitInput;
    private EditText expiryDateInput;
    private LinearLayout pantryList;
    private TextView emptyMessage;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemNameInput = findViewById(R.id.itemNameInput);
        quantityInput = findViewById(R.id.quantityInput);
        categoryInput = findViewById(R.id.categoryInput);
        unitInput = findViewById(R.id.unitInput);
        expiryDateInput = findViewById(R.id.expiryDateInput);
        pantryList = findViewById(R.id.pantryList);
        emptyMessage = findViewById(R.id.emptyMessage);

        databaseHelper = new DatabaseHelper(this);

        Button addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(v -> addPantryItem());

        Button viewRecipesButton = findViewById(R.id.viewRecipesButton);
        viewRecipesButton.setOnClickListener(v -> {
            android.content.Intent intent =
                    new android.content.Intent(MainActivity.this,
                            SuggestedRecipesActivity.class);
            startActivity(intent);
        });
        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            android.content.Intent intent =
                    new android.content.Intent(MainActivity.this,
                            SettingsActivity.class);
            startActivity(intent);
        });

        loadPantryItems();
    }

    private void addPantryItem() {
        String name = itemNameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String category = categoryInput.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid quantity.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity <= 0) {
            Toast.makeText(this, "Quantity must be greater than zero.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String unit = unitInput.getText().toString().trim();
        String expiryDate = expiryDateInput.getText().toString().trim();

        if (unit.isEmpty()) {
            unit = "pieces";
        }

        if (expiryDate.isEmpty()) {
            expiryDate = null;
        }

        boolean saved = databaseHelper.addItem(
                name, quantity, category, unit, expiryDate);

        if (saved) {
            itemNameInput.setText("");
            quantityInput.setText("");
            categoryInput.setText("");
            unitInput.setText("");
            expiryDateInput.setText("");
            loadPantryItems();
            Toast.makeText(this, "Pantry item saved!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Could not save item.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPantryItems() {
        pantryList.removeAllViews();

        Cursor cursor = databaseHelper.getAllItems();

        try {
            if (cursor.getCount() == 0) {
                emptyMessage.setVisibility(View.VISIBLE);
            } else {
                emptyMessage.setVisibility(View.GONE);

                while (cursor.moveToNext()) {
                    int id = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                    int quantity = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY));
                    String category = cursor.getString(
                            cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY));
                    String unit = cursor.getString(
                            cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT));

                    String expiryDate = cursor.getString(
                            cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPIRY_DATE));

                    LinearLayout itemRow = new LinearLayout(this);
                    itemRow.setOrientation(LinearLayout.VERTICAL);
                    itemRow.setPadding(16, 16, 16, 16);
                    itemRow.setBackgroundColor(0xFFFFFFFF);

                    TextView itemView = new TextView(this);
                    String itemDetails = name
                            + "\nQuantity: " + quantity + " " + unit
                            + "\nCategory: " + category;

                    if (expiryDate != null && !expiryDate.trim().isEmpty()) {
                        itemDetails += "\nExpiry date: " + expiryDate;
                    } else {
                        itemDetails += "\nExpiry date: Not provided";
                    }

                    itemView.setText(itemDetails);
                    itemView.setTextSize(16);

                    Button deleteButton = new Button(this);
                    Button updateButton = new Button(this);
                    updateButton.setText("Update Quantity");
                    updateButton.setOnClickListener(v -> updateItemQuantity(id));
                    itemRow.addView(updateButton);
                    deleteButton.setText("Delete");
                    deleteButton.setOnClickListener(v -> {
                        databaseHelper.deleteItem(id);
                        loadPantryItems();
                        Toast.makeText(this, "Item deleted.", Toast.LENGTH_SHORT).show();
                    });

                    itemRow.addView(itemView);
                    itemRow.addView(deleteButton);

                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 8, 0, 8);
                    itemRow.setLayoutParams(params);

                    pantryList.addView(itemRow);
                }
            }
        } finally {
            cursor.close();
        }
    }
    private void updateItemQuantity(int id) {
        android.widget.EditText quantityInput =
                new android.widget.EditText(this);

        quantityInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER);

        new android.app.AlertDialog.Builder(this)
                .setTitle("Update Quantity")
                .setMessage("Enter the new quantity:")
                .setView(quantityInput)
                .setPositiveButton("Update", (dialog, which) -> {
                    String text = quantityInput.getText()
                            .toString().trim();

                    if (text.isEmpty()) {
                        Toast.makeText(this,
                                "Please enter a quantity.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int quantity = Integer.parseInt(text);

                    if (quantity <= 0) {
                        Toast.makeText(this,
                                "Quantity must be greater than zero.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean updated =
                            databaseHelper.updateQuantity(id, quantity);

                    if (updated) {
                        loadPantryItems();
                        Toast.makeText(this,
                                "Quantity updated!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
