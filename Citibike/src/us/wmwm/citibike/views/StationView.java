package us.wmwm.citibike.views;

import us.wmwm.citibike.api.Station;
import us.wmwm.citibike2.R;
import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StationView extends RelativeLayout {

	TextView name;
	TextView distance;
	TextView available;
	
	public StationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_station, this);
		name = (TextView) findViewById(R.id.name);
		distance = (TextView) findViewById(R.id.distance);
		available = (TextView) findViewById(R.id.available);
	}

	public StationView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public StationView(Context context) {
		this(context,null,0);
	}

	public void setStation(Location me, Float magneticX, Float magneticY, Float magneticZ, Station station) {
		name.setText(station.getLabel());
		if(me!=null) {
			float[] distanceHeading = new float[2];
			Location.distanceBetween(station.getLatitude(),
					station.getLongitude(), me.getLatitude(),
					me.getLongitude(), distanceHeading);
			distance.setVisibility(View.VISIBLE);
			
			distance.setText(distanceHeading[0] + "m " + distanceHeading[1]);
		} else {
			distance.setVisibility(View.GONE);
		}
		
		if(station.getAvailableBikes()>-1) {
			available.setVisibility(View.VISIBLE);
			available.setText(station.getAvailableBikes() + "/" + station.getAvailableDocks());
		} else {
			available.setVisibility(View.GONE);
		}
		
	}

}
