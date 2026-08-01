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

public class RoomServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_service);

        List<RoomRequest> requestList = Arrays.asList(
                new RoomRequest("Rafiq Ahmed",  "Room 201", "25 Jul", "Fix leaking tap in bathroom", "In Progress"),
                new RoomRequest("Tanvir Islam", "Room 204", "24 Jul", "Replace ceiling fan",         "Pending"),
                new RoomRequest("Nabil Rahman", "Room 205", "22 Jul", "Door lock not working",       "Completed")
        );

        RecyclerView rvRequests = findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(new RoomRequestAdapter(requestList));

        // + button ও Submit Request button দুটোই একই bottom sheet খুলবে
        View.OnClickListener showBottomSheet = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewRequestSheet();
            }
        };

        findViewById(R.id.btnAddRequest).setOnClickListener(showBottomSheet);
        findViewById(R.id.btnSubmitRequest).setOnClickListener(showBottomSheet);
    }

    private void openNewRequestSheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_new_room_request, null);
        bottomSheet.setContentView(view);

        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Rafiq Ahmed", "Karim Hossain", "Sajid Ullah", "Tanvir Islam"});
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) view.findViewById(R.id.spinnerMember)).setAdapter(memberAdapter);

        ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Room 201", "Room 202", "Room 203", "Room 204", "Room 205"});
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) view.findViewById(R.id.spinnerRoom)).setAdapter(roomAdapter);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"High", "Medium", "Low"});
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) view.findViewById(R.id.spinnerRequestPriority)).setAdapter(priorityAdapter);

        view.findViewById(R.id.btnCloseRoomRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                bottomSheet.dismiss();
            }
        });

        bottomSheet.show();
    }
}