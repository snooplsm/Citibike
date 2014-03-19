package us.wmwm.citibike.fragments;

import java.util.List;
import java.util.concurrent.Future;

import org.json.JSONObject;

import us.wmwm.citibike.adapters.StationAdapter;
import us.wmwm.citibike.api.Station;
import us.wmwm.citibike.util.LocationUtil;
import us.wmwm.citibike.util.LocationUtil.LocationUtilListener;
import us.wmwm.citibike.util.Streams;
import us.wmwm.citibike.util.ThreadHelper;
import us.wmwm.citibike2.R;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StationsFragment extends Fragment {

	ListView list;

	Handler handler = new Handler();
	
	StationAdapter adapter;
	
	LocationUtil locationUtil = LocationUtil.get();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_stations, container,
				false);
		list = (ListView) root.findViewById(R.id.list);
		list.setAdapter(adapter = new StationAdapter());
		return root;
	}
	
	private LocationUtilListener listener = new LocationUtilListener() {
		
		@Override
		public FragmentActivity getActivity() {		
			return (FragmentActivity) StationsFragment.this.getActivity();
		}
		
		@Override
		public int onActivityRequestId() {
			return 1000;
		}
		
		@Override
		public void onConnected(LocationUtil util) {			
		}
		
		@Override
		public void onConnectionFailed(LocationUtil util) {			
		}
		
		@Override
		public boolean shouldHandleResulution() {
			return true;
		}
		
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		locationUtil.onActivityCreated(listener,savedInstanceState);
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
					System.out.println("There are " + parsedStations.size() + " stations.");
					final Location location = locationUtil.getLastLocation();
					System.out.println("Last Location: " + location.getLatitude() + " , " + location.getLongitude());
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
		locationUtil.onDestroy(listener);
	}

	public static StationsFragment newInstance() {
		StationsFragment f = new StationsFragment();
		Bundle b = new Bundle();
		f.setArguments(b);
		return f;
	}

}
