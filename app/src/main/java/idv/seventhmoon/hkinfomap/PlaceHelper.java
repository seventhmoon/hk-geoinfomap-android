package idv.seventhmoon.hkinfomap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PlaceHelper {
	private static String readInputStream(InputStream in) {
		BufferedReader buffIn = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
//		String message = ""; 
				
				
		try {
			for (String line = buffIn.readLine(); line != null; line = buffIn
					.readLine()) {
				sb.append(line);
//				message += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
//		return message;
	}

	public static List<Place> getPlaces(InputStream in) {
		return getPlaces(readInputStream(in));
	}

	private static List<Place> getPlaces(String json) {
//		Log.i("***", json);
		List<Place> places = new ArrayList<Place>();
		try {
			JSONObject jsonRoot = new JSONObject(json);
			// try {
			// jsonRoot = new JSONObject(json);
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			JSONArray jsonArray = jsonRoot.getJSONArray("rows");
			// JSONArray jsonArray = new JSONArray(json);
			// Log.i(this.getClass().getName(),
			// "Number of entries " + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONArray record = jsonArray.getJSONArray(i);
				// Log.i(this.getClass().getName(), record.toString());

				Place place = new Place(record.getDouble(0),
						record.getDouble(1), record.getString(2),
						record.getString(3));
				places.add(place);
				// JSONObject jsonObject = jsonArray.getJSONObject(i);
				// Log.i(this.getClass().getName(),
				// jsonObject.getString("text"));
			}
			return places;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		// }finally{
		// return null;
		// }

	}
}
