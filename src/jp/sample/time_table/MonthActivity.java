package jp.sample.time_table;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MonthActivity extends Activity implements OnClickListener,View.OnTouchListener{
	public Button[] button_table = new Button[43];
	public TextView todayLabel;
	//カレンダーのインスタンスを作成
	public Calendar calendar = Calendar.getInstance();
	//今日の年月日を取得する
	public int year = calendar.get(Calendar.YEAR);
	public int month = calendar.get(Calendar.MONTH);
	public int day = calendar.get(Calendar.DAY_OF_MONTH);
	public int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.month);
		//yyyy/mm  を表示するラベル
		todayLabel = (TextView)this.findViewById(R.id.monthLabel);
		//ボタンを一つずつ取得し、配列に格納する。
		button_table[1] = (Button)this.findViewById(R.id.dayB01);
		button_table[2] = (Button)this.findViewById(R.id.dayB02);
		button_table[3] = (Button)this.findViewById(R.id.dayB03);
		button_table[4] = (Button)this.findViewById(R.id.dayB04);
		button_table[5] = (Button)this.findViewById(R.id.dayB05);
		button_table[6] = (Button)this.findViewById(R.id.dayB06);
		button_table[7] = (Button)this.findViewById(R.id.dayB07);
		button_table[8] = (Button)this.findViewById(R.id.dayB08);
		button_table[9] = (Button)this.findViewById(R.id.dayB09);
		button_table[10] = (Button)this.findViewById(R.id.dayB10);
		button_table[11] = (Button)this.findViewById(R.id.dayB11);
		button_table[12] = (Button)this.findViewById(R.id.dayB12);
		button_table[13] = (Button)this.findViewById(R.id.dayB13);
		button_table[14] = (Button)this.findViewById(R.id.dayB14);
		button_table[15] = (Button)this.findViewById(R.id.dayB15);
		button_table[16] = (Button)this.findViewById(R.id.dayB16);
		button_table[17] = (Button)this.findViewById(R.id.dayB17);
		button_table[18] = (Button)this.findViewById(R.id.dayB18);
		button_table[19] = (Button)this.findViewById(R.id.dayB19);
		button_table[20] = (Button)this.findViewById(R.id.dayB20);
		button_table[21] = (Button)this.findViewById(R.id.dayB21);
		button_table[22] = (Button)this.findViewById(R.id.dayB22);
		button_table[23] = (Button)this.findViewById(R.id.dayB23);
		button_table[24] = (Button)this.findViewById(R.id.dayB24);
		button_table[25] = (Button)this.findViewById(R.id.dayB25);
		button_table[26] = (Button)this.findViewById(R.id.dayB26);
		button_table[27] = (Button)this.findViewById(R.id.dayB27);
		button_table[28] = (Button)this.findViewById(R.id.dayB28);
		button_table[29] = (Button)this.findViewById(R.id.dayB29);
		button_table[30] = (Button)this.findViewById(R.id.dayB30);
		button_table[31] = (Button)this.findViewById(R.id.dayB31);
		button_table[32] = (Button)this.findViewById(R.id.dayB32);
		button_table[33] = (Button)this.findViewById(R.id.dayB33);
		button_table[34] = (Button)this.findViewById(R.id.dayB34);
		button_table[35] = (Button)this.findViewById(R.id.dayB35);
		button_table[36] = (Button)this.findViewById(R.id.dayB36);
		button_table[37] = (Button)this.findViewById(R.id.dayB37);
		button_table[38] = (Button)this.findViewById(R.id.dayB38);
		button_table[39] = (Button)this.findViewById(R.id.dayB39);
		button_table[40] = (Button)this.findViewById(R.id.dayB40);
		button_table[41] = (Button)this.findViewById(R.id.dayB41);
		button_table[42] = (Button)this.findViewById(R.id.dayB42);

		//取得したボタンに対してタッチ判定を当てる。　詳細はonCrickメソッドにて。
		for(int i=1;i<=42;i++){
			//ボタンをクリックを受け付ける
			button_table[i].setOnClickListener(this);
			//ボタンのタッチ（フリック）を受け付ける
			button_table[i].setOnTouchListener(this);
			// ボタンに画像を設定する
			button_table[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.schedule_none));
		}
		drawText();


	}
	public void drawText(){
				//セットされたテキストのリセット
				for(int i=1;i<=42;i++){
					button_table[i].setText(null);
				}
				//上で取得した「今日の日付」を元に、カレンダーをセットする。
				calendar.set(year,month,1);
				//ラベルにセット。（ただし、monthは0~11までで「ズレ」ているため、+1して治す。意味がわからなきゃ+1外してみろ
				if(month <0){
					month=month+12;
					year--;
				}else if(month >11){
					month=month-12;
					year++;
				}
				todayLabel.setText(year+"/"+(month+1));
				//月の日
				day = calendar.get(Calendar.DAY_OF_MONTH);
				//曜日
				dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
				//取得した月に１を足す（来月の１日にセット）
				calendar.add(Calendar.MONTH, 1);
				//来月の頭から１日引く（=今月末）
				calendar.add(Calendar.DATE, -1);
				//今月末の日にちをlastDateにセット
				int lastDate = calendar.get(Calendar.DATE);
				//今月初日の曜日から最終日まで日付を格納していく
				for(int i=dayOfWeek; i <= dayOfWeek+lastDate-1;i++){
					button_table[i].setText(String.valueOf(day));
					button_table[i].setTextSize(15);
					day=day+1;
				}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.MonthActivity, menu);
		return true;
	}

	//最後にタッチされたX座標
	private float lastTouchX;
	private float currentX;
	//タッチされた時に行うメソッド
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastTouchX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			currentX = event.getX();
			if (lastTouchX < currentX) {
				//前に戻る動作
				Log.d("main","前に戻る");
				month--;
				drawText();
			}
			if (lastTouchX > currentX) {
				//次に移動する動作
				Log.d("main","次");
				month++;
				drawText();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			currentX = event.getX();
			if (lastTouchX < currentX) {
				//前に戻る動作
			}
			if (lastTouchX > currentX) {
				//次に移動する動作
			}
			break;
		}
		return false;
	}
	@Override
	public void onClick(View v) {
		calendar.set(year,month,1);
		day=calendar.get(Calendar.DAY_OF_MONTH);
		month=calendar.get(Calendar.MONTH);
		dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		int lastDate = calendar.get(Calendar.DATE);
		calendar.set(year,month,1);
		for(int i=dayOfWeek;i<=dayOfWeek+lastDate-1;i++){
			if(v==button_table[i]){
				Toast.makeText(this, (month+1)+"月の"+day+"日がタッチされました", Toast.LENGTH_SHORT).show();
			}
			day=day+1;
		}

	}



}
