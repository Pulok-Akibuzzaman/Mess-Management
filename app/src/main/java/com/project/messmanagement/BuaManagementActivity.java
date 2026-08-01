package com.project.messmanagement;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class BuaManagementActivity extends AppCompatActivity {

    Button btnProfile, btnSalary, btnSchedule;
    FrameLayout tabContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bua_management);

        btnProfile  = findViewById(R.id.btnTabProfile);
        btnSalary   = findViewById(R.id.btnTabSalary);
        btnSchedule = findViewById(R.id.btnTabSchedule);
        tabContent  = findViewById(R.id.tabContent);

        showProfile();

        btnProfile.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              setActiveTab(btnProfile);
                                              showProfile();
                                          }
                                      });
        btnSalary.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             setActiveTab(btnSalary);
                                             showSalary();
                                         }
                                     });
        btnSchedule.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               setActiveTab(btnSchedule);
                                               showSchedule();
                                           }
                                       });
    }

    private void setActiveTab(Button active) {
        for (Button btn : new Button[]{btnProfile, btnSalary, btnSchedule}) {
            btn.setBackgroundResource(android.R.color.transparent);
            btn.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        active.setBackgroundResource(R.drawable.bg_tab_active);
        active.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void showProfile() {
        tabContent.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_profile, tabContent, false);
        setRow(view, R.id.rowFullName,  "Full Name",         "Fatema Khatun");
        setRow(view, R.id.rowPhone,     "Phone",             "017XXXXXXXX");
        setRow(view, R.id.rowAddress,   "Address",           "Mirpur-10, Dhaka");
        setRow(view, R.id.rowJoinDate,  "Join Date",         "March 2023");
        setRow(view, R.id.rowNationalId,"National ID",       "19XXXXXXXXX");
        setRow(view, R.id.rowEmergency, "Emergency Contact", "018XXXXXXXX");
        tabContent.addView(view);
    }

    private void setRow(View parent, int rowId, String label, String value) {
        View row = parent.findViewById(rowId);
        ((TextView) row.findViewById(R.id.tvLabel)).setText(label);
        ((TextView) row.findViewById(R.id.tvValue)).setText(value);
    }

    private void showSalary() {
        tabContent.removeAllViews();
        // Salary tab — static view bananor jonno LinearLayout inflate koro
        // অথবা RecyclerView use koro
        tabContent.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_salary, tabContent, false);
        tabContent.addView(view);
    }

    private void showSchedule() {
        tabContent.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_schedule, tabContent, false);
        tabContent.addView(view);
    }
}
