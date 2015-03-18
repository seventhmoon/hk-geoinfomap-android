package idv.seventhmoon.hkinfomap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

//import android.util.Log;

import com.csvreader.CsvReader;

public class CSVReader {
	// InputStream in;
	// public CSVReader(InputStream in){
	// this.in = in;
	// }

	public static List<Place> getFareSavers(InputStream in) {
		// Map<String, ArrayList<HashMap<String, Object>>> map = new
		// TreeMap<String, ArrayList<HashMap<String, Object>>>();
		// for (int i = 0; i < rawList.length; i++) {
		Vector<Place> list = new Vector<Place>();

		try {
			// InputStream rawIn =
			// this.getResources().openRawResource(resId);
			CsvReader csvReader = new CsvReader(new InputStreamReader(in));
			csvReader.readHeaders();

			while (csvReader.readRecord()) {
				String name = csvReader.get("name");
				String address = csvReader.get("address");
				// String stationField = csvReader.get("Station");
				double latitude = Double.parseDouble(csvReader.get("latitude"));
				double longitude = Double.parseDouble(csvReader
						.get("longitude"));
				list.add(new Place(latitude, longitude, name, address));
				// float discount = Float.parseFloat(csvReader.get("Save"));
				// String link = csvReader.get("link");

				// TreeSet<String> stations = new TreeSet<String>();

				// for (String station : stationField.split("/")) {
				// stations.add(station.trim());
				// }
				// //Log.i("***",stations.toString());
				// FareSaver saver = new FareSaver(name, stations, latitude,
				// longitude, discount, link);

				// String phone = csvReader.get("phone");
				// String category = csvReader.get("category");

				// ArrayList<HashMap<String, Object>> list = map.get(category);
				// if (list == null)
				// list = new ArrayList<HashMap<String, Object>>();
				//
				// HashMap<String, Object> entry = new HashMap<String,
				// Object>();
				// entry.put("name", name);
				// entry.put("phone", phone);
				// list.add(entry);
				// map.put(category, list);
				// savers.add(saver);
			}
			csvReader.close();
			in.close();

			// Collections.shuffle(list);
			// lists.add(list);

		} catch (IOException e) {
			e.printStackTrace();

		}

		return list;

		// }

	}

}
