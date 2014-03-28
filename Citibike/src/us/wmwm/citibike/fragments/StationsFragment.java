package us.wmwm.citibike.fragments;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.maps.model.LatLng;

import us.wmwm.citibike.adapters.StationAdapter;
import us.wmwm.citibike.api.Api;
import us.wmwm.citibike.api.Api.StationsResponse;
import us.wmwm.citibike.api.Station;
import us.wmwm.citibike.util.LocationUtil;
import us.wmwm.citibike.util.LocationUtil.LocationUtilListener;
import us.wmwm.citibike.util.StationUtil;
import us.wmwm.citibike.util.ThreadHelper;
import us.wmwm.citibike2.R;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class StationsFragment extends LocationAwareFragment {

	ListView list;

	Handler handler = new Handler();

	StationAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		list.setOnScrollListener(onScrollListener);
		
	}
	
	OnScrollListener onScrollListener = new OnScrollListener() {
		
		private int previousFirstVisibleItem = 0;
	    private long previousEventTime = 0;
	    private double speed = 0;

	    @Override
	    public void onScroll(AbsListView view, int firstVisibleItem,
	            int visibleItemCount, int totalItemCount) {

	        if (previousFirstVisibleItem != firstVisibleItem){
	            long currTime = System.currentTimeMillis();
	            long timeToScrollOneElement = currTime - previousEventTime;
	            speed = ((double)1/timeToScrollOneElement)*1000;

	            previousFirstVisibleItem = firstVisibleItem;
	            previousEventTime = currTime;

	            //Log.d("DBG", "Speed: " +speed + " elements/second");
	        }

	    }

		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		
			if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING && speed>10.0) {
	            ThreadHelper.getImagePool().pause();
	        } else {
	            ThreadHelper.getImagePool().resume();
	        }
		}
	};

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
		loadData();
	}

	Future<?> loadDataFuture;

	private void loadData() {
		loadDataFuture = ThreadHelper.getScheduler().submit(new Runnable() {
			@Override
			public void run() {
				try {
					final List<Station> parsedStations = StationUtil.getStations();
					final Location location = locationUtil.getLastLocation();
					if (location != null) {
						System.out.println("Last Location: "
								+ location.getLatitude() + " , "
								+ location.getLongitude());
						StationUtil.sort(parsedStations, location);
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
		if(updateStationsFuture!=null) {
			updateStationsFuture.cancel(true);
		}
	}



	@Override
	public void onResume() {
		super.onResume();
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
				try {
				} catch (Exception e) {
					
				}
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
	}

	public static StationsFragment newInstance() {
		StationsFragment f = new StationsFragment();
		Bundle b = new Bundle();
		f.setArguments(b);
		return f;
	}

}
