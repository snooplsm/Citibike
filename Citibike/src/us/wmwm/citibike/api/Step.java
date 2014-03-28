package us.wmwm.citibike.api;

import org.json.JSONObject;

import android.location.Location;
import android.location.LocationManager;
import android.util.Pair;

public class Step {

	public Step(JSONObject o) {
		JSONObject dist = o.optJSONObject("distance");
		distance = new Pair<String,Integer>(dist.optString("text"),dist.optInt("value"));
		JSONObject dur = o.optJSONObject("duration");
		duration = new Pair<String,Integer>(dur.optString("text"), dur.optInt("value"));
		startAddress = new Location(LocationManager.NETWORK_PROVIDER);
		JSONObject start = o.optJSONObject("start_address");
		startAddress.setLatitude(start.optDouble("lat"));
		startAddress.setLongitude(start.optDouble("lng"));
		JSONObject end = o.optJSONObject("end_address");
		endAddress = new Location(LocationManager.NETWORK_PROVIDER);
		endAddress.setLatitude(end.optDouble("lat"));
		endAddress.setLongitude(end.optDouble("lng"));
		polyline = o.optJSONObject("polyline").optString("points");
		travelMode = o.optString("travel_mode");
		htmlInstructions = o.optString("html_instructions");
	}
	Pair<String, Integer> distance;
	Pair<String, Integer> duration;
	
	Location endAddress;
	Location startAddress;
	
	String htmlInstructions;
	String travelMode;
	String manuver;
	String polyline;
}
