package us.wmwm.citibike.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;

import us.wmwm.citibike.api.Station;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class StationUtil {

	public static List<Station> getStations() {
		String stations = Streams.readFully(Streams
				.getStream("stations.json"));
		try {
			JSONObject obj = new JSONObject(stations);
			final List<Station> parsedStations = Station
					.parseStations(obj);
			return parsedStations;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Comparator<LatLng> newStationComparator(final LatLng myLocation) {
		return new Comparator<LatLng>() {
			
			float[] a = new float[1];
			float[] b = new float[1];
			
			@Override
			public int compare(LatLng lhs, LatLng rhs) {				
				Location.distanceBetween(lhs.latitude,
						lhs.longitude, myLocation.latitude,
						myLocation.longitude, a);
				Location.distanceBetween(rhs.latitude,
						rhs.longitude, myLocation.latitude,
						myLocation.longitude, b);
				return Float.valueOf(a[0]).compareTo(Float.valueOf(b[0]));
			}
		};
	}
	
	public static void sort(List<Station> stations, Location location) {
		if(location==null) {
			return;
		}
		final LatLng ll =new LatLng(location.getLatitude(),location.getLongitude());
		final Station me = new Station() {
			public LatLng getLatLng() {
				return ll;
			};
		};
		Comparator<Station> sort = new Comparator<Station>() {
			float[] a = new float[1];
			float[] b = new float[1];

			@Override
			public int compare(Station lhs, Station rhs) {
				Location.distanceBetween(lhs.getLatLng().latitude,
						lhs.getLatLng().longitude, me.getLatLng().latitude,
						me.getLatLng().longitude, a);
				Location.distanceBetween(rhs.getLatLng().latitude,
						rhs.getLatLng().longitude, me.getLatLng().latitude,
						me.getLatLng().longitude, b);
				return Float.valueOf(a[0]).compareTo(b[0]);
			}
		};
		Collections.sort(stations, sort);
	}
	
}
