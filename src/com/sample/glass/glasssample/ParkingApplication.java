package com.sample.glass.glasssample;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.sample.glass.glasssample.model.GreenParking;
import com.sample.glass.glasssample.model.LawnParking;
import com.sample.glass.glasssample.utilities.Debug;

import android.app.Application;

public class ParkingApplication extends Application{

	public static final Gson GSON = new Gson();
	public static ArrayList<GreenParking> GREEN_PARKING_LIST;
	public static ArrayList<LawnParking> LAWN_PARKING_LIST;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Debug.setIsDebug(true);
	}
}
