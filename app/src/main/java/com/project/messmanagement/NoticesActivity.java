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

public class NoticesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        List<Notice> noticeList = Arrays.asList(
                new Notice("Clean kitchen after use", "Please ensure the kitchen is clean after cooking.", "High"),
                new Notice("Laundry schedule reminder", "Use the washing area only during 7am–10am.", "Medium"),
                new Notice("Grocery shopping today", "Collect your share of bazar money by 5 PM.", "High")
        );

        RecyclerView rvNotices = findViewById(R.id.rvNotices);
        rvNotices.setLayoutManager(new LinearLayoutManager(this));
        rvNotices.setAdapter(new NoticeAdapter(noticeList));

        ImageButton btnAddNotice = findViewById(R.id.btnAddNotice);
        btnAddNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog(NoticesActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_create_notice, null);
                bottomSheet.setContentView(view);

                ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(NoticesActivity.this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"High", "Medium", "Low"});
                priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner) view.findViewById(R.id.spinnerPriority)).setAdapter(priorityAdapter);

                ArrayAdapter<String> audienceAdapter = new ArrayAdapter<>(NoticesActivity.this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"All Members", "Bua Only", "Admin Only"});
                audienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner) view.findViewById(R.id.spinnerAudience)).setAdapter(audienceAdapter);

                view.findViewById(R.id.btnCloseNotice).setOnClickListener(new View.OnClickListener() {
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