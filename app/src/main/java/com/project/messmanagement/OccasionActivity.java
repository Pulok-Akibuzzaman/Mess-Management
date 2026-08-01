package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OccasionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occasion);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add_occasion);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddOccasionDialog());
        }
    }

    private void showAddOccasionDialog() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_create_notice, null);
        bottomSheet.setContentView(view);
        bottomSheet.show();
    }
}