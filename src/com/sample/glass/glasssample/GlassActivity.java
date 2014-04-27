package com.sample.glass.glasssample;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardScrollView;
import com.sample.glass.glasssample.adapters.ParkingAdapter;
import com.sample.glass.glasssample.model.GreenParking;
import com.sample.glass.glasssample.model.LawnParking;
import com.sample.glass.glasssample.model.Parking;
import com.sample.glass.glasssample.tasks.FindParkingTask;
import com.sample.glass.glasssample.tasks.GetParkingTask;
import com.sample.glass.glasssample.utilities.Debug;
import com.sample.glass.glasssample.utilities.LocationHelper;

public class GlassActivity extends Activity implements OnItemClickListener {
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
	private View mResultsContainer;
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
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_glass);
		mProgressTextView = (TextView) findViewById(R.id.activity_glass_progress_text);
		mProgressContainer = findViewById(R.id.activity_glass_progress_container);
		mResultsContainer = findViewById(R.id.activity_glass_results_container);

		mCardScrollView = (CardScrollView) findViewById(R.id.activity_glass_results);
		mCardScrollView.setOnItemClickListener(this);

		mLocationHelper = new LocationHelper(this);
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
			mResultsContainer.setVisibility(View.VISIBLE);
			final ParkingAdapter parkingAdapter = new ParkingAdapter(getApplicationContext());
			parkingAdapter.setParkingList(parkingList);
			mCardScrollView.setAdapter(parkingAdapter);
			mCardScrollView.activate();
			final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audio.playSoundEffect(Sounds.SUCCESS);
			return;
		}

		mProgressContainer.setVisibility(View.VISIBLE);
		mResultsContainer.setVisibility(View.GONE);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audio.playSoundEffect(Sounds.TAP);
		final Parking parking = (Parking) parent.getItemAtPosition(position);
		navigateTo(parking.getLocation());
		if (parking instanceof GreenParking) {
			final GreenParking greenParking = (GreenParking) parking;
			GlassService.launchCard(this, greenParking);
			finish();
		} else {
			final LawnParking lawnParking = (LawnParking) parking;
			GlassService.launchCard(this, lawnParking);
			finish();
		}
	}

	public void navigateTo(final Location location) {
		if (location == null) {
			return;
		}
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		final double latitude = location.getLatitude();
		final double longitude = location.getLongitude();
		intent.setData(Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=d"));
		startActivity(intent);
	}
}
