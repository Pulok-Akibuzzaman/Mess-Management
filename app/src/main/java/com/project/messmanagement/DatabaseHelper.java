package com.project.messmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MessManager.db";
    private static final int DATABASE_VERSION = 17; 

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bazar (id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT, amount REAL, date TEXT)");
        db.execSQL("CREATE TABLE utilities (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, amount REAL, date TEXT)");
        db.execSQL("CREATE TABLE cash (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT, amount REAL, type TEXT, date TEXT)");
        db.execSQL("CREATE TABLE members (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, room TEXT, status TEXT, email TEXT)");
        db.execSQL("CREATE TABLE equipment (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, location TEXT, status TEXT, purchase_date TEXT, price REAL)");
        db.execSQL("CREATE TABLE notices (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT, priority TEXT, audience TEXT, date TEXT)");
        db.execSQL("CREATE TABLE loans (id INTEGER PRIMARY KEY AUTOINCREMENT, lender TEXT, amount REAL, status TEXT, date TEXT)");
        db.execSQL("CREATE TABLE polls (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, option1 TEXT, option2 TEXT, votes1 INTEGER, votes2 INTEGER, status TEXT, date TEXT)");
        db.execSQL("CREATE TABLE poll_votes (poll_id INTEGER, user_email TEXT, option_number INTEGER, PRIMARY KEY (poll_id, user_email))");
        db.execSQL("CREATE TABLE complaints (id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT, date TEXT)");
        db.execSQL("CREATE TABLE emergency_contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT)");
        db.execSQL("CREATE TABLE meal_tracking (id INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT, date TEXT, breakfast INTEGER DEFAULT 0, lunch INTEGER DEFAULT 0, dinner INTEGER DEFAULT 0, UNIQUE(user_email, date))");
        db.execSQL("CREATE TABLE bua_profile (id INTEGER PRIMARY KEY, name TEXT, phone TEXT, address TEXT, salary REAL, join_date TEXT)");
        db.execSQL("CREATE TABLE room_requests (id INTEGER PRIMARY KEY AUTOINCREMENT, member_name TEXT, room_no TEXT, issue TEXT, priority TEXT, status TEXT, date TEXT)");
        db.execSQL("CREATE TABLE guest_meals (id INTEGER PRIMARY KEY AUTOINCREMENT, member_name TEXT, guest_name TEXT, meal_count INTEGER, meal_type TEXT, date TEXT)");
        db.execSQL("CREATE TABLE occasions (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, type TEXT, total_cost REAL, member_count INTEGER, date TEXT)");
        db.execSQL("CREATE TABLE bua_salary_history (id INTEGER PRIMARY KEY AUTOINCREMENT, month_year TEXT, amount REAL, paid_date TEXT, status TEXT)");
        
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO members (name, room, status, email) VALUES ('Rafiq Ahmed', 'Room 201', 'Active', 'rafiq@gmail.com')");
        db.execSQL("INSERT INTO members (name, room, status, email) VALUES ('Karim Hossain', 'Room 202', 'Active', 'karim@gmail.com')");
        db.execSQL("INSERT INTO bazar (item_name, amount, date) VALUES ('Rice', 1200, '2026-08-01')");
        db.execSQL("INSERT INTO cash (description, amount, type, date) VALUES ('Initial Deposit', 10000, 'IN', '2026-08-01')");
        db.execSQL("INSERT INTO bua_profile (id, name, phone, address, salary, join_date) VALUES (1, 'Fatema Khatun', '017XXXXXXXX', 'Mirpur-10, Dhaka', 4000, 'March 2023')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 12) {
            db.execSQL("DROP TABLE IF EXISTS meal_tracking");
            db.execSQL("CREATE TABLE meal_tracking (id INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT, date TEXT, breakfast INTEGER DEFAULT 0, lunch INTEGER DEFAULT 0, dinner INTEGER DEFAULT 0, UNIQUE(user_email, date))");
        }
        if (oldVersion < 13) {
            db.execSQL("CREATE TABLE IF NOT EXISTS bua_profile (id INTEGER PRIMARY KEY, name TEXT, phone TEXT, address TEXT, salary REAL, join_date TEXT)");
            db.execSQL("INSERT OR IGNORE INTO bua_profile (id, name, phone, address, salary, join_date) VALUES (1, 'Fatema Khatun', '017XXXXXXXX', 'Mirpur-10, Dhaka', 4000, 'March 2023')");
            db.execSQL("CREATE TABLE IF NOT EXISTS room_requests (id INTEGER PRIMARY KEY AUTOINCREMENT, member_name TEXT, room_no TEXT, issue TEXT, priority TEXT, status TEXT, date TEXT)");
        }
        if (oldVersion < 14) {
            db.execSQL("CREATE TABLE IF NOT EXISTS guest_meals (id INTEGER PRIMARY KEY AUTOINCREMENT, member_name TEXT, guest_name TEXT, meal_count INTEGER, meal_type TEXT, date TEXT)");
        }
        if (oldVersion < 15) {
            db.execSQL("ALTER TABLE members ADD COLUMN email TEXT");
        }
        if (oldVersion < 16) {
            db.execSQL("CREATE TABLE IF NOT EXISTS occasions (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, type TEXT, total_cost REAL, member_count INTEGER, date TEXT)");
        }
        if (oldVersion < 17) {
            db.execSQL("CREATE TABLE IF NOT EXISTS bua_salary_history (id INTEGER PRIMARY KEY AUTOINCREMENT, month_year TEXT, amount REAL, paid_date TEXT, status TEXT)");
        }
    }

    // --- MEMBER METHODS ---
    public void addMember(String name, String room, String status, String email) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("room", room); v.put("status", status); v.put("email", email);
        this.getWritableDatabase().insert("members", null, v);
    }
    public void updateMember(int id, String name, String room, String status, String email) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("room", room); v.put("status", status); v.put("email", email);
        this.getWritableDatabase().update("members", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteMember(int id) {
        this.getWritableDatabase().delete("members", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllMembers() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM members", null);
    }
    public Cursor searchMembers(String query) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM members WHERE name LIKE ?", new String[]{"%" + query + "%"});
    }
    public int getActiveMembersCount() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM members WHERE status='Active'", null);
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close(); return count;
    }

    // --- BAZAR METHODS ---
    public void addBazarItem(String name, double amount, String date) {
        ContentValues v = new ContentValues();
        v.put("item_name", name); v.put("amount", amount); v.put("date", date);
        this.getWritableDatabase().insert("bazar", null, v);
    }
    public void updateBazarItem(int id, String name, double amount, String date) {
        ContentValues v = new ContentValues();
        v.put("item_name", name); v.put("amount", amount); v.put("date", date);
        this.getWritableDatabase().update("bazar", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteBazarItem(int id) {
        this.getWritableDatabase().delete("bazar", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllBazarItems() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM bazar ORDER BY date DESC", null);
    }
    public double getTotalBazar() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(amount) FROM bazar", null);
        double total = 0; if (c.moveToFirst()) total = c.getDouble(0); c.close(); return total;
    }
    public int getBazarCount() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM bazar", null);
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close(); return count;
    }

    // --- CASH METHODS ---
    public void addCashTransaction(String desc, double amount, String type, String date) {
        ContentValues v = new ContentValues();
        v.put("description", desc); v.put("amount", amount); v.put("type", type); v.put("date", date);
        this.getWritableDatabase().insert("cash", null, v);
    }
    public void updateCashTransaction(int id, String desc, double amount, String type, String date) {
        ContentValues v = new ContentValues();
        v.put("description", desc); v.put("amount", amount); v.put("type", type); v.put("date", date);
        this.getWritableDatabase().update("cash", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteCashTransaction(int id) {
        this.getWritableDatabase().delete("cash", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllCashTransactions() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM cash ORDER BY id DESC", null);
    }
    public double getTotalIn() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(amount) FROM cash WHERE type='IN'", null);
        double t = 0; if (c.moveToFirst()) t = c.getDouble(0); c.close(); return t;
    }
    public double getTotalOut() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(amount) FROM cash WHERE type='OUT'", null);
        double t = 0; if (c.moveToFirst()) t = c.getDouble(0); c.close(); return t;
    }
    public double getCashBalance() { return getTotalIn() - getTotalOut(); }

    // --- UTILITIES METHODS ---
    public void addUtility(String type, double amount, String date) {
        ContentValues v = new ContentValues();
        v.put("type", type); v.put("amount", amount); v.put("date", date);
        this.getWritableDatabase().insert("utilities", null, v);
    }
    public double getUtilityTotalByType(String type) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(amount) FROM utilities WHERE type=?", new String[]{type});
        double t = 0; if (c.moveToFirst()) t = c.getDouble(0); c.close(); return t;
    }
    public double getUtilitiesTotal() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(amount) FROM utilities", null);
        double t = 0; if (c.moveToFirst()) t = c.getDouble(0); c.close(); return t;
    }
    public int getUtilitiesCount() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM utilities", null);
        int n = 0; if (c.moveToFirst()) n = c.getInt(0); c.close(); return n;
    }

    // --- EQUIPMENT METHODS ---
    public void addEquipment(String name, String loc, String status, String date, double price) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("location", loc); v.put("status", status); v.put("purchase_date", date); v.put("price", price);
        this.getWritableDatabase().insert("equipment", null, v);
    }
    public void updateEquipment(int id, String name, String loc, String status, String date, double price) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("location", loc); v.put("status", status); v.put("purchase_date", date); v.put("price", price);
        this.getWritableDatabase().update("equipment", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteEquipment(int id) {
        this.getWritableDatabase().delete("equipment", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllEquipment() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM equipment ORDER BY id DESC", null);
    }

    // --- NOTICES METHODS ---
    public void addNotice(String title, String content, String prio, String aud, String date) {
        ContentValues v = new ContentValues();
        v.put("title", title); v.put("content", content); v.put("priority", prio); v.put("audience", aud); v.put("date", date);
        this.getWritableDatabase().insert("notices", null, v);
    }
    public void updateNotice(int id, String title, String content, String prio, String aud, String date) {
        ContentValues v = new ContentValues();
        v.put("title", title); v.put("content", content); v.put("priority", prio); v.put("audience", aud); v.put("date", date);
        this.getWritableDatabase().update("notices", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteNotice(int id) {
        this.getWritableDatabase().delete("notices", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllNotices() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM notices ORDER BY id DESC", null);
    }

    // --- LOANS METHODS ---
    public void addLoan(String lender, double amount, String status, String date) {
        ContentValues v = new ContentValues();
        v.put("lender", lender); v.put("amount", amount); v.put("status", status); v.put("date", date);
        this.getWritableDatabase().insert("loans", null, v);
    }
    public void updateLoan(int id, String lender, double amount, String status, String date) {
        ContentValues v = new ContentValues();
        v.put("lender", lender); v.put("amount", amount); v.put("status", status); v.put("date", date);
        this.getWritableDatabase().update("loans", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteLoan(int id) {
        this.getWritableDatabase().delete("loans", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllLoans() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM loans ORDER BY id DESC", null);
    }

    // --- POLLS METHODS ---
    public void addPoll(String q, String o1, String o2, String date) {
        ContentValues v = new ContentValues();
        v.put("question", q); v.put("option1", o1); v.put("option2", o2);
        v.put("votes1", 0); v.put("votes2", 0); v.put("status", "Open"); v.put("date", date);
        this.getWritableDatabase().insert("polls", null, v);
    }
    public void updatePoll(int id, String q, String o1, String o2) {
        ContentValues v = new ContentValues();
        v.put("question", q); v.put("option1", o1); v.put("option2", o2);
        this.getWritableDatabase().update("polls", v, "id=?", new String[]{String.valueOf(id)});
    }
    public int getUserVote(int pollId, String userEmail) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT option_number FROM poll_votes WHERE poll_id=? AND user_email=?", 
                new String[]{String.valueOf(pollId), userEmail});
        int opt = 0; if (c.moveToFirst()) opt = c.getInt(0); c.close(); return opt;
    }
    public void toggleVote(int pollId, String userEmail, int selectedOpt) {
        SQLiteDatabase db = this.getWritableDatabase();
        int existingOpt = getUserVote(pollId, userEmail);
        if (existingOpt == selectedOpt) {
            db.delete("poll_votes", "poll_id=? AND user_email=?", new String[]{String.valueOf(pollId), userEmail});
            if (selectedOpt == 1) db.execSQL("UPDATE polls SET votes1 = votes1 - 1 WHERE id = " + pollId);
            else db.execSQL("UPDATE polls SET votes2 = votes2 - 1 WHERE id = " + pollId);
        } else {
            if (existingOpt != 0) {
                if (existingOpt == 1) db.execSQL("UPDATE polls SET votes1 = votes1 - 1 WHERE id = " + pollId);
                else db.execSQL("UPDATE polls SET votes2 = votes2 - 1 WHERE id = " + pollId);
            }
            if (selectedOpt == 1) db.execSQL("UPDATE polls SET votes1 = votes1 + 1 WHERE id = " + pollId);
            else db.execSQL("UPDATE polls SET votes2 = votes2 + 1 WHERE id = " + pollId);
            ContentValues v = new ContentValues();
            v.put("poll_id", pollId);
            v.put("user_email", userEmail);
            v.put("option_number", selectedOpt);
            db.replace("poll_votes", null, v);
        }
    }
    public void deletePoll(int id) {
        this.getWritableDatabase().delete("polls", "id=?", new String[]{String.valueOf(id)});
        this.getWritableDatabase().delete("poll_votes", "poll_id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllPolls() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM polls ORDER BY id DESC", null);
    }

    // --- COMPLAINTS METHODS ---
    public void addComplaint(String message, String date) {
        ContentValues v = new ContentValues();
        v.put("message", message); v.put("date", date);
        this.getWritableDatabase().insert("complaints", null, v);
    }
    public void deleteComplaint(int id) {
        this.getWritableDatabase().delete("complaints", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllComplaints() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM complaints ORDER BY id DESC", null);
    }

    // --- EMERGENCY CONTACT METHODS ---
    public void addEmergencyContact(String name, String phone) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("phone", phone);
        this.getWritableDatabase().insert("emergency_contacts", null, v);
    }
    public void deleteEmergencyContact(int id) {
        this.getWritableDatabase().delete("emergency_contacts", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllEmergencyContacts() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM emergency_contacts ORDER BY id DESC", null);
    }

    // --- BUA PROFILE METHODS ---
    public Cursor getBuaProfile() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM bua_profile WHERE id=1", null);
    }
    public void updateBuaProfile(String name, String phone, String address, double salary, String joinDate) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("phone", phone); v.put("address", address); 
        v.put("salary", salary); v.put("join_date", joinDate);
        this.getWritableDatabase().update("bua_profile", v, "id=1", null);
    }

    // --- ROOM REQUEST METHODS ---
    public void addRoomRequest(String member, String room, String issue, String prio, String date) {
        ContentValues v = new ContentValues();
        v.put("member_name", member); v.put("room_no", room); v.put("issue", issue);
        v.put("priority", prio); v.put("status", "Pending"); v.put("date", date);
        this.getWritableDatabase().insert("room_requests", null, v);
    }
    public void updateRoomRequestStatus(int id, String status) {
        ContentValues v = new ContentValues();
        v.put("status", status);
        this.getWritableDatabase().update("room_requests", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteRoomRequest(int id) {
        this.getWritableDatabase().delete("room_requests", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllRoomRequests() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM room_requests ORDER BY id DESC", null);
    }

    // --- MEAL TRACKING METHODS ---
    public void updateDailyMeals(String email, String date, int b, int l, int d) {
        ContentValues v = new ContentValues();
        v.put("user_email", email);
        v.put("date", date);
        v.put("breakfast", b);
        v.put("lunch", l);
        v.put("dinner", d);
        this.getWritableDatabase().replace("meal_tracking", null, v);
    }
    public Cursor getMealStatus(String email, String date) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM meal_tracking WHERE user_email=? AND date=?", new String[]{email, date});
    }
    public int getTotalMeals() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(breakfast + lunch + dinner) FROM meal_tracking", null);
        int total = 0; if (c.moveToFirst()) total = c.getInt(0); c.close(); return total;
    }
    public int getUserTotalMeals(String email) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(breakfast + lunch + dinner) FROM meal_tracking WHERE user_email=?", new String[]{email});
        int total = 0; if (c.moveToFirst()) total = c.getInt(0); c.close(); return total;
    }

    // --- GUEST MEALS METHODS ---
    public void addGuestMeal(String member, String guest, int count, String type, String date) {
        ContentValues v = new ContentValues();
        v.put("member_name", member); v.put("guest_name", guest);
        v.put("meal_count", count); v.put("meal_type", type); v.put("date", date);
        this.getWritableDatabase().insert("guest_meals", null, v);
    }
    public void updateGuestMeal(int id, String member, String guest, int count, String type, String date) {
        ContentValues v = new ContentValues();
        v.put("member_name", member); v.put("guest_name", guest);
        v.put("meal_count", count); v.put("meal_type", type); v.put("date", date);
        this.getWritableDatabase().update("guest_meals", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteGuestMeal(int id) {
        this.getWritableDatabase().delete("guest_meals", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllGuestMeals() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM guest_meals ORDER BY id DESC", null);
    }

    // --- OCCASIONS METHODS ---
    public void addOccasion(String title, String type, double cost, int members, String date) {
        ContentValues v = new ContentValues();
        v.put("title", title); v.put("type", type);
        v.put("total_cost", cost); v.put("member_count", members); v.put("date", date);
        this.getWritableDatabase().insert("occasions", null, v);
    }
    public void updateOccasion(int id, String title, String type, double cost, int members, String date) {
        ContentValues v = new ContentValues();
        v.put("title", title); v.put("type", type);
        v.put("total_cost", cost); v.put("member_count", members); v.put("date", date);
        this.getWritableDatabase().update("occasions", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteOccasion(int id) {
        this.getWritableDatabase().delete("occasions", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllOccasions() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM occasions ORDER BY id DESC", null);
    }
    public double getTotalOccasionCost() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(total_cost) FROM occasions", null);
        double total = 0; if (c.moveToFirst()) total = c.getDouble(0); c.close(); return total;
    }

    // --- BUA SALARY METHODS ---
    public void addBuaSalaryPayment(String monthYear, double amount, String paidDate) {
        ContentValues v = new ContentValues();
        v.put("month_year", monthYear); v.put("amount", amount);
        v.put("paid_date", paidDate); v.put("status", "Paid");
        this.getWritableDatabase().insert("bua_salary_history", null, v);
    }
    public Cursor getBuaSalaryHistory() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM bua_salary_history ORDER BY id DESC", null);
    }
    public boolean isBuaSalaryPaid(String monthYear) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT id FROM bua_salary_history WHERE month_year=?", new String[]{monthYear});
        boolean paid = c.getCount() > 0; c.close(); return paid;
    }
}
