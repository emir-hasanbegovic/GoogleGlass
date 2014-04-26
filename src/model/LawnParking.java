package model;

import com.google.gson.annotations.SerializedName;

public class LawnParking {
	public static class Keys {
		public static final String ADDRESS = "address";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
	}

	@SerializedName(Keys.ADDRESS)
	public String mAddress;

	@SerializedName(Keys.LATITUDE)
	public Float mLatitude;

	@SerializedName(Keys.LONGITUDE)
	public Float mLongitude;

}
