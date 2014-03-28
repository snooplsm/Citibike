package us.wmwm.citibike.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Leg extends Step {

	List<Step> steps;
	
	public Leg(JSONObject o) {
		super(o);
		JSONArray steps = o.optJSONArray("steps");
		if(steps==null) {
			this.steps = Collections.emptyList();
			return;
		}
		this.steps = new ArrayList<Step>(steps.length());
		for(int i =0; i < steps.length(); i++) {
			JSONObject step = steps.optJSONObject(i);
			this.steps.add(new Step(step));
		}
	}
}
