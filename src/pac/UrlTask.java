package pac;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gregorajder.openweather.FirstActivity;

public class UrlTask extends AsyncTask<Void, Integer, Integer> {
	private String urlStrDay;
	private String urlStrForecast;
	private String title;
	private Data data;
	private FirstActivity firstActivity;
	private String greg = "UrlTask";
	private DayWeather nowWeather;
	private DayWeather[] forecastWeather;
	JSONObject joDay;
	JSONObject joForecast;

	public UrlTask(FirstActivity firstActivity, Data d, String str1,
			String str2, String title) {
		MyLog.d(greg, "constructor ");
		data = d;
		this.title = title;
		urlStrDay = str1; // day
		urlStrForecast = str2; // forecast
		this.firstActivity = firstActivity;

	}

	@Override
	protected void onPreExecute() {
		firstActivity.loadAnimationStart();
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(Void... arg0) {
		if (isCancelled())
			return null;

		try {// day
			MyLog.d(greg, urlStrDay);
			joDay = urlConnect2(urlStrDay);
			parsDay(joDay);
		} catch (JSONException e) {
			e.printStackTrace();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		} catch (ParseException e) {
			e.printStackTrace();
			return 3;
		}

		if (isCancelled())
			return null;

		// try {
		// Thread.sleep(10000); // openweather ask wait 10 seconds between url
		// // request
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// if (isCancelled()) return null;

		try {// forecast
			MyLog.d(greg, urlStrForecast);
			joForecast = urlConnect2(urlStrForecast);
			parsForcast(joForecast);
		} catch (JSONException e) {
			e.printStackTrace();
			return 1;

		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		} catch (ParseException e) {
			e.printStackTrace();
			return 3;
		}
		return 0;
	}

	@Override
	protected void onPostExecute(Integer result) {
		switch (result) {
		// if data was not received
		case 2:
			Toast.makeText(firstActivity, "�������� ����������� � ���������",
					Toast.LENGTH_LONG).show();
			break;
		case 1:
			Toast.makeText(firstActivity,
					"������ �� ����� �������� �� ������ ������",
					Toast.LENGTH_LONG).show();
			break;
		case 3:
			Toast.makeText(firstActivity,
					"error", 
					Toast.LENGTH_LONG).show();
			break;
			
		// if data has been successfully received
		case 0:
			// save query string and title to refresh data next time
			data.urlStrDay = urlStrDay;
			data.urlStrForecast = urlStrForecast;
			data.title = title;
			data.setForecast(forecastWeather);
			data.setNowWeather(nowWeather);

			Editor editor = firstActivity.mSettings.edit();
			editor.putString("title", title);
			editor.putString("urlStrDay", urlStrDay);
			editor.putString("urlStrForecast", urlStrForecast);
			editor.putString("joDay", String.valueOf(joDay));
			editor.putString("joForecast", String.valueOf(joForecast));
			editor.apply();

			// refresh visible activity
			if (firstActivity.visibleOnScreen)
				firstActivity.afterUrlTask();
			else
				firstActivity.showNewData = true;
			break;
		}
		firstActivity.loadAnimationStop();
	}



	JSONObject urlConnect2(String url) throws ParseException,
			ClientProtocolException, JSONException, IOException {
		JSONObject jo = new JSONObject(
				EntityUtils.toString(new DefaultHttpClient().execute(
						new HttpGet(url)).getEntity()));
		return jo;
	}

	void parsForcast(JSONObject weatherJson) throws JSONException {
		MyLog.d(greg, "try pars jsonForecast: " + weatherJson);
		if (weatherJson.has("cnt")) {
			MyLog.d(greg, "try pars forecast start");
			int d = weatherJson.getInt("cnt");
			DayWeather[] forecast = new DayWeather[d];
			JSONArray list = weatherJson.getJSONArray("list");
			for (int i = 0; i <= d - 1; i++) {
				JSONObject day = (JSONObject) list.get(i);
				MyLog.d("greg", "forecast for (i=" + i + ")");
				forecast[i] = new DayWeather();
				forecast[i].parsForecast(day, firstActivity);
			}
			forecastWeather = forecast;
			return;
		}
		throw new JSONException("server error");
	}

	void parsDay(JSONObject weatherJson) throws JSONException {
		MyLog.d(greg, "try pars jsonDay: " + weatherJson);
		if (weatherJson.has("main")) {
			MyLog.d(greg, "try pars nowweather start");
			DayWeather nowWeather = new DayWeather();
			nowWeather.parsDay(weatherJson, firstActivity);
			this.nowWeather = nowWeather;
			return;
		}
		throw new JSONException("server error");
	}

}
