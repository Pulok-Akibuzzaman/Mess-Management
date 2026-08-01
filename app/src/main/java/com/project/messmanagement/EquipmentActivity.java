package com.project.messmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.Arrays;
import java.util.List;

public class EquipmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        List<Equipment> equipmentList = Arrays.asList(
                new Equipment("Rice Cooker",   "Kitchen",     "Available"),
                new Equipment("Water Filter",  "Common Area", "In Use"),
                new Equipment("Iron",          "Laundry",     "Available"),
                new Equipment("Mixer Grinder", "Kitchen",     "Damaged"),
                new Equipment("TV (32\")",     "Common Room", "In Use")
        );

        RecyclerView rvEquipment = findViewById(R.id.rvEquipment);
        rvEquipment.setLayoutManager(new LinearLayoutManager(this));
        rvEquipment.setAdapter(new EquipmentAdapter(equipmentList));

        ImageButton btnAdd = findViewById(R.id.btnAddEquipment);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog(EquipmentActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_add_equipment, null);
                bottomSheet.setContentView(view);

                ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(EquipmentActivity.this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"Kitchen", "Common Room", "Laundry", "Common Area", "Other"});
                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner) view.findViewById(R.id.spinnerLocation)).setAdapter(locationAdapter);

                ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(EquipmentActivity.this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"Available", "In Use", "Damaged", "Lost"});
                statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner) view.findViewById(R.id.spinnerEquipmentStatus)).setAdapter(statusAdapter);

                view.findViewById(R.id.btnCloseEquipment).setOnClickListener(new View.OnClickListener() {
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