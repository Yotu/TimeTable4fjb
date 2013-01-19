package jp.sample.time_table;

import jp.sample.time_table_info.*;
import java.util.ArrayList;
import java.util.List;

import jp.sample.db_helper.TimeTableSqlHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class TimeTableDayOfWeekActivity extends Activity implements OnItemClickListener  {
	private final int ITEM_HEIGHT = 96;
	private final String TAG = "TimeTableDayOfWeekActivity";//デバッグ用TAG
	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT; //必要最低限のサイズで表示
	private final int FP = ViewGroup.LayoutParams.FILL_PARENT; //画面の最大サイズで表示


	private final int REQUES_TTIME_TABLE_LIST = 1;

	private List<ListView> itemLv;
	private String dayOfWeek;

	public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate");//デバッグ用コード

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        dayOfWeek = intent.getStringExtra("jp.sample.time_table.DayOfTheWeekString");
        itemLv = new ArrayList<ListView>();
        setContentView(R.layout.timetabledayofweek);
        createLayout();
    }

    private void createLayout() {
		ScrollView scrollVw = (ScrollView) findViewById(R.id.scrollView);
		scrollVw.removeAllViews();

		//レイアウト上に縦に配置 横に配置する場合はLinearLayout.HORIZONTAL
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		scrollVw.addView(linearLayout,createParam(FP,FP));

        //曜日を先頭に表示
		TextView dayOfweekTv = new TextView(this);
        dayOfweekTv.setText(dayOfWeek);
  	  	linearLayout.addView(dayOfweekTv,createParam(WC,WC));

    	//時間割の表示設定
  	  	for (String title : TimeTableInfo.timeTables) {
  	  		//○時間タイトルを設定
  	  		TextView titleTv = new TextView(this);
	    	titleTv.setText(title);
	    	titleTv.setBackgroundColor(Color.GRAY);//バックグラウンドカラー設定
	    	titleTv.setTextColor(Color.BLACK);
	    	titleTv.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
	    	titleTv.setTextSize(18.0f);
	    	linearLayout.addView(titleTv,createParam(FP,FP));

	    	//時間ごとの内容を表示するListViewを設定
	    	ListView listView = new ListView(this);
	    	listView.setOnItemClickListener(this);
	    	itemLv.add(listView);
	    	linearLayout.addView(listView,createParam(FP,WC));
  	  	}
    }
	private LinearLayout.LayoutParams createParam(int w, int h) {
		return new LinearLayout.LayoutParams(w, h);
	}


    protected void onResume(){
    	super.onResume();
    	readData();
    }

    private void readData(){
      TimeTableSqlHelper h = new TimeTableSqlHelper(this);

      //選択された曜日のみのカーソルを取得
      for(int i=0 ; i < TimeTableInfo.timeTables.length;i++){
    	  List<TimeTableInfo> list = h.dayOfWeekAndTimeRead(dayOfWeek,TimeTableInfo.timeTables[i]);
    	  List<String> titleList = new ArrayList<String>();
    	  List<Integer> idList = new ArrayList<Integer>();
    	  for (TimeTableInfo info : list) {
    		  titleList.add(info.getTitle());
    		  idList.add(info.getId());
    	  }

 		  if (list.size() == 0) {
 			 titleList.add("--");
 		  }


 		  //ListViewに値とIDが入ったリストを設定。
 	  	  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
  	  	         android.R.layout.simple_list_item_1,titleList);
 	  	  ListView view = itemLv.get(i);
 	  	  view.setAdapter(adapter);
 	  	  view.setTag(idList); //IDを設定

 	  	  //ListViewの高さを調整
 	  	  int height = (list.size() > 0 ?(ITEM_HEIGHT * list.size()):ITEM_HEIGHT);
 	  	  view.setLayoutParams(this.createParam(FP, height));
      }
      h.close();
    }


	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// スケジュールが登録されている場合は詳細画面
		@SuppressWarnings("unchecked")
		List<Integer> list = (List<Integer>) parent.getTag();

		if (list.size() == 0) {
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	        // アラートダイアログのメッセージを設定します
	        alertDialogBuilder.setMessage("スケジュールが設定されていません。");
	        alertDialogBuilder.setPositiveButton("OK", null);
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        // アラートダイアログを表示します
	        alertDialog.show();
		} else {
			int itemId = list.get(position);
			//アクティビティ(TimeTableDetaileActivity)を起動する設定
			Intent intent = new Intent(TimeTableDayOfWeekActivity.this,TimeTableDetaileActivity.class);
			intent.putExtra("jp.sample.time_table.TimeTableIdString", String.valueOf(itemId));
			//アクティビティ起動
			startActivityForResult(intent,REQUES_TTIME_TABLE_LIST);
		}
	}

}
