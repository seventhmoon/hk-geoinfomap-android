package idv.seventhmoon.hkinfomap;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.io.FileNotFoundException;
import java.util.List;

import idv.seventhmoon.hkinfomap.HKInfoMapApplication.TrackerName;

//import com.google.analytics.tracking.android.EasyTracker;

public class MapsActivity extends ActionBarActivity implements
		ActionBar.OnNavigationListener,
		ClusterManager.OnClusterItemClickListener<Place> {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final LatLng HONG_KONG = new LatLng(22.3501, 114.1833);

	private AdView adView;
	private InterstitialAd interstitial;
	private ClusterManager<Place> mClusterManager;
	private GoogleMap googleMap;
//	private android.support.v7.app.ActionBar actionBar;

    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;

//    private String[] mDrawerTitles;
//    private DrawerLayout mDrawerLayout;

	private int itemClickCount = 0;

	// private ClusterManager mClusterManager;

	public void onStart() {
		super.onStart();
		// EasyTracker.getInstance().activityStart(this); // Add this method.
	}

	public void onStop() {
		super.onStop();
		// EasyTracker.getInstance().activityStop(this); // Add this method.
	}

	private void startTracking() {
		// Get tracker.
		Tracker t = ((HKInfoMapApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);

		// Set screen name.
		// Where path is a String representing the screen name.
		t.setScreenName(this.getLocalClassName());

		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_map);
		setContentView(R.layout.activity_maps);
		adView = (AdView) this.findViewById(R.id.ad);
//		actionBar = getSupportActionBar();

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);

		setupActionBar();
		prepareMap();
//		prepareInterstitalAd();
        startTracking();
//		loadAd();
    }

	private void setupActionBar() {
		// Set up the action bar to show a dropdown list.
//		actionBar = getSupportActionBar();
//		actionBar.setDisplayShowTitleEnabled(false);

        Toolbar toolbar = mActionBarToolbar;
//        toolbar.setLogo(R.drawable.ic_launcher);
        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.actionbar_spinner,
                toolbar, false);

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this.getSupportActionBar().getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, this.getResources().getStringArray(
								R.array.menu_categories));

        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.actionbar_spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
//                onTopLevelTagSelected(mTopLevelSpinnerAdapter.getTag(position));
//                loadItems(position);
                googleMap.clear();
                setUpClusterer(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);

