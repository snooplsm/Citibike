package us.wmwm.citibike.fragments;

import us.wmwm.citibike.util.LocationUtil;
import us.wmwm.citibike.util.LocationUtil.LocationUtilListener;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public abstract class LocationAwareFragment extends Fragment {

	LocationUtil locationUtil = LocationUtil.get();
	
	private LocationUtilListener listener = new LocationUtilListener() {

		@Override
		public FragmentActivity getActivity() {
			return (FragmentActivity) LocationAwareFragment.this.getActivity();
		}

		@Override
		public int onActivityRequestId() {
			return 1000;
		}

		@Override
		public void onConnected(LocationUtil util) {
			Location location = util.getLastLocation();
			if(location!=null) {
				onUserLocation(location);
			}
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
			
		}
	};
	
	protected void onUserLocation(Location location) {
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		locationUtil.onActivityCreated(listener, savedInstanceState);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		locationUtil.onPause();
	}



	@Override
	public void onResume() {
		super.onResume();
		locationUtil.onResume();
	}
	

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		locationUtil.onDestroy(listener);
	}
	
}
