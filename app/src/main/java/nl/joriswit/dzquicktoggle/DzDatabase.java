package nl.joriswit.dzquicktoggle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DzDatabase {
    public static void loadItems(Context context, ArrayList<Switch> items){
        DzDatabaseOpenHelper openHelper = new DzDatabaseOpenHelper(context);
        try (SQLiteDatabase db = openHelper.getReadableDatabase()) {
            db.beginTransaction();
            try {
                Cursor cur = db.query("Switches", new String[] {"Idx", "Name"}, null, null, null, null, "Name");

                int idxColumnIndex = cur.getColumnIndex("Idx");
                int nameColumnIndex = cur.getColumnIndex("Name");

                cur.moveToFirst();
                items.clear();
                while (!cur.isAfterLast()) {
                    int idx = cur.getInt(idxColumnIndex);
                    String name = cur.getString(nameColumnIndex);
                    Switch lightSwitch = new Switch(idx, name);

                    items.add(lightSwitch);
                    cur.moveToNext();
                }
                cur.close();

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    public static void update(Context context, ArrayList<Switch> items){
        DzDatabaseOpenHelper openHelper = new DzDatabaseOpenHelper(context);
        try (SQLiteDatabase db = openHelper.getWritableDatabase()) {
            db.beginTransaction();

            try {
                db.delete("Switches", null, null);

                for (Switch sw : items) {
                    ContentValues values = new ContentValues();
                    values.put("Idx", sw.idx);
                    values.put("Name", sw.name);
                    db.insert("Switches", "ID", values);
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }
}