//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//
//		// Set up the dropdown list navigation in the action bar.
//		actionBar.setListNavigationCallbacks(
//		// Specify a SpinnerAdapter to populate the dropdown list.
//				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
//						android.R.layout.simple_list_item_1,
//						android.R.id.text1, this.getResources().getStringArray(
//								R.array.menu_categories)), this);
	}

	private void prepareMap() {
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		if (googleMap != null) {
			// googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HONG_KONG,
			// 12));
			mClusterManager = new ClusterManager<Place>(this, googleMap);
			mClusterManager.setOnClusterItemClickListener(this);

			// ClusterManager<MyItem> mClusterManager = new
			// ClusterManager<MyItem>(this, getMap());
			mClusterManager.setRenderer(new MyClusterRenderer(this, googleMap,
					mClusterManager));
			// Point the map's listeners at the listeners implemented by the
			// cluster
			// manager.
			googleMap.setOnCameraChangeListener(mClusterManager);
			googleMap.setOnMarkerClickListener(mClusterManager);
			googleMap.setMyLocationEnabled(true);

			Location myLocation = googleMap.getMyLocation();
			if (myLocation != null) {
				LatLng myLatLng = new LatLng(myLocation.getLatitude(),
						myLocation.getLongitude());
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
						myLatLng, 15));
			} else {
				try {
					LocationManager locationManager = (LocationManager) this
							.getSystemService(Context.LOCATION_SERVICE);
					Location lastKnownLocation = locationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
							new LatLng(lastKnownLocation.getLatitude(),
									lastKnownLocation.getLongitude()), 14));
				} catch (Exception e) {
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
							HONG_KONG, 14));
				}

			}

			// googleMap.

		}

	}

	private void prepareInterstitalAd() {
		String adUnitId = this.getString(R.string.ad_unit_id_interstitial);
		// Create the interstitial.
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(adUnitId);

		// Create ad request.
		Builder builder = new AdRequest.Builder();
		if (googleMap != null) {
			Location currentLocation = googleMap.getMyLocation();
			if (currentLocation != null) {
				builder.setLocation(currentLocation);
			}
		}

		AdRequest adRequest = builder.build();

		// Begin loading your interstitial.
		interstitial.loadAd(adRequest);
	}

	private void loadAd() {
		// load Ad
		Builder builder = new AdRequest.Builder();
		if (googleMap != null) {
			Location currentLocation = googleMap.getMyLocation();
			if (currentLocation != null) {
				builder.setLocation(currentLocation);
			}
		}
		if (adView != null) {

			// adView.setVisibility(AdView.INVISIBLE);
			AdRequest adRequest = builder.addTestDevice(
					AdRequest.DEVICE_ID_EMULATOR).build();
			// .addTestDevice("TEST_DEVICE_ID").build();
			adView.setAdListener(new AdListener() {
				@Override
				public void onAdFailedToLoad(int errorCode) {
					adView.setVisibility(AdView.GONE);
				}

				public void onAdLoaded() {
					adView.setVisibility(AdView.VISIBLE);
				}
			});
			adView.loadAd(adRequest);
		}
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
//	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	private Context getActionBarThemedContextCompat() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//			return getActionBar().getThemedContext();
//		} else {
//			return this;
//		}
//	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
//		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
//			actionBar.setSelectedNavigationItem(savedInstanceState
//					.getInt(STATE_SELECTED_NAVIGATION_ITEM));
//		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
//		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
//				.getSelectedNavigationIndex());
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}

	private List<Place> getPlaces(int itemGroup) {
		List<Place> places = getPlacesFromCache(itemGroup);
		if (places == null) {
			places = getPlacesFromLocal(itemGroup);
		}
		return places;
	}

	private List<Place> getPlacesFromLocal(int itemGroup) {
		int[] csvId = { R.raw.govwifi_ogcio, R.raw.po_hkpost, R.raw.spb_hkpost,
				R.raw.library_lcsd, R.raw.toilet_fehd };
		List<Place> places = CSVReader.getFareSavers(getResources()
				.openRawResource(csvId[itemGroup]));
		return places;
	}

	private List<Place> getPlacesFromCache(int itemGroup) {
		String[] fusionTableIds = getResources().getStringArray(
				R.array.table_ids);
		try {
			return PlaceHelper.getPlaces(this
					.openFileInput(fusionTableIds[itemGroup]));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Deprecated
	private void loadItems(int itemGroup) {

		googleMap.clear();

		List<Place> places = getPlaces(itemGroup);

		for (Place place : places) {

			LatLng location = new LatLng(place.getLatitude(),
					place.getLongitude());

			Marker marker = googleMap.addMarker(new MarkerOptions()
					.draggable(false).position(location).title(place.getName())
					.snippet(place.getAdddress()));

		}
	}

	private void showInterstitalAd() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}

	@Override
    @Deprecated
	public boolean onNavigationItemSelected(int position, long id) {

		googleMap.clear();
		// loadItems(position);
		setUpClusterer(position);
		// showInterstitalAd();
		return true;
	}

	@Override
	public void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
	}

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

	private void setUpClusterer(int itemGroup) {

		mClusterManager.clearItems();

		// Add cluster items (markers) to the cluster manager.
		addItems(itemGroup);
		mClusterManager.cluster();

	}

	private void addItems(int itemGroup) {

		List<Place> places = getPlaces(itemGroup);

		mClusterManager.addItems(places);

	}

	@Override
	public boolean onClusterItemClick(Place place) {

//		if (itemClickCount++ >= 3) {
//			showInterstitalAd();
//		}

		return false;
	}

	// private List<Place> getPlaces(String json) {
	//
	//
	// try {
	// JSONArray jsonArray = new JSONArray(json);
	// Log.i(this.getClass().getName(),
	// "Number of entries " + jsonArray.length());
	// for (int i = 0; i < jsonArray.length(); i++) {
	// JSONObject jsonObject = jsonArray.getJSONObject(i);
	// Log.i(this.getClass().getName(), jsonObject.getString("text"));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
//    private Toolbar getActionBarToolbar() {
//        if (mActionBarToolbar == null) {
//            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
//            if (mActionBarToolbar != null) {
//                setSupportActionBar(mActionBarToolbar);
//            }
//        }
//        return mActionBarToolbar;
//    }
}
