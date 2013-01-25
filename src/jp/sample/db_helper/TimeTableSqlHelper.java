package jp.sample.db_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TimeTableSqlHelper extends SQLiteOpenHelper {
	private final String TAG = "TimeTableSqlHelper";
	private static final int DB_VERSION = 5;

	/** データベース名 */
	private static final String DB = "time_table.db";

	/** create および　drop */
	private static final String CREATE = "create table ";
	private static final String DROP = "drop table ";
	
	/** テーブル名 */
	public static final String TIME_TABLE = "time";
	public static final String TIMENAME_TABLE = "time_name";
	public static final String SUBJECT_TABLE = "subject";
	public static final String TYPE_TABLE = "type";
	public static final String CREATOR_TABLE = "creator";
	public static final String REMARKS_TABLE = "remarks";


	/** CREATEテーブル */
	private static final String CREATE_TIME = CREATE + TIME_TABLE + "("
			+ "id integer primary key autoincrement,"
			+ "week integer not null,"
			+ "timeid integer not null,"
			+ "subjectid integer not null,"
			+ "typeid integer not null default 1,"
			+ "share integer not null default 1,"
			+ "creatorid integer not null,"
			+ "uptime timestamp not null" + ");";
	private static final String CREATE_TIMENAME = CREATE + TIMENAME_TABLE + "("
			+ "timeid integer primary key autoincrement,"
			+ "time_name varchar(15) not null" + ");";
	private static final String CREATE_SUBJECT = CREATE + SUBJECT_TABLE + "("
			+ "subjectid integer primary key autoincrement,"
			+ "subject_name varchar(12) not null,"
			+ "place varchar(12)" + ");";
	private static final String CREATE_TYPE = CREATE + TYPE_TABLE + "("
			+ "typeid integer primary key autoincrement,"
			+ "type varchar(15) not null" + ");";
	private static final String CREATE_CREATOR = CREATE + CREATOR_TABLE + "("
			+ "creatorid integer primary key autoincrement,"
			+ "userid varchar(20)" + ");";
	private static final String CREATE_REMARKS = CREATE + REMARKS_TABLE + "("
			+ "id integer primary key autoincrement,"
			+ "date timestamp not null,"
			+ "timeid integer default null,"
			+ "remarks text,"
			+ "share numeric not null,"
			+ "creatorid integer not null,"
			+ "upremarks timestamp not null" + ")";

	public TimeTableSqlHelper(Context context) {
		super(context, DB, null, DB_VERSION);
	}

	/**
	 * 新規追加
	 * table_name: テーブル名
	 * ct: 追加データ
	 */
	public long insert(String table, ContentValues ct) {
		Log.d(TAG, "insert");
		SQLiteDatabase db = getWritableDatabase();
		long rec = db.insert(table, null, ct);
		Log.d(TAG, String.format("insert: return code=%d", rec));
		db.close();
		Log.d(TAG, "DB閉じたよ");
		return rec;
	}

	/**
	 * 更新 table_name: 更新テーブル名 ct: 更新する項目と値をもったContentValuesオブジェクト whereCode:
	 * 条件式。"id = ? and name != ?"のような書き方をしたら、whereParam[]も設定。
	 * "id = 1"のような書き方をした場合は、whereParam[]はnullにする。 whereParam:
	 * 条件の値。whereCodeの内容に応じて設定。
	 */
	public long update(String table, ContentValues ct, String wCode, String[] wParam) {
		Log.d(TAG, "update");

		SQLiteDatabase db = getWritableDatabase();
		long rec = db.update(table, ct, wCode, wParam);
		Log.d(TAG, String.format("update: return code=%d", rec));
		db.close();

		return rec;
	}

	/**
	 * 削除 table_name: 削除テーブル名 whereCode:
	 * 条件式。"id = ? and name != ?"のような書き方をしたら、whereParam[]も設定。
	 * "id = 1"のような書き方をした場合は、whereParam[]はnullにする。 whereParam:
	 * 条件の値。whereCodeの内容に応じて設定。
	 */
	public long delete(String table, String where, String[] wParam) {
		Log.v(TAG, String.format("delete: where=%s wParam=%s", where, wParam));

		SQLiteDatabase db = getWritableDatabase();
		long rec = -1;
		try {
			rec = db.delete(table, where, wParam);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Log.d(TAG, String.format("delete: return code=%d", rec));
			if (db != null) {
				db.close();
			}
		}
		return rec;
	}

	public void dummyDataInsert() {
		Log.d(TAG, "dummyDataInsert");

		SQLiteDatabase db = getWritableDatabase();
		db.rawQuery("insert into subject values(null, 'Java演習', '204');", null);
		db.rawQuery("insert into subject values(null, 'コンピュータ・システム', '303');", null);
		db.rawQuery("insert into subject values(null, 'Office演習', '401');", null);
		Log.d(TAG,"３つは追加した");
		db.rawQuery("insert into type values(null, '私リカちゃん');", null);
		Log.d(TAG,"リカちゃん");

		db.close();
	}


	public void defaultTypeTable() {
		Log.d(TAG, "defaultTypeTable");
		SQLiteDatabase db = getWritableDatabase();
		db.rawQuery("delete from type;", null);
		Log.d(TAG, "delete");
		ContentValues ct = new ContentValues();

		Log.d(TAG, "create");
		ct.put("type", "学科");
		insert(TYPE_TABLE, ct);
		ct.clear();
		ct.put("type", "実技");
		insert(TYPE_TABLE, ct);
		ct.clear();
		ct.put("type", "プライベート");
		insert(TYPE_TABLE, ct);
		ct.clear();

		db.close();
	}

	public void defaultTimeNameTable() {
		Log.d(TAG, "defaultTimeNameTable");
		SQLiteDatabase db = getWritableDatabase();
		db.rawQuery("delete from " + TIMENAME_TABLE, null);
		ContentValues ct = new ContentValues();

		for (int i = 0; i < 8; i++) {
			ct.put("time_name", i + "時限目");
			insert(TIMENAME_TABLE, ct);
			ct.clear();
		}
		ct.put("time_name", "放課後");
		insert(TIMENAME_TABLE, ct);
		ct.clear();

		db.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate");
		try {
			db.execSQL(CREATE_TIMENAME);
			db.execSQL(CREATE_SUBJECT);
			db.execSQL(CREATE_TYPE);
			db.execSQL(CREATE_TIME);
			db.execSQL(CREATE_CREATOR);
			db.execSQL(CREATE_REMARKS);
		} catch (SQLException e) {
			Log.d(TAG, "onCreate()でSQLException発生");
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(TAG, String.format(
				"onUpgrade: oldVersion=%dformat, newVersion=%d",
				oldVersion, newVersion));

		try {
			db.execSQL(DROP + TIME_TABLE);
			db.execSQL(DROP + TIMENAME_TABLE);
			db.execSQL(DROP + SUBJECT_TABLE);
			db.execSQL(DROP + TYPE_TABLE);
			db.execSQL(DROP + CREATE_CREATOR);
			db.execSQL(DROP + REMARKS_TABLE);
			onCreate(db);
		} catch (SQLException e) {
			Log.d(TAG, "onUpgrade()でSQLException発生");
			e.printStackTrace();
		}
	}
}
