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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class SnsReceiver {

	/** 受信用用URL*/
	private String RECEIVE_URL = "http://203.138.125.240/api/httpdocs/s01_rcv.php";

	private int timeId;
	private int subjectId;
	private int typeId;
	private int creatorId;


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

				for(String item:items){
					//フィールド名と値は#container#で区切られている為
					//#container#でフィールドと値を分割
					// 例) title#container#test
					TimeTableSqlHelper h = new TimeTableSqlHelper(null);

					String[] column = item.split("#container#");

					String value = (column.length == 2)?column[1]:"";
					if ("title".equals(column[0])) {
						info.setTitle(value);
						SQLiteDatabase db = getReadableDatabase();
						Cursor c = db.rawQuery("select～", null);
					} else if ("type".equals(column[0])) {
						info.setType(value);
					} else if ("week".equals(column[0])) {
						info.setDayOfWeek(value);
					} else if ("time_table".equals(column[0])) {
						info.setTimeTable(value);
					} else if ("todo".equals(column[0])) {
						info.setTodo(value);
					} else if ("uid".equals(column[0])) {
						info.setUid(value);
					}
					//Time_Tableに格納するために、それぞれのＩＤとテキストを比較

				}
				list.add(info);
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
