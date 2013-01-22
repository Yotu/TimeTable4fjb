package jp.sample.sns_sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jp.sample.db_helper.TimeTableSqlHelper;
import jp.sample.time_table_info.TimeTableInfo;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SnsReceiver {

	/** 受信用用URL */
	private String RECEIVE_URL = "http://203.138.125.240/api/httpdocs/s01_rcv.php";
	private static String TAG ="SnsReceiver";
	private int timeId;
	private int subjectId;
	private int typeId;
	private int userId;
	private int week;
	private int getData;
	private String todo;
	private String table;
	private String field;
	private String[] weekArray = { "日曜日", "月曜日", "火曜日", "水曜日", "木曜日", "金曜日","土曜日" };
	private int isShare;



	/***
	 *
	 * @param userName
	 * ご自分のユーザ名を指定して下さい。
	 * @return
	 */
	public List<TimeTableInfo> receive(String userName) throws ReceiveException {
		List<TimeTableInfo> list = new ArrayList<TimeTableInfo>();
		HttpClient objHttp = new DefaultHttpClient();
		Log.d(TAG,"start SnsReceiver");
		try {
			// Postリクエストの準備を行う。
			HttpPost objPost = new HttpPost(RECEIVE_URL);

			// POSTするデータを設定
			// 自分のデータ以外のスケジュールを取得するためにuserNameを渡す。
			List<NameValuePair> objValuePairs = new ArrayList<NameValuePair>(2);
			objValuePairs.add(new BasicNameValuePair("uid", userName));
			objPost.setEntity(new UrlEncodedFormEntity(objValuePairs, "UTF-8"));

			HttpResponse objResponse = objHttp.execute(objPost);

			// リクエストのステータスを取得し、通信が成功しているか確認
			// 400以上のステータスコードはエラーなので終了する。
			if (objResponse.getStatusLine().getStatusCode() >= 400) {
				Log.d(TAG,"400>error");
				throw new ReceiveException();
			}

			// 戻って来たものStreamで読み込み
			Log.d(TAG,"start　インスタンス");
			InputStream objStream = objResponse.getEntity().getContent();
			InputStreamReader objReader = new InputStreamReader(objStream);
			BufferedReader objBuf = new BufferedReader(objReader);

			// 一レコードにあたるデータを読み込み
			String sLine;
			Log.d(TAG,"１行ずつ読み込み開始");
			while ((sLine = objBuf.readLine()) != null) {
				// 1つのフィールドは\tで区切られている為
				// \tで分割
				String[] items = sLine.split("\t");
				TimeTableInfo info = new TimeTableInfo();
				Log.d(TAG,"SQLのインスタンス作成");
				TimeTableSqlHelper h = new TimeTableSqlHelper(null);
				SQLiteDatabase db = h.getWritableDatabase();
				ContentValues values = new ContentValues();
				Log.d(TAG,"make cursor");
				Cursor c = null;
				Log.d(TAG,"SQLのインスタンス作成完了！");

				for (String item : items) {
					// フィールド名と値は#container#で区切られている為
					// #container#でフィールドと値を分割
					// 例) title#container#test

					String[] column = item.split("#container#");

					String value = (column.length == 2) ? column[1] : "";
					if ("title".equals(column[0])) {
						Log.d(TAG,"授業科目");
						//		-----------------------------------------------------------------------
						//
						//		授業科目
						//
						//		-----------------------------------------------------------------------

						info.setTitle(value);
						table = "subject";
						field = "subject_name";
						ContentValues subjectValues = new ContentValues();

						//subjectテーブルにない場合はplaceを nullにして格納する
						String sql = "SELECT * FROM subject WHERE subject_name ='"+ value +"' AND place is null;";

						c = db.rawQuery(sql,null);
						if(c.getCount() == 0){
							subjectValues.put(table,value); //subjectテーブルに授業科目名を格納する
							c = db.rawQuery(sql,null);      //もう一度検索
							c.moveToFirst();
							subjectId =c.getInt(0);
						}else{
							c.moveToFirst();
							subjectId =c.getInt(0);
						}

					} else if ("type".equals(column[0])) {
						Log.d(TAG,"種類");
						//		-----------------------------------------------------------------------
						//
						//		種類
						//
						//		-----------------------------------------------------------------------
						info.setType(value);
						table = "type";
						field = "type";
						c = db.rawQuery("SELECT typeid FROM type WHERE " + table+ "."+ field +" = "+ value +";",null);
						typeId =c.getInt(0);



					} else if ("week".equals(column[0])) {
						Log.d(TAG,"曜日");
						//						-----------------------------------------------------------------------
						//
						//		曜日
						//
						//		-----------------------------------------------------------------------
						info.setDayOfWeek(value);
						for (int i = 0; value.equals(weekArray); i++) {
							week = i;
						}


					} else if ("time_table".equals(column[0])) {
						Log.d(TAG,"時限名");
						//		-----------------------------------------------------------------------
						//
						//		時限名
						//
						//		-----------------------------------------------------------------------
						info.setTimeTable(value);
						table = "time_table";
						field = "time_name";
						c = db.rawQuery("SELECT time_table FROM timeid WHERE " + table + "."+ field +" = "+ value +";",null);
						timeId = c.getInt(0);


					} else if ("todo".equals(column[0])) {
						Log.d(TAG,"備考情報");
						//		-----------------------------------------------------------------------
						//
						//		備考情報
						//
						//		-----------------------------------------------------------------------
						info.setTodo(value);
						todo = value;



					} else if ("uid".equals(column[0])) {
						Log.d(TAG,"ユーザー情報");
						//		-----------------------------------------------------------------------
						//
						//		ユーザー情報
						//
						//		-----------------------------------------------------------------------
						info.setUid(value);
						table = "creator";
						field = "userid";
						ContentValues creatorValues = new ContentValues();

						//CreatorTableにない場合は格納する
						String sql = "SELECT * FROM creator WHERE userid ='"+ value +"';";
						c = db.rawQuery(sql,null);
						if(c.getCount() == 0){
							creatorValues.put(table,value); //subjectテーブルに授業科目名を格納する
							c = db.rawQuery(sql,null);      //もう一度検索
							c.moveToFirst();
							userId =c.getInt(0);
						}else{
							c.moveToFirst();
							userId =c.getInt(0);
						}
					}
					//		-----------------------------------------------------------------------
					//
					//		データベースに追加処理
					//
					//		-----------------------------------------------------------------------

					//					// Time_Tableに格納するために、それぞれのＩＤとテキストを比較
					//					if (!(table == null) || !(field == null)) {
					//						c = db.rawQuery("SELECT * FROM time,'" + table
					//								+ "' WHERE '" + column[0] + "' like '" + table
					//								+ "'.'" + field + "';", null);
					//					}

					//					// データを検索
					//					c.moveToFirst();
					//					int get = c.getCount();
					//					for (int i = 0; i < get; i++) {
					//						getData = c.getInt(0);
					//						c.moveToNext();
					//					}
					//					// insert用のvaluesに追加していく
					//					values.put(table, getData);


				}
				// 現在のUnixタイム取得
				long currentTimeMillis = System.currentTimeMillis();
				// 数値から文字列に変更
				String timestamp = String.valueOf(currentTimeMillis);
				//シェア判定は当然True
				isShare = 1;
				Log.d(TAG,"受け取ったものを格納開始");
				Log.d(TAG,"week ="+week);
				Log.d(TAG,"時限="+timeId);
				Log.d(TAG,"授業科目="+subjectId);
				Log.d(TAG,"シェア＝"+isShare);
				Log.d(TAG,"ユーザID="+userId);
				Log.d(TAG,"タイムスタンプ="+timestamp);
				//time  |曜日	|時限ID	|授業科目ID	|種類ID	|シェア（時間割)	|作成者ID		|更新日	|
				values.put("week", week);
				values.put("timeid", timeId);
				values.put("subjectid", subjectId);
				values.put("typeid", typeId);
				values.put("share", isShare);
				values.put("creatorid", userId);
				values.put("uptime", timestamp);
				h.insert("time", values);
				c.close();
				db.close();
				h.close();


			}
			objBuf.close();
			objReader.close();
			objStream.close();

		} catch (IOException e) {
			throw new ReceiveException(e);
		}
		return list;
	}

}