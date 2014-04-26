package com.sample.glass.glasssample.model;

import com.google.gson.annotations.SerializedName;

public class GreenParking {
	public static class Keys {
		public static final String ID = "id";
		public static final String LAT = "lat";
		public static final String LNG = "lng";
		public static final String RATE_HALF_HOUR = "rate_half_hour";
	}
	
	@SerializedName(Keys.ID)
	public String mId;
	
	@SerializedName(Keys.LAT)
	public String mLat;
	
	@SerializedName(Keys.LNG)
	public String mLong;
	
	@SerializedName(Keys.RATE_HALF_HOUR)
	public String mRateHalfHour;
}
