package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.Arrays;
import java.util.List;

public class GuestMealsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        ImageButton btnAdd = findViewById(R.id.btnAddNotice);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> showAddGuestMealDialog());
        }
    }

    private void showAddGuestMealDialog() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_create_notice, null);
        bottomSheet.setContentView(view);
        bottomSheet.show();
    }
}
