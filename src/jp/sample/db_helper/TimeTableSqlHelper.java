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
	private static final int DB_VERSION = 4;

	/** データベース名 */
	private static final String DB = "time_table.db";

	/** テーブル名 */
	public static final String TIME_TABLE = "time";
	public static final String TIMENAME_TABLE = "time_name";
	public static final String SUBJECT_TABLE = "subject";
	public static final String TYPE_TABLE = "type";
	public static final String CREATOR_TABLE = "creator";
	public static final String REMARKS_TABLE = "remarks";

	/** CREATEテーブル */
	private static final String CREATE_TIME = "create table "
			+ TIME_TABLE + "("
			+ "week integer not null,"
			+ "timeid integer not null,"
			+ "subjectid integer not null,"
			+ "typeid integer not null default 0,"
			+ "share integer not null default 0,"
			+ "creatorid integer not null,"
			+ "uptime timestamp not null,"
			+ "primary key(week, timeid)"
			+ ");";
	private static final String CREATE_TIMENAME = "create table "
			+ TIMENAME_TABLE + "("
			+ "timeid integer primary key autoincrement,"
			+ "time_name varchar(15) not null"
			+ ");";
	private static final String CREATE_SUBJECT = "create table "
			+ SUBJECT_TABLE + "("
			+ "subjectid integer primary key autoincrement,"
			+ "subject_name varchar(12) not null,"
			+ "place varchar(12) not null,"
			+ "charge varchar(10) default null"
			+ ");";
	private static final String CREATE_TYPE = "create table "
			+ TYPE_TABLE + "("
			+ "typeid integer primary key autoincrement,"
			+ "type varchar(15) not null"
			+ ");";
	private static final String CREATE_CREATOR = "create table "
			+ CREATOR_TABLE + "("
			+ "creatorid integer primary key autoincrement,"
			+ "androidid varchar(15) not null,"
			+ "userid varchar(20),"
			+ "password varchar(20)"
			+ ");";
	private static final String CREATE_REMARKS = "create table "
			+ REMARKS_TABLE + "("
			+ "date timestamp not null,"
			+ "timeid integer default null,"
			+ "remarks text,"
			+ "share numeric not null,"
			+ "creatorid integer not null,"
			+ "upremarks timestamp not null,"
			+ "primary key(date, timeid)"
			+ ");";

	public TimeTableSqlHelper(Context context) {
		super(context, DB, null, DB_VERSION);
		Log.d(TAG, "timeTable");
	}

	/** 新規追加
	 * table_name: テーブル名
	 * ct: 追加データ
	 * @return
	 */
	public long insert(String table_name, ContentValues ct) {
		Log.d(TAG, "insert");

		SQLiteDatabase db = getWritableDatabase();
		long rec = db.insert(table_name, null, ct);
		db.close();

		return rec;
	}

	/** 更新
	 * table_name: 更新テーブル名
	 * ct: 更新する項目と値をもったContentValuesオブジェクト
	 * whereCode: 条件式。"id = ? and name != ?"のような書き方をしたら、whereParam[]も設定。
	 *                    "id = 1"のような書き方をした場合は、whereParam[]はnullにする。
	 * whereParam: 条件の値。whereCodeの内容に応じて設定。
	 */
	public long update(String table_name, ContentValues ct, String whereCode, String[] whereParam) {
		Log.d(TAG, "update");

		SQLiteDatabase db = getWritableDatabase();
		long rec = db.update(table_name, ct, whereCode, whereParam);
		db.close();

		return rec;
	}

	/** 削除
	 * table_name: 削除テーブル名
	 * whereCode: 条件式。"id = ? and name != ?"のような書き方をしたら、whereParam[]も設定。
	 *                    "id = 1"のような書き方をした場合は、whereParam[]はnullにする。
	 * whereParam: 条件の値。whereCodeの内容に応じて設定。
	 */
	public long delete(String table_name, String where, String[] whereParam) {
		Log.v(TAG, String.format("delete: where=%s whereParam=%s", where, whereParam));
		SQLiteDatabase db = null;
		long ret = -1;
		try {
			db = getWritableDatabase();
			ret = db.delete(table_name, where, whereParam);
		} catch (SQLException e) {

		} finally {
			if (db != null) {
				db.close();
			}
		}
		return ret;
	}

	public void defaultTypeTable() {
		Log.d(TAG, "defaultTypeTable");
		getWritableDatabase().rawQuery("delete from " + TYPE_TABLE, null);
		ContentValues ct = new ContentValues();
		Cursor c = null;
		try {
				c = getReadableDatabase().rawQuery("select * from " + TYPE_TABLE + " where type like '講義';", null);
				if (c.getCount() <= 0) {
					ct.put("type",  "講義");
					insert(TYPE_TABLE, ct);
					ct.clear();
				}
				c = getReadableDatabase().rawQuery("select * from " + TYPE_TABLE + " where type like '演習';", null);
				if (c.getCount() <= 0) {
					ct.put("type",  "演習");
					insert(TYPE_TABLE, ct);
					ct.clear();
				}
				c = getReadableDatabase().rawQuery("select * from " + TYPE_TABLE + " where type like 'プライベート';", null);
				if (c.getCount() <= 0) {
					ct.put("type",  "プライベート");
					insert(TYPE_TABLE, ct);
					ct.clear();
				}
		} catch (SQLException e) {
			Log.d(TAG, "SQLException");
			e.printStackTrace();
		}
	}

	public void defaultTimeNameTable() {
		Log.d(TAG, "defaultTimeNameTable");
		getWritableDatabase().rawQuery("delete from " + TIMENAME_TABLE, null);
		ContentValues ct = new ContentValues();
		Cursor c = null;
		try {
			for (int i = 0; i < 6; i++) {
				c = getReadableDatabase().rawQuery("select * from " + TIMENAME_TABLE + " where time_name like '" + ((i + 1) + "限目") + "';", null);
				if (c.getCount() <= 0) {
					ct.put("time_name",  (i + 1) + "限目");
					insert(TIMENAME_TABLE, ct);
					ct.clear();
				}
			}
		} catch (SQLException e) {
			Log.d(TAG, "SQLException");
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate");
		db.execSQL(CREATE_TIMENAME);
		db.execSQL(CREATE_SUBJECT);
		db.execSQL(CREATE_TYPE);
		db.execSQL(CREATE_TIME);
		db.execSQL(CREATE_CREATOR);
		db.execSQL(CREATE_REMARKS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(TAG, String.format("onUpgrade: oldVersion=%dformat, newVersion=%d", oldVersion, newVersion));
		db.execSQL("drop table " + TIME_TABLE);
		db.execSQL("drop table " + TIMENAME_TABLE);
		db.execSQL("drop table " + SUBJECT_TABLE);
		db.execSQL("drop table " + TYPE_TABLE);
		db.execSQL("drop table " + CREATE_CREATOR);
		db.execSQL("drop table " + REMARKS_TABLE);
		onCreate(db);
	}
}
