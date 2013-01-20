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

	/** デバッグ用タグ*/
	private static final String TAG = "TimeTableActivity";

	/** EditText 授業名 */
	private EditText titleEdt;
	/** EditText 備考 */
	private EditText todoEdt;
	/** Spinner 曜日 */
	private Spinner weekSpr;
	/** Spinner 時間割*/
	private Spinner timeTableSpr;
	/** Spinner 予定の種類 */
	private Spinner typeSpr;
	/** Button 登録用ボタン*/
	private Button addBtn;
	/** Button キャンセル用ボタン*/
	private Button cancelBtn;
	/** Button 種類追加用ボタン*/
	private Button addVarietyButton;
	/** int timeTableId */
	private int timeTableId;
	/** シェア */
	private CheckBox shareCb; //時間割のシェア
	private CheckBox bikoShare;
	/** 場所  */
	private EditText placeEdt;
	/** ユニークID */
	private static String UID = "test user";
	private static String androidid = android.provider.Settings.Secure.ANDROID_ID;

	private MyDbHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor c;
	private String table;
	private	int index;

	//アクティビティの開始時にボタンを登録する
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetableedit);

		//授業名のインスタンス取得
		titleEdt = (EditText) findViewById(R.id.title);
		//曜日のインスタンス取得
		weekSpr = (Spinner) findViewById(R.id.week);
		//時間割のインスタンス取得
		timeTableSpr = (Spinner) findViewById(R.id.time_table);
		//場所のインスタンス取得
		placeEdt = (EditText) findViewById(R.id.place);
		//登録用ボタンのインスタンス取得
		addBtn = (Button) findViewById(R.id.edit);
		addBtn.setOnClickListener(this);
		//キャンセル用ボタンのインスタンス取得
		cancelBtn = (Button) findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(this);
		//todoのインスタンス取得
		todoEdt = (EditText) findViewById(R.id.todo);
		//予定の種類のインスタンス取得
		typeSpr = (Spinner) findViewById(R.id.type);
		//シェアボタンのインスタンス取得
		shareCb = (CheckBox) findViewById(R.id.share); //時間割のシェア
		bikoShare = (CheckBox) findViewById(R.id.bikoShare); //備考情報のシェア
		//種類追加用ボタンのインスタンスを取得
		addVarietyButton = (Button)findViewById(R.id.addVarietyButton);
		addVarietyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(TimeTableEditActivity.this, AddVarietyActivity.class);
				int requestCode = 100;
				startActivityForResult(intent, requestCode);
			}
		});

		dbHelper = new MyDbHelper(this);
		db = dbHelper.getWritableDatabase();
	}

	//フォアグラウンドになった際に処理が実行
	public void onResume(){
		super.onResume();

		/**
		 *  曜日spinnerの初期設定
		 * */
		ArrayAdapter<String> weekAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,TimeTableInfo.dayOfWeeks);
		weekSpr.setAdapter(weekAdapter);

		/**
		 * 時間割spinnerの初期設定
		 * */
		ArrayAdapter<String> timeTableAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,TimeTableInfo.timeTables);
		timeTableSpr.setAdapter(timeTableAdapter);

		/**
		 * 予定の種類
		 */
		//		ArrayAdapter<String> typeTableAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,TimeTableInfo.types);
		//		typeSpr.setAdapter(typeTableAdapter);

		ArrayAdapter<String> typeTableAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		typeTableAdapter.add("学科");
		typeTableAdapter.add("実技");
		typeTableAdapter.add("プライベート");
		String sql = "select variety from " + MyDbHelper.TABLE;
		Cursor cursor = db.rawQuery(sql, null);
		//カーソルをDBの最初の行へ移動
		cursor.moveToFirst();
		for(int i=0; i<cursor.getCount(); i++){
			typeTableAdapter.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		typeSpr.setAdapter(typeTableAdapter);

		Intent intent = getIntent();
		String id = intent.getStringExtra("jp.sample.time_table.TimeTableIdString");//値がなければNull

		weekSpr.setSelection(intent.getIntExtra("weekDay", 0));
		timeTableSpr.setSelection(intent.getIntExtra("num", 3));


		//idが存在する場合は更新処理
		if(id != null){
			timeTableId = Integer.parseInt(id);//更新データのためIdを保持

			//データベースからデータを取得する
			TimeTableSqlHelper h = new TimeTableSqlHelper(this);
			SQLiteDatabase db = h.getReadableDatabase();

			//titleEdt.setText(info.getTitle());

			/*dayOfWeeks
			    		for(int i=0; i < TimeTableInfo.dayOfWeeks.length; i++){
			    			if(info.getDayOfWeek().equals(TimeTableInfo.dayOfWeeks[i])) {
			   	    		weekSpr.setSelection(i);
			    				break;
			    			}
			   		}
			*/

			/*timeTables
			    		for(int i=0; i < TimeTableInfo.timeTables.length; i++){
			    			if(info.getTimeTable().equals(TimeTableInfo.timeTables[i])) {
			    				timeTableSpr.setSelection(i);
			    				break;
			    			}
			    		}
			*/
			//Todo
			//todoEdt.setText(info.getTodo());

			//type
			//for(int i=0; i < TimeTableInfo.types.length; i++){
			//	if(info.getType().equals(TimeTableInfo.types[i])) {
			//		typeSpr.setSelection(i);
			//	}
			//}

			//shareCb.setChecked(info.getIsShare());
			h.close();
		}

		//強制終了用処理
		if(TimeTableActivity.endFlag == true){
			finish();
		}
	}


	public void onClick(View v) {
		TimeTableInfo info = new TimeTableInfo();
		info.setTitle(titleEdt.getText().toString());
		info.setDayOfWeek(weekSpr.getSelectedItem().toString());
		info.setTimeTable(timeTableSpr.getSelectedItem().toString());
		info.setTodo(todoEdt.getText().toString());
		info.setType(typeSpr.getSelectedItem().toString());
		info.setPlace(placeEdt.getText().toString());
		info.setIsShare(shareCb.isChecked());
		info.setBikoShare(bikoShare.isChecked());
		info.setUid(UID);

		TimeTableSqlHelper h = new TimeTableSqlHelper(this);
		SQLiteDatabase db = h.getReadableDatabase();
		ContentValues values = new ContentValues();

		//入力された情報を元に、関連のあるデータを検索　無ければ格納する
		String sql = "SELECT * FROM subject WHERE subject_name = '" + info.getTitle() +
												"' AND place = '" + info.getPlace() + "';";

		c = db.rawQuery(sql, null);
		if(c.getCount() == 0){
			//検索結果ゼロ
			table = "subject";
			ContentValues ct = new ContentValues();
			ct.put("subject_name",info.getTitle());
			ct.put("place", info.getPlace());
			h.insert(table, ct);
			Log.d("logloglog","追加完了");
		}

		///_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
		//					要確認箇所				//
		///_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
		sql = "SELECT * FROM creator WHERE androidid = '"+ androidid + "';";
		c = db.rawQuery(sql,null);
		if(c.getCount() == 0){
			//検索結果ゼロ
			table = "creator";
			ContentValues ct = new ContentValues();
			ct.put("androidid", androidid);
			h.insert(table, ct);
		}
		///_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
		//					ここに備考検索入れる　	//
		///_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/

		//データベースにデータを保存
		index = weekSpr.getSelectedItemPosition();
		Log.d("spinner",index+":weekSpr");
		try {
			if(timeTableId != 0){
				h.update(null, null, null, null);
			}else{
				h.insert(null, null);
			}
		}catch(SQLiteException e){
			e.printStackTrace();
			Toast.makeText(this, "データの保存に失敗しました。", Toast.LENGTH_LONG);
			return;
		} finally {
			h.close();
		}

		//データを送信する
		if(shareCb.isChecked()){//シェアする歳の処理
			Log.d(TAG, "データを送信する");
			SnsSender sender = new SnsSender();
			try {
				sender.send(info);
			} catch (SendException e) {
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
			}
		}


		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自動生成されたメソッド・スタブ
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 100:
			//			Log.d("debug", "onActResult");
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();	//Extraがないときにこれするとエラー出る（落ちる）みたいなので注意
				String value = bundle.getString("varietyWord");
				Log.d("debug", "adding value is " + value);
				MyDbHelper.insert(db, value);
			}else if(resultCode == RESULT_CANCELED){
				//				Log.d("debug", "adding canceled");
			}
			//			Log.d("debug", "exit onActResult");
			break;

		default:
			break;
		}
	}

	//バック長押し用強制終了処理

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
}
