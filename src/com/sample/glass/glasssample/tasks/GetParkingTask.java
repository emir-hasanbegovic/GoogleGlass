package com.sample.glass.glasssample.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.os.AsyncTask;

import com.sample.glass.glasssample.GlassActivity;
import com.sample.glass.glasssample.ParkingApplication;
import com.sample.glass.glasssample.R;
import com.sample.glass.glasssample.model.CarParks;
import com.sample.glass.glasssample.model.GreenParking;
import com.sample.glass.glasssample.model.LawnParkingResults;
import com.sample.glass.glasssample.utilities.Debug;

public class GetParkingTask extends AsyncTask<Void, Void, Void> {
	private static final String UTF8 = "UTF8";
	private final GlassActivity mActivity;

	public GetParkingTask(final GlassActivity activity) {
		mActivity = activity;
	}

	@Override
	protected Void doInBackground(final Void... params) {
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		try {
			inputStream = mActivity.getResources().openRawResource(R.raw.green_parking);
			inputStreamReader = new InputStreamReader(inputStream, UTF8);
			final CarParks carParks = ParkingApplication.GSON.fromJson(inputStreamReader, CarParks.class);
			if (carParks != null) {
				final ArrayList<GreenParking> greenParkingList = carParks.mCarPark;
				
				if (greenParkingList != null) {
					ParkingApplication.GREEN_PARKING_LIST = greenParkingList;
				} else {
					Debug.log("greenParkingList: " + null);
				}
			}
		} catch (final UnsupportedEncodingException unsupportedEncodingException) {
			Debug.log(unsupportedEncodingException.getMessage());
		} finally {
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (final IOException ioException) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (final IOException ioException) {
				}
			}
		}
		inputStream = null;
		inputStreamReader = null;
		try {
			inputStream = mActivity.getResources().openRawResource(R.raw.lawn_parking);
			inputStreamReader = new InputStreamReader(inputStream, UTF8);
			ParkingApplication.LAWN_PARKING_LIST = ParkingApplication.GSON.fromJson(inputStreamReader, LawnParkingResults.class);
			if (ParkingApplication.LAWN_PARKING_LIST == null){
				Debug.log("lawnParkingList: " + null);	
			}
			
		} catch (final UnsupportedEncodingException unsupportedEncodingException) {
			Debug.log(unsupportedEncodingException.getMessage());
		} finally {
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (final IOException ioException) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (final IOException ioException) {
				}
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		if (mActivity.mIsDestroyed) {
			return;
		}
		mActivity.findParking();
	}
}