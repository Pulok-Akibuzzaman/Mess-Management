package com.project.messmanagement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MemberActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private MemberAdapter adapter;
    private final List<Member> memberList = new ArrayList<>();
    private EditText etSearch;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = pref.getString("role", "Member");
        isAdmin = "Admin".equalsIgnoreCase(role);

        RecyclerView rvMembers = findViewById(R.id.rvMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new MemberAdapter(memberList, isAdmin,
                isAdmin ? new MemberAdapter.OnMemberLongClickListener() {
                    @Override
                    public void onItemLongClick(int position, Member member) {
                        showConsentDialog(position);
                    }
                } : null,
                isAdmin ? new MemberAdapter.OnMemberClickListener() {
                    @Override
                    public void onItemClick(int position, Member member) {
                        showEditMemberDialog(member);
                    }
                } : null);
        rvMembers.setAdapter(adapter);

        loadMembers(null);

        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadMembers(s.toString().trim());
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        setupNavigation();

        ImageButton btnAdd = findViewById(R.id.btnAdd);
        // Admin can no longer add new members manually to prevent login issues
        btnAdd.setVisibility(View.GONE);
    }

    /** Reloads memberList from the database (optionally filtered) and refreshes the adapter. */
    private void loadMembers(String query) {
        memberList.clear();

        Cursor c = (query == null || query.isEmpty())
                ? dbHelper.getAllMembers()
                : dbHelper.searchMembers(query);

        int idxId     = c.getColumnIndexOrThrow("id");
        int idxName   = c.getColumnIndexOrThrow("name");
        int idxRoom   = c.getColumnIndexOrThrow("room");
        int idxStatus = c.getColumnIndexOrThrow("status");
        int idxEmail  = c.getColumnIndexOrThrow("email");
        int idxPhone  = c.getColumnIndexOrThrow("phone");
        int idxDate   = c.getColumnIndexOrThrow("join_date");
        int idxPaid   = c.getColumnIndexOrThrow("paid_amount");
        int idxPass   = c.getColumnIndexOrThrow("password");

        // Fetch shared values ONCE for efficiency
        int residentCount = dbHelper.getResidentCount();
        int activeCount = dbHelper.getActiveMemberCount();
        double utilities = dbHelper.getUtilitiesTotal();
        double buaSalary = dbHelper.getBuaSalary();
        double houseRent = dbHelper.getHouseRent();
        double occasionCost = dbHelper.getTotalOccasionCost();
        double otherExpenses = dbHelper.getUtilityTotalByType("Others");
        
        double sharedUtility = residentCount > 0 ? (utilities + buaSalary + houseRent + otherExpenses) / residentCount : 0; 
        double sharedOccasion = activeCount > 0 ? occasionCost / activeCount : 0;
        
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        double fixedRate = sp.getFloat("fixed_meal_rate", 0.0f);

        while (c.moveToNext()) {
            int id        = c.getInt(idxId);
            String name   = c.getString(idxName);
            String room   = c.getString(idxRoom);
            String status = c.getString(idxStatus);
            String email  = c.getString(idxEmail);
            String phone  = c.getString(idxPhone);
            String date   = c.getString(idxDate);
            double paid   = c.getDouble(idxPaid);
            String pass   = c.getString(idxPass);

            // Fetch actual meals from DB (using direct name for guest meals)
            int userMeals = dbHelper.getUserTotalMeals(email, name);
            
            double mealShare = userMeals * fixedRate;
            double mySharedTotal = sharedUtility;
            if ("Active".equalsIgnoreCase(status)) {
                mySharedTotal += sharedOccasion;
            }
            
            double totalShare = mealShare + mySharedTotal;
            double finalDue = totalShare - paid;

            memberList.add(new Member(id, name, initialsOf(name), room, 
                    phone != null ? phone : "N/A", userMeals, "৳" + (int)finalDue, status, email, date, paid, pass, "৳" + (int)mealShare));
        }
        c.close();

        adapter.notifyDataSetChanged();
    }

    private String initialsOf(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                sb.append(Character.toUpperCase(parts[i].charAt(0)));
            }
        }
        return sb.toString();
    }

    /** Shown on long-press of a member card. */
    private void showConsentDialog(int position) {
        Member member = memberList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete Member")
                .setMessage("Are you sure you want to delete " + member.name + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteMember(member.id);
                    loadMembers(etSearch.getText().toString().trim());
                    Toast.makeText(this, member.name + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /** existingMember == null → add mode. Otherwise → edit mode, pre-filled and saved via update. */
    private void showMemberDialog(Member existingMember) {
        boolean isEdit = existingMember != null;

        BottomSheetDialog bottomSheet = new BottomSheetDialog(MemberActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
        bottomSheet.setContentView(view);

        // Fix for keyboard scrolling the whole screen
        if (bottomSheet.getWindow() != null) {
            bottomSheet.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        bottomSheet.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            android.widget.FrameLayout internalSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (internalSheet != null) {
                com.google.android.material.bottomsheet.BottomSheetBehavior behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(internalSheet);
                behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                internalSheet.getLayoutParams().height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                internalSheet.requestLayout();
            }
        });

        EditText etFullName   = view.findViewById(R.id.etFullName);
        EditText etRoomNumber = view.findViewById(R.id.etRoomNumber);
        EditText etEmail      = view.findViewById(R.id.etEmail);
        EditText etPhone      = view.findViewById(R.id.etContact);
        EditText etJoinDate   = view.findViewById(R.id.etJoinDate);
        Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);
        TextView tvTitle      = view.findViewById(R.id.tvMemberDialogTitle);
        Button btnAction      = view.findViewById(R.id.btnAddMember);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Active", "Away"});
        spinnerStatus.setAdapter(statusAdapter);

        final Calendar calendar = Calendar.getInstance();
        final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        if (isEdit) {
            tvTitle.setText("Edit Member Details");
            btnAction.setText("Update Information");
            
            etFullName.setText(existingMember.name);
            etRoomNumber.setText(existingMember.room);
            etEmail.setText(existingMember.email);
            etPhone.setText(existingMember.phone);
            etJoinDate.setText(existingMember.joinDate);
            
            // Disable email editing to maintain login integrity
            etEmail.setEnabled(false);
            etEmail.setAlpha(0.6f);
            
            int statusPos = statusAdapter.getPosition(existingMember.status);
            spinnerStatus.setSelection(statusPos >= 0 ? statusPos : 0);
        } else {
            etJoinDate.setText(sdf.format(calendar.getTime()));
        }

        etJoinDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view1, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                etJoinDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> bottomSheet.dismiss());

        view.findViewById(R.id.btnAddMember).setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String room = etRoomNumber.getText().toString().trim();
            String email = etEmail.getText().toString().trim().toLowerCase();
            String phone = etPhone.getText().toString().trim();
            String date = etJoinDate.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem() != null
                    ? spinnerStatus.getSelectedItem().toString() : "Active";

            if (name.isEmpty() || room.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEdit) {
                dbHelper.updateMember(existingMember.id, name, room, status, email, phone, date, existingMember.password, existingMember.paidAmount);
                Toast.makeText(this, "Member updated", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addMember(name, room, status, email, phone, date, "1234", 0.0);
            }

            etSearch.setText("");
            loadMembers(null);
            bottomSheet.dismiss();
        });

        bottomSheet.show();
    }

    private void showEditMemberDialog(Member member) {
        showMemberDialog(member);
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
                // Already here
            }
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> startActivity(new Intent(this, MealRoutineActivity.class)));
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> startActivity(new Intent(this, BazarActivity.class)));
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> startActivity(new Intent(this, CashLedgerActivity.class)));
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> startActivity(new Intent(this, AllFeaturesActivity.class)));
    }
}
