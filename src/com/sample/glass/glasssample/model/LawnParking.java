package com.sample.glass.glasssample.model;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

public class LawnParking extends Parking  {
	
	public LawnParking(final LawnParking lawnParking, final float distance) {
		super(distance);
		mAddress = lawnParking.mAddress;
		mLatitude = lawnParking.mLatitude;
		mLongitude = lawnParking.mLongitude;
	}

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

	@Override
	public Location getLocation() {
		if (mLatitude != null && mLongitude != null) {
			final Location greenParkingLocation = new Location("Lawn Parking");
			greenParkingLocation.setLatitude(mLatitude);
			greenParkingLocation.setLongitude(mLongitude);
			return greenParkingLocation;
		}
		return null;
	}

}
