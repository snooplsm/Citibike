package us.wmwm.citibike;

import android.app.Application;

public class CitibikeApplication extends Application {

	private static CitibikeApplication INSTANCE;
	
	public void onCreate() {
		INSTANCE = this;
	};
	
	public static CitibikeApplication get() {
		return INSTANCE;
	}
	
}
