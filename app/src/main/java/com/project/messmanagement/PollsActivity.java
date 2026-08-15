package com.project.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PollsActivity extends AppCompatActivity {

    private LinearLayout container;
    private DatabaseHelper db;
    private String currentUserEmail;
    private boolean isAdmin = false;
    private boolean isMember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices); 

        db = new DatabaseHelper(this);
        container = findViewById(R.id.notice_container);
        
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserEmail = pref.getString("email", "anonymous");
        String role = pref.getString("role", "Member");
        isAdmin = "Admin".equalsIgnoreCase(role);
        isMember = "Member".equalsIgnoreCase(role);

        TextView tvTitle = findViewById(R.id.tvActivityTitle);
        if (tvTitle != null) tvTitle.setText("Polls");

        View btnAdd = findViewById(R.id.btnAddNotice);
        if (isAdmin || isMember) {
            btnAdd.setOnClickListener(v -> showPollDialog(-1, "", "", ""));
        } else {
            btnAdd.setVisibility(View.GONE);
        }

        fetchPollsFromCloud();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPollList();
        fetchPollsFromCloud();
    }

    private void refreshPollList() {
        if (container == null) return;
        container.removeAllViews();

        Cursor cursor = db.getAllPolls();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String question = cursor.getString(cursor.getColumnIndexOrThrow("question"));
                String opt1 = cursor.getString(cursor.getColumnIndexOrThrow("option1"));
                String opt2 = cursor.getString(cursor.getColumnIndexOrThrow("option2"));
                int v1 = cursor.getInt(cursor.getColumnIndexOrThrow("votes1"));
                int v2 = cursor.getInt(cursor.getColumnIndexOrThrow("votes2"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                addPollToUI(id, question, opt1, opt2, v1, v2, date);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void showPollDialog(final int id, String initialQ, String initialO1, String initialO2) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_poll, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        final EditText etQ = view.findViewById(R.id.etQuestion);
        final EditText etO1 = view.findViewById(R.id.etOption1);
        final EditText etO2 = view.findViewById(R.id.etOption2);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        if (id != -1) {
            tvTitle.setText("Edit Poll");
            etQ.setText(initialQ);
            etO1.setText(initialO1);
            etO2.setText(initialO2);
            btnSave.setText("Update Poll");
        }

        btnSave.setOnClickListener(v -> {
            String q = etQ.getText().toString().trim();
            String o1 = etO1.getText().toString().trim();
            String o2 = etO2.getText().toString().trim();
            String date = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(Calendar.getInstance().getTime());

            if (!q.isEmpty() && !o1.isEmpty() && !o2.isEmpty()) {
                if (id == -1) {
                    db.addPoll(q, o1, o2, date);
                    
                    // Sync to Supabase
                    String json = "{" +
                            "\"question\": \"" + q + "\"," +
                            "\"option1\": \"" + o1 + "\"," +
                            "\"option2\": \"" + o2 + "\"," +
                            "\"date\": \"" + date + "\"," +
                            "\"status\": \"Open\"" +
                            "}";
                    RemoteAccess.getInstance().syncToSupabase("polls", json);
                    
                    Toast.makeText(this, "Poll created", Toast.LENGTH_SHORT).show();
                } else {
                    db.updatePoll(id, q, o1, o2);
                }
                refreshPollList();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addPollToUI(final int id, final String q, final String o1, final String o2, final int v1, final int v2, String date) {
        final LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(30, 40, 30, 40);
        itemLayout.setBackgroundResource(android.R.drawable.list_selector_background);

        TextView tvQ = new TextView(this);
        tvQ.setText(q);
        tvQ.setTextSize(18);
        tvQ.setTypeface(null, android.graphics.Typeface.BOLD);
        tvQ.setTextColor(Color.BLACK);
        itemLayout.addView(tvQ);

        TextView tvDate = new TextView(this);
        tvDate.setText("Posted on: " + date);
        tvDate.setTextSize(12);
        tvDate.setTextColor(Color.GRAY);
        tvDate.setPadding(0, 5, 0, 20);
        itemLayout.addView(tvDate);

        int userVote = db.getUserVote(id, currentUserEmail);

        // Option 1 Button
        Button btn1 = new Button(this);
        btn1.setText(o1 + " (" + v1 + " votes)");
        btn1.setBackgroundResource(userVote == 1 ? R.drawable.bg_button_teal : R.drawable.bg_input_field);
        if (userVote == 1) btn1.setTextColor(Color.WHITE);
        btn1.setAllCaps(false);
        btn1.setOnClickListener(v -> {
            db.toggleVote(id, currentUserEmail, 1);
            
            // Sync Vote to Supabase
            String json = "{" +
                    "\"poll_id\": " + id + "," +
                    "\"user_email\": \"" + currentUserEmail + "\"," +
                    "\"option_number\": 1" +
                    "}";
            RemoteAccess.getInstance().syncToSupabase("poll_votes", json);
            
            refreshPollList();
        });
        itemLayout.addView(btn1);

        // Option 2 Button
        Button btn2 = new Button(this);
        btn2.setText(o2 + " (" + v2 + " votes)");
        btn2.setBackgroundResource(userVote == 2 ? R.drawable.bg_button_teal : R.drawable.bg_input_field);
        if (userVote == 2) btn2.setTextColor(Color.WHITE);
        btn2.setAllCaps(false);
        btn2.setOnClickListener(v -> {
            db.toggleVote(id, currentUserEmail, 2);
            
            // Sync Vote to Supabase
            String json = "{" +
                    "\"poll_id\": " + id + "," +
                    "\"user_email\": \"" + currentUserEmail + "\"," +
                    "\"option_number\": 2" +
                    "}";
            RemoteAccess.getInstance().syncToSupabase("poll_votes", json);
            
            refreshPollList();
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 0);
        btn2.setLayoutParams(params);
        itemLayout.addView(btn2);

        if (isAdmin) {
            itemLayout.setOnLongClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Manage Poll")
                        .setItems(new String[]{"Edit Poll", "Delete Poll"}, (dialog, which) -> {
                            if (which == 0) showPollDialog(id, q, o1, o2);
                            else {
                                db.deletePoll(id);
                                
                                // Sync Delete to Supabase
                                try {
                                    String query = "question=eq." + java.net.URLEncoder.encode(q, "UTF-8") +
                                            "&date=eq." + java.net.URLEncoder.encode(date, "UTF-8");
                                    RemoteAccess.getInstance().syncActionToSupabase("polls", "DELETE", null, query);
                                } catch (Exception ignored) {}

                                refreshPollList();
                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return true;
            });
        }

        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(Color.LTGRAY);

        container.addView(itemLayout);
        container.addView(line);
    }

    private void setupNavigation() {
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = sp.getString("role", "Member");
        boolean isBua = "Bua".equalsIgnoreCase(role);

        findViewById(R.id.btn_home_layout).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
            if (isBua) {
                startActivity(new Intent(this, BuaManagementActivity.class));
            } else {
                startActivity(new Intent(this, MemberActivity.class));
            }
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> startActivity(new Intent(this, MealRoutineActivity.class)));
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> startActivity(new Intent(this, BazarActivity.class)));
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> startActivity(new Intent(this, CashLedgerActivity.class)));
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> startActivity(new Intent(this, AllFeaturesActivity.class)));
    }

    private void fetchPollsFromCloud() {
        new Thread(() -> {
            String response = RemoteAccess.getInstance().syncFromSupabase("polls", "order=id.desc");
            if (response != null && !response.isEmpty()) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String q = obj.getString("question");
                        String o1 = obj.getString("option1");
                        String o2 = obj.getString("option2");
                        String date = obj.getString("date");

                        if (!pollExistsLocally(q, date)) {
                            db.addPoll(q, o1, o2, date);
                        }
                    }
                    runOnUiThread(this::refreshPollList);
                    fetchVotesFromCloud();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void fetchVotesFromCloud() {
        new Thread(() -> {
            String response = RemoteAccess.getInstance().syncFromSupabase("poll_votes", "");
            if (response != null && !response.isEmpty()) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        int pollId = obj.getInt("poll_id");
                        String email = obj.getString("user_email");
                        int opt = obj.getInt("option_number");

                        // Locally record the vote if not present
                        if (db.getUserVote(pollId, email) == 0) {
                            db.toggleVote(pollId, email, opt);
                        }
                    }
                    runOnUiThread(this::refreshPollList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean pollExistsLocally(String question, String date) {
        Cursor c = db.getReadableDatabase().rawQuery("SELECT id FROM polls WHERE question=? AND date=?", new String[]{question, date});
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }
}
