package idv.seventhmoon.hkinfomap;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class MyClusterRenderer extends DefaultClusterRenderer<Place> {

	public MyClusterRenderer(Context context, GoogleMap map,
			ClusterManager<Place> clusterManager) {
		super(context, map, clusterManager);
	}

	@Override
	protected void onBeforeClusterItemRendered(Place item,
			MarkerOptions markerOptions) {
		super.onBeforeClusterItemRendered(item, markerOptions);

		markerOptions.title(item.getName());
		markerOptions.snippet(item.getDescription());
	}

	@Override
	protected void onClusterItemRendered(Place clusterItem, Marker marker) {
		super.onClusterItemRendered(clusterItem, marker);

		// here you have access to the marker itself
	}
}