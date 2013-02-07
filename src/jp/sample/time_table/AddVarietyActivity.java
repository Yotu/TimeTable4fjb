package jp.sample.time_table;

import jp.sample.db_helper.TimeTableSqlHelper;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddVarietyActivity extends Activity implements OnClickListener {
	private EditText addText;
	private Button addButton, cancelButton, deleteButton;
	private Spinner existList;
	private ArrayAdapter<String> typeTableAdapter;
	private TimeTableSqlHelper dbHelper;
	private String nowList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.variety);

		addText = (EditText) findViewById(R.id.addVarietyEdit);
		addText.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO 自動生成されたメソッド・スタブ
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					addVariety();
				}
				return false;
			}
		});
		addButton = (Button) findViewById(R.id.addVarietyButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		addButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

		// 既存の項目を読み込み（デフォルト項目以外）
		existList = (Spinner) findViewById(R.id.existList);

		dbHelper = new TimeTableSqlHelper(this);
		SQLiteDatabase tdb = dbHelper.getWritableDatabase();

		typeTableAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);

		String sql = "SELECT type FROM " + TimeTableSqlHelper.TYPE_TABLE +
					" WHERE typeid > 3" +
					" order by typeid;";
		Cursor c = tdb.rawQuery(sql, null);
		c.moveToFirst();
		for(int i=0; i<c.getCount(); i++){
			typeTableAdapter.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
		tdb.close();

		existList.setAdapter(typeTableAdapter);
		existList.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterV, View arg1,
					int arg2, long arg3) {
				// TODO 自動生成されたメソッド・スタブ
				nowList = adapterV.getSelectedItem().toString();
				Log.d("debug", "nowList is " + nowList);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自動生成されたメソッド・スタブ
			}
		});

		deleteButton = (Button) findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		if (v.equals(addButton)) {
			addVariety();
		} else if (v.equals(cancelButton)) {
			setResult(RESULT_CANCELED);
			// Log.d("debug", "exitting addActivity");
		} else if (v.equals(deleteButton)) {
			// DELETE FROM tyoe_table where variety = 現在のスピナー
			if (nowList != null) {
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.execSQL("delete from " + TimeTableSqlHelper.TYPE_TABLE
						+ " where type = '" + nowList + "';");
				db.close();
			}
		}
		finish();
	}

	public void addVariety() {
		// ここでDBへ追加させる用のリザルトを設定
		// 元アクティビティに戻ったら、DBとアダプタに追加して反映させる
		// また、onCreateではDBから読み込ませる
		Intent data = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("varietyWord", addText.getText().toString());
		data.putExtras(bundle);

		setResult(RESULT_OK, data);
		finish();
	}

	// バック長押し用強制終了処理
	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		if (TimeTableActivity.endFlag == true) {
			finish();
		}
	}

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
