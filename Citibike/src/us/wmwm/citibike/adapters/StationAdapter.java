package us.wmwm.citibike.adapters;

import java.util.List;

import us.wmwm.citibike.api.Station;
import us.wmwm.citibike.views.StationView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class StationAdapter extends BaseAdapter {

	private List<Station> stations;
	
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
		view.setStation(station);
		return view;
	}

	public void setData(List<Station> stations) {
		this.stations = stations;
		notifyDataSetChanged();
	}
}
