package com.sample.glass.glasssample.model;

import android.location.Location;

public abstract class Parking implements Comparable<Parking>{
	
	public static final String PRICE = "$ %s / 30 mins";
	public static final String DISTANCE = "%.1f km";
	
	public static class Keys {
		public static final String DISTANCE = "distance";
	}
	public final float mDistance;
	
	public Parking(final float distance) {
		mDistance = distance;
	}

	public abstract Location getLocation();

	@Override
	public int compareTo(final Parking another) {
		if (another == null){
			return -1; 
		}
		
		return Float.compare(mDistance, another.mDistance);
	}
}
