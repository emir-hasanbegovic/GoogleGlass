package com.sample.glass.glasssample.tasks;

import java.util.ArrayList;

import android.location.Location;
import android.os.AsyncTask;

import com.sample.glass.glasssample.GlassActivity;
import com.sample.glass.glasssample.ParkingApplication;
import com.sample.glass.glasssample.model.GreenParking;
import com.sample.glass.glasssample.model.LawnParking;

public class FindParkingTask extends AsyncTask<Void, Void, Void> {
	private final GlassActivity mActivity;
	private final Location mLocation;
	private final int mRadius;
	private final ArrayList<GreenParking> mGreenParkings = new ArrayList<GreenParking>();
	private final ArrayList<LawnParking> mLawnParkings = new ArrayList<LawnParking>();

	public FindParkingTask(final GlassActivity activity, final int radius, final Location location) {
		mActivity = activity;
		mLocation = location;
		mRadius = radius;
	}

	@Override
	protected Void doInBackground(final Void... params) {
		for (final GreenParking greenParking : ParkingApplication.GREEN_PARKING_LIST) {
			final Location greenParkingLocation = new Location("Green Parking");
			if (greenParking.mLat != null && greenParking.mLong != null) {
				greenParkingLocation.setLatitude(Double.parseDouble(greenParking.mLat));
				greenParkingLocation.setLongitude(Double.parseDouble(greenParking.mLong));
				final float distance = mLocation.distanceTo(greenParkingLocation);
				if (distance < mRadius) {
					mGreenParkings.add(greenParking);
				}
			}
		}

		for (final LawnParking lawnParking : ParkingApplication.LAWN_PARKING_LIST) {
			final Location lawnParkingLocation = new Location("Lawn Parking");
			lawnParkingLocation.setLatitude(lawnParking.mLatitude);
			lawnParkingLocation.setLongitude(lawnParking.mLongitude);
			final float distance = mLocation.distanceTo(lawnParkingLocation);
			if (distance < mRadius) {
				mLawnParkings.add(lawnParking);
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		if (mActivity.mIsDestroyed) {
			return;
		}
		mActivity.updateProgress(mGreenParkings, mLawnParkings);
	}
}