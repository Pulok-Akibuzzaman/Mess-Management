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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private TextView tvTotalAmount, tvPurchasesCount, tvMonthLabel;

    private LinearLayout btn_home, btn_member, btn_meals, btn_bazar, btn_cash, btn_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bazar);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = pref.getString("role", "Member");
        isAdmin = "Admin".equalsIgnoreCase(role);
        isMember = !"Admin".equalsIgnoreCase(role) && !"Bua".equalsIgnoreCase(role);

        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvPurchasesCount = findViewById(R.id.tv_purchases_subtitle);
        tvMonthLabel = findViewById(R.id.tv_total_label);

        // Update Month Label dynamically
        Calendar cal = Calendar.getInstance();
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        int year = cal.get(Calendar.YEAR);
        if (tvMonthLabel != null) tvMonthLabel.setText(month.toUpperCase() + " " + year + " TOTAL BAZAR");

        rvHistory = findViewById(R.id.rv_purchase_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new BazarAdapter(historyList, 
                (isAdmin || isMember) ? this::confirmDeleteItem : null, 
                (isAdmin || isMember) ? this::showEditBazarDialog : null);
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

        fetchBazarFromCloud();
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
        int idxBy     = c.getColumnIndex("bought_by");

        while (c.moveToNext()) {
            int id = c.getInt(idxId);
            String name = c.getString(idxName);
            double amount = c.getDouble(idxAmount);
            String date = c.getString(idxDate);
            String boughtBy = (idxBy != -1) ? c.getString(idxBy) : "Admin";
            historyList.add(new Bazar(id, name, date, amount, boughtBy));
        }
        c.close();

        adapter.notifyDataSetChanged();

        updateBazarSummary();
    }

    private void updateBazarSummary() {
        double total = dbHelper.getTotalBazar();
        int count = dbHelper.getBazarCount();
        
        if (tvTotalAmount != null) tvTotalAmount.setText("৳" + String.format(Locale.getDefault(), "%,.0f", total));
        if (tvPurchasesCount != null) tvPurchasesCount.setText(count + " purchases this month");
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
                SharedPreferences userPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String currentUserName = userPref.getString("name", "Admin");
                dbHelper.addBazarItem(name, amount, date, currentUserName);
                // Automatically record in Cash Ledger
                dbHelper.addCashTransaction("Bazar: " + name, amount, "OUT", date, currentUserName, "");

                // Sync to Supabase
                String json = "{" +
                        "\"item_name\": \"" + name + "\"," +
                        "\"amount\": " + amount + "," +
                        "\"date\": \"" + date + "\"," +
                        "\"bought_by\": \"" + currentUserName + "\"" +
                        "}";
                RemoteAccess.getInstance().syncToSupabase("bazar", json);
            }

            loadHistoryData();
            bottomSheet.dismiss();
        });

        bottomSheet.show();
    }

    private void setupNavigation() {
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = sp.getString("role", "Member");
        boolean isBua = "Bua".equalsIgnoreCase(role);

        findViewById(R.id.btn_home_layout).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
            if (isBua) {
                startActivity(new Intent(this, BuaManagementActivity.class));
            } else {
                startActivity(new Intent(this, MemberActivity.class));
            }
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> startActivity(new Intent(this, MealRoutineActivity.class)));
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> {
            // Already here
        });
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> startActivity(new Intent(this, CashLedgerActivity.class)));
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> startActivity(new Intent(this, AllFeaturesActivity.class)));
    }

    private void fetchBazarFromCloud() {
        new Thread(() -> {
            String response = RemoteAccess.getInstance().syncFromSupabase("bazar", "order=id.desc");
            if (response != null && !response.isEmpty()) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String name = obj.getString("item_name");
                        double amount = obj.getDouble("amount");
                        String date = obj.getString("date");
                        String boughtBy = obj.getString("bought_by");

                        if (!bazarExistsLocally(name, amount, date)) {
                            dbHelper.addBazarItem(name, amount, date, boughtBy);
                        }
                    }
                    runOnUiThread(this::loadHistoryData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean bazarExistsLocally(String name, double amount, String date) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT id FROM bazar WHERE item_name=? AND amount=? AND date=?",
                new String[]{name, String.valueOf(amount), date});
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }
}
