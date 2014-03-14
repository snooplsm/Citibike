package us.wmwm.citibike.fragments;

import java.util.List;
import java.util.concurrent.Future;

import org.json.JSONObject;

import us.wmwm.citibike.adapters.StationAdapter;
import us.wmwm.citibike.api.Station;
import us.wmwm.citibike.util.Streams;
import us.wmwm.citibike.util.ThreadHelper;
import us.wmwm.citibike2.R;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StationsFragment extends Fragment {

	ListView list;

	Handler handler = new Handler();
	
	StationAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_stations, container,
				false);
		list = (ListView) root.findViewById(R.id.list);
		list.setAdapter(adapter = new StationAdapter());
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadData();
	}

	Future<?> loadDataFuture;

	private void loadData() {
		loadDataFuture = ThreadHelper.getScheduler().submit(new Runnable() {
			@Override
			public void run() {
				try {
					String stations = Streams.readFully(Streams
							.getStream("stations.json"));
					JSONObject obj = new JSONObject(stations);
					final List<Station> parsedStations = Station.parseStations(obj);
					handler.post(new Runnable() {
						@Override
						public void run() {
							adapter.setData(parsedStations);
						}
					});					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (loadDataFuture != null) {
			loadDataFuture.cancel(true);
		}
	}

	public static StationsFragment newInstance() {
		StationsFragment f = new StationsFragment();
		Bundle b = new Bundle();
		f.setArguments(b);
		return f;
	}

}
