//package us.wmwm.citibike.api;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import org.json.JSONObject;
//
//import us.wmwm.citibike.util.Streams;
//import android.net.Uri;
//
//public class Api {
//
//	private static final String STATIONS = "http://appservices.citibikenyc.com/data2/stations.php";
//	
//	public class StationsUpdatedResponse {
//
//		boolean success;
//
//		StationsUpdatedResponse(JSONObject o) {
//			success = o.optBoolean("ok");
//		}
//
//		public boolean isSuccess() {
//			return success;
//		}
//	}
//
//	public class StationsResponse {
//		
//		boolean success;
//
//		StationsResponse(JSONObject o) {
//			success = o.optBoolean("ok");
//		}
//	}
//
//	private HttpURLConnection get(Map<String, String> map,
//			Map<String, String> map2, String format) {
//		return req("GET", map, map2, format);
//	}
//
//	Map<String, String> auth(String userId, String token) {
//		return map("USER-ID", userId, "ACCESS-TOKEN", token);
//	}
//
//	private Map<String, String> map(Object... os) {
//		Map<String, String> d = new HashMap<String, String>();
//		for (int i = 0, j = 1; i < os.length; i += 2, j += 2) {
//			if (os[i] == null || os[j] == null) {
//				continue;
//			}
//			d.put(os[i].toString(), os[j].toString());
//		}
//		return d;
//	};
//
//	JSONObject consumeJsonObject(HttpURLConnection conn) {
//		try {
//			int status = conn.getResponseCode();
//			if (status >= 200 && status <= 200) {
//				JSONObject data = new JSONObject(Streams.readFully(conn
//						.getInputStream()));
//				return data;
//			}
//			return null;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	HttpURLConnection post(Map<String, String> postContent,
//			Map<String, String> headers, String url) {
//		return req("POST", postContent, headers, url);
//	}
//
//	private HttpURLConnection req(String method,
//			Map<String, String> postContent, Map<String, String> headers,
//			String url) {
//		URL u = null;
//		try {
//			if("GET".equals(method)) {
//				Uri.Builder uri = Uri.parse(url).buildUpon();
//				for(Map.Entry<String, String> entry : postContent.entrySet()) {
//					uri.appendQueryParameter(entry.getKey(), entry.getValue());
//				}
//				u = new URL(uri.build().toString());
//			} else {
//				u = new URL(url);
//			}
//		} catch (Exception e) {
//			throw new RuntimeException("invalid url", e);
//		}
//		HttpURLConnection conn = null;
//		OutputStream out = null;
//		try {
//			conn = (HttpURLConnection) u.openConnection();
//		} catch (Exception e) {
//			throw new RuntimeException("can't open connection, e");
//		}
//
//		try {
//			conn.setRequestMethod(method);
//			
//			for (Iterator<Map.Entry<String, String>> iter = headers.entrySet()
//					.iterator(); iter.hasNext();) {
//				Map.Entry<String, String> e = iter.next();
//				try {
//					conn.setRequestProperty(
//							URLEncoder.encode(e.getKey(), "utf-8"),
//							URLEncoder.encode(e.getValue(), "utf-8"));
//				} catch (UnsupportedEncodingException e1) {
//					throw new RuntimeException("utf-8 encoding exception", e1);
//				}
//			}
//			conn.setDoInput(true);
//			if ("POST".equals(method)) {
//				conn.setDoOutput(true);
//				conn.setRequestProperty("Content-Type",
//						"application/x-www-form-urlencoded");
//				StringBuilder b = new StringBuilder();
//				for (Iterator<Map.Entry<String, String>> iter = postContent
//						.entrySet().iterator(); iter.hasNext();) {
//					Map.Entry<String, String> e = iter.next();
//					try {
//						b.append(URLEncoder.encode(e.getKey(), "utf-8"))
//								.append("=")
//								.append(URLEncoder.encode(e.getValue(), "utf-8"));
//						if (iter.hasNext()) {
//							b.append("&");
//						}
//					} catch (UnsupportedEncodingException e1) {
//						throw new RuntimeException("utf-8 encoding exception",
//								e1);
//					}
//				}
//				byte[] data = b.toString().getBytes("utf-8");
//				out = conn.getOutputStream();
//				out.write(data, 0, data.length);
//				out.close();
//			}
//			
//			
//		} catch (IOException e2) {
//			throw new RuntimeException("Can't write data", e2);
//		} finally {
//			try {
//				if(out!=null) {
//					out.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return conn;
//	}
//
//}
