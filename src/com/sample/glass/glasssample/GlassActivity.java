package com.sample.glass.glasssample;

import java.util.ArrayList;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollView;
import com.sample.glass.glasssample.adapters.ParkingAdapter;
import com.sample.glass.glasssample.model.Parking;
import com.sample.glass.glasssample.tasks.FindParkingTask;
import com.sample.glass.glasssample.tasks.GetParkingTask;
import com.sample.glass.glasssample.utilities.Debug;
import com.sample.glass.glasssample.utilities.LocationHelper;

public class GlassActivity extends Activity {
	private static final int RADIUS = 1000;
	private static final String MARS_LOCATION = "Mars Location";
	private static final float MARS_LATITUDE = 43.659968f;
	private static final float MARS_LONGITUDE = -79.388934f;
	private static final long GPS_TIMEOUT = 1000 * 5; // 5 seconds

	public boolean mIsDestroyed;
	public boolean mFindingParking;
	private ArrayList<Parking> mParkingList;

	private Location mLocation;
	private Handler mHandler;
	private TextView mProgressTextView;
	private View mProgressContainer;
	private CardScrollView mCardScrollView;
	private LocationHelper mLocationHelper;
	private final Runnable mTimeout = new Runnable() {

		@Override
		public void run() {
			if (mIsDestroyed) {
				return;
			}
			mLocationHelper.stopLocationSearch();
			if (mLocation == null) {
				final Location location = new Location(MARS_LOCATION);
				location.setLatitude(MARS_LATITUDE);
				location.setLongitude(MARS_LONGITUDE);
				setLocation(location);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mHandler = new Handler();

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_glass);
		mProgressTextView = (TextView) findViewById(R.id.activity_glass_progress_text);
		mProgressContainer = findViewById(R.id.activity_glass_progress_container);
		mCardScrollView = (CardScrollView) findViewById(R.id.activity_glass_results);

		mLocationHelper = new LocationHelper(this);
		// Intent intent = new Intent(Intent.ACTION_VIEW);
		// intent.setData(Uri.parse("google.navigation:q=48.649469,-2.02579&mode=d"));
		// startActivity(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getParking();
		mLocationHelper.startLocationSearch();
		updateUI(null);
		mHandler.postDelayed(mTimeout, GPS_TIMEOUT);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationHelper.stopLocationSearch();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIsDestroyed = true;
	}

	private void getParking() {
		if (ParkingApplication.GREEN_PARKING_LIST == null || ParkingApplication.LAWN_PARKING_LIST == null) {
			Debug.log("Getting lists");
			final GetParkingTask parkingTask = new GetParkingTask(this);
			parkingTask.execute((Void) null);
		} else {
			Debug.log("LAWN_PARKING_LIST: " + ParkingApplication.LAWN_PARKING_LIST.size());
			Debug.log("GREEN_PARKING_LIST: " + ParkingApplication.GREEN_PARKING_LIST.size());
		}
	}

	public void updateUI(final ArrayList<Parking> parkingList) {
		if (mParkingList == null) {
			mParkingList = parkingList;
		}
		final boolean foundParking = mParkingList != null;
		if (foundParking) {
			mFindingParking = false;
			mProgressContainer.setVisibility(View.GONE);
			mCardScrollView.setVisibility(View.VISIBLE);
			final ParkingAdapter parkingAdapter = new ParkingAdapter(getApplicationContext());
			parkingAdapter.setParkingList(parkingList);
			mCardScrollView.setAdapter(parkingAdapter);
			mCardScrollView.activate();
			return;
		}

		mProgressContainer.setVisibility(View.VISIBLE);
		mCardScrollView.setVisibility(View.GONE);
		final boolean hasLocation = mLocation != null;
		final boolean hasParkingLists = ParkingApplication.LAWN_PARKING_LIST != null && ParkingApplication.GREEN_PARKING_LIST != null;
		if (!hasParkingLists) {
			mProgressTextView.setText(R.string.activity_glass_progress_getting_parking);
		} else if (!hasLocation) {
			mProgressTextView.setText(R.string.activity_glass_progress_finding_location);
		} else {
			mProgressTextView.setText(R.string.activity_glass_progress_finding_parking);
		}
	}

	public void setLocation(final Location location) {
		mLocation = location;
		findParking();
	}

	public void findParking() {
		updateUI(null);
		final boolean foundParking = mParkingList != null;
		if (foundParking || mFindingParking || mLocation == null || ParkingApplication.GREEN_PARKING_LIST == null || ParkingApplication.LAWN_PARKING_LIST == null) {
			return;
		}
		mFindingParking = true;
		final FindParkingTask findParkingTask = new FindParkingTask(this, RADIUS, mLocation);
		findParkingTask.execute((Void) null);
	}
}
