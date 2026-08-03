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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.Locale;

public class UtilityActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private TextView tvTotalAmount, tvElectricity, tvWater, tvInternet, tvGas;
    private TextView tvPerMember, tvElectricityHead, tvWaterHead, tvInternetHead, tvGasHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility);

        db = new DatabaseHelper(this);
        initViews();
        loadUtilityData();
        setupNavigation();

        ImageButton btnAddUtility = findViewById(R.id.btnAddUtility);
        btnAddUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUtilityDialog();
            }
        });
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
    }

    private void loadUtilityData() {
        double total = db.getUtilitiesTotal();
        double electricity = db.getUtilityTotalByType("Electricity");
        double water = db.getUtilityTotalByType("Water");
        double internet = db.getUtilityTotalByType("Internet");
        double gas = db.getUtilityTotalByType("Gas");
        
        int memberCount = db.getActiveMembersCount();
        if (memberCount <= 0) memberCount = 1; // Prevent division by zero

        tvTotalAmount.setText(String.format(Locale.US, "৳%.0f", total));
        tvElectricity.setText(String.format(Locale.US, "৳%.0f", electricity));
        tvWater.setText(String.format(Locale.US, "৳%.0f", water));
        tvInternet.setText(String.format(Locale.US, "৳%.0f", internet));
        tvGas.setText(String.format(Locale.US, "৳%.0f", gas));

        // Update "Per Head" calculations
        tvPerMember.setText(String.format(Locale.US, "৳%.0f per member", total / memberCount));
        tvElectricityHead.setText(String.format(Locale.US, "৳%.0f / head", electricity / memberCount));
        tvWaterHead.setText(String.format(Locale.US, "৳%.0f / head", water / memberCount));
        tvInternetHead.setText(String.format(Locale.US, "৳%.0f / head", internet / memberCount));
        tvGasHead.setText(String.format(Locale.US, "৳%.0f / head", gas / memberCount));
    }

    private void showAddUtilityDialog() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(UtilityActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_utility, null);
        bottomSheet.setContentView(view);

        Spinner spinnerBillType = view.findViewById(R.id.spinnerBillType);
        EditText etAmount = view.findViewById(R.id.etAmount);
        Button btnAddBill = view.findViewById(R.id.btnAddBill);
        ImageButton btnClose = view.findViewById(R.id.btnCloseUtility);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(UtilityActivity.this,
                android.R.layout.simple_spinner_item,
                new String[]{"Electricity", "Water", "Internet", "Gas", "Others"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBillType.setAdapter(adapter);

        btnAddBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = spinnerBillType.getSelectedItem().toString();
                String amountStr = etAmount.getText().toString();
                
                if (!amountStr.isEmpty()) {
                    double amount = Double.parseDouble(amountStr);
                    db.addUtility(type, amount, "2026-08-03"); // Hardcoded date for now
                    loadUtilityData();
                    bottomSheet.dismiss();
                    Toast.makeText(UtilityActivity.this, "Bill added successfully", Toast.LENGTH_SHORT).show();
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
