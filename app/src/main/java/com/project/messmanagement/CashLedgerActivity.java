package com.project.messmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CashLedgerActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private CashLedgerAdapter adapter;
    private final List<CashTransaction> transactionList = new ArrayList<>();
    private TextView tvBalance, tvIncoming, tvOutgoing;
    private boolean isAdmin = false;
    private boolean isMember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_ledger);

        db = new DatabaseHelper(this);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = pref.getString("role", "Member");
        isAdmin = "Admin".equalsIgnoreCase(role);
        isMember = !"Admin".equalsIgnoreCase(role) && !"Bua".equalsIgnoreCase(role);

        RecyclerView rvTransactions = findViewById(R.id.transaction_container);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new CashLedgerAdapter(transactionList,
                (isAdmin || isMember) ? (position, transaction) -> showCashDialog(
                        transaction.id, transaction.description, transaction.amount,
                        transaction.type, transaction.date) : null,
                (isAdmin || isMember) ? (position, transaction) -> confirmDeleteTransaction(transaction) : null);
        rvTransactions.setAdapter(adapter);

        tvBalance = findViewById(R.id.tv_balance_amount);
        tvIncoming = findViewById(R.id.tv_incoming);
        tvOutgoing = findViewById(R.id.tv_outgoing);

        View fabAdd = findViewById(R.id.fab_add_transaction);
        if (isAdmin || isMember) {
            fabAdd.setOnClickListener(v -> showCashDialog(-1, "", 0, "IN", ""));
        } else {
            fabAdd.setVisibility(View.GONE);
        }

        fetchTransactionsFromCloud();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCashList();
    }

    private void refreshCashList() {
        transactionList.clear();

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = pref.getString("email", "");
        String role = pref.getString("role", "Member");

        Cursor cursor = db.getFilteredCashTransactions(userEmail, role);
        if (cursor != null) {
            int idxId = cursor.getColumnIndexOrThrow("id");
            int idxDesc = cursor.getColumnIndexOrThrow("description");
            int idxAmount = cursor.getColumnIndexOrThrow("amount");
            int idxType = cursor.getColumnIndexOrThrow("type");
            int idxDate = cursor.getColumnIndex("date");
            int idxPerformed = cursor.getColumnIndex("performed_by");
            int idxMemberEmail = cursor.getColumnIndex("member_email");

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idxId);
                String desc = cursor.getString(idxDesc);
                double amount = cursor.getDouble(idxAmount);
                String type = cursor.getString(idxType);
                String date = idxDate != -1 ? cursor.getString(idxDate) : "N/A";
                String performedBy = idxPerformed != -1 ? cursor.getString(idxPerformed) : "System";
                String memberEmail = idxMemberEmail != -1 ? cursor.getString(idxMemberEmail) : "";

                transactionList.add(new CashTransaction(id, desc, amount, type, date, performedBy, memberEmail));
            }
            cursor.close();
        }

        adapter.notifyDataSetChanged();

        if (isAdmin) {
            tvBalance.setText("৳" + (int) db.getCashBalance());
            tvIncoming.setText("↗ ৳" + (int) db.getTotalIn() + " in");
        } else {
            double myPaid = db.getMemberPaidAmount(userEmail);
            tvBalance.setText("৳" + (int) myPaid);
            tvIncoming.setText("↗ ৳" + (int) myPaid + " in");
            ((TextView)findViewById(R.id.tv_balance_label)).setText("My Payments");
        }
        tvOutgoing.setText("↘ ৳" + (int) db.getTotalOut() + " out");
    }

    /** Shown on long-press of a transaction card. */
    private void confirmDeleteTransaction(CashTransaction transaction) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deleteCashTransaction(transaction.id);
                    refreshCashList();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCashDialog(final int id, String initialDesc, double initialAmount, String initialType, String initialDate) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_cash, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        final Spinner spinnerType = view.findViewById(R.id.spinnerType);
        final Spinner spinnerMember = view.findViewById(R.id.spinnerMember);
        final View layoutMemberSelect = view.findViewById(R.id.layout_member_select);
        final EditText etDesc = view.findViewById(R.id.etDescription);
        final EditText etAmount = view.findViewById(R.id.etAmount);
        final EditText etDate = view.findViewById(R.id.etDate);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"IN", "OUT"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerAdapter);

        final List<String> memberNames = new ArrayList<>();
        final List<Integer> memberIds = new ArrayList<>();
        final List<String> memberEmails = new ArrayList<>();
        memberNames.add("None / Other");
        memberIds.add(-1);
        memberEmails.add("");

        Cursor memberCursor = db.getAllMembers();
        if (memberCursor != null) {
            while (memberCursor.moveToNext()) {
                memberNames.add(memberCursor.getString(memberCursor.getColumnIndexOrThrow("name")));
                memberIds.add(memberCursor.getInt(memberCursor.getColumnIndexOrThrow("id")));
                memberEmails.add(memberCursor.getString(memberCursor.getColumnIndexOrThrow("email")));
            }
            memberCursor.close();
        }

        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, memberNames);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMember.setAdapter(memberAdapter);

        spinnerType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id_view) {
                // ONLY ADMIN can see the member selector, and only for IN transactions
                if (isAdmin && position == 0 && id == -1) { 
                    layoutMemberSelect.setVisibility(View.VISIBLE);
                } else {
                    layoutMemberSelect.setVisibility(View.GONE);
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        if (id != -1) {
            tvTitle.setText("Edit Transaction");
            etDesc.setText(initialDesc);
            etAmount.setText(String.valueOf(initialAmount));
            etDate.setText(initialDate);
            spinnerType.setSelection(initialType.equals("IN") ? 0 : 1);
            layoutMemberSelect.setVisibility(View.GONE);
        } else {
            etDate.setText(sdf.format(calendar.getTime()));
        }

        etDate.setOnClickListener(v -> new DatePickerDialog(this, (view1, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            etDate.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());

        btnSave.setOnClickListener(v -> {
            String desc = etDesc.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String date = etDate.getText().toString();

            if (desc.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (id == -1) {
                SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String currentUserName = pref.getString("name", "User");
                String currentUserEmail = pref.getString("email", "");
                
                String targetMemberEmail;

                if (isAdmin) {
                    int selectedPos = spinnerMember.getSelectedItemPosition();
                    targetMemberEmail = memberEmails.get(selectedPos);
                } else {
                    targetMemberEmail = currentUserEmail;
                }

                String finalDesc = desc.isEmpty() ? "Payment from " + currentUserName : desc;

                // DUPLICATE BILL CHECK
                if (type.equals("IN") && finalDesc.startsWith("Bill Payment: ")) {
                    String billType = finalDesc.substring("Bill Payment: ".length());
                    String monthYear = date.length() >= 8 ? date.substring(3) : "";
                    if (!monthYear.isEmpty() && db.isBillPaidThisMonth(targetMemberEmail, billType, monthYear)) {
                        Toast.makeText(this, "Already paid " + billType + " for this month!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                db.addCashTransaction(finalDesc, amount, type, date, currentUserName, targetMemberEmail);
                
                // Sync to Supabase
                String json = "{" +
                        "\"description\": \"" + finalDesc + "\"," +
                        "\"amount\": " + amount + "," +
                        "\"type\": \"" + type + "\"," +
                        "\"date\": \"" + date + "\"," +
                        "\"performed_by\": \"" + currentUserName + "\"," +
                        "\"member_email\": \"" + targetMemberEmail + "\"" +
                        "}";
                RemoteAccess.getInstance().syncToSupabase("cash", json);

                if (type.equals("IN")) {
                    int memberId;
                    if (isAdmin) {
                        memberId = memberIds.get(spinnerMember.getSelectedItemPosition());
                    } else {
                        memberId = db.getMemberIdByEmail(currentUserEmail);
                    }
                    
                    if (memberId != -1) {
                        db.addMemberPayment(memberId, amount);
                    }
                }
                
                Toast.makeText(this, "Transaction added", Toast.LENGTH_SHORT).show();
            } else {
                db.updateCashTransaction(id, desc, amount, type, date);
                Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();
            }
            refreshCashList();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
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
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> startActivity(new Intent(this, BazarActivity.class)));
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> {
            // Already here
        });
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> startActivity(new Intent(this, AllFeaturesActivity.class)));
    }

    private void fetchTransactionsFromCloud() {
        new Thread(() -> {
            String response = RemoteAccess.getInstance().syncFromSupabase("cash", "order=id.desc");
            if (response != null && !response.isEmpty()) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String desc = obj.getString("description");
                        double amount = obj.getDouble("amount");
                        String type = obj.getString("type");
                        String date = obj.getString("date");
                        String performedBy = obj.getString("performed_by");
                        String memberEmail = obj.optString("member_email", "");

                        if (!transactionExistsLocally(desc, amount, date)) {
                            db.addCashTransaction(desc, amount, type, date, performedBy, memberEmail);
                        }
                    }
                    runOnUiThread(this::refreshCashList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean transactionExistsLocally(String desc, double amount, String date) {
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT id FROM cash WHERE description=? AND amount=? AND date=?",
                new String[]{desc, String.valueOf(amount), date});
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }
}
