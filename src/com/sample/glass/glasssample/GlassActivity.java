package com.sample.glass.glasssample;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.CarParks;
import model.GreenParking;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.glass.widget.CardScrollView;
import com.google.gson.Gson;
import com.sample.glass.glasssample.utilities.Debug;

public class GlassActivity extends Activity implements LocationListener {

	private static final Gson GSON = new Gson();
	public static ArrayList<GreenParking> GREEN_PARKING_LIST;
	private CardScrollView mCardScrollView;
	private boolean mIsDestroyed;
	LocationManager locationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCardScrollView = new CardScrollView(this);
		mCardScrollView.setAdapter(new ProductsAdapter(getApplicationContext(), R.layout.list_item_picture));
		mCardScrollView.activate();
		setContentView(mCardScrollView);
		
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(
                criteria, true /* enabledOnly */);

        for (String provider : providers) {
            locationManager.requestLocationUpdates(provider, 0,
                    0, this);
        }
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

	@Override
	public void onLocationChanged(Location location) {
		Log.d("location provider: ", location.toString());
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
