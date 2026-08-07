package com.project.messmanagement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BazarActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private BazarAdapter adapter;
    private final List<Bazar> historyList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private boolean isAdmin = false;
    private boolean isMember = false;

    private LinearLayout btn_home, btn_member, btn_meals, btn_bazar, btn_cash, btn_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bazar);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = pref.getString("role", "Member");
        isAdmin = "Admin".equalsIgnoreCase(role);
        isMember = "Member".equalsIgnoreCase(role);

        rvHistory = findViewById(R.id.rv_purchase_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new BazarAdapter(historyList, 
                isAdmin ? this::confirmDeleteItem : null, 
                isAdmin ? this::showEditBazarDialog : null);
        rvHistory.setAdapter(adapter);

        loadHistoryData();

        ImageButton btnAdd = findViewById(R.id.btn_add_bazar);
        if (btnAdd != null) {
            if (isAdmin || isMember) {
                btnAdd.setOnClickListener(v -> showBazarDialog(null));
            } else {
                btnAdd.setVisibility(View.GONE);
            }
        }

        setupNavigation();
    }

    /** Reloads historyList from the database and refreshes the adapter. */
    private void loadHistoryData() {
        historyList.clear();

        Cursor c = dbHelper.getAllBazarItems();
        int idxId     = c.getColumnIndexOrThrow("id");
        int idxName   = c.getColumnIndexOrThrow("item_name");
        int idxAmount = c.getColumnIndexOrThrow("amount");
        int idxDate   = c.getColumnIndexOrThrow("date");

        while (c.moveToNext()) {
            int id = c.getInt(idxId);
            String name = c.getString(idxName);
            double amount = c.getDouble(idxAmount);
            String date = c.getString(idxDate);
            historyList.add(new Bazar(id, name, date, amount));
        }
        c.close();

        adapter.notifyDataSetChanged();
    }

    /** Shown on long-press of a bazar item card. */
    private void confirmDeleteItem(Bazar item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete " + item.name + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteBazarItem(item.id);
                    loadHistoryData();
                    Toast.makeText(this, item.name + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showEditBazarDialog(Bazar item) {
        showBazarDialog(item);
    }

    /** existingItem == null → add mode. Otherwise → edit mode, pre-filled and saved via update. */
    private void showBazarDialog(Bazar existingItem) {
        boolean isEdit = existingItem != null;

        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_bazar, null);
        bottomSheet.setContentView(view);

        EditText etItemName = view.findViewById(R.id.etItemName);
        EditText etAmount   = view.findViewById(R.id.etAmount);
        EditText etDate     = view.findViewById(R.id.etDate);
        Button btnSave      = view.findViewById(R.id.btnSave);

        if (isEdit) {
            etItemName.setText(existingItem.name);
            etAmount.setText(String.valueOf(existingItem.amount));
            etDate.setText(existingItem.date);
        }

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog picker = new DatePickerDialog(this, (datePicker, year, month, dayOfMonth) -> {
                Calendar picked = Calendar.getInstance();
                picked.set(year, month, dayOfMonth);
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                etDate.setText(sdf.format(picked.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            picker.show();
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> bottomSheet.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etItemName.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            if (name.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEdit) {
                dbHelper.updateBazarItem(existingItem.id, name, amount, date);
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addBazarItem(name, amount, date);
            }

            loadHistoryData();
            bottomSheet.dismiss();
        });

        bottomSheet.show();
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, CashLedgerActivity.class));
            finish();
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MealRoutineActivity.class));
            finish();
        });
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MemberActivity.class));
            finish();
        });
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, AllFeaturesActivity.class));
            finish();
        });
    }
}