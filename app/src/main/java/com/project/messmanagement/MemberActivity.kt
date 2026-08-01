package com.project.messmanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog  // ← এই import যোগ করো
import android.widget.ImageButton

import android.content.Intent
import android.widget.LinearLayout
import android.widget.Toast

class MemberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)

        // RecyclerView setup
        val memberList = listOf(
            Member("Rafiq Ahmed",  "RA", "Room 201", "017XXXXXXXX", 28, "৳2,450", "Active"),
            Member("Karim Hossain","KH", "Room 202", "018XXXXXXXX", 22, "৳1,920", "Active"),
            Member("Sajid Ullah",  "SU", "Room 203", "019XXXXXXXX", 15, "৳1,310", "Away"),
            Member("Tanvir Islam", "TI", "Room 204", "016XXXXXXXX", 30, "৳2,620", "Active")
        )

        val rvMembers = findViewById<RecyclerView>(R.id.rvMembers)
        rvMembers.layoutManager = LinearLayoutManager(this)
        rvMembers.adapter = MemberAdapter(memberList)

        // Add Member Button
        val btnAdd = findViewById<ImageButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.dialog_add_member, null)
            bottomSheet.setContentView(view)

            view.findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
                bottomSheet.dismiss()
            }

            bottomSheet.show()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val btnHome = findViewById<LinearLayout>(R.id.btn_home_layout)
        val btnMeals = findViewById<LinearLayout>(R.id.btn_meals_layout)
        val btnBazar = findViewById<LinearLayout>(R.id.btn_bazar_layout)
        val btnCash = findViewById<LinearLayout>(R.id.btn_cash_layout)
        val btnMore = findViewById<LinearLayout>(R.id.btn_more_layout)

        btnHome?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnMeals?.setOnClickListener {
            startActivity(Intent(this, MealRoutineActivity::class.java))
            finish()
        }

        btnBazar?.setOnClickListener {
            startActivity(Intent(this, BazarActivity::class.java))
            finish()
        }

        btnCash?.setOnClickListener {
            startActivity(Intent(this, CashLedgerActivity::class.java))
            finish()
        }

        btnMore?.setOnClickListener {
            startActivity(Intent(this, AllFeaturesActivity::class.java))
            finish()
        }
    }
}