package jp.sample.time_table_info;

public class TimeTableInfo {
	/** タイムテーブルID */
	int _id;
	/** タイムテーブル */
	String subject;
	/** 曜日 */
	String day_of_week;
	/** 時間割 */
	String time_table;
	/** 備考メモ */
	String todo;
	/** 　場所 */
	String place;
	/** 予定の種類 */
	String type;
	/** シェアする */
	boolean isShare = false;
	boolean bikoShare = false;
	/** ユーザー名 */
	String uid;

	// listViewに表示する曜日一覧
	public static String[] dayOfWeeks = { "日曜日", "月曜日", "火曜日", "水曜日", "木曜日",
			"金曜日", "土曜日", };

	public static String[] timeTables = { "0時限目", "1時限目", "2時限目", "3時限目",
			"4時限目", "5時限目", "6時限目", "7時限目", "放課後", };

	public static String[] types = { "学科", "実技", "プライベート", };

	public TimeTableInfo() {
		subject = "";
		day_of_week = "";
		time_table = "";
		todo = "";
		type = "";
		place = "";
		uid = "";
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		this._id = id;
	}

	public String getTitle() {
		return subject;
	}

	public void setTitle(String title) {
		this.subject = title;
	}

	public String getDayOfWeek() {
		return day_of_week;
	}

	public void setDayOfWeek(String day_of_week) {
		this.day_of_week = day_of_week;
	}

	public String getTimeTable() {
		return time_table;
	}

	public void setTimeTable(String time_table) {
		this.time_table = time_table;
	}

	public String getTodo() {
		return todo;
	}

	public void setTodo(String todo) {
		this.todo = todo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getIsShare() {
		return isShare;
	}

	public void setIsShare(boolean isShare) {
		this.isShare = isShare;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public boolean getBikoShare() {
		return bikoShare;
	}

	public void setBikoShare(boolean bikoShare) {
		this.bikoShare = bikoShare;
	}

}
