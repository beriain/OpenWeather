package com.gregorajder.openweather;

import pac.MyLog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LocationActivity extends Activity implements OnClickListener {
	String greg = "locationActivity";

	Activity locationActivity = this;
	private AutoCompleteTextView tvEnterSity;
	private EditText lat;
	private EditText lon;

	public void onCreate(Bundle savedInstanceState) {
		MyLog.d(greg, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

		tvEnterSity = (AutoCompleteTextView) findViewById(R.id.autoCompleteCity);
		String[] cities = getResources().getStringArray(R.array.city);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, cities);
		tvEnterSity.setAdapter(adapter);

		Button btnSearchByCity = (Button) findViewById(R.id.btnSearchByCity);
		btnSearchByCity.setOnClickListener(this);

		lat = (EditText) findViewById(R.id.editLat);
		lon = (EditText) findViewById(R.id.editLon);

		InputFilter filter = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				Toast toast = Toast.makeText(locationActivity,
						"����������� ���������� ���������",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0); 
				for (int i = start; i < end; i++) {
					if (!isEnglish(source.charAt(i))) {
						toast.show();
						return "";
					}
				}
				return null;
			}

			private boolean isEnglish(char charAt) {
				String validationString = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM, ";
				if (validationString.indexOf(charAt) == -1)
					return false;
				else
					return true;
			}
		};
		tvEnterSity.setFilters(new InputFilter[] { filter });

		Button btnSearchByCrd = (Button) findViewById(R.id.btnSearchByCrd);
		btnSearchByCrd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btnSearchByCity:
			String str = tvEnterSity.getText().toString();
			MyLog.d(greg, "read city " + str);
			intent.putExtra("city", str);
			setResult(1, intent);
			break;
		case R.id.btnSearchByCrd:
			String lat = this.lat.getText().toString();
			String lon = this.lon.getText().toString();
			MyLog.d(greg, "read lat " + lat + "lon " + lon);
			intent.putExtra("lat", lat);
			intent.putExtra("lon", lon);
			setResult(2, intent);
			break;
		}
		finish();
	}
}
