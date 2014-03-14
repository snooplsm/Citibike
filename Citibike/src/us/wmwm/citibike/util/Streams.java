package us.wmwm.citibike.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import us.wmwm.citibike.CitibikeApplication;

public class Streams {

	public static InputStream getStream(String resource) {
		try {
			return CitibikeApplication.get().openFileInput(resource);			
		} catch (Exception e) {
			int resId = CitibikeApplication.get().getResources().getIdentifier(resource.split("\\.")[0], "raw", CitibikeApplication.get().getPackageName());
			return CitibikeApplication.get().getResources().openRawResource(resId);
		}
	}
	
	public static String readFully(InputStream in) {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		StringBuilder b = new StringBuilder();
		try {
			while ((line = r.readLine()) != null) {
				b.append(line);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return b.toString();
	}
	
	private static final SimpleDateFormat DATE = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	
	public static Calendar date(String d) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTimeInMillis(DATE.parse(d).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
}
