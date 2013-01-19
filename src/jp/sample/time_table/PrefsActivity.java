package jp.sample.time_table;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class PrefsActivity extends Activity implements OnItemSelectedListener, View.OnClickListener{
	private Spinner intSpin;
	private Editor edit;
	private Button returnButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefs);

		intSpin = (Spinner)findViewById(R.id.spinner1);
		intSpin.setOnItemSelectedListener(this);

		returnButton = (Button)findViewById(R.id.returnButton);
		returnButton.setOnClickListener(this);

		String[] intArray = {"0","1","2","3","4","5","6","7"};
		ArrayAdapter<String> intAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, intArray);
		intSpin.setAdapter(intAdapter);

		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		edit = sp.edit();
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO 自動生成されたメソッド・スタブ
		Log.d("debug", arg0.getSelectedItem().toString());
		edit.putInt("listLimit", Integer.parseInt(arg0.getSelectedItem().toString()));
		edit.commit();
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

}