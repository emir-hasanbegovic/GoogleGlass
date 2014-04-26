package com.sample.glass.glasssample.tasks;

import java.util.ArrayList;
import java.util.Collections;

import android.location.Location;
import android.os.AsyncTask;

import com.sample.glass.glasssample.GlassActivity;
import com.sample.glass.glasssample.ParkingApplication;
import com.sample.glass.glasssample.model.GreenParking;
import com.sample.glass.glasssample.model.LawnParking;
import com.sample.glass.glasssample.model.Parking;

public class FindParkingTask extends AsyncTask<Void, Void, ArrayList<Parking>> {
	private final GlassActivity mActivity;
	private final Location mLocation;
	private final int mRadius;

	public FindParkingTask(final GlassActivity activity, final int radius, final Location location) {
		mActivity = activity;
		mLocation = location;
		mRadius = radius;
	}

	@Override
	protected ArrayList<Parking> doInBackground(final Void... params) {
		final ArrayList<Parking> parkingList = new ArrayList<Parking>();
		for (final GreenParking greenParking : ParkingApplication.GREEN_PARKING_LIST) {
			final Location greenParkingLocation = greenParking.getLocation();
			if (greenParkingLocation != null) {
				final float distance = mLocation.distanceTo(greenParkingLocation);
				if (distance < mRadius) {
					parkingList.add(new GreenParking(greenParking, distance));
				}
			}
		}

		for (final LawnParking lawnParking : ParkingApplication.LAWN_PARKING_LIST) {
			final Location lawnParkingLocation = new Location("Lawn Parking");
			lawnParkingLocation.setLatitude(lawnParking.mLatitude);
			lawnParkingLocation.setLongitude(lawnParking.mLongitude);
			final float distance = mLocation.distanceTo(lawnParkingLocation);
			if (distance < mRadius) {
				parkingList.add(new LawnParking(lawnParking, distance));
			}
		}
		
		Collections.sort(parkingList);
		
		return parkingList;
	}

	@Override
	protected void onPostExecute(final ArrayList<Parking> parkingList) {
		if (mActivity.mIsDestroyed) {
			return;
		}
		mActivity.updateUI(parkingList);
	}
}