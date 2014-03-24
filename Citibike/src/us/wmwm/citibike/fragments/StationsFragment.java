package us.wmwm.citibike.fragments;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import us.wmwm.citibike.adapters.StationAdapter;
import us.wmwm.citibike.api.Api;
import us.wmwm.citibike.api.Api.StationsResponse;
import us.wmwm.citibike.api.Station;
import us.wmwm.citibike.util.LocationUtil;
import us.wmwm.citibike.util.LocationUtil.LocationUtilListener;
import us.wmwm.citibike.util.Streams;
import us.wmwm.citibike.util.ThreadHelper;
import us.wmwm.citibike2.R;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
		
		@Override
		public void onBearingChanged(float magneticX, float magneticY,
				float magneticZ) {
			if(adapter!=null) {
				adapter.updateBearing(magneticX,magneticY,magneticZ);
			}
		}

	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		locationUtil.onActivityCreated(listener, savedInstanceState);
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
					final List<Station> parsedStations = Station
							.parseStations(obj);
					System.out.println("There are " + parsedStations.size()
							+ " stations.");
					final Location location = locationUtil.getLastLocation();
					if (location != null) {
						System.out.println("Last Location: "
								+ location.getLatitude() + " , "
								+ location.getLongitude());
						final Station me = new Station() {
							@Override
							public double getLatitude() {
								return location.getLatitude();
							}

							public double getLongitude() {
								return location.getLongitude();
							}
						};
						Comparator<Station> sort = new Comparator<Station>() {
							float[] a = new float[1];
							float[] b = new float[1];

							@Override
							public int compare(Station lhs, Station rhs) {
								Location.distanceBetween(lhs.getLatitude(),
										lhs.getLongitude(), me.getLatitude(),
										me.getLongitude(), a);
								Location.distanceBetween(rhs.getLatitude(),
										rhs.getLongitude(), me.getLatitude(),
										me.getLongitude(), b);
								return Float.valueOf(a[0]).compareTo(b[0]);
							}
						};
						Collections.sort(parsedStations, sort);
					}
					handler.post(new Runnable() {
						@Override
						public void run() {
							adapter.setData(location, parsedStations);
							updateStations();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}
	
	

	@Override
	public void onPause() {
		super.onPause();
		locationUtil.onPause();
		if(updateStationsFuture!=null) {
			updateStationsFuture.cancel(true);
		}
	}



	@Override
	public void onResume() {
		super.onResume();
		locationUtil.onResume();
		if(adapter!=null) {
			updateStations();
		}
	}
	
	Future<?> updateStationsFuture;
	
	private void updateStations() {
		if(updateStationsFuture!=null) {
			updateStationsFuture.cancel(true);
		}
		updateStationsFuture = ThreadHelper.getScheduler().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Api api = new Api();
				final StationsResponse resp;
				try {
					resp = api.updateStations();
				} catch (Exception e) {
					//TODO: SHOW ERROR
					return;
				}
				if(!resp.isSuccess()) {
					
				} else {
					Map<String, Station> updatedStations = new HashMap<String,Station>();
					for(Station s : resp.getStations()) {
						updatedStations.put(s.getId(), s);
					}
					handler.post(new Runnable() {
						@Override
						public void run() {							
							adapter.updateStations(resp.getStations());
						}						
					});
					
				}
			}
		}, 1, 60*1000, TimeUnit.MILLISECONDS);
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
