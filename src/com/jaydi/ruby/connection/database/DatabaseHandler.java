package com.jaydi.ruby.connection.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.appspot.ruby_mine.rubymine.model.Rubyzone;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "RubyzoneDB";

	private static final String TABLE_RUBYZONES = "Rubyzones";
	private static final String COL_ID = "id";
	private static final String COL_NAME = "name";
	private static final String COL_LAT = "lat";
	private static final String COL_LNG = "lng";
	private static final String COL_RANGE = "range";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createMessagePinTable = "CREATE TABLE " + TABLE_RUBYZONES + " (" + COL_ID + " INTEGER PRIMARY KEY, " + COL_NAME + " TEXT, " + COL_LAT
				+ " REAL, " + COL_LNG + " REAL, " + COL_RANGE + " INTEGER)";
		db.execSQL(createMessagePinTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUBYZONES);
			onCreate(db);
		}
	}

	public void addRubyzone(Rubyzone rubyzone) {
		if (rubyzone == null)
			return;

		ContentValues values = new ContentValues();
		values.put(COL_ID, rubyzone.getId());
		values.put(COL_NAME, rubyzone.getName());
		values.put(COL_LAT, rubyzone.getLat());
		values.put(COL_LNG, rubyzone.getLng());
		values.put(COL_RANGE, rubyzone.getRange());

		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_RUBYZONES, COL_ID + " = ?", new String[] { String.valueOf(rubyzone.getId()) });
		db.insert(TABLE_RUBYZONES, null, values);
		db.close();
	}

	public void addRubyzones(List<Rubyzone> rubyzones) {
		if (rubyzones == null)
			return;

		for (Rubyzone rubyzone : rubyzones)
			addRubyzone(rubyzone);
	}

	public List<Rubyzone> getRubyzones() {
		List<Rubyzone> rubyzones = new ArrayList<Rubyzone>();

		String[] cols = new String[] { COL_ID, COL_NAME, COL_LAT, COL_LNG, COL_RANGE };

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_RUBYZONES, cols, null, null, null, null, null);
		if (cursor == null)
			return rubyzones;
		if (cursor.isAfterLast())
			return rubyzones;

		if (cursor.moveToFirst())
			do {
				Rubyzone rubyzone = new Rubyzone();
				rubyzone.setId(cursor.getLong(0));
				rubyzone.setName(cursor.getString(1));
				rubyzone.setLat(cursor.getDouble(2));
				rubyzone.setLng(cursor.getDouble(3));
				rubyzone.setRange(cursor.getInt(4));
				rubyzones.add(rubyzone);
			} while (cursor.moveToNext());

		cursor.close();
		db.close();

		return rubyzones;
	}

	public void deleteRubyzone(long id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_RUBYZONES, COL_ID + " = ?", new String[] { String.valueOf(id) });
		db.close();
	}

	public void deleteRubyzoneAll() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_RUBYZONES, null, null);
		db.close();
	}

}
