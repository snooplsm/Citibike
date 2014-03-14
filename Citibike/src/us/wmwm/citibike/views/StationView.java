package us.wmwm.citibike.views;

import us.wmwm.citibike.api.Station;
import us.wmwm.citibike2.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StationView extends RelativeLayout {

	TextView name;
	
	public StationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_station, this);
		name = (TextView) findViewById(R.id.name);
	}

	public StationView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public StationView(Context context) {
		this(context,null,0);
	}

	public void setStation(Station station) {
		name.setText(station.getLabel());
	}

}
