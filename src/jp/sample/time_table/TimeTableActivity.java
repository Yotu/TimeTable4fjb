package jp.sample.time_table;

//／   ,/  ／  ／    _,..-''
//／ / ／／/   〃  ,r'',.-'" ／／       ／
//,.ｨ <´   〈,/〃／-／/-'"／  ／ ,／__,,..-／
//} }  ,〉  //" ,r'´ノ ,r'′／／-''"二´‐'''~＿＿_ノ
//,) Y  ﾉ  ／／ ,r''´‐''"    -='"―-､彡"r'"￣´
//,ｲ    ﾘ  ﾉ り ,=、、-ﾆ_~―  ~''-=二  ∠"´
//ヾ,ヽ  ､, , ﾉﾉ {.ﾄヾｰ-~ﾆ_-二  ―  二ニ＝=-‐''"
//ゝ   ミｨｲｨ彡`ﾕ.|｀＼ヽ丶､‐ __ヽ'''三二,,,＿
//〈  `ヾべ_{::::     'ｩ)   `8-､ ヽ‐ ､ヽ`''-_＝-        貴様、見ているなッ！！！！
//ゝ_ﾉ⌒ﾍ~"     _”＼      ヽ!＼ヽ､'''ー-"ﾆ.._
//〈 (._      |∠ｨ ,.ノ   /   __ﾊ_j!          ＼
//`‐''^    ,. -‐`ヾ__/ヽ.  >,､く              ＼
//r;>-＝<´ 〈  ﾉ_,.                   ヽ  ＼
//(l》L -―''''"~´                        ヽ   ｀ 、
//／                               `、     `、   lヽ
///                                    ヽ.      l   ｜|
//!                                      ｀、    !    !｜
//｀、                         、          |     }  Ｖ,/
//ヽ                         |        ／    /  〃
//＼                     ﾍ_,. -''"      / ,.ｲ′
//＼                    ヽ          l / /
//  ｌ                    |          |′,'

/*-----------------------ここでのおやくそく-----------------------*/
//																  //
//						weekDay  =  曜日						  //
//						monthDay = 日にち						  //
//																  //
//					weekDayの日曜日は7でなく0。					  //
//																  //
//				setCurrentDb()を実行すると各配列変数が			  //
//				現在曜日のDbの内容に上書きされる。				  //
//				各配列変数 = title, todo, type, time_table		  //
//																  //
//				例：0限目,1限目,3限目の予定が発見された場合		  //
//				    ・title[0] = 0限目のタイトル				  //
//				    ・title[1] = 1限目のタイトル				  //
//				    ・title[2] = 3限目のタイトル				  //
//			この形のままでは0限目、1限目、2限目、3限目...		  //
//			となっている形式では配列の添字とズレるので、		  //
//			createExpandList()では								  //
//			  nullJudg											  //
//			   = timeTrue[i].equals(time_table[itemsPointer]);	  //
//			として判断している。								  //
//			なおsetCurrentDb()では配列オーバー対策として		  //
//			１つ余分にスペースを作成し最後にnullを挿入している。  //
//																  //
//			clickedWeekDayは曜日ボタンを押したときに設定される	  //
//			clickedItemNuberは親項目を長押ししたときに設定される  //
//																  //
//----------------------------------------------------------------//
//										  						  //
//				某軍師の助言によりメソッドの仕様は				  //
//				極力控えています。（処理効率の問題）			  //
//				見にくいと思いますがごめんなさい。	  			  //
//				ただし控え方は私の独断と偏見で決めていますので、  //
//				軍師に罪はありません。							  //
//																  //
/*----------------------------------------------------------------*/

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.sample.db_helper.TimeTableSqlHelper;
import jp.sample.sns_sdk.ReceiveException;
import jp.sample.sns_sdk.SnsReceiver;
import jp.sample.time_table_info.TimeTableInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

//デバッグ用key

