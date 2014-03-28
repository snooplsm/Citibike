package us.wmwm.citibike.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.wmwm.citibike.api.Station;
import us.wmwm.citibike.views.StationView;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class StationAdapter extends BaseAdapter {

	private List<Station> stations;
	
	Map<String,Station> stationIdToStation = new HashMap<String,Station>();
	
	private Location myLocation;
	
	
	@Override
	public int getCount() {
		if(stations==null) {
			return 0;
		}
		return stations.size();
	}

	@Override
	public Station getItem(int position) {
		return stations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		StationView view = (StationView)convertView;
		if(view==null) {
			view = new StationView(parent.getContext());
		}
		Station station = getItem(position);
		view.setStation(myLocation, magneticX,magneticY,magneticZ, station);
		return view;
	}

	public void setData(Location myLocation, List<Station> stations) {
		this.stations = stations;
		this.myLocation = myLocation;
		for(Station s : stations) {
			this.stationIdToStation.put(s.getId(), s);
		}
		notifyDataSetChanged();
	}
	
	Float magneticX, magneticY, magneticZ;

	public void updateBearing(float magneticX, float magneticY, float magneticZ) {
		this.magneticX = magneticX;
		this.magneticY = magneticY;
		this.magneticZ = magneticZ;
		System.out.println(String.format("%s,%s,%s",magneticX,magneticY,magneticZ));
		notifyDataSetChanged();
	}

	public void updateStations(List<Station> stations) {
		for(Station s : stations) {
			s.merge(this.stationIdToStation.get(s.getId()));
		}	
		notifyDataSetChanged();
	}
}
