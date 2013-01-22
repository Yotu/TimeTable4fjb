package jp.sample.time_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDbHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "lists.sqlite";
	public static final String TABLE = "type";
	private static final int DB_VERSION = 4;
	private static final String CREATE_TABLE = "create table " + TABLE + "("
			+ "id integer primary key autoincrement ,"
			+ "variety varchar(15) not null);";

	public MyDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 自動生成されたメソッド・スタブ
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public static void insert(SQLiteDatabase db, String varietyValue) {
		ContentValues cv = new ContentValues();
		cv.put("variety", varietyValue);
		long id = db.insert(TABLE, null, cv);
		if (id == -1) {
			Log.d("debug", "DBが不正です、データが追加できませんでした");
		}
	}
}
