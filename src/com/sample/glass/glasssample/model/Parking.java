package com.sample.glass.glasssample.model;

import android.location.Location;

public abstract class Parking implements Comparable<Parking>{

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
