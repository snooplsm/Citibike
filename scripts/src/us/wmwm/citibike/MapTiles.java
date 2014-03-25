package us.wmwm.citibike;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapTiles {

	public static void main(String... args) throws Exception {
		File file = new File(args[0]);
		File export = new File(args[1]);
		FileInputStream fin = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fin));
		StringBuilder b = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			b.append(line).append("\n");
		}
		br.close();
		JSONArray stations = new JSONObject(b.toString())
				.optJSONArray("results");
		byte[] buffer = new byte[1024 * 4];
		export.mkdirs();
		System.out.println("There are " + stations.length() + " stations and we need to process " + (stations.length() - export.list().length));
		for (int i = 0; i < stations.length(); i++) {
			JSONObject station = stations.optJSONObject(i);
			String url = "http://maps.googleapis.com/maps/api/staticmap?"
					+ "center=:lat,:lng&zoom=:zoom&size=:widthx:height"
					+ "&markers=size:small%7C:lat,:lng&sensor=false&format=png";
			if(System.getProperty("apiKey")!=null) {
				url+=("&key="+System.getProperty("apiKey"));
			}
			double lat = station.optDouble("latitude");
			double lon = station.optDouble("longitude");
			url = url.replaceAll(":lat", String.valueOf(lat));
			url = url.replaceAll(":lng", String.valueOf(lon));
			url = url.replace(":zoom", "16");
			url = url.replace(":width", "400");
			url = url.replace(":height", "270");
			File outFile = new File(export, "map_" + station.optString("id")
					+ ".png");

			if (outFile.exists()) {
				continue;
			}
			if (!fetchUrl(url, buffer, outFile, station)) {
				Thread.sleep(60000);
			} else {
				Thread.sleep(550);
			}

			System.out.println(url);
		}
	}

	private static boolean fetchUrl(String url, byte[] buffer, File export,
			JSONObject station) throws Exception {
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setDoInput(true);
		conn.setRequestMethod("GET");
		int status = conn.getResponseCode();
		System.out.println(status);
		if (status != 200) {
			conn.disconnect();
			return false;
		}
		InputStream in = conn.getInputStream();

		int read = 0;
		FileOutputStream out = new FileOutputStream(export);
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		out.close();
		return true;
	}

	public static String getTileNumber(final double lat, final double lon,
			final int zoom) {
		int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
		int ytile = (int) Math
				.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1
						/ Math.cos(Math.toRadians(lat)))
						/ Math.PI)
						/ 2 * (1 << zoom));
		if (xtile < 0)
			xtile = 0;
		if (xtile >= (1 << zoom))
			xtile = ((1 << zoom) - 1);
		if (ytile < 0)
			ytile = 0;
		if (ytile >= (1 << zoom))
			ytile = ((1 << zoom) - 1);
		return ("" + zoom + "/" + xtile + "/" + ytile);
	}

}
