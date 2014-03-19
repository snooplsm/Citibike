package us.wmwm.citibike.util;

import java.util.HashSet;
import java.util.Set;

import us.wmwm.citibike.CitibikeApplication;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class LocationUtil {
	
	private static LocationUtil INSTANCE;
	
	public LocationUtil() {
		INSTANCE = this;
	}
	
	public static LocationUtil get() {
		return INSTANCE;
	}

	public interface LocationUtilListener {
		void onConnectionFailed(LocationUtil util);
		void onConnected(LocationUtil util);
		boolean shouldHandleResulution();
		int onActivityRequestId();
		FragmentActivity getActivity();		
	}
	
	private void addListener(LocationUtilListener listener) {
		listeners.add(listener);
	}
	
	private boolean removeListener(LocationUtilListener listener) {
		return listeners.remove(listener);
	}
	
	Set<LocationUtilListener> listeners = new HashSet<LocationUtilListener>(2);
	
	boolean isConnected;
	boolean isFailedConnection;
	Bundle connectedBundle;
	ConnectionResult result;

	ConnectionCallbacks callbacks = new ConnectionCallbacks() {

		@Override
		public void onConnected(Bundle bundle) {
			connectedBundle = bundle;
			isConnected = true;
		}

		@Override
		public void onDisconnected() {
			isConnected = false;
			connectedBundle = null;
		}
		
	};
	
	OnConnectionFailedListener onFailed = new OnConnectionFailedListener() {
		@Override
		public void onConnectionFailed(ConnectionResult connResult) {
			isFailedConnection = true;
			isConnected = false;
			connectedBundle = null;
			result = connResult;
			LocationUtil.this.onConnectionFailed();
		}
	};
	
	LocationClient locationClient = new LocationClient(CitibikeApplication.get(),callbacks,onFailed);
	
	private void onConnectionFailed() {
		for(LocationUtilListener listener : listeners) {
			listener.onConnectionFailed(this);
			if(result.hasResolution() && listener.shouldHandleResulution()) {
				try {
	                // Start an Activity that tries to resolve the error
	                result.startResolutionForResult(
	                        listener.getActivity(),
	                        listener.onActivityRequestId());
	                /*
	                * Thrown if Google Play services canceled the original
	                * PendingIntent
	                */
	            } catch (IntentSender.SendIntentException e) {
	                // Log the error
	                e.printStackTrace();
	            }
			}
		}
	}
	
	private boolean servicesConnected() {
		if(listeners.size()==0) {
			throw new RuntimeException("Need at least one listener");
		}
		int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(listeners.iterator().next().getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Get the error code
            int errorCode = result.getErrorCode();
            // Get the error dialog from Google Play services
            LocationUtilListener first = listeners.iterator().next();
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode,
                    first.getActivity(),
                    first.onActivityRequestId());

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(first.getActivity().getSupportFragmentManager(),
                        "Location Updates");
            }
        }
        return false;
	}
	
	public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
	
	public void onActivityCreated(LocationUtilListener listener, Bundle savedInstanceState) {
		addListener(listener);
		if(servicesConnected() && !isConnected) {
			locationClient.connect();
		}
	}	
	
	public void onDestroy(LocationUtilListener listener) {
		removeListener(listener);
		if(listeners.size()==0) {
			locationClient.disconnect();
		}
	}
	
	public Location getLastLocation() {
		if(isConnected) {
			return locationClient.getLastLocation();
		}
		return null;
	}
	
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean retVal = false;
		for(LocationUtilListener listener : listeners) {
			if(requestCode==listener.onActivityRequestId()) {
				retVal = true;
				if(resultCode==Activity.RESULT_OK) {
					
				}
				
			}
		}
		return retVal;
	}
	
}
