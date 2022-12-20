package nl.joriswit.dzquicktoggle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DzDatabaseOpenHelper extends SQLiteOpenHelper {

    public DzDatabaseOpenHelper(Context context) {
        super(context, "DZQUICKTOGGLE", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE Switches (";
        sql += " Idx integer PRIMARY KEY,";
        sql += " Name text NOT NULL";
        sql += ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
