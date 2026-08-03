package com.project.messmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MessManager.db";
    private static final int DATABASE_VERSION = 9; 

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bazar (id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT, amount REAL, date TEXT)");
        db.execSQL("CREATE TABLE meals (id INTEGER PRIMARY KEY AUTOINCREMENT, count INTEGER, date TEXT)");
        db.execSQL("CREATE TABLE utilities (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, amount REAL, date TEXT)");
        db.execSQL("CREATE TABLE cash (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT, amount REAL, type TEXT, date TEXT)");
        db.execSQL("CREATE TABLE members (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, room TEXT, status TEXT)");
        db.execSQL("CREATE TABLE equipment (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, location TEXT, status TEXT, purchase_date TEXT, price REAL)");
        db.execSQL("CREATE TABLE notices (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT, priority TEXT, audience TEXT, date TEXT)");
        db.execSQL("CREATE TABLE loans (id INTEGER PRIMARY KEY AUTOINCREMENT, lender TEXT, amount REAL, status TEXT, date TEXT)");
        db.execSQL("CREATE TABLE polls (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, option1 TEXT, option2 TEXT, votes1 INTEGER, votes2 INTEGER, status TEXT, date TEXT)");
        db.execSQL("CREATE TABLE poll_votes (poll_id INTEGER, user_email TEXT, option_number INTEGER, PRIMARY KEY (poll_id, user_email))");
        db.execSQL("CREATE TABLE complaints (id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT, date TEXT)");
        db.execSQL("CREATE TABLE emergency_contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT)");
        
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO members (name, room, status) VALUES ('Rafiq Ahmed', 'Room 201', 'Active')");
        db.execSQL("INSERT INTO bazar (item_name, amount, date) VALUES ('Rice', 1200, '2026-08-01')");
        db.execSQL("INSERT INTO cash (description, amount, type, date) VALUES ('Initial Deposit', 10000, 'IN', '2026-08-01')");
        db.execSQL("INSERT INTO emergency_contacts (name, phone) VALUES ('Police', '999')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) db.execSQL("ALTER TABLE cash ADD COLUMN date TEXT");
        if (oldVersion < 3) db.execSQL("CREATE TABLE IF NOT EXISTS equipment (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, location TEXT, status TEXT, purchase_date TEXT, price REAL)");
        if (oldVersion < 4) db.execSQL("CREATE TABLE IF NOT EXISTS notices (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT, priority TEXT, audience TEXT, date TEXT)");
        if (oldVersion < 5) db.execSQL("CREATE TABLE IF NOT EXISTS loans (id INTEGER PRIMARY KEY AUTOINCREMENT, lender TEXT, amount REAL, status TEXT, date TEXT)");
        if (oldVersion < 6) db.execSQL("CREATE TABLE IF NOT EXISTS polls (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, option1 TEXT, option2 TEXT, votes1 INTEGER, votes2 INTEGER, status TEXT, date TEXT)");
        if (oldVersion < 7) db.execSQL("CREATE TABLE IF NOT EXISTS poll_votes (poll_id INTEGER, user_email TEXT, option_number INTEGER, PRIMARY KEY (poll_id, user_email))");
        if (oldVersion < 8) db.execSQL("CREATE TABLE IF NOT EXISTS complaints (id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT, date TEXT)");
        if (oldVersion < 9) db.execSQL("CREATE TABLE IF NOT EXISTS emergency_contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT)");
    }

    // --- MEMBER METHODS ---
    public void addMember(String name, String room, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("room", room); v.put("status", status);
        db.insert("members", null, v);
    }
    public void updateMember(int id, String name, String room, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("room", room); v.put("status", status);
        db.update("members", v, "id=?", new String[]{String.valueOf(id)});
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

    // --- DASHBOARD AGGREGATES ---
    public int getTotalMeals() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(count) FROM meals", null);
        int total = 0; if (c.moveToFirst()) total = c.getInt(0); c.close(); return total;
    }
}
