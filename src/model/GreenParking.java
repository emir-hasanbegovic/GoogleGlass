package model;

import com.google.gson.annotations.SerializedName;

public class GreenParking {
	public static class Keys {
		public static final String ID = "id";
		public static final String LAT = "lat";
		public static final String LONG = "long";
		public static final String RATE_HALF_HOUR = "rate_half_hour";
	}
	
	@SerializedName(Keys.ID)
	public String mId;
	
	@SerializedName(Keys.LAT)
	public String mLat;
	
	@SerializedName(Keys.LONG)
	public String mLong;
	
	@SerializedName(Keys.RATE_HALF_HOUR)
	public String mRateHalfHour;
}
