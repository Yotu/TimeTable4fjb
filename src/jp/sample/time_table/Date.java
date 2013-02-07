package jp.sample.time_table;

import java.util.Calendar;
import java.util.Locale;

public class Date {
	// 日付の区切り記号
	private final String sep = "/";

	private Calendar cal = null;
	private String[] WEEK = {
			"日",		// 0
			"月",		// 1
			"火",		// 2
			"水",		// 3
			"木",		// 4
			"金",		// 5
			"土",		// 6
	};
	
	// 現在日時
	private int nowYear;
	private int nowMonth;
	private int nowDay;
	private int nowWeek;
		
	// 現在の操作日時
	private int curYear;
	private int curMonth;
	private int curDay;
	private int curWeek;

	public Date() {
		if (cal == null) {
			cal = Calendar.getInstance(Locale.JAPAN);
			curYear = nowYear = cal.get(Calendar.YEAR);
			curMonth = nowMonth = cal.get(Calendar.MONTH) + 1;
			curDay = nowDay = cal.get(Calendar.DAY_OF_MONTH);
			curWeek = nowWeek = cal.get(Calendar.DAY_OF_WEEK);
		}
	}

	/**
	 * 現在操作対象になっている年を返す
	 * @return
	 */
	public int getCurrentYear() {
		return curYear;
	}
	
	/**
	 * 現在操作対象になっている月を返す
	 * @return
	 */
	public int getCurrentMonth() {
		return curMonth;
	}
	
	/**
	 * 現在操作対象になっている日を返す
	 * @return
	 */
	public int getCurrentDay() {
		return curDay;
	}

	/**
	 * 現在操作対象になっている曜日を数値で返す
	 * @return
	 */
	public int getCurrentWeek() {
		return curWeek;
	}

	/**
	 * 現在操作対象になっている曜日を文字で返す
	 * @return
	 */
	public String getCurrentWeekStr() {
		return WEEK[curWeek];
	}
	
	/**
	 * 現在、操作対象になっている年月日を文字列型で返す
	 * @return
	 */
	public String getCurrentDate() {
		return new String(curYear + sep + curMonth + sep + curDay);
	}
	

	/**
	 * 現在の年を返す
	 * @return
	 */
	public int getYear() {
		return nowYear;
	}
	
	/**
	 * 現在の月を返す
	 * @return
	 */
	public int getMonth() {
		return nowMonth;
	}
	
	/**
	 * 現在の日を返す
	 * @return
	 */
	public int getDay() {
		return nowDay;
	}

	/**
	 * 現在の曜日を数値で返す
	 * @return
	 */
	public int getWeek() {
		return nowWeek;
	}

	/**
	 * 現在の曜日を文字で返す
	 * @return
	 */
	public String getWeekStr() {
		return WEEK[nowWeek];
	}
	
	/**
	 * 現在の年月日を文字列型で返す
	 * @return
	 */
	public String getDate() {
		return new String(nowYear + sep + nowMonth + sep + nowDay);
	}

	
	
	/**
	 * Dateクラスの情報を返す
	 */
	public String toString() {
		return "" + Date.class;
	}
	
}
