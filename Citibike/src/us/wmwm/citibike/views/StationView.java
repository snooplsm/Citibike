package us.wmwm.citibike.views;

import java.util.concurrent.Future;

import us.wmwm.citibike.api.Station;
import us.wmwm.citibike.util.ThreadHelper;
import us.wmwm.citibike2.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StationView extends RelativeLayout {

	TextView name;
	TextView distance;
	TextView available;
	ImageView map;	
	Station station;
	
	public StationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_station, this);
		name = (TextView) findViewById(R.id.name);
		distance = (TextView) findViewById(R.id.distance);
		available = (TextView) findViewById(R.id.available);
		map = (ImageView) findViewById(R.id.map);
	}

	public StationView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public StationView(Context context) {
		this(context,null,0);
	}
	
	Bitmap bitmap;
	
	Runnable setImage = new Runnable() {
		public void run() {
			if(bitmap!=null && !bitmap.isRecycled()) {
				map.setImageBitmap(bitmap);
				bitmap = null;
			}
		};
	};
	
	Runnable loadImage = new Runnable() {
		@Override
		public void run() {
			int identifier = getResources().getIdentifier("map_"+station.getId(), "drawable", getContext().getPackageName());
			if(identifier!=0) {
				bitmap = BitmapFactory.decodeResource(getResources(), identifier);
				post(setImage);
			}
		}
	};
	
	Future<?> loadImageFuture = null;

	public void setStation(Location me, Float magneticX, Float magneticY, Float magneticZ, Station station) {
		System.out.println("setStation");
		if(loadImageFuture!=null) {
			loadImageFuture.cancel(true);
		}
		this.station =station;
		this.map.setImageBitmap(null);
		name.setText(station.getLabel());
		if(me!=null) {
			float[] distanceHeading = new float[2];
			Location.distanceBetween(station.getLatLng().latitude,
					station.getLatLng().longitude, me.getLatitude(),
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
		
		loadImageFuture = ThreadHelper.getImagePool().submit(loadImage);
		
	}

}
