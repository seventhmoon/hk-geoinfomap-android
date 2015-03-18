package idv.seventhmoon.hkinfomap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


public class Place implements ClusterItem  {
	public Place(double latitude, double longitude, String name, String adddress) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.adddress = adddress;
	}

	private double latitude;
	private double longitude;
	private String name;
	private String adddress;

	public String toString(){
		return this.name + "(" + this.latitude + "," + this.longitude + ")";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdddress() {
		return adddress;
	}

	public void setAdddress(String adddress) {
		this.adddress = adddress;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public String getDescription(){
		return this.adddress;
	}

	@Override
	public LatLng getPosition() {
		return new LatLng(this.latitude, this.longitude);
	}

}
