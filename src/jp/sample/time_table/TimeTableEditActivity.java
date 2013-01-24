package jp.sample.time_table;

import jp.sample.db_helper.TimeTableSqlHelper;
import jp.sample.sns_sdk.SendException;
import jp.sample.sns_sdk.SnsSender;
import jp.sample.time_table_info.TimeTableInfo;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TimeTableEditActivity extends Activity implements OnClickListener {

	/** デバッグ用タグ */
	private static final String TAG = "TimeTableActivity";

	/** EditText 授業名 */
	private EditText subjectEdt;
	/** EditText 備考 */
	private EditText remarksEdt;
	/** Spinner 曜日 */
	private Spinner weekSpr;
	/** Spinner 時間割 */
	private Spinner timeTableSpr;
	/** Spinner 予定の種類 */
	private Spinner typeSpr;
	/** Button 登録用ボタン */
	private Button addBtn;
	/** Button キャンセル用ボタン */
	private Button cancelBtn;
	/** Button 種類追加用ボタン */
	private Button addVarietyButton;
	/** int timeTableId */
	/** シェア */
	private CheckBox shareCb; // 時間割のシェア
	private CheckBox bikoShareCb;
	/** 場所 */
	private EditText placeEdt;
	/**Edit mode*/
	private boolean EditMode = false;



	private TimeTableSqlHelper sqlHelper = new TimeTableSqlHelper(this);
	private SQLiteDatabase db;
	private SQLiteDatabase mDb;  //MyDbHelper用
	private String table;

	private int subjectid;
	private int typeid;
	private int week;
	private int timeid;
	private int isShare = 0;
	private int bikoShare = 0;
	private int creatorid;



	// アクティビティの開始時にボタンを登録する
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetableedit);

		init();

		// 登録用ボタンのインスタンス取得
		addBtn = (Button) findViewById(R.id.edit);
		addBtn.setOnClickListener(this);
		// キャンセル用ボタンのインスタンス取得
		cancelBtn = (Button) findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(this);
		// todoのインスタンス取得
		remarksEdt = (EditText)findViewById(R.id.remarks);
		// 予定の種類のインスタンス取得
		typeSpr = (Spinner) findViewById(R.id.type);
		// シェアボタンのインスタンス取得
		shareCb = (CheckBox) findViewById(R.id.share); //時間割のシェア
		bikoShareCb = (CheckBox) findViewById(R.id.remarkShare); //備考情報のシェア
		// 種類追加用ボタンのインスタンスを取得
		addVarietyButton = (Button) findViewById(R.id.addVarietyButton);
		addVarietyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(TimeTableEditActivity.this,
						AddVarietyActivity.class);
				int requestCode = 100;
				startActivityForResult(intent, requestCode);
			}
		});

		//
		//		sqlHelper = new TimeTableSqlHelper(this);
		//		Log.d(TAG, "dummyDataInsertを実行");
		//		sqlHelper.dummyDataInsert();
	}

	// フォアグラウンドになった際に処理が実行
	public void onResume() {
		super.onResume();


		/**
		 * 曜日spinnerの初期設定
		 * */
		Log.d(TAG,"曜日spinner");
		ArrayAdapter<String> weekAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, TimeTableInfo.dayOfWeeks);
		weekSpr.setAdapter(weekAdapter);

		/**
		 * 時間割spinnerの初期設定
		 * */
		Log.d(TAG,"時間割spinner");
		ArrayAdapter<String> timeTableAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, TimeTableInfo.timeTables);
		timeTableSpr.setAdapter(timeTableAdapter);

		/**
		 * 予定の種類
		 */

		// ArrayAdapter<String> typeTableAdapter = new
		// ArrayAdapter<String>(this,
		// android.R.layout.simple_spinner_item,TimeTableInfo.types);
		// typeSpr.setAdapter(typeTableAdapter);

		//dbHelper = new MyDbHelper(this);
		//mDb = dbHelper.getWritableDatabase();
		mDb = sqlHelper.getReadableDatabase();
		ArrayAdapter<String> typeTableAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		//		typeTableAdapter.add("学科");
		//		typeTableAdapter.add("実技");
		//		typeTableAdapter.add("プライベート");
		//		String sql = "select variety from " + MyDbHelper.TABLE + " ;";
		String sql = "SELECT type FROM " + TimeTableSqlHelper.TYPE_TABLE + ";";
		Cursor cursor = mDb.rawQuery(sql, null);
		// カーソルをDBの最初の行へ移動
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			typeTableAdapter.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		mDb.close();
		typeSpr.setAdapter(typeTableAdapter);
		Intent intent = getIntent();
		String id = intent
				.getStringExtra("jp.sample.time_table.TimeTableIdString");// 値がなければNull

		weekSpr.setSelection(intent.getIntExtra("weekDay", 0));
		timeTableSpr.setSelection(intent.getIntExtra("num", 3));
		intent.getBooleanExtra("Editmode",EditMode);   //編集モードがON

		// 強制終了用処理
		if (TimeTableActivity.endFlag == true) {
			finish();
		}
	}

	public void onClick(View v) {
		if(v == cancelBtn){
			Log.d(TAG,"キャンセルボタンが押されました");
			finish();
		}else {

			Log.d(TAG, "TimeTableEditActivity onClick");

			TimeTableInfo info = new TimeTableInfo();
			info.setTitle(subjectEdt.getText().toString());
			info.setDayOfWeek(weekSpr.getSelectedItem().toString());
			info.setTimeTable(timeTableSpr.getSelectedItem().toString());
			info.setTodo(remarksEdt.getText().toString());
			info.setType(typeSpr.getSelectedItem().toString());
			info.setPlace(placeEdt.getText().toString());
			info.setIsShare(shareCb.isChecked());
			info.setBikoShare(bikoShareCb.isChecked());
			Log.d(TAG,"インスタンス、セッター");

			// 現在のUnixタイム取得
			long currentTimeMillis = System.currentTimeMillis();
			// 数値から文字列に変更
			String timestamp = String.valueOf(currentTimeMillis);
			Log.d(TAG,"タイム取得");

			ContentValues values = new ContentValues();

			//		-----------------------------------------------------------------------
			//
			//		チェック項目を取得&インデックスを取得
			//
			//		-----------------------------------------------------------------------
			if (shareCb.isChecked()) {
				isShare = 1; // 時間割シェアのチェック判定
			}
			if (bikoShareCb.isChecked()) {
				bikoShare = 1; // 備考シェアのチェック判定
			}

			timeid = timeTableSpr.getSelectedItemPosition()+1; //時限IDのインデックス
			week = weekSpr.getSelectedItemPosition(); // 曜日のインデックス
			typeid = typeSpr.getSelectedItemPosition() +1; // 種類のインデックス
			Log.d(TAG,"チェック項目＆＆インデックス取得");
			Log.d(TAG,"曜日番号を取得 ="+week+" 種類番号="+ typeid +"時限ID="+ timeid);
			//		-----------------------------------------------------------------------
			//
			//		授業科目IDを取得
			//
			//		-----------------------------------------------------------------------
			// 入力された情報を元に、関連のあるデータを検索　無ければ格納する
			db = sqlHelper.getReadableDatabase();
			String sql = "SELECT subjectid FROM subject WHERE subject_name = '"
					+ info.getTitle() + "' AND place = '" + info.getPlace() + "';";
			Log.d(TAG,"start query ="+sql);
			Cursor c = db.rawQuery(sql, null);
			Log.d(TAG,"start if");
			if (c.getCount() == 0) {
				// 検索結果ゼロ
				Log.d(TAG, "検索結果ゼロ");
				table = "subject";
				ContentValues ct = new ContentValues();
				ct.put("subject_name", info.getTitle());
				ct.put("place", info.getPlace());
				subjectid = (int)sqlHelper.insert(table, ct);
				Log.d(TAG, "Subject追加完了");
			}else{
				c.moveToFirst();
				subjectid = c.getInt(0);
				Log.d(TAG,"既に登録されている科目データです。　index:"+subjectid);
			}
			c.close();
			db.close();

			Log.d(TAG,"授業科目名取得");
			//		-----------------------------------------------------------------------
			//
			//		ユーザーIDを取得
			//
			//		-----------------------------------------------------------------------
			//自分のUIDを検索
			//自分のUIDは必ず１になるので、1を指定する
			creatorid = 1;
			db = sqlHelper.getReadableDatabase();
			sql = "SELECT userid FROM creator WHERE creatorid =" + creatorid +";";
			c = db.rawQuery(sql,null);
			if(c.getCount() == 0){
				Toast.makeText(this, "データの保存に失敗しました。", Toast.LENGTH_LONG).show();
				finish();
			}else{
				c.moveToFirst();
				info.setUid(c.getString(0));
			}

			Log.d(TAG,"CreatorId取得");
			//		-----------------------------------------------------------------------
			//
			//		備考情報の取得＆登録
			//
			//		-----------------------------------------------------------------------
			if(info.getTodo() != null){
				table = "remarks";

				ContentValues ct = new ContentValues();
				ct.put("date", timestamp);// ※ここをトップ画面から受け取った登録したい日時の情報にする
				//========================================================================================================
				//======================↑要注意箇所↑（現時点だと、下と同じtimestampが格納されるよこれ)==============================
				//========================================================================================================
				ct.put("timeid", timeid);
				ct.put("remarks", info.getTodo());
				ct.put("share", bikoShare);
				ct.put("creatorid", creatorid);
				ct.put("upremarks", timestamp);

				db = sqlHelper.getReadableDatabase();
				// timestampは、後でトップ画面から受け取った登録したい日時の情報に変更する
				sql = "SELECT * FROM remarks WHERE date=" + timestamp + " and timeid=" + timeid + ";";
				// このSQLで、～日の～時限目の備考情報があるかどうかをチェック
				c = db.rawQuery(sql, null);
				Log.v(TAG, String.format("c=%d件", c.getCount()));
				if (c.getCount() == 0) {
					// 検索結果ゼロの場合は新規登録
					Log.d(TAG, "remarks insert");
					sqlHelper.insert(table, ct);
				} else {
					// 検索結果がゼロでなかった場合は更新処理
					// 脆弱性あり。実行確認優先
					Log.d(TAG, "remarks update");
					sqlHelper.update(table, ct, "date=" + timestamp + " and timeid=" + info.getTimeTable(), null);
				}
				db.close();
			}
			Log.d(TAG,"備考取得");
			//		-----------------------------------------------------------------------
			//
			//		===================timeデータベースにデータを保存===========================
			//
			//		-----------------------------------------------------------------------

			//time  |曜日	|時限ID	|授業科目ID	|種類ID	|シェア（時間割)	|作成者ID		|更新日	|
			values.put("week", week);
			values.put("timeid", timeid);
			values.put("subjectid", subjectid);
			values.put("typeid", typeid);
			values.put("share", isShare);
			values.put("creatorid", creatorid);
			values.put("uptime", timestamp);

			//すでに登録されていないか検索
			sql ="SELECT week,timeid FROM time WHERE week = " + week + " AND timeid =" + timeid +";";
			db = sqlHelper.getReadableDatabase();
			c = db.rawQuery(sql,null);
			try {
				if (EditMode) {
					Log.d(TAG,"data update");
					sqlHelper.update("time", values,"week = ? AND timeid = ?",null);
					Toast.makeText(this,"予定を更新しました",Toast.LENGTH_LONG).show();
				} else {
					Log.d(TAG, "data insert");
					sqlHelper.insert("time", values);
					Toast.makeText(this,"予定を登録しました",Toast.LENGTH_LONG).show();
				}
			} catch (SQLiteException e) {
				e.printStackTrace();
				Toast.makeText(this, "データの保存に失敗しました。", Toast.LENGTH_LONG).show();
				return;
			}

			//		-----------------------------------------------------------------------
			//
			//		データを送信する
			//
			//		-----------------------------------------------------------------------
			if (shareCb.isChecked()) {// シェアする歳の処理
				Log.d(TAG, "データを送信する");
				SnsSender sender = new SnsSender();
				try {
					sender.send(info);
				} catch (SendException e) {
					e.printStackTrace();
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}

			setResult(RESULT_OK);
			finish();
			sqlHelper.close();
			db.close();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自動生成されたメソッド・スタブ
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 100:
			// Log.d("debug", "onActResult");
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras(); // Extraがないときにこれするとエラー出る（落ちる）みたいなので注意
				String value = bundle.getString("varietyWord");
				Log.d(TAG, "adding value is " + value);
				MyDbHelper.insert(db, value);
			} else if (resultCode == RESULT_CANCELED) {
				// Log.d("debug", "adding canceled");
			}
			// Log.d("debug", "exit onActResult");
			break;

		default:
			break;
		}

	}

	// バック長押し用強制終了処理

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			TimeTableActivity.endFlag = true;
			finish();
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	private void init() {
		/** 授業名入力欄 */
		subjectEdt = (EditText)findViewById(R.id.subject);

		/** 曜日選択 */
		weekSpr = (Spinner)findViewById(R.id.week);

		/** 時限選択 */
		timeTableSpr = (Spinner)findViewById(R.id.time_table);
		//timeTableSpr = (Spinner) findViewById(R.id.time);

		/** 場所入力欄 */
		placeEdt = (EditText)findViewById(R.id.place);

	}
}
