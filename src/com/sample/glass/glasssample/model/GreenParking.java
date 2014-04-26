package com.sample.glass.glasssample.model;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

public class GreenParking extends Parking {
	public static class Keys {
		public static final String ID = "id";
		public static final String ADDRESS = "address";
		public static final String LAT = "lat";
		public static final String LNG = "lng";
		public static final String RATE_HALF_HOUR = "rate_half_hour";
	}

	@SerializedName(Keys.ID)
	public String mId;
	
	@SerializedName(Keys.ADDRESS)
	public String mAddress;

	@SerializedName(Keys.LAT)
	public String mLat;

	@SerializedName(Keys.LNG)
	public String mLong;

	@SerializedName(Keys.RATE_HALF_HOUR)
	public String mRateHalfHour;

	public GreenParking(GreenParking greenParking, float distance) {
		super(distance);
		mId = greenParking.mId;
		mLat = greenParking.mLat;
		mLong = greenParking.mLong;
		mRateHalfHour = greenParking.mRateHalfHour;
	}

	@Override
	public Location getLocation() {
		if (mLat != null && mLong != null) {
			final Location greenParkingLocation = new Location("Green Parking");
			greenParkingLocation.setLatitude(Double.parseDouble(mLat));
			greenParkingLocation.setLongitude(Double.parseDouble(mLong));
			return greenParkingLocation;
		}
		return null;
	}
}
