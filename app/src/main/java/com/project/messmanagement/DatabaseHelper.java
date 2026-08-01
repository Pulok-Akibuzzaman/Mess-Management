package com.project.messmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MessManager.db";
    private static final int DATABASE_VERSION = 2; // Increased version to force update

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
        
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Sample Bazar
        db.execSQL("INSERT INTO bazar (item_name, amount, date) VALUES ('Rice', 1200, '2026-08-01')");
        db.execSQL("INSERT INTO bazar (item_name, amount, date) VALUES ('Oil', 800, '2026-08-01')");
        
        // Sample Meals
        db.execSQL("INSERT INTO meals (count, date) VALUES (150, '2026-08-01')");
        
        // Sample Utilities
        db.execSQL("INSERT INTO utilities (type, amount, date) VALUES ('Gas', 1000, '2026-08-01')");
        
        // Sample Cash
        db.execSQL("INSERT INTO cash (description, amount, type, date) VALUES ('Initial Deposit', 10000, 'IN', '2026-08-01')");
        db.execSQL("INSERT INTO cash (description, amount, type, date) VALUES ('Mess Rent', 2000, 'OUT', '2026-08-01')");
        
        // Sample Members
        db.execSQL("INSERT INTO members (name, room, status) VALUES ('Rafiq Ahmed', 'Room 201', 'Active')");
        db.execSQL("INSERT INTO members (name, room, status) VALUES ('Karim Hossain', 'Room 202', 'Active')");
        db.execSQL("INSERT INTO members (name, room, status) VALUES ('Sajid Ullah', 'Room 203', 'Away')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS bazar");
        db.execSQL("DROP TABLE IF EXISTS meals");
        db.execSQL("DROP TABLE IF EXISTS utilities");
        db.execSQL("DROP TABLE IF EXISTS cash");
        db.execSQL("DROP TABLE IF EXISTS members");
        onCreate(db);
    }

    // --- MEMBER METHODS ---

    public void addMember(String name, String room, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("room", room);
        values.put("status", status);
        db.insert("members", null, values);
    }

    public void updateMember(int id, String name, String room, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("room", room);
        values.put("status", status);
        db.update("members", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteMember(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("members", "id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllMembers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM members", null);
    }

    public Cursor searchMembers(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM members WHERE name LIKE ?", new String[]{"%" + query + "%"});
    }

    public int getActiveMembersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM members WHERE status = 'Active'", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // --- BAZAR METHODS ---

    public void addBazarItem(String name, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("item_name", name);
        values.put("amount", amount);
        values.put("date", date);
        db.insert("bazar", null, values);
    }

    public void updateBazarItem(int id, String name, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("item_name", name);
        values.put("amount", amount);
        values.put("date", date);
        db.update("bazar", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteBazarItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("bazar", "id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllBazarItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bazar ORDER BY date DESC", null);
    }

    // --- CASH METHODS ---

    public void addCashTransaction(String desc, double amount, String type, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("description", desc);
        values.put("amount", amount);
        values.put("type", type);
        values.put("date", date);
        db.insert("cash", null, values);
    }

    public void updateCashTransaction(int id, String desc, double amount, String type, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("description", desc);
        values.put("amount", amount);
        values.put("type", type);
        values.put("date", date);
        db.update("cash", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteCashTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cash", "id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllCashTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM cash ORDER BY id DESC", null);
    }

    public double getTotalIn() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM cash WHERE type = 'IN'", null);
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public double getTotalOut() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM cash WHERE type = 'OUT'", null);
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    // --- OTHER DASHBOARD METHODS ---

    public double getTotalBazar() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM bazar", null);
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public int getBazarCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM bazar", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getTotalMeals() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(count) FROM meals", null);
        int total = 0;
        if (cursor.moveToFirst()) total = cursor.getInt(0);
        cursor.close();
        return total;
    }

    public double getUtilitiesTotal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM utilities", null);
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public int getUtilitiesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM utilities", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public double getCashBalance() {
        return getTotalIn() - getTotalOut();
    }
}
