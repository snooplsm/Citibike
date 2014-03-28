package us.wmwm.citibike.api;

import org.json.JSONObject;

import android.location.Location;
import android.location.LocationManager;

public class Bounds {
	
	public Bounds(JSONObject o) {
		northeast = new Location(LocationManager.NETWORK_PROVIDER);
		JSONObject n = o.optJSONObject("northeast");
		JSONObject s = o.optJSONObject("southwest");
		northeast.setLatitude(n.optDouble("lat"));
		northeast.setLongitude(n.optDouble("lng"));
		southwest = new Location(LocationManager.NETWORK_PROVIDER);
		southwest.setLatitude(s.optDouble("lat"));
		southwest.setLongitude(s.optDouble("lng"));
	}

	Location northeast;
	Location southwest;
	
	public Location getNortheast() {
		return northeast;
	}
	
	public Location getSouthwest() {
		return southwest;
	}
	
}
