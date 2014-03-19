package us.wmwm.citibike;

import us.wmwm.citibike.util.LocationUtil;
import android.app.Application;

public class CitibikeApplication extends Application {

	private static CitibikeApplication INSTANCE;
	
	LocationUtil locationUtil;
	
	public void onCreate() {
		INSTANCE = this;
		locationUtil = new LocationUtil();
	};
	
	public LocationUtil getLocationUtil() {
		return locationUtil;
	}
	
	public static CitibikeApplication get() {
		return INSTANCE;
	}
	
}
