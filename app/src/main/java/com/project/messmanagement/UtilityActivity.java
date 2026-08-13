package com.project.messmanagement;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.Locale;

public class UtilityActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private boolean isAdmin = false;
    private TextView tvTotalAmount, tvElectricity, tvWater, tvInternet, tvGas, tvBua, tvRent;
    private TextView tvPerMember, tvElectricityHead, tvWaterHead, tvInternetHead, tvGasHead, tvBuaHead, tvRentHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility);

        db = new DatabaseHelper(this);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = pref.getString("role", "Member");
        isAdmin = "Admin".equalsIgnoreCase(role);
        boolean isResident = !"Admin".equalsIgnoreCase(role) && !"Bua".equalsIgnoreCase(role);

        initViews();
        loadUtilityData();
        setupNavigation();

        ImageButton btnAddUtility = findViewById(R.id.btnAddUtility);
        if (isAdmin || isResident) {
            btnAddUtility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddUtilityDialog();
                }
            });
        } else {
            btnAddUtility.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvElectricity = findViewById(R.id.tvElectricity);
        tvWater = findViewById(R.id.tvWater);
        tvInternet = findViewById(R.id.tvInternet);
        tvGas = findViewById(R.id.tvGas);
        
        tvPerMember = findViewById(R.id.tvPerMember);
        tvElectricityHead = findViewById(R.id.tvElectricityHead);
        tvWaterHead = findViewById(R.id.tvWaterHead);
        tvInternetHead = findViewById(R.id.tvInternetHead);
        tvGasHead = findViewById(R.id.tvGasHead);
        
        tvBua = findViewById(R.id.tvBua);
        tvBuaHead = findViewById(R.id.tvBuaHead);
        tvRent = findViewById(R.id.tvRent);
        tvRentHead = findViewById(R.id.tvRentHead);
    }

    private void loadUtilityData() {
        double total = db.getUtilitiesTotal();
        double electricity = db.getUtilityTotalByType("Electricity");
        double ePaid = db.getUtilityCollected("Electricity");

        double water = db.getUtilityTotalByType("Water");
        double wPaid = db.getUtilityCollected("Water");

        double internet = db.getUtilityTotalByType("Internet");
        double iPaid = db.getUtilityCollected("Internet");

        double gas = db.getUtilityTotalByType("Gas");
        double gPaid = db.getUtilityCollected("Gas");

        // Use direct utility table check to stay in sync with Admin's "Set Official Bill"
        double bua = db.getUtilityTotalByType("Bua Salary");
        if (bua == 0) bua = db.getBuaSalary(); // Fallback to profile value
        double bPaid = db.getUtilityCollected("Bua Salary");

        double rent = db.getUtilityTotalByType("House Rent");
        double rPaid = db.getUtilityCollected("House Rent");
        
        int memberCount = db.getResidentCount();
        if (memberCount <= 0) memberCount = 1;

        double grandTotal = electricity + water + internet + gas + bua + rent;
        tvTotalAmount.setText(String.format(Locale.US, "৳%.0f", grandTotal));
        
        // Show Total vs Collected
        double totalCollected = ePaid + wPaid + iPaid + gPaid + bPaid + rPaid;
        tvPerMember.setText(String.format(Locale.US, "Collected: ৳%.0f / ৳%.0f", totalCollected, grandTotal));

        // Update cards with Paid vs Bill info
        tvElectricity.setText(String.format(Locale.US, "৳%.0f / ৳%.0f", ePaid, electricity));
        tvElectricityHead.setText(String.format(Locale.US, "৳%.0f per head", electricity / memberCount));

        tvWater.setText(String.format(Locale.US, "৳%.0f / ৳%.0f", wPaid, water));
        tvWaterHead.setText(String.format(Locale.US, "৳%.0f per head", water / memberCount));

        tvInternet.setText(String.format(Locale.US, "৳%.0f / ৳%.0f", iPaid, internet));
        tvInternetHead.setText(String.format(Locale.US, "৳%.0f per head", internet / memberCount));

        tvGas.setText(String.format(Locale.US, "৳%.0f / ৳%.0f", gPaid, gas));
        tvGasHead.setText(String.format(Locale.US, "৳%.0f per head", gas / memberCount));

        tvBua.setText(String.format(Locale.US, "৳%.0f / ৳%.0f", bPaid, bua));
        tvBuaHead.setText(String.format(Locale.US, "৳%.0f per head", bua / memberCount));

        tvRent.setText(String.format(Locale.US, "৳%.0f / ৳%.0f", rPaid, rent));
        tvRentHead.setText(String.format(Locale.US, "৳%.0f per head", rent / memberCount));
    }

    private void showAddUtilityDialog() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(UtilityActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_utility, null);
        bottomSheet.setContentView(view);

        // Fix for keyboard pushing the whole dialog off-screen
        if (bottomSheet.getWindow() != null) {
            bottomSheet.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        bottomSheet.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            android.widget.FrameLayout bottomSheetView = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetView != null) {
                com.google.android.material.bottomsheet.BottomSheetBehavior behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheetView);
                behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                
                // Force height to Match Parent so adjustResize can work inside the sheet
                android.view.ViewGroup.LayoutParams layoutParams = bottomSheetView.getLayoutParams();
                layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                bottomSheetView.setLayoutParams(layoutParams);
            }
        });

        Spinner spinnerBillType = view.findViewById(R.id.spinnerBillType);
        Spinner spinnerEntryType = view.findViewById(R.id.spinnerEntryType);
        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etMonth = view.findViewById(R.id.etMonth);
        EditText etDueDate = view.findViewById(R.id.etDueDate);
        Button btnAddBill = view.findViewById(R.id.btnAddBill);
        ImageButton btnClose = view.findViewById(R.id.btnCloseUtility);

        // Populate Month and Due Date dynamically
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat monthFormat = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.US);
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.US);

        if (etMonth != null) etMonth.setText(monthFormat.format(cal.getTime()));
        if (etDueDate != null) {
            cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
            etDueDate.setText(dateFormat.format(cal.getTime()));
        }

        // Date Pickers for Month and Due Date (Restricted to Admin)
        if (isAdmin) {
            if (etMonth != null) {
                etMonth.setOnClickListener(v -> {
                    java.util.Calendar mCal = java.util.Calendar.getInstance();
                    new android.app.DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                        mCal.set(year, month, dayOfMonth);
                        etMonth.setText(monthFormat.format(mCal.getTime()));
                    }, mCal.get(java.util.Calendar.YEAR), mCal.get(java.util.Calendar.MONTH), mCal.get(java.util.Calendar.DAY_OF_MONTH)).show();
                });
            }

            if (etDueDate != null) {
                etDueDate.setOnClickListener(v -> {
                    java.util.Calendar dCal = java.util.Calendar.getInstance();
                    new android.app.DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                        dCal.set(year, month, dayOfMonth);
                        etDueDate.setText(dateFormat.format(dCal.getTime()));
                    }, dCal.get(java.util.Calendar.YEAR), dCal.get(java.util.Calendar.MONTH), dCal.get(java.util.Calendar.DAY_OF_MONTH)).show();
                });
            }
        } else {
            // Non-admins can only see the current values, not change them
            if (etMonth != null) {
                etMonth.setClickable(false);
                etMonth.setFocusable(false);
            }
            if (etDueDate != null) {
                etDueDate.setClickable(false);
                etDueDate.setFocusable(false);
            }
        }

        ArrayAdapter<String> billAdapter = new ArrayAdapter<>(UtilityActivity.this,
                android.R.layout.simple_spinner_item,
                new String[]{"Electricity", "Water", "Internet", "Gas", "Bua Salary", "House Rent", "Others"});
        billAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBillType.setAdapter(billAdapter);

        // Entry Type Logic
        if (isAdmin) {
            spinnerEntryType.setVisibility(View.GONE);
            // Default Admin to "Set Official Bill"
            ((TextView)view.findViewById(R.id.tvDialogTitle)).setText("Set Official Bill");
            btnAddBill.setText("Save Bill Amount");
        } else {
            spinnerEntryType.setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.tvDialogTitle)).setText("Pay My Share");
            btnAddBill.setText("Confirm Payment");
            
            // Auto-calculate suggested payment amount for Member
            int residents = db.getResidentCount();
            if (residents > 0) {
                spinnerBillType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        String type = billAdapter.getItem(position);
                        double totalForType;
                        
                        if ("Bua Salary".equalsIgnoreCase(type)) {
                            totalForType = db.getBuaSalary();
                        } else {
                            totalForType = db.getUtilityTotalByType(type);
                        }
                        
                        if (residents > 0) {
                            etAmount.setText(String.valueOf((int)(totalForType / residents)));
                        }
                    }
                    @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });
            }
        }

        btnAddBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String billType = spinnerBillType.getSelectedItem().toString();
                String amountStr = etAmount.getText().toString();
                
                if (!amountStr.isEmpty()) {
                    double amount = Double.parseDouble(amountStr);
                    String today = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.US).format(new java.util.Date());
                    
                    SharedPreferences userPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    String currentUserName = userPref.getString("name", "User");
                    String currentUserEmail = userPref.getString("email", "");

                    if (isAdmin) {
                        db.addUtility(billType, amount, today);
                        Toast.makeText(UtilityActivity.this, "Official Bill Set Dynamically", Toast.LENGTH_SHORT).show();
                    } else {
                        // Member records their Payment
                        db.addCashTransaction("Bill Payment: " + billType, amount, "IN", today, currentUserName, currentUserEmail);
                        int mid = db.getMemberIdByEmail(currentUserEmail);
                        if (mid != -1) db.addMemberPayment(mid, amount);
                        Toast.makeText(UtilityActivity.this, "Your Payment Recorded Universally", Toast.LENGTH_SHORT).show();
                    }
                    
                    loadUtilityData();
                    bottomSheet.dismiss();
                } else {
                    Toast.makeText(UtilityActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                bottomSheet.dismiss();
            }
        });

        bottomSheet.show();
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MemberActivity.class));
            finish();
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MealRoutineActivity.class));
            finish();
        });
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, BazarActivity.class));
            finish();
        });
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, CashLedgerActivity.class));
            finish();
        });
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, AllFeaturesActivity.class));
            finish();
        });
    }
}
