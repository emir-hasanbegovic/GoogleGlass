package model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class CarParks {

	public static class Keys {
		public static final String CARPARKS = "carparks";
	}
	
	@SerializedName(Keys.CARPARKS)
	public ArrayList<GreenParking> mCarPark;
}
