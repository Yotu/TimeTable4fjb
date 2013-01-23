package jp.sample.sns_sdk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import jp.sample.time_table_info.TimeTableInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class SnsSender {
	private static String SEND_URL = "http://203.138.125.240/api/httpdocs/index.php";

	public void send(TimeTableInfo info) throws SendException {
		Log.d("SnsSender", String.format("UID=%s", info.getUid()));
		if (info.getUid() == null || info.getUid().equals("")) {
			throw new SendException("UIDが指定されていません。");
		}

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("title", info.getTitle()));
		params.add(new BasicNameValuePair("week", info.getDayOfWeek()));
		params.add(new BasicNameValuePair("time_table", info.getTimeTable()));
		params.add(new BasicNameValuePair("todo", info.getTodo()));
		params.add(new BasicNameValuePair("type", info.getType()));
		params.add(new BasicNameValuePair("uid", info.getUid()));

		HttpPost httpPost = new HttpPost(SEND_URL);

		try {
			// パラメータ設定
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse;

			httpResponse = client.execute(httpPost);

			// リクエストのステータスを取得し、通信が成功しているか確認
			// 400以上のステータスコードはエラーなので終了する。
			if (httpResponse.getStatusLine().getStatusCode() >= 400) {
				throw new SendException();
			}
			// レスポンスを取得
			HttpEntity entity = httpResponse.getEntity();
			// リソースを解放
			entity.consumeContent();
			// クライアントを終了させる
			client.getConnectionManager().shutdown();
		} catch (UnsupportedEncodingException e) {
			throw new SendException(e);
		} catch (ClientProtocolException e) {
			throw new SendException(e);
		} catch (ParseException e) {
			throw new SendException(e);
		} catch (IOException e) {
			throw new SendException(e);
		}

	}
}
