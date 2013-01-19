package jp.sample.time_table;

import jp.sample.db_helper.TimeTableSqlHelper;
import jp.sample.time_table_info.TimeTableInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TimeTableDetaileActivity extends Activity implements OnClickListener {
	private final String TAG = "TimeTableDetaileActivity";//デバッグ用TAG
	private static String UID = "test user";

	//Button 編集ボタン
	private Button editBtn;

	//Button 削除ボタン
	private Button delBtn;

	//TextView
	private TextView timeTableTv;

	//TextView
	private TextView titleTv;

	//TimeTable
	private String ttiId;

	//予定の種類
	private TextView typeTv;

	//Todo
	private TextView todoTv;

	//owner
	private TextView ownTv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetabledetaile);

        timeTableTv = (TextView) findViewById(R.id.timeTable);
        titleTv = (TextView) findViewById(R.id.title);

        editBtn = (Button) findViewById(R.id.editBtn);

        //編集ボタンにコールバックリスナーに追加
        editBtn.setOnClickListener(this);

        delBtn = (Button) findViewById(R.id.delBtn);

        //削除ボタンにコールバックリスナーに追加
        delBtn.setOnClickListener(this);

        //予定の種類
        typeTv = (TextView) findViewById(R.id.type);

        //Todo
        todoTv = (TextView) findViewById(R.id.todo);

        //所有者
        ownTv = (TextView) findViewById(R.id.owner);

    }

    //フォアグラウンドのタイミングでタイムテーブルの表示
    protected void onResume(){
    	super.onResume();

    	//詳細を表示するタイムテーブルのIdを取得
    	Intent intent = getIntent();

    	ttiId = intent.getStringExtra("jp.sample.time_table.TimeTableIdString");

        //データベースからデータを取得する
		TimeTableSqlHelper h = new TimeTableSqlHelper(this);
		TimeTableInfo info = h.fetchOne(ttiId);

		timeTableTv.setText(info.getDayOfWeek() + "["+ info.getTimeTable() +"]");
		titleTv.setText(info.getTitle());
		typeTv.setText(info.getType());
		todoTv.setText(info.getTodo());

		if(UID.equals(info.getUid())){
			ownTv.setText("自分");
		}else{
			ownTv.setText(info.getUid());
			editBtn.setEnabled(false);
		}
		h.close();
    }

	public void onClick(View v) {
		Log.d(TAG, "onClick");
		if( editBtn == v ){ // 編集ボタンクリックされた時の処理
			//アクティビティ(TimeTableDetaileActivity)を起動する設定
			Intent intent = new Intent(TimeTableDetaileActivity.this,TimeTableEditActivity.class);
			//取得したパラメータを起動するアクティビティに渡す設定
			intent.putExtra("jp.sample.time_table.TimeTableIdString", ttiId);

			//アクティビティ起動
			startActivity(intent);

		}else if( delBtn == v){ // 削除ボタンクリックされた時の処理
			TimeTableSqlHelper h = new TimeTableSqlHelper(this);
			h.delete(ttiId);
			h.close();

			setResult(RESULT_OK);
			finish();
		}
	}

}