public class TimeTableActivity extends Activity implements OnClickListener,
OnGroupClickListener, OnItemLongClickListener,
android.content.DialogInterface.OnClickListener, OnTouchListener {
	/** デバッグ用タグ */
	private static final String TAG = "TimeTableActivity";
	private final static int REQUES_TTIME_TABLE_EDIT = 1;
	private final static int REQUES_TTIME_TABLE_LIST = 2;

	public Calendar calendar = Calendar.getInstance();
	public int year = calendar.get(Calendar.YEAR);
	public int month = calendar.get(Calendar.MONTH)+1;

	public int day = calendar.get(Calendar.DAY_OF_MONTH);
	public int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
	/*-----僕らの追加要素たち-----*/
	// 他の画面から戻ってきたときにフラグが正だと強制終了させる
	public static boolean endFlag;

	// 曜日ボタンと日付テキストと押した曜日番号
	private Button monButton, tueButton, wedButton, thuButton, friButton,
	satButton, sunButton,backBtn,nextBtn;
	private TextView monD, tueD, wedD, thuD, friD, satD, sunD;

	// 曜日ボタンを押したときに設定される現在曜日の数字
	private int clickedWeekDay;

	//マンスビューへのボタン  イメージビューで代用
	private ImageView MonthBtn;

	// リストと現在時間とインテント
	private ExpandableListView dayList;
	private TextView dayNowTextView;
	private Intent intent;

	// データ関連＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private TimeTableSqlHelper dbHelper = new TimeTableSqlHelper(this);
	private SQLiteDatabase db;
	private SharedPreferences getPreference;

	// ExpandList用
	private String currentWeekDay;
	private int limit;
	private static final String[] weekDayTrue = { "日曜日", "月曜日", "火曜日", "水曜日",
		"木曜日", "金曜日", "土曜日" };

	//定数では既存以外の項目と比較できないのでDBから読み込むようにする
	private static final String[] timeTrue = { "0時限目", "1時限目", "2時限目", "3時限目", "4時限目", "5時限目","６時限目","７時限目","放課後"};
	private static String[] weekDays = new String[7];
	private boolean mode;
	private boolean changeMode = false;


	// ExpandListに表示する用
	private String[] subject;
	private String[] todo;
	private String[] type;
	private String[] time_name;
	private String[] place;
	private String[] userid;

	// ExpandList長押し用ダイアログの項目リスト
	private final CharSequence[] adbList = { "編集", "削除" };
	// ダイアログを出した（長押しした）項目番目
	private int clickedItemNumber;

	// バックボタンを押したときの確認用(trueの状態で押すと終了)
	boolean backButtonFirstFlag = false;

	private int week;

	//プリファレンス
	private SharedPreferences preference;
	private Editor editor;
	String UID;

	//登録されていないユーザーで起動した場合に出る
	//ユーザー登録ダイアログ用の入力欄
	private EditText editView;

	//ダイアログをエンターキーで閉じた場合のダイアログの終了処理用
	private AlertDialog disD;

	private int creatorid;


	//入力欄用フィルター
	class MinuteFilter implements InputFilter{
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			// TODO 自動生成されたメソッド・スタブ
			String sStr = source.toString();
			if (sStr.matches("\n")) {
				return "";
			}else{
				return source;
			}
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		endFlag = false;
		clickedWeekDay = calendar.get(Calendar.WEEK_OF_MONTH)+1;
		//プリファレンスの準備
		preference = getSharedPreferences("Preference Name", MODE_PRIVATE);
		editor = preference.edit();

		if (preference.getBoolean("Launched", false)==false) {
			//初回起動時の処理
			Log.d(TAG,"初回起動時の処理");

			//ユーザー登録処理
			editView = new EditText(TimeTableActivity.this);
			editView.setLines(1);
			editView.setFilters(new InputFilter[]{
					new MinuteFilter()
			});
			editView.setOnKeyListener(new View.OnKeyListener() {

				@Override
				public boolean onKey(View arg0, int keyCode, KeyEvent event) {
					// TODO 自動生成されたメソッド・スタブ
					if (event.getAction() == KeyEvent.ACTION_DOWN
							&& keyCode == KeyEvent.KEYCODE_ENTER) {
						//入力した文字をトースト出力する
						Toast.makeText(TimeTableActivity.this,
								editView.getText().toString()+"で入力を受付ました！",
								Toast.LENGTH_LONG).show();
						UID =editView.getText().toString();
						Log.d(TAG,UID);
						init(UID);
						disD.dismiss();
					}
					return false;
				}
			});
			AlertDialog.Builder adb = new AlertDialog.Builder(TimeTableActivity.this)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle("ユーザーIDを入力してください")
			//setViewにてビューを設定します。
			.setView(editView)
			.setPositiveButton("登録", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					//入力した文字をトースト出力する
					Toast.makeText(TimeTableActivity.this,
							editView.getText().toString()+"で入力を受付ました！",
							Toast.LENGTH_LONG).show();
					UID = editView.getText().toString();
					Log.d(TAG,UID);
					init(UID);
				}
			});
			disD = adb.show();

			//プリファレンスの書き変え
			editor.putBoolean("Launched", true);
			editor.commit();
			creatorid =1;
		} else {
			//二回目以降の処理
			//表示するデータを予め自分のものに設定しておく
			creatorid = 1;

		}

		/*-------------------ここからあいやのターン-------------------*/
		/*↑トラップカード発動！！！  首無しキリンの雄叫び！！
		 * このカードは、あいやがコードを書き間違えた際、強制的に首無しキリンの手によってコードが改変される!*/


		// 曜日ボタン処理
		Log.d("test","曜日ボタン処理");
		monButton = (Button) findViewById(R.id.monButton);
		monD = (TextView) findViewById(R.id.monDay);
		monButton.setOnClickListener(this);
		tueButton = (Button) findViewById(R.id.tueButton);
		tueD = (TextView) findViewById(R.id.tueDay);
		tueButton.setOnClickListener(this);
		wedButton = (Button) findViewById(R.id.wedButton);
		wedD = (TextView) findViewById(R.id.wedDay);
		wedButton.setOnClickListener(this);
		thuButton = (Button) findViewById(R.id.thuButton);
		thuD = (TextView) findViewById(R.id.thuDay);
		thuButton.setOnClickListener(this);
		friButton = (Button) findViewById(R.id.friButton);
		friD = (TextView) findViewById(R.id.friDay);
		friButton.setOnClickListener(this);
		satButton = (Button) findViewById(R.id.satButton);
		satD = (TextView) findViewById(R.id.satDay);
		satButton.setOnClickListener(this);
		sunButton = (Button) findViewById(R.id.sunButton);
		sunD = (TextView) findViewById(R.id.sunDay);
		sunButton.setOnClickListener(this);
		monButton.setBackgroundColor(Color.WHITE);
		tueButton.setBackgroundColor(Color.WHITE);
		wedButton.setBackgroundColor(Color.WHITE);
		thuButton.setBackgroundColor(Color.WHITE);
		friButton.setBackgroundColor(Color.WHITE);
		satButton.setBackgroundColor(Color.WHITE);
		sunButton.setBackgroundColor(Color.WHITE);
		Log.d("test","曜日ボタン処理");
		// オプションボタン処理
		// optButton = (Button)findViewById(R.id.optButton);
		// optButton.setOnClickListener(this);

		//マンスビュー用意
		MonthBtn = (ImageView)findViewById(R.id.monthBtn);
		MonthBtn.setOnClickListener(this);

		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		nextBtn = (Button)findViewById(R.id.nextBtn);
		nextBtn.setOnClickListener(this);

		// ExpandList連携
		dayList = (ExpandableListView) findViewById(R.id.dayExpandList);
		dayList.setOnGroupClickListener(this);
		dayList.setOnItemLongClickListener(this);
		dayList.setScrollingCacheEnabled(false);
		initWeek();
		onClick(new View(this));

	}

	@Override
	public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		// TODO 自動生成されたメソッド・スタブ
		if (adbList[paramInt].equals("編集")) {
			Log.d(TAG, "EditModeButton");
			setCurrentDb();
			intent = new Intent(this, TimeTableEditActivity.class);
			int itemPointer = 0;
			String[] newTime_table = new String[timeTrue.length], newTitle = new String[timeTrue.length],
					newPlace = new String[timeTrue.length], newType = new String[timeTrue.length], newTodo = new String[timeTrue.length];
			for (int i = 0; i < timeTrue.length; i++) {
				if (timeTrue[i].equals(time_name[itemPointer])) {
					newTitle[i] = subject[itemPointer];
					newPlace[i] = place[itemPointer];
					newType[i] = type[itemPointer];
					newTodo[i] = todo[itemPointer];
					newTime_table[i] = time_name[itemPointer];
					itemPointer++;
				} else {
					newTitle[i] = null;
					newPlace[i] = null;
					newType[i] = null;
					newTodo[i] = null;
					newTime_table[i] = null;
				}
			}
			db = dbHelper.getReadableDatabase();
			int timeid = clickedItemNumber +1;
			Log.d(TAG,"start db +"+clickedItemNumber+" weekday = "+week);
			String sql ="SELECT id FROM time WHERE week ="+ week +" AND timeid = " + timeid +";";
			Cursor c = db.rawQuery(sql, null);
			if(c.getCount() ==0){
				Log.d(TAG,"エラー");
			}else{
				c.moveToFirst();
				String id = String.valueOf(c.getInt(0));
				Log.d(TAG,"selected id = "+ id);
				//ここ特有でないものも入っていることに注意
				Log.d("debug", newTime_table[clickedItemNumber]);
				intent.putExtra("editMode", true);
				intent.putExtra("weekDay", clickedWeekDay);
				intent.putExtra("num", clickedItemNumber);
				intent.putExtra("title", newTitle[clickedItemNumber]);
				intent.putExtra("place", newPlace[clickedItemNumber]);
				intent.putExtra("type", newType[clickedItemNumber]);
				intent.putExtra("todo", newTodo[clickedItemNumber]);
				intent.putExtra("time_table", newTime_table[clickedItemNumber]);
				intent.putExtra("date", calendar.get(Calendar.YEAR)
						+ "/" + (calendar.get(Calendar.MONTH)+1)
						+ "/" + calendar.get(Calendar.DATE));
				intent.putExtra("id", id);
				startActivity(intent);
			}
			db.close();
			c.close();
		} else if (adbList[paramInt].equals("削除")) {
			// 選択した場所の予定をDBから削除
			// ifExist文もつけたい
			Log.d(TAG, "DeleteRowButton");
			try{
				new AlertDialog.Builder(this)
				.setMessage("データを削除します。よろしいですか？")
				.setCancelable(false)
				.setPositiveButton("はい",new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int getId){
						db = dbHelper.getWritableDatabase();
						int timeid = clickedItemNumber +1;
						Log.d(TAG,"start db +"+clickedItemNumber+" weekday = "+week);
						String sql ="SELECT id FROM time WHERE week ="+ week +" AND timeid = " + timeid +";";
						Cursor c = db.rawQuery(sql, null);
						c.moveToFirst();
						String id =  String.valueOf(c.getInt(0));
						dbHelper.delete("time", "id = "+ id, null);
						Log.d(TAG,"データの削除完了");
						Toast.makeText(TimeTableActivity.this,
								"データを削除しました",
								Toast.LENGTH_LONG).show();
						setCurrentDb();
						createExpandList();
					}
				})
				.setNegativeButton("いいえ", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int id){
						dialog.cancel();
					}
				})
				.show();
			}catch(Exception e){
				Log.d(TAG, "データ削除に失敗しました");
			}
		} else {
			Log.d("debug", adbList[paramInt] + " Button is failed");
		}
	}

	//スワイプ動作
	private float lastTouchX;
	private float currentX;
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastTouchX = event.getX();
			break;

		case MotionEvent.ACTION_UP:
			currentX = event.getX();
			if(lastTouchX < currentX){
				mode = true;
				changeMode = true;
				initWeek();
			}else if(lastTouchX > currentX){
				mode = false;
				changeMode = true;
				initWeek();
			}
			break;

		default:
			break;
		}
		mode = false;
		changeMode = true;
		initWeek();
		return false;
	}

	// 親要素クリック時
	@Override
	public boolean onGroupClick(ExpandableListView arg0, View arg1, int number,
			long arg3) {
		// TODO 自動生成されたメソッド・スタブ
		boolean existFlag = false;
		// createExpandListメソッドで残った配列が上書きされていないことを
		// 利用して、setCurrentDb()をせずに総マッチングをかけている
		for (int i = 0; i < time_name.length; i++) {
			if (timeTrue[number].equals(time_name[i])) {
				existFlag = true;
				break;
			}
		}
		if (!existFlag) {
			// Log.d("debug", "matching is suceed");
			intent = new Intent(this, TimeTableEditActivity.class);
			intent.putExtra("editMode", false);
			intent.putExtra("weekDay", clickedWeekDay);
			intent.putExtra("num", number);
			intent.putExtra("date", calendar.get(Calendar.YEAR)+ "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.DATE));
			startActivity(intent);
		}
		return false;
	}

	// 親要素（多分子要素も）長押しクリック時
	@Override
	public boolean onItemLongClick(AdapterView<?> paramAdapterView,
			View paramView, int paramInt, long paramLong) {
		// TODO 自動生成されたメソッド・スタブ
		// Log.d("debug", "onItemLongClick");

		// 現在のリストの状況を再設定
		setCurrentDb();

		//リストの予定の有無を全判定
		int itemsPointer = 0;
		boolean[] checked = new boolean[timeTrue.length];
		for (int i = 0; i < timeTrue.length; i++) {
			if (timeTrue[i].equals(time_name[itemsPointer])) {
				checked[i] = true;
				itemsPointer++;
			} else {
				checked[i] = false;
			}
		}

		//長押しした項目に予定があるかないか判定、なければなにも行わない
		if (checked[paramInt]) {
			clickedItemNumber = paramInt;
			AlertDialog.Builder adbuilder = new AlertDialog.Builder(this);
			adbuilder.setTitle("Manu");
			adbuilder.setItems(adbList, this);
			// AlertDialog表示
			adbuilder.show();
		}
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		/*
		 * このメソッドに、マンスビューから戻ってきた時の日時を受け取る処理を記述する予定。
		 * 受け取った値でトップ画面の表示も変更する。
		 */
		Log.d(TAG, String.format("requestCode=%d, resultCode=%d", requestCode, resultCode));
		if (requestCode == REQUES_TTIME_TABLE_EDIT && resultCode == RESULT_OK) {
			// 同期終了時にアラート
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setPositiveButton("OK", null);
			// アラートダイアログのメッセージを設定します
			alertDialogBuilder.setMessage("スケジュールの登録が完了しました。");
			AlertDialog alerSyncFalseDialog = alertDialogBuilder.create();
			// アラートダイアログを表示します
			alerSyncFalseDialog.show();
		}

		Log.d(TAG, "onActivityResult");
	}

	// 通常ボタンクリック時
	public void onClick(View v) {

		// いったん全てのボタンを白に戻す
		// 関係ないボタンを押すと全てが白のままになってしまうが、現状では他は画面遷移するので問題ないと判断
		monButton.setBackgroundColor(Color.WHITE);
		tueButton.setBackgroundColor(Color.WHITE);
		wedButton.setBackgroundColor(Color.WHITE);
		thuButton.setBackgroundColor(Color.WHITE);
		friButton.setBackgroundColor(Color.WHITE);
		satButton.setBackgroundColor(Color.WHITE);
		sunButton.setBackgroundColor(Color.WHITE);

		// Log.d("debug", String.valueOf(v.getId()));
		switch (v.getId()) {
		case R.id.backBtn:
			mode = true;
			changeMode =true;
			initWeek();
			break;
		case R.id.nextBtn:
			mode = false;
			changeMode = true;
			initWeek();
			break;
		case R.id.monthBtn:
			intent = new Intent(this,MonthActivity.class);
			startActivityForResult(intent, REQUES_TTIME_TABLE_EDIT);
			break;

		case R.id.monButton:
			currentWeekDay = "月曜日";
			clickedWeekDay = 1;
			monButton.setBackgroundColor(Color.BLUE);
			setCurrentDb();
			createExpandList();
			break;

		case R.id.tueButton:
			currentWeekDay = "火曜日";
			clickedWeekDay = 2;
			tueButton.setBackgroundColor(Color.BLUE);
			setCurrentDb();
			createExpandList();
			break;

		case R.id.wedButton:
			currentWeekDay = "水曜日";
			clickedWeekDay = 3;
			wedButton.setBackgroundColor(Color.BLUE);
			setCurrentDb();
			createExpandList();
			break;

		case R.id.thuButton:
			currentWeekDay = "木曜日";
			clickedWeekDay = 4;
			thuButton.setBackgroundColor(Color.BLUE);
			setCurrentDb();
			createExpandList();
			break;

		case R.id.friButton:
			currentWeekDay = "金曜日";
			clickedWeekDay = 5;
			friButton.setBackgroundColor(Color.BLUE);
			setCurrentDb();
			createExpandList();
			break;

		case R.id.satButton:
			currentWeekDay = "土曜日";
			clickedWeekDay = 6;
			satButton.setBackgroundColor(Color.BLUE);
			setCurrentDb();
			createExpandList();
			break;

		case R.id.sunButton:
			currentWeekDay = "日曜日";
			clickedWeekDay = 0;
			sunButton.setBackgroundColor(Color.BLUE);
			setCurrentDb();
			createExpandList();
			break;

			// おそらく初期値が-1なので、どのボタンでもないとここに入る、
		case -1: // それを利用してnew
			// View(this)での強制イベントをここに分岐させている（本来は特別なIDを発行したほうがバグには強いかもしれない）
			switch (clickedWeekDay) {
			case 1:
				monButton.setBackgroundColor(Color.BLUE);
				setCurrentDb();
				createExpandList();
				break;

			case 2:
				tueButton.setBackgroundColor(Color.BLUE);
				setCurrentDb();
				createExpandList();
				break;

			case 3:
				wedButton.setBackgroundColor(Color.BLUE);
				setCurrentDb();
				createExpandList();
				break;

			case 4:
				thuButton.setBackgroundColor(Color.BLUE);
				setCurrentDb();
				createExpandList();
				break;

			case 5:
				friButton.setBackgroundColor(Color.BLUE);
				setCurrentDb();
				createExpandList();
				break;

			case 6:
				satButton.setBackgroundColor(Color.BLUE);
				setCurrentDb();
				createExpandList();
				break;

			case 0:
				sunButton.setBackgroundColor(Color.BLUE);
				setCurrentDb();
				createExpandList();
				break;

			default:

				break;
			}
			break;

		default:
			Log.d("error", "UnknownButton clicked = " + v.getId());
			break;
		}
		initWeek();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCraeteOptionMenu");

		menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "データ受信");
		menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, "ユーザーリスト");


		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST:
			// データを受信
			SnsReceiver receiver = new SnsReceiver();
			List<TimeTableInfo> list;
			try {
				receiver.setContext(getApplicationContext());
				list = receiver.receive(UID);
			} catch (ReceiveException e) {
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			/*
			 * 受信したデータを１レコードごとデータベースに追加 TimeTableSqlHelper h = new
			 * TimeTableSqlHelper(this); for (TimeTableInfo info : list) {
			 * h.insert(info); } h.close();
			 */
			Toast.makeText(this, "データを受信しました。", Toast.LENGTH_LONG).show();
			return true;

			//---------------------------------------//

		case Menu.FIRST+1:
			//ユーザーリストの実態を用意
			//ユーザーズ抽出
			db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT userid" +
				" FROM creator" +
				" ORDER BY creatorid" +
				";", null);
		cursor.moveToFirst();
		String[] users = new String[cursor.getCount()];
		for(int i=0; i<cursor.getCount(); i++){
			Log.d("debug", cursor.getString(0));
			users[i] = cursor.getString(0);
			cursor.moveToNext();
		}
		cursor.close();				//まず閉じる、話はそれからだ。
		db.close();					//過去の過ちを繰り返さない。

		//ユーザーリスト表示用ダイアログ設置
		new AlertDialog.Builder(this)
		.setTitle("表示したい予定のユーザーを選択してください")
		.setItems(users, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				creatorid = which+1;
				Log.d(TAG,"中のCreatorId;"+creatorid);
			}
		})
		.show();

		setCurrentDb();
		Log.d(TAG,"外のCreatorId;"+creatorid);
		return true;
		case Menu.FIRST+2:
			//オプション画面へ
			Intent intent  = new Intent(this,OptionActivity.class);
		startActivity(intent);
		return true;
		}
		return false;

	}

	/*-----ここから全てあいやのターン-----*/

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

		// 現在曜日を新規登録画面に送る変数に設定
		//clickedWeekDay = time.weekDay;

		// 他の画面でバックボタン長押しが発生して戻ってきた場合強制終了
		if (endFlag) {
			Toast.makeText(this, "アプリを強制終了します。", Toast.LENGTH_SHORT).show();
			finish();
		}

		backButtonFirstFlag = false;

		// xmlから設定したリミットを読み込む
		getPreference = getSharedPreferences("PrefsActivity", MODE_PRIVATE);
		limit = getPreference.getInt("listLimit", 7);

		// ここで現在日付の曜日をcurrentWeekDayに設定

		// （setCurrentDbに使うため）

		setCurrentDb();
		createExpandList();
	}

	private void setCurrentDb() {
		// TODO 自動生成されたメソッド・スタブ
		Log.d(TAG, "setCurrentDb");
		initWeek();
		// スタブ
		//				subject = new String[1];
		//				todo = new String[1];
		//				type = new String[1];
		//				place = new String[1];
		//				time_name = new String[2];
		//				subject[0] = "stab1";
		//				todo[0] = "stab2";
		//				type[0] = "stab3";
		//				place[0] = "stab4";
		//				time_name[0] = "stab5";

		// todo以外読み込み
		db = dbHelper.getWritableDatabase();
		week = clickedWeekDay;
		Log.d(TAG,"week = "+ week);
		String sql ="SELECT time_name, subject_name, type, place, userid" +
				" FROM time, time_name, subject, type, creator" +
				" WHERE time.timeid = time_name.timeid" +
				" AND time.subjectid = subject.subjectid" +
				" AND time.typeid = type.typeid" +
				" AND time.week = " + week +
				" AND time.creatorid = creator.creatorid"+
				" AND time.creatorid = " +creatorid +
				" ORDER BY time.timeid";
		Log.d(TAG,sql);
		Cursor cursor = db.rawQuery(sql, null);

		Log.d(TAG,"Result = " + cursor.getCount());
		//		Log.d("debug","SelectedResult = " + cursor.getCount());
		time_name = new String[cursor.getCount() + 1];
		subject = new String[cursor.getCount()];
		type = new String[cursor.getCount()];
		place = new String[cursor.getCount()];
		userid = new String[cursor.getCount()];

		cursor.moveToFirst();
		for(int i=0; i<cursor.getCount(); i++){
			time_name[i] = cursor.getString(0);
			subject[i] = cursor.getString(1);
			type[i] = cursor.getString(2);
			place[i] = cursor.getString(3);
			userid[i] = cursor.getString(4);
			//			Log.d("debug", "selected = \n time_name : " + time_name[i] +
			//					",\n subject : " + subject[i] +
			//					",\n type : " + type[i] +
			//					",\n place : " + place[i]
			//					);
			cursor.moveToNext();
		}
		cursor.close();

		// remarks(todo)読み込み
		cursor = db.rawQuery("SELECT remarks " +
				" FROM remarks" +
				" WHERE (SELECT strftime('%w', date) FROM remarks) = '1'" +
				";", null);
		todo = new String[cursor.getCount()];
		cursor.moveToFirst();

		Log.d("debug", "SelectResult of remarks = " + cursor.getCount());
		for(int i=0; i<cursor.getCount(); i++){
			todo[i] = cursor.getString(0);
			Log.d("debug", "select = " + todo[i]);
			cursor.moveToNext();
		}
		cursor.close();

		// createExpandList最初の分岐判定での配列オーバー対策、元からnullになっているのかもしれないが、書いておく
		time_name[time_name.length - 1] = null;
		db.close();

	}

	// weekは現在0("月曜日")を考慮した状態、後で追加する
	private void createExpandList() {
		// TODO 自動生成されたメソッド・スタブ

		String[] parentArray = { "0限目", "1限目", "2限目", "3限目", "4限目", "5限目",
				"6限目", "7限目" };
		// String[] childArray = new String[todo.length];

		List<Map<String, Object>> groupData = new ArrayList<Map<String, Object>>();
		List<List<Map<String, Object>>> childData = new ArrayList<List<Map<String, Object>>>();

		String item;
		Map<String, Object> group;
		int itemsPointer = 0;
		boolean nullJudg; // 現在の時限に情報があるかの判定用
		String[] childArray;
		for (int i = 0; i <= limit; i++) {
			// 何回か同じ判定をするので、まとめて行う
			// 判定内容はこの時限に予定があるかないか
			nullJudg = timeTrue[i].equals(time_name[itemsPointer]);
			//			Log.d("debug", "nullJudg = " + nullJudg);
			// 予定の入っている（行のある）時限のみ各項目を設定する
			if (nullJudg) {
				childArray = new String[5];
				childArray[0] = "授業名 : " + subject[itemsPointer];
				childArray[1] = "場所 : " + ( !place[itemsPointer].equals("") ? place[itemsPointer] : "登録なし" );
				childArray[2] = "種類 : " + type[itemsPointer];
				//				childArray[3] = "備考 : " + todo[itemsPointer]; // nullでも大丈夫だった
				childArray[3] = "備考 : " + ( !todo[itemsPointer].equals("") ? todo[itemsPointer] : "登録なし" );
				childArray[4] = "ユーザー :"+(userid[itemsPointer] != null ?  userid[itemsPointer] : "不明");
				// アイテムポインタは下でインクリメントするので、ここではしない
			} else {
				childArray = new String[0];
			}

			// 親リスト作成
			group = new HashMap<String, Object>();
			item = parentArray[i];

			// i限目 != titleの中のi限目のタイトルであることに注意
			if (nullJudg) {
				group.put("PTag", item + "  " + subject[itemsPointer]);
				for(int j=0; j<3; j++){
					Log.d("main", "i = " + i + " : " + place[i]);
				}
				group.put("appendInfo", ( !place[itemsPointer].equals("") ? "[場所]   " + place[itemsPointer] : "" ));
				itemsPointer++;
			} else {
				group.put("PTag", item + "   +");
			}
			groupData.add(group);

			// 子リスト作成
			List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
			Map<String, Object> childHash;
			for (String childItem : childArray) {
				childHash = new HashMap<String, Object>();
				childHash.put("PTag", item);
				childHash.put("CTag", childItem);
				childList.add(childHash);
			}
			childData.add(childList);
		}

		// 上で作ったものをひとつに合体
		SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
				this, groupData,
				R.layout.raw2, new String[] {
						"PTag", "appendInfo" }, new int[] { R.id.text1 ,
						R.id.text2 }, childData, R.layout.raw,
						new String[] { "CTag" }, new int[] { R.id.child_text });
		// セット
		dayList.setAdapter(adapter);
	}

	// バックボタン長押しで強制終了処理
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Toast.makeText(this, "アプリを強制終了します。", Toast.LENGTH_SHORT).show();
			TimeTableActivity.endFlag = true;
			finish();
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	public void init(String UID){
		Log.d(TAG,"データベース初期化");

		ContentValues values = new ContentValues();

		values.put("userid",UID);
		dbHelper.insert("creator",values);
		Log.d(TAG,"ユーザーID:"+UID+" is Inseerted");

		dbHelper.defaultTimeNameTable();
		dbHelper.defaultTypeTable();
		Log.d(TAG,"種類テーブルと時限テーブルに項目が追加されました。");
	}

	public void initWeek(){
		Log.d(TAG,"initWeek");
		dayNowTextView = (TextView) findViewById(R.id.dayNowText);


		//currentWeekDay = weekDayTrue[calendar.get(Calendar.DAY_OF_WEEK)];
		//clickedWeekDay = calendar.get(Calendar.DAY_OF_WEEK);
		day = calendar.get(Calendar.DATE);
		month = calendar.get(Calendar.MONTH);
		month++;
		year = calendar.get(Calendar.YEAR);
		//1,2,3,4,5,6,0
		//月火水木金土日


		calendar.add(Calendar.DATE, (clickedWeekDay-1));
		dayNowTextView.setText(
				calendar.get(Calendar.YEAR) + "年" +
						(calendar.get(Calendar.MONTH)+1) +"月"+
						(calendar.get(Calendar.DATE))+"日"
				);

		Log.d(TAG,"クリックされた日付"+
				calendar.get(Calendar.YEAR) + "年" +
				(calendar.get(Calendar.MONTH)+1) +"月"+
				(calendar.get(Calendar.DATE))+"日");


		//日付の取得したいならこの中でやってね
		calendar.add(Calendar.DATE, -(clickedWeekDay-1));


		//日付
		day = calendar.get(Calendar.DAY_OF_MONTH);

		Calendar.getInstance();
		//曜日(日曜:0,月曜:1,火曜:2,,,,土曜:6)
		dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);


		//今週の月曜日をセット
		calendar.add(Calendar.DATE, -(dayOfWeek-2));
		Log.d(TAG,"セットした月曜日の日付は"+calendar.get(Calendar.DATE));
		if(changeMode){
			if(mode){
				calendar.add(Calendar.DATE,-7);
			}else{
				calendar.add(Calendar.DATE,7);
			}
		}



		//月曜日を基準に、７日分の日付を格納していく

		for(int i=0;i<7;i++){

			weekDays[i] = ((calendar.get(Calendar.MONTH)+1) +"/" + calendar.get(Calendar.DATE));
			Log.d(TAG,"calendar.get(Calendar.DATE) = "+ calendar.get(Calendar.DATE));;
			calendar.add(Calendar.DATE, 1); //ここで１日増やす。途中で月をまたいでもCalendarクラスなので大丈夫

			Log.d("test",weekDays[i]);
		}
		monD.setText(weekDays[0]);
		tueD.setText(weekDays[1]);
		wedD.setText(weekDays[2]);
		thuD.setText(weekDays[3]);
		friD.setText(weekDays[4]);
		satD.setText(weekDays[5]);
		sunD.setText(weekDays[6]);
		calendar.add(Calendar.DATE, -7);


		changeMode = false;
		mode = false;
	}

}
