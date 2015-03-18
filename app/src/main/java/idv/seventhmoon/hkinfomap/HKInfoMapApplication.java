package idv.seventhmoon.hkinfomap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class HKInfoMapApplication extends Application {
	private static final String PROPERTY_ID = "UA-50795249-6";
	private final String PREFS_NAME = "pref";
	private static final long CHECK_INTERVAL = AlarmManager.INTERVAL_DAY * 7;
	// private static final int BUFFER_SIZE = 1024;
	private final String LAST_UPDATE = "last_update";

	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 * 
	 * A single tracker is usually enough for most purposes. In case you do need
	 * multiple trackers, storing them all in Application object helps ensure
	 * that they are created only once per application instance.
	 */
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
						// roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a
							// company.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
					.newTracker(PROPERTY_ID)
					: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
							.newTracker(R.xml.global_tracker) : analytics
							.newTracker(R.xml.ecommerce_tracker);
			mTrackers.put(trackerId, t);

		}
		return mTrackers.get(trackerId);
	}

	public void onCreate() {
		super.onCreate();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		long lastUpdate = settings.getLong(LAST_UPDATE, 0l);

		 if (new Date().getTime() - lastUpdate > CHECK_INTERVAL) {
//		if (true) {
			String[] fusionTableIds = getResources().getStringArray(
					R.array.table_ids);
			for (String tableId : fusionTableIds) {
				new DownloadTask().execute(tableId);
			}
			SharedPreferences.Editor editor = settings.edit();
			editor.putLong(LAST_UPDATE, new Date().getTime());

			// Commit the edits!
			editor.commit();
		}
	}

	private void downloadFusionTable(String tableId) {

		String queryUrl = getResources().getString(R.string.query_url);
		String selectQuery = getResources().getString(R.string.select_query);
		String key = getResources().getString(R.string.api_key_fusion_table);

		// Log.i("***", url);
		FileOutputStream out;
		InputStream in;
		try {
			String url = queryUrl + "sql="
					+ URLEncoder.encode(selectQuery + " " + tableId, "UTF-8")
					+ "&key=" + key;
			out = openFileOutput(tableId, Context.MODE_PRIVATE);
			in = getInputStreamFromUrl(url);

			long size = IOUtils.copyLarge(in, out);
//			Log.i("***", String.valueOf(size));
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// in.close();
		// out.close();

		// ByteStreams.copy(InputStream, OutputStream)

		// fos.write(string.getBytes());
		// fos.close();

	}

	// Given a string representation of a URL, sets up a connection and gets
	// an input stream.
	private InputStream getInputStreamFromUrl(String urlString)
			throws IOException {
		java.net.URL url = new java.net.URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();

		if (conn.getResponseCode() != 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getErrorStream()));
			for (String line = in.readLine(); line != null; line = in
					.readLine()) {
				Log.i("***", line);
			}
			in.close();
		}

		// conn.get
		return conn.getInputStream();
	}

	// Implementation of AsyncTask used to download XML feed from
	// stackoverflow.com.
	private class DownloadTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... urls) {
			downloadFusionTable(urls[0]);
			return null;
		}

		// @Override
		// protected void onPostExecute(String result) {
		// setContentView(R.layout.main);
		// // Displays the HTML string in the UI via a WebView
		// WebView myWebView = (WebView) findViewById(R.id.webview);
		// myWebView.loadData(result, "text/html", null);
		// }
	}
}
