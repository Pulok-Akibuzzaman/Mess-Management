package com.project.messmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MessManager.db";
    private static final int DATABASE_VERSION = 22; 

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bazar (id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT, amount REAL, date TEXT, bought_by TEXT)");
        db.execSQL("CREATE TABLE utilities (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, amount REAL, date TEXT)");
        db.execSQL("CREATE TABLE cash (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT, amount REAL, type TEXT, date TEXT, performed_by TEXT, member_email TEXT)");
        db.execSQL("CREATE TABLE members (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, room TEXT, status TEXT, email TEXT, phone TEXT, join_date TEXT, password TEXT, paid_amount REAL DEFAULT 0)");
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
        // Create the Default Admin account if it doesn't exist
        Cursor cursor = db.rawQuery("SELECT id FROM members WHERE email = 'admin@mess.com'", null);
        if (cursor.getCount() == 0) {
            ContentValues v = new ContentValues();
            v.put("name", "Mess Admin");
            v.put("email", "admin@mess.com");
            v.put("password", "1234");
            v.put("status", "Admin");
            v.put("room", "Office");
            v.put("phone", "01XXXXXXXXX");
            v.put("join_date", "System");
            v.put("paid_amount", 0.0);
            db.insert("members", null, v);
        }
        cursor.close();
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
        if (oldVersion < 21) {
            try {
                db.execSQL("ALTER TABLE cash ADD COLUMN performed_by TEXT");
                db.execSQL("ALTER TABLE cash ADD COLUMN member_email TEXT");
            } catch (Exception ignored) {}
        }
        if (oldVersion < 22) {
            try {
                db.execSQL("ALTER TABLE bazar ADD COLUMN bought_by TEXT");
            } catch (Exception ignored) {}
        }
        
        // Ensure Admin exists on every upgrade
        insertSampleData(db);
    }

    // --- MEMBER METHODS ---
    public long addMember(String name, String room, String status, String email, String phone, String joinDate, String password, double paid) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("room", room); v.put("status", status); v.put("email", email);
        v.put("phone", phone); v.put("join_date", joinDate); v.put("password", password);
        v.put("paid_amount", paid);
        long id = this.getWritableDatabase().insert("members", null, v);
        
        if ("Bua".equalsIgnoreCase(status)) {
            updateBuaProfile(name, phone, "Not Set", 0.0, joinDate);
        }
        return id;
    }
    public void updateMember(int id, String name, String room, String status, String email, String phone, String joinDate, String password, double paid) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("room", room); v.put("status", status); v.put("email", email);
        v.put("phone", phone); v.put("join_date", joinDate); v.put("password", password);
        v.put("paid_amount", paid);
        this.getWritableDatabase().update("members", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void updateUserProfile(String email, String phone, String password) {
        ContentValues v = new ContentValues();
        v.put("phone", phone);
        v.put("password", password);
        this.getWritableDatabase().update("members", v, "email=?", new String[]{email});
    }
    public Cursor checkLogin(String email, String password) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM members WHERE email=? AND password=?", new String[]{email, password});
    }
    public void deleteMember(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, email FROM members WHERE id = ?", new String[]{String.valueOf(id)});
        
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            cursor.close();

            db.beginTransaction();
            try {
                // 1. Delete Meal Tracking
                if (email != null) {
                    db.delete("meal_tracking", "user_email = ?", new String[]{email});
                    // 2. Delete Poll Votes
                    db.delete("poll_votes", "user_email = ?", new String[]{email});
                }
                
                // 3. Delete Guest Meals (Linked by Name)
                if (name != null) {
                    db.delete("guest_meals", "member_name = ?", new String[]{name});
                    // 4. Delete Room Requests (Linked by Name)
                    db.delete("room_requests", "member_name = ?", new String[]{name});
                }

                // 5. Finally, Delete the Member
                db.delete("members", "id = ?", new String[]{String.valueOf(id)});
                
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } else if (cursor != null) {
            cursor.close();
        }
    }
    public Cursor getAllMembers() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM members WHERE status NOT IN ('Bua', 'Admin')", null);
    }
    public Cursor getMemberByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM members WHERE email=?", new String[]{email});
        
        // Robustness: If Admin is missing, insert it immediately and re-fetch
        if (c.getCount() == 0 && "admin@mess.com".equalsIgnoreCase(email)) {
            c.close();
            insertSampleData(this.getWritableDatabase());
            return this.getReadableDatabase().rawQuery("SELECT * FROM members WHERE email=?", new String[]{email});
        }
        return c;
    }
    public Cursor searchMembers(String query) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM members WHERE name LIKE ? AND status NOT IN ('Bua', 'Admin')", new String[]{"%" + query + "%"});
    }
    public int getResidentCount() {
        // Everyone except Bua and Admin should share the fixed costs
        Cursor c = this.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM members WHERE status NOT IN ('Bua', 'Admin')", null);
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close(); 
        return (count > 0) ? count : 1; // Prevent division by zero
    }
    public void addMemberPayment(int id, double amount) {
        this.getWritableDatabase().execSQL("UPDATE members SET paid_amount = paid_amount + ? WHERE id = ?", new Object[]{amount, id});
    }
    public int getMemberIdByEmail(String email) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT id FROM members WHERE email=?", new String[]{email});
        int id = -1; if (c.moveToFirst()) id = c.getInt(0); c.close(); return id;
    }
    public double getMemberPaidAmount(String email) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(amount) FROM cash WHERE member_email=? AND type='IN'", new String[]{email});
        double paid = 0; if (c.moveToFirst()) paid = c.getDouble(0); c.close(); return paid;
    }

    // --- BAZAR METHODS ---
    public void addBazarItem(String name, double amount, String date, String boughtBy) {
        ContentValues v = new ContentValues();
        v.put("item_name", name); v.put("amount", amount); v.put("date", date);
        v.put("bought_by", boughtBy);
        this.getWritableDatabase().insert("bazar", null, v);
    }
    public void updateBazarItem(int id, String name, double amount, String date) {
        ContentValues v = new ContentValues();
        v.put("item_name", name); v.put("amount", amount); v.put("date", date);
        this.getWritableDatabase().update("bazar", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteBazarItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Find item name to delete from ledger too
        Cursor c = db.rawQuery("SELECT item_name, date FROM bazar WHERE id = ?", new String[]{String.valueOf(id)});
        if (c != null && c.moveToFirst()) {
            String name = c.getString(0);
            String date = c.getString(1);
            c.close();
            // Delete corresponding ledger entry
            db.delete("cash", "description = ? AND date = ? AND type = 'OUT'", new String[]{"Bazar: " + name, date});
        }
        
        db.delete("bazar", "id=?", new String[]{String.valueOf(id)});
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
    public void addCashTransaction(String desc, double amount, String type, String date, String performedBy, String memberEmail) {
        ContentValues v = new ContentValues();
        v.put("description", desc); v.put("amount", amount); v.put("type", type); v.put("date", date);
        v.put("performed_by", performedBy); v.put("member_email", memberEmail);
        this.getWritableDatabase().insert("cash", null, v);
    }
    public void updateCashTransaction(int id, String desc, double amount, String type, String date) {
        ContentValues v = new ContentValues();
        v.put("description", desc); v.put("amount", amount); v.put("type", type); v.put("date", date);
        this.getWritableDatabase().update("cash", v, "id=?", new String[]{String.valueOf(id)});
    }
    public void deleteCashTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Find if this was an 'IN' transaction for a member before deleting
        Cursor c = db.rawQuery("SELECT type, amount, member_email FROM cash WHERE id = ?", new String[]{String.valueOf(id)});
        if (c != null && c.moveToFirst()) {
            String type = c.getString(0);
            double amount = c.getDouble(1);
            String email = c.getString(2);
            c.close();

            if ("IN".equalsIgnoreCase(type) && email != null && !email.isEmpty()) {
                // REVERT: Subtract the amount from member's paid total
                db.execSQL("UPDATE members SET paid_amount = paid_amount - ? WHERE email = ?", new Object[]{amount, email});
            }
        }
        
        db.delete("cash", "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getAllCashTransactions() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM cash ORDER BY id DESC", null);
    }
    public Cursor getFilteredCashTransactions(String userEmail, String role) {
        if ("Admin".equalsIgnoreCase(role)) {
            return getAllCashTransactions();
        }
        // Members see their own payments (IN) and all mess expenses (OUT)
        return this.getReadableDatabase().rawQuery(
                "SELECT * FROM cash WHERE type='OUT' OR (type='IN' AND member_email=?) ORDER BY id DESC",
                new String[]{userEmail});
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

    public double getBuaSalary() {
        double salary = 0;
        
        // 1. Try to get from utilities table first (where admin sets monthly bills)
        salary = getUtilityTotalByType("Bua Salary");
        if (salary > 0) return salary;

        // 2. Fallback to profile table
        Cursor c = getBuaProfile();
        if (c != null) {
            if (c.moveToFirst()) salary = c.getDouble(c.getColumnIndexOrThrow("salary"));
            c.close();
        }
        return salary;
    }

    // --- UTILITIES METHODS ---
    public void addUtility(String type, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete existing entry for this type to "Replace" it
        db.delete("utilities", "type = ?", new String[]{type});
        
        ContentValues v = new ContentValues();
        v.put("type", type); v.put("amount", amount); v.put("date", date);
        db.insert("utilities", null, v);

        // SYNC FIX: If it's Bua Salary, update the profile as well
        if ("Bua Salary".equalsIgnoreCase(type)) {
            db.execSQL("UPDATE bua_profile SET salary = ? WHERE id = 1", new Object[]{amount});
        }
    }
    public double getUtilityTotalByType(String type) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(amount) FROM utilities WHERE type=?", new String[]{type});
        double t = 0; if (c.moveToFirst()) t = c.getDouble(0); c.close(); return t;
    }
    public double getUtilitiesTotal() {
        // Exclude Bua Salary and House Rent to avoid double counting in shared costs
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(amount) FROM utilities WHERE type NOT IN ('Bua Salary', 'House Rent')", null);
        double t = 0; if (c.moveToFirst()) t = c.getDouble(0); c.close(); return t;
    }
    public double getHouseRent() {
        return getUtilityTotalByType("House Rent");
    }
    public int getUtilitiesCount() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM utilities", null);
        int n = 0; if (c.moveToFirst()) n = c.getInt(0); c.close(); return n;
    }
    public double getUtilityCollected(String type) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(amount) FROM cash WHERE type='IN' AND description LIKE ?", new String[]{"%Bill Payment: " + type + "%"});
        double t = 0; if (c.moveToFirst()) t = c.getDouble(0); c.close(); return t;
    }
    public double getTotalBillsCollected() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(amount) FROM cash WHERE type='IN' AND description LIKE 'Bill Payment:%'", null);
        double t = 0; if (c.moveToFirst()) t = c.getDouble(0); c.close(); return t;
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
        v.put("id", 1);
        v.put("name", name); v.put("phone", phone); v.put("address", address); 
        v.put("salary", salary); v.put("join_date", joinDate);
        this.getWritableDatabase().replace("bua_profile", null, v);
    }
    public void deleteBuaProfile() {
        this.getWritableDatabase().delete("bua_profile", "id=1", null);
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
    public Cursor getUserMealHistory(String email, String name) {
        String query = "SELECT date, SUM(b), SUM(l), SUM(d) FROM (" +
                "SELECT date, breakfast as b, lunch as l, dinner as d FROM meal_tracking WHERE user_email = ? " +
                "UNION ALL " +
                "SELECT date, " +
                "CASE WHEN meal_type = 'Breakfast' THEN meal_count ELSE 0 END as b, " +
                "CASE WHEN meal_type = 'Lunch' THEN meal_count ELSE 0 END as l, " +
                "CASE WHEN meal_type = 'Dinner' THEN meal_count ELSE 0 END as d " +
                "FROM guest_meals WHERE member_name = ?" +
                ") GROUP BY date ORDER BY date DESC LIMIT 30";
        
        return this.getReadableDatabase().rawQuery(query, new String[]{email, name});
    }
    public int getTotalMeals() {
        int regular = 0;
        Cursor c1 = this.getReadableDatabase().rawQuery("SELECT SUM(breakfast + lunch + dinner) FROM meal_tracking", null);
        if (c1.moveToFirst()) regular = c1.getInt(0);
        c1.close();

        int guest = 0;
        Cursor c2 = this.getReadableDatabase().rawQuery("SELECT SUM(meal_count) FROM guest_meals", null);
        if (c2.moveToFirst()) guest = c2.getInt(0);
        c2.close();

        return regular + guest;
    }
    public int getUserTotalMeals(String email, String name) {
        int regular = 0;
        Cursor c1 = this.getReadableDatabase().rawQuery("SELECT SUM(breakfast + lunch + dinner) FROM meal_tracking WHERE user_email=?", new String[]{email});
        if (c1.moveToFirst()) regular = c1.getInt(0);
        c1.close();

        int guest = 0;
        if (name != null && !name.isEmpty()) {
            Cursor c2 = this.getReadableDatabase().rawQuery("SELECT SUM(meal_count) FROM guest_meals WHERE member_name=?", new String[]{name});
            if (c2.moveToFirst()) guest = c2.getInt(0);
            c2.close();
        }

        return regular + guest;
    }

    public Cursor getGlobalMealHistory() {
        String query = "SELECT date, SUM(b), SUM(l), SUM(d) FROM (" +
                "SELECT date, breakfast as b, lunch as l, dinner as d FROM meal_tracking " +
                "UNION ALL " +
                "SELECT date, " +
                "CASE WHEN meal_type = 'Breakfast' THEN meal_count ELSE 0 END as b, " +
                "CASE WHEN meal_type = 'Lunch' THEN meal_count ELSE 0 END as l, " +
                "CASE WHEN meal_type = 'Dinner' THEN meal_count ELSE 0 END as d " +
                "FROM guest_meals" +
                ") GROUP BY date ORDER BY date DESC LIMIT 30";
        return this.getReadableDatabase().rawQuery(query, null);
    }

    public Cursor getMemberMealDetailsForDate(String date) {
        String query = "SELECT m.name, t.breakfast, t.lunch, t.dinner " +
                "FROM meal_tracking t " +
                "JOIN members m ON t.user_email = m.email " +
                "WHERE t.date = ? AND (t.breakfast > 0 OR t.lunch > 0 OR t.dinner > 0)";
        return this.getReadableDatabase().rawQuery(query, new String[]{date});
    }

    public int getMemberMealCount(String date, String type) {
        String col = "breakfast";
        if ("Lunch".equalsIgnoreCase(type)) col = "lunch";
        else if ("Dinner".equalsIgnoreCase(type)) col = "dinner";
        
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(" + col + ") FROM meal_tracking WHERE date=?", new String[]{date});
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close();
        return count;
    }

    public int getGuestMealCount(String date, String type) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT SUM(meal_count) FROM guest_meals WHERE date=? AND meal_type=?", new String[]{date, type});
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close();
        return count;
    }

    public int[] getTodaysMealCounts(String date) {
        int[] counts = new int[]{0, 0, 0};
        
        // Regular Meals
        Cursor c1 = this.getReadableDatabase().rawQuery(
                "SELECT SUM(breakfast), SUM(lunch), SUM(dinner) FROM meal_tracking WHERE date=?", 
                new String[]{date});
        if (c1.moveToFirst()) {
            counts[0] += c1.getInt(0);
            counts[1] += c1.getInt(1);
            counts[2] += c1.getInt(2);
        }
        c1.close();

        // Guest Meals
        Cursor c2 = this.getReadableDatabase().rawQuery(
                "SELECT meal_type, SUM(meal_count) FROM guest_meals WHERE date=? GROUP BY meal_type",
                new String[]{date});
        if (c2 != null) {
            while (c2.moveToNext()) {
                String type = c2.getString(0);
                int count = c2.getInt(1);
                if ("Breakfast".equalsIgnoreCase(type)) counts[0] += count;
                else if ("Lunch".equalsIgnoreCase(type)) counts[1] += count;
                else if ("Dinner".equalsIgnoreCase(type)) counts[2] += count;
            }
            c2.close();
        }

        return counts;
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
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT title, date FROM occasions WHERE id = ?", new String[]{String.valueOf(id)});
        if (c != null && c.moveToFirst()) {
            String title = c.getString(0);
            String date = c.getString(1);
            c.close();
            db.delete("cash", "description = ? AND date = ? AND type = 'OUT'", new String[]{"Occasion: " + title, date});
        }
        db.delete("occasions", "id=?", new String[]{String.valueOf(id)});
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
