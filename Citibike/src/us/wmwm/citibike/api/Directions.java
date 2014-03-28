package us.wmwm.citibike.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Directions {

	List<Leg> legs;

	public Directions(JSONObject o) {
		bounds = new Bounds(o.optJSONObject("bounds"));
		JSONArray legs = o.optJSONArray("legs");
		if (legs == null) {
			this.legs = Collections.emptyList();
			return;
		}
		this.legs = new ArrayList<Leg>(legs.length());
		for (int i = 0; i < legs.length(); i++) {
			JSONObject leg = legs.optJSONObject(i);
			this.legs.add(new Leg(leg));
		}
	}

	Bounds bounds;

	public Bounds getBounds() {
		return bounds;
	}

	public List<Leg> getLegs() {
		return legs;
	}

}
