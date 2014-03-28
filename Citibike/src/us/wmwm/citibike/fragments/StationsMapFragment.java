package us.wmwm.citibike.fragments;

import java.util.List;

import us.wmwm.citibike.api.Station;
import us.wmwm.citibike.util.StationUtil;
import us.wmwm.citibike2.R;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class StationsMapFragment extends LocationAwareFragment {

	private GoogleMap mMap;
	
	Handler handler = new Handler();
	
	SupportMapFragment mapFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_station_map, container,
				false);
		return root;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mapFragment = SupportMapFragment.newInstance();
		FragmentTransaction t = getChildFragmentManager().beginTransaction();
		t.replace(R.id.map_fragment, mapFragment);
		t.commit();
		
		setUpMapIfNeeded();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
			if(mapFragment!=null) {
				mMap = mapFragment.getMap();
				if (mMap != null) {
					setUpMap();
				} else {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							setUpMapIfNeeded();
						}
					}, 100);
				}
			}
		}
	}
	
	Location lastLocation;
	
	@Override
	protected void onUserLocation(Location location) {
		super.onUserLocation(location);
		lastLocation = location;
		updateMap();
	}

	private void updateMap() {
		 final View mapView = mapFragment.getView();
		 if(mapView.getWidth()!=0) {
				List<Station> stations = StationUtil.getStations();
				StationUtil.sort(stations, locationUtil.getLastLocation());
				final List<Station> nearest = stations.subList(0,
						Math.min(stations.size(), 3));
				if (nearest.size() == 0) {
					//
				}
				final Station near = nearest.get(0);
				final LatLngBounds llb = new LatLngBounds(near.getLatLng(),
						near.getLatLng());
				for (Station s : nearest) {
					llb.including(s.getLatLng());
				}

				final int padding = (int) getResources().getDimension(
						R.dimen.map_padding);
				
				//mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(near.getLatLng()).zoom(16).build()));
				for(Station st : nearest) {
					MarkerOptions o = new MarkerOptions().title(st.getLabel()).position(st.getLatLng());
					mMap.addMarker(o);
				}
				mMap.moveCamera(CameraUpdateFactory
						.newLatLngBounds(llb, padding));
		 }
	}

	private void setUpMap() {
		mMap.setMyLocationEnabled(true);
		// mMap.addMarker(new MarkerOptions().position(new LatLng(0,
		// 0)).title("Marker"));

        final View mapView = mapFragment.getView();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@SuppressWarnings("deprecation")
						// We use the new method when supported
						@SuppressLint("NewApi")
						// We check which build version we are using.
						@Override
						public void onGlobalLayout() {
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								mapView.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							} else {
								mapView.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							}							
						}
					});
		}
//		mMap.moveCamera(CameraUpdateFactory
//				.newLatLngBounds(llb, padding));
	}

	public static StationsMapFragment newInstance() {
		StationsMapFragment f = new StationsMapFragment();
		Bundle b = new Bundle();
		f.setArguments(b);
		return f;
	}
}
