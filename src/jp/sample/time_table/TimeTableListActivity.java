package jp.sample.time_table;

import jp.sample.time_table_info.TimeTableInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TimeTableListActivity extends Activity implements OnItemClickListener {
	private final String TAG = "TimeTableListActivity";

	private final int REQUES_TTIME_TABLE_LIST=1;

	/** ListView 各曜日を表示*/
	private ListView weekLv;

	//アクティビティの開始時にviewを登録する
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate start");//デバッグ用コード
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetablelist);//contentViewを設定

        //layoutから
        weekLv = (ListView) findViewById(R.id.weekList);
    }

    //フォアグラウンドのタイミングで曜日を表示する
    protected void onResume(){
    	super.onResume();

    	//ListViewに登録するデータを作成
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1,TimeTableInfo.dayOfWeeks);

        //リストのデータをListViewに登録
        weekLv.setAdapter(adapter);
        //ListViewのitem(曜日)がクリックされた時に呼び出されるコールバックリスナーを登録
        weekLv.setOnItemClickListener(this);
    }

	//item(曜日)がクリックされたタイミングで処理実行
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ListView listView = (ListView)parent;
		//クリックされたitemを取得
		String item = (String)listView.getItemAtPosition(position);

		//アクティビティ(TimeTableDayOfWeekActivity)を起動する設定
		Intent intent = new Intent(TimeTableListActivity.this,TimeTableDayOfWeekActivity.class);
		//取得したパラメータを起動するアクティビティに渡す設定
		intent.putExtra("jp.sample.time_table.DayOfTheWeekString", item);

		//アクティビティ起動
		startActivityForResult(intent,REQUES_TTIME_TABLE_LIST);
	}
}
