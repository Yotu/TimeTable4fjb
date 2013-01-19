package jp.sample.db_helper;

import java.util.ArrayList;
import java.util.List;

import jp.sample.time_table_info.TimeTableInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TimeTableSqlHelper extends SQLiteOpenHelper {
	private final String TAG = "timeTableSqlHelper";
	/** データベースバージョン */
	static final int DB_VERSION = 4;
	/** データベース ファイル名 */
	static final String DB = "time_table.db";

	public static final String TIME_TABLE = "time_table";

	/** テーブル作成 */
	private static final String CREATE_TABLE_SQL = "" +
			" create table " +
			TIME_TABLE +
			" (" +
//			" _id integer primary key autoincrement," +
			" _id integer," +
			" title varchar(255) null," +
			" week varchar(255) not null," +
			" time_table varchar(255) not null," +
			" todo varchar(255) not null," +
			" type varchar(255) not null," +
			" is_share integer," +
			" uid varchar(255) not null," +
//			" uid varchar(255) not null" +
			" primary key(week, time_table) " +
			" );" +
			"";
	private static String[] ITEMS = {"_id","title","week","time_table","todo","type","is_share","uid"};
	/** テーブル削除 */
	private static final String DROP_TABLE = "drop table " + TIME_TABLE+";";

	public TimeTableSqlHelper(Context context) {
		super(context, DB, null , DB_VERSION);
		Log.d(TAG,"timeTableSqlHelper");
	}

	/** テーブルを作成する */
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG,"onCreate");
		db.execSQL(CREATE_TABLE_SQL);
	}

	/** テーブル内のデータの変換を行う */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG,"onUpgrade");
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

	/** 新規レコード追加 */
	public long insert(TimeTableInfo info){
		ContentValues values = convertToContentValues(info);
		SQLiteDatabase db = getWritableDatabase();
		long rec = db.insert(TIME_TABLE, null, values);
		db.close();
		return rec;
	}


	/**  idからレコードを1件取得 */
	public TimeTableInfo fetchOne(String id){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TIME_TABLE, ITEMS, "_id = ?", new String[] { id }, null, null, null, null);
		cursor.moveToNext();
		TimeTableInfo info = convertToTimeTableInfo(cursor);
		cursor.close();
		db.close();
		return info;
	}

	/** idで指定したレコードを削除 */
	public void delete(String id){
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TIME_TABLE, "_id = ?", new String[] { id });
		db.close();
	}

	/** idで指定したレコードを更新 */
	public long update(TimeTableInfo info ,String id){
		ContentValues values = convertToContentValues(info);
		SQLiteDatabase db = getWritableDatabase();
		long rec =db.update(TIME_TABLE, values, "_id = ?", new String[] { id });
		db.close();
		return rec;
	}

	/** レコードを取得 */
	public List<TimeTableInfo> dayOfWeekAndTimeRead(String week, String time){
		List<TimeTableInfo> list = new ArrayList<TimeTableInfo>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TIME_TABLE, ITEMS, "week = ? AND time_table = ?" , new String[] {week,time}, null, null, "time_table",null);
		while(cursor.moveToNext()){
			list.add(convertToTimeTableInfo(cursor));
	   	}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * Cursor ー＞ TimeTableInfo
	 */
	private TimeTableInfo convertToTimeTableInfo(Cursor cursor) {
		TimeTableInfo info = new TimeTableInfo();
		info.setId(cursor.getInt(cursor.getColumnIndex("_id")));
		info.setTitle(cursor.getString(cursor.getColumnIndex("title")));
		info.setDayOfWeek(cursor.getString(cursor.getColumnIndex("week")));
		info.setTimeTable(cursor.getString(cursor.getColumnIndex("time_table")));
		info.setTodo(cursor.getString(cursor.getColumnIndex("todo")));
		info.setType(cursor.getString(cursor.getColumnIndex("type")));
		info.setUid(cursor.getString(cursor.getColumnIndex("uid")));
		if (cursor.getInt(cursor.getColumnIndex("is_share")) == 0) {
			info.setIsShare(false);
		} else {
			info.setIsShare(true);
		}
		return info;
	}

	/** TimeTableInfo ー＞ ContentValues */
	private ContentValues convertToContentValues(TimeTableInfo info) {
		ContentValues values = new ContentValues();
		values.put("title", info.getTitle());
		values.put("week", info.getDayOfWeek());
		values.put("time_table",info.getTimeTable());
		values.put("todo", info.getTodo());
		values.put("type", info.getType());
		values.put("uid",info.getUid());
		values.put("is_share",info.getIsShare());
		return values;
	}

	/*--------ここからあいやさんエリア--------*/

//	public static void weekDaySelectAndView(SQLiteDatabase db, String selectedWeekDay){
//		String sql
//		Cursor cursor = db.rawQuery(sql, null);
//	}
}
