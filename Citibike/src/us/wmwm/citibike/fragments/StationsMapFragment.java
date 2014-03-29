package us.wmwm.citibike.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import us.wmwm.citibike.api.Station;
import us.wmwm.citibike.util.StationUtil;
import us.wmwm.citibike.util.ThreadHelper;
import us.wmwm.citibike2.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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

		bitmap = new CitiMarker(getActivity());
		setUpMapIfNeeded();
	}
	
	class CitiMarker extends View {
		
		int width;
		
		Station station;
		
		Paint p;
		
		int orange = Color.parseColor("#FFFC6D14");
		int lorange = Color.parseColor("#44FC6D14");
		
		public CitiMarker(Context ctx) {		
			super(ctx);
			 width = (int) getResources().getDimension(R.dimen.marker_width);
			 p = new Paint();
		}
		
		
		@Override
		protected void onDraw(Canvas canvas) {
			float total = station.getAvailableBikes()+station.getAvailableDocks();
			float percentFull = station.getAvailableBikes() / total;
			int xstart = canvas.getWidth()/4;
			int ystart = canvas.getHeight();
			int xend = canvas.getWidth()*3/4;
			int yend = (int)(canvas.getHeight()*(1-percentFull));
			p.setColor(orange);
			canvas.drawRect(xstart, ystart, xend, yend,p);
			//xstart = canvas.getWidth()/2;			
			ystart = yend;
			//xend = canvas.getWidth();
			yend = 0;
			p.setColor(lorange);
			canvas.drawRect(xstart, ystart, xend, yend,p);			
			
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {			
			setMeasuredDimension(width, width);
		}
		
		public void setStation(Station station) {
			this.station = station;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
					.findFragmentById(R.id.map_fragment);
			if (mapFragment != null) {
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

	List<Station> stations;

	private void updateMap() {
		final View mapView = mapFragment.getView();
		if (mapView.getWidth() != 0) {
			stations = StationUtil.getStations();
			StationUtil.sort(stations, locationUtil.getLastLocation());
			final List<Station> nearest = stations.subList(0,
					Math.min(stations.size(), 20));
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

			mMap.setOnCameraChangeListener(onCameraChangeListener);
			mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
			CameraPosition pos = CameraPosition.builder()
					.target(llb.getCenter()).zoom(15.5f).build();
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
		}
	}

	Future<?> sortFuture;

	Map<Station, Marker> markers = new HashMap<Station, Marker>();

	Object lock = new Object();

	OnCameraChangeListener onCameraChangeListener = new OnCameraChangeListener() {
		@Override
		public void onCameraChange(CameraPosition arg) {
			// TODO Auto-generated method stub

			final LatLngBounds llb = mMap.getProjection().getVisibleRegion().latLngBounds;
			if (sortFuture != null) {
				sortFuture.cancel(true);
			}
			sortFuture = ThreadHelper.getScheduler().schedule(new Runnable() {
				@Override
				public void run() {
					synchronized (lock) {
						final List<Station> toAdd = new ArrayList<Station>();
						final List<Station> toRemove = new ArrayList<Station>();
						for (Station s : stations) {
							if (llb.contains(s.getLatLng())) {
								if (!markers.containsKey(s)) {
									toAdd.add(s);
								}
							} else {
								toRemove.add(s);
							}
						}

						handler.post(new Runnable() {
							@Override
							public void run() {
								Iterator<Station> removeIter = toRemove
										.iterator();
								while (removeIter.hasNext()) {
									Marker marker = markers.remove(removeIter
											.next());
									if (marker != null) {
										marker.remove();
									}
								}
								Iterator<Station> addIter = toAdd.iterator();
								while (addIter.hasNext()) {
									Station station = addIter.next();
									Marker marker = mMap
											.addMarker(makeMarker(station));
									markers.put(station, marker);
								}
							}
						});
					}
				}
			}, 0, TimeUnit.MILLISECONDS);

		}
	};
	
	CitiMarker bitmap;

	private MarkerOptions makeMarker(Station st) {
		MarkerOptions o = new MarkerOptions().title(st.getLabel()).position(
				st.getLatLng());
		bitmap.setStation(st);
		Bitmap mb;
		bitmap.measure(0, 0);
		Canvas canvas = new Canvas(mb = Bitmap.createBitmap(bitmap.getMeasuredWidth(), bitmap.getMeasuredHeight(), Config.ARGB_8888));
		bitmap.draw(canvas);
		o.icon(BitmapDescriptorFactory.fromBitmap(mb));
		return o;
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
		// mMap.moveCamera(CameraUpdateFactory
		// .newLatLngBounds(llb, padding));
	}

	public static StationsMapFragment newInstance() {
		StationsMapFragment f = new StationsMapFragment();
		Bundle b = new Bundle();
		f.setArguments(b);
		return f;
	}
}
