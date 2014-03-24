package us.wmwm.citibike.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class Station implements Serializable {	

	private static final long serialVersionUID = 1L;

	String id;
	String status;
	double latitude;
	double longitude;
	String label;
	String address;
	int availableBikes;
	int availableDocks;

	//by keeping doubles here might be prone to collisions.  hopefully not
	TreeMap<Double, Station> nearbyStations = new TreeMap<Double, Station>();
	Map<String, Double> nearbyStationIds = new HashMap<String, Double>();

	void addNearbyStation(double distance, Station station) {
		if(station==null) {
			return;
		}
		nearbyStations.put(distance, station);
	}

	public Station() {
	}

	public Station(JSONObject o) {
		id = o.optString("id");
		status = o.optString("status");
		latitude = o.optDouble("latitude");
		longitude = o.optDouble("longitude");
		label = o.optString("label");
		address = o.optString("stationAddress");
		availableBikes = o.optInt("availableBikes",Integer.MIN_VALUE);
		availableDocks = o.optInt("availableDocks",Integer.MIN_VALUE);
		JSONArray nearby = o.optJSONArray("nearbyStations");
		if (nearby != null) {
			for (int i = 0; i < nearby.length(); i++) {
				JSONObject n = nearby.optJSONObject(i);
				this.nearbyStationIds.put(n.optString("id"),
						n.optDouble("distance"));
			}
		}
	}

	/**
	 * This object is from updateOnly=true which only contains needed fields,
	 * merge in the address/longitude/latitude from the full station and update
	 * full stations availableBikes and availableDocks. Results in both objects
	 * being equal.
	 * 
	 * @param Station
	 *            fully represented
	 */
	public void merge(Station full) {
		if(full==null) {
			return;
		}
		latitude = full.latitude;
		longitude = full.longitude;
		address = full.address;
		full.availableBikes = availableBikes;
		full.availableDocks = availableDocks;
	}
	
	void updateNearbyStations(Map<String,Station> allStations) {
		for(Map.Entry<String, Double> e : nearbyStationIds.entrySet() ) {
			addNearbyStation(e.getValue(), allStations.get(e.getKey()));
		}
		
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getAvailableBikes() {
		return availableBikes;
	}
	
	public int getAvailableDocks() {
		return availableDocks;
	}
	
	public String getId() {
		return id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public Map<Double, Station> getNearbyStations() {
		return nearbyStations;
	}
	
	public String getStatus() {
		return status;
	}

	public static List<Station> parseStations(JSONObject o) {
		JSONArray results = o.optJSONArray("results");
		List<Station> stations = new ArrayList<Station>();
		for(int i = 0; i < results.length(); i++) {
			JSONObject obj = results.optJSONObject(i);
			stations.add(new Station(obj));
		}
		return stations;
	}
	
}
