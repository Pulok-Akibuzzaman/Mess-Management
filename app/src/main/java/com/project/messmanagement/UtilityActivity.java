package com.project.messmanagement;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class UtilityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility);

        ImageButton btnAddUtility = findViewById(R.id.btnAddUtility);
        btnAddUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog(UtilityActivity.this);
                android.view.View view = getLayoutInflater().inflate(R.layout.dialog_add_utility, null);
                bottomSheet.setContentView(view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(UtilityActivity.this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"Electricity", "Water", "Internet", "Gas", "Others"});
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner) view.findViewById(R.id.spinnerBillType)).setAdapter(adapter);

                view.findViewById(R.id.btnCloseUtility).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v2) {
                        bottomSheet.dismiss();
                    }
                });

                bottomSheet.show();
            }
        });
    }
}
