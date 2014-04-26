package com.sample.glass.glasssample;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import model.CarParks;
import model.GreenParking;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.glass.widget.CardScrollView;
import com.google.gson.Gson;
import com.sample.glass.glasssample.utilities.Debug;

public class GlassActivity extends Activity {

	private static final Gson GSON = new Gson();
	public static ArrayList<GreenParking> GREEN_PARKING_LIST;
	private CardScrollView mCardScrollView;
	private boolean mIsDestroyed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Debug.setIsDebug(true);
		mCardScrollView = new CardScrollView(this);
		mCardScrollView.setAdapter(new ProductsAdapter(getApplicationContext(), R.layout.list_item_picture));
		mCardScrollView.activate();
		setContentView(mCardScrollView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (GREEN_PARKING_LIST != null) {
			final GreenParkingTask greenParkingTask = new GreenParkingTask();
			greenParkingTask.execute((Void) null);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCardScrollView = null;
		mIsDestroyed = true;
	}

	private class GreenParkingTask extends AsyncTask<Void, Void, CarParks> {
		private static final String UTF8 = "UTF8";

		@Override
		protected CarParks doInBackground(final Void... params) {
			Debug.log();
			InputStream inputStream = null;
			InputStreamReader inputStreamReader = null;
			try {
				inputStream = getResources().openRawResource(R.raw.green_parking);
				inputStreamReader = new InputStreamReader(inputStream, UTF8);
				final CarParks carParks = GSON.fromJson(inputStreamReader, CarParks.class);
				return carParks;
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
		protected void onPostExecute(final CarParks result) {
			if (mIsDestroyed) {
				return;
			}
			if (result != null) {
				final ArrayList<GreenParking> greenParkingList = result.mCarPark;
				if (greenParkingList != null) {
					GREEN_PARKING_LIST = greenParkingList;
				}
			}
		}

	}
}
