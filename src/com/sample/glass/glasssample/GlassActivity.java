package com.sample.glass.glasssample;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.CarParks;
import model.GreenParking;
import model.LawnParking;
import model.LawnParkingResults;
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
	public static ArrayList<LawnParking> LAWN_PARKING_LIST;
	private CardScrollView mCardScrollView;
	private boolean mIsDestroyed;
	LocationManager locationManager;
	private Location mLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Debug.setIsDebug(true);
		// Intent intent = new Intent(Intent.ACTION_VIEW);
		// intent.setData(Uri.parse("google.navigation:q=48.649469,-2.02579&mode=d"));
		// startActivity(intent);
		super.onCreate(savedInstanceState);
		mCardScrollView = new CardScrollView(this);
		mCardScrollView.setAdapter(new ProductsAdapter(getApplicationContext(), R.layout.list_item_picture));
		mCardScrollView.activate();
		setContentView(mCardScrollView);

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		startLocationSearch();
	}

	private void startLocationSearch() {
		Debug.log("");
		final Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(true);
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(criteria, true /* enabledOnly */);

		float smallestAccuracy = Float.MAX_VALUE;
		Location smallestLocation = null;
		for (String provider : providers) {
			locationManager.requestLocationUpdates(provider, 0, 0, this);
			final Location location = locationManager.getLastKnownLocation(provider);
			Debug.log("provider: " + provider + " location: " + location);
			if (location != null) {
				final float accuracy = location.getAccuracy();
				if (smallestAccuracy < accuracy) {
					smallestLocation = location;
				}
			}
		}
		Debug.log("smallestLocation: " + smallestLocation);
		if (smallestLocation != null) {
			setLocation(smallestLocation);
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
		stopLocationSearch();
	}
	
	private void stopLocationSearch() {
		Debug.log("");
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (GREEN_PARKING_LIST == null || LAWN_PARKING_LIST == null) {
			Debug.log("Getting lists");
			final ParkingTask parkingTask = new ParkingTask();
			parkingTask.execute((Void) null);
		} else {
			Debug.log("LAWN_PARKING_LIST: " + LAWN_PARKING_LIST.size());
			Debug.log("GREEN_PARKING_LIST: " + GREEN_PARKING_LIST.size());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCardScrollView = null;
		mIsDestroyed = true;
	}

	private class ParkingTask extends AsyncTask<Void, Void, Void> {
		private static final String UTF8 = "UTF8";

		@Override
		protected Void doInBackground(final Void... params) {
			InputStream inputStream = null;
			InputStreamReader inputStreamReader = null;
			try {
				inputStream = getResources().openRawResource(R.raw.green_parking);
				inputStreamReader = new InputStreamReader(inputStream, UTF8);
				final CarParks carParks = GSON.fromJson(inputStreamReader, CarParks.class);
				if (carParks != null) {
					final ArrayList<GreenParking> greenParkingList = carParks.mCarPark;
					if (greenParkingList != null) {
						GREEN_PARKING_LIST = greenParkingList;
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
				inputStream = getResources().openRawResource(R.raw.residential_addresses_3_2501);
				inputStreamReader = new InputStreamReader(inputStream, UTF8);
				LAWN_PARKING_LIST = GSON.fromJson(inputStreamReader, LawnParkingResults.class);
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
			if (mIsDestroyed) {
				return;
			}
			Debug.log("LAWN_PARKING_LIST: " + LAWN_PARKING_LIST.size());
			Debug.log("GREEN_PARKING_LIST: " + GREEN_PARKING_LIST.size());
			if (mLocation != null) {
				findParking(mLocation);
			}
		}
	}

	@Override
	public void onLocationChanged(final Location location) {
		Debug.log("location: " + location );
		setLocation(location);
	}

	private void setLocation(final Location location) {
		Debug.log("location: " + location );
		mLocation = location;
		findParking(location);
	}

	private void findParking(final Location location) {
		if (GREEN_PARKING_LIST == null || LAWN_PARKING_LIST == null) {
			return;
		}
		final ArrayList<GreenParking> greenParkings = new ArrayList<GreenParking>();
		for (final GreenParking greenParking : GREEN_PARKING_LIST) {
			final Location greenParkingLocation = new Location("Green Parking");
			greenParkingLocation.setLatitude(Double.parseDouble(greenParking.mLat));
			greenParkingLocation.setLongitude(Double.parseDouble(greenParking.mLong));
			final float distance = location.distanceTo(greenParkingLocation);
			if (distance < 50) {
				greenParkings.add(greenParking);
			}
		}

		final ArrayList<LawnParking> lawnParkings = new ArrayList<LawnParking>();
		for (final LawnParking lawnParking : LAWN_PARKING_LIST) {
			final Location greenParkingLocation = new Location("Lawn Parking");
			greenParkingLocation.setLatitude(lawnParking.mLatitude);
			greenParkingLocation.setLongitude(lawnParking.mLongitude);
			final float distance = location.distanceTo(greenParkingLocation);
			if (distance < 50) {
				lawnParkings.add(lawnParking);
			}
		}
		Debug.log("greenParkings: " + greenParkings.size());
		Debug.log("lawnParkings: " + lawnParkings.size());
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Debug.log("provider: " + provider );
	}

	@Override
	public void onProviderEnabled(String provider) {
		Debug.log("provider: " + provider );
	}

	@Override
	public void onProviderDisabled(String provider) {
		Debug.log("provider: " + provider );
	}
}
