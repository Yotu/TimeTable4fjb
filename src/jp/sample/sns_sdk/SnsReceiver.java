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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class SnsReceiver {

	/** 受信用用URL*/
	private String RECEIVE_URL = "http://203.138.125.240/api/httpdocs/s01_rcv.php";

	private Cursor c;
	private int timeId;
	private int subjectId;
	private int typeId;
	private int creatorId;
	private int week;
	private int getData;
	private String todo;
	private String table;
	private String field;
	private String[] weekArray = {"日曜日","月曜日","火曜日","水曜日","木曜日","金曜日","土曜日"};




	/***
	 *
	 * @param userName ご自分のユーザ名を指定して下さい。
	 * @return
	 */
	public List<TimeTableInfo> receive(String userName) throws ReceiveException {
		List<TimeTableInfo> list = new ArrayList<TimeTableInfo>();
		HttpClient objHttp = new DefaultHttpClient();

		try {
			//Postリクエストの準備を行う。
			HttpPost objPost = new HttpPost(RECEIVE_URL);

			//POSTするデータを設定
			//自分のデータ以外のスケジュールを取得するためにuserNameを渡す。
			List<NameValuePair> objValuePairs = new ArrayList<NameValuePair>(2);
			objValuePairs.add(new BasicNameValuePair("uid",userName));
			objPost.setEntity(new UrlEncodedFormEntity(objValuePairs, "UTF-8"));

			HttpResponse objResponse = objHttp.execute(objPost);


			//リクエストのステータスを取得し、通信が成功しているか確認
			//400以上のステータスコードはエラーなので終了する。
			if (objResponse.getStatusLine().getStatusCode() >= 400) {
				throw new ReceiveException();
			}

			//戻って来たものStreamで読み込み
			InputStream objStream = objResponse.getEntity().getContent();
			InputStreamReader objReader = new InputStreamReader(objStream);
			BufferedReader objBuf = new BufferedReader(objReader);

			//一レコードにあたるデータを読み込み
			String sLine;
			while((sLine = objBuf.readLine()) != null){
				//1つのフィールドは\tで区切られている為
				//\tで分割
				String[] items = sLine.split("\t");

				TimeTableInfo info = new TimeTableInfo();
				TimeTableSqlHelper h = new TimeTableSqlHelper(null);
				SQLiteDatabase db = h.getReadableDatabase();
				ContentValues values = new ContentValues();

				for(String item:items){
					//フィールド名と値は#container#で区切られている為
					//#container#でフィールドと値を分割
					// 例) title#container#test



					String[] column = item.split("#container#");

					String value = (column.length == 2)?column[1]:"";

					if ("title".equals(column[0])) {
						info.setTitle(value);
						table = "subject";
						field = "subject_name";
					} else if ("type".equals(column[0])) {
						info.setType(value);
						table = "type";
						field = "type";
					} else if ("week".equals(column[0])) {
						info.setDayOfWeek(value);
						for(int i = 0;column[0].equals(weekArray);i++){
							week = i;
						}
					} else if ("time_table".equals(column[0])) {
						info.setTimeTable(value);
						table = "time_table";
						field = "time_name";
					} else if ("todo".equals(column[0])) {
						info.setTodo(value);
						todo = column[0];
					} else if ("uid".equals(column[0])) {
						info.setUid(value);
						table = "creator";
						field = "androidid";
					}
					//Time_Tableに格納するために、それぞれのＩＤとテキストを比較
					if(!(table == null) || !(field == null)){
						c = db.rawQuery("SELECT * FROM time,'"+ table +"' WHERE '" + column[0] + "' like '"+ table +"'.'" + field +"';",null);
					}

					//データを検索
					c.moveToFirst();
					int get = c.getCount();
					for (int i = 0; i < get; i++) {
					    getData =c.getInt(0);
					    c.moveToNext();
					}
					//insert用のvaluesに追加していく
					values.put(table,getData);
					c.close();

				}


				list.add(info);
				h.insert("time",values);
			}
			objBuf.close();
			objReader.close();
			objStream.close();

		}catch (IOException e){
			throw new ReceiveException(e);
		}
		return list;
	}


}
