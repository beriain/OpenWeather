package pac;

import com.gregorajder.openweather.BuildConfig;

import android.util.Log;

public class MyLog {
	public static void d( String tag, String message) {
  if (BuildConfig.DEBUG) {
      Log.d(tag, message);
  }
} 
	
}
