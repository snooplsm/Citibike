package us.wmwm.citibike.views;

import us.wmwm.citibike2.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MapView extends ImageView {

	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MapView(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int)getResources().getDimension(R.dimen.map_height);
        setMeasuredDimension(width, height);
	}

}
