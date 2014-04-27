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
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
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
	private boolean mHaveWakeLock;
	private boolean mIsError;
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
			releaseLock();
			mLocationHelper.stopLocationSearch();
			if (mLocation == null) {
				final Location location = new Location(MARS_LOCATION);
				location.setLatitude(MARS_LATITUDE);
				location.setLongitude(MARS_LONGITUDE);
				setLocation(location);
			}
		}
	};

	private synchronized void releaseLock() {
		if (!mHaveWakeLock) {
			return;
		}
		mHaveWakeLock = false;
		mWakeLock.release();
	}

	private synchronized void acquireLock() {
		if (mHaveWakeLock) {
			return;
		}
		mWakeLock.acquire();
		mHaveWakeLock = true;
	}

	private WakeLock mWakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mHandler = new Handler();
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_glass);
		mProgressTextView = (TextView) findViewById(R.id.activity_glass_progress_text);
		mProgressContainer = findViewById(R.id.activity_glass_progress_container);
		mResultsContainer = findViewById(R.id.activity_glass_results_container);

		mCardScrollView = (CardScrollView) findViewById(R.id.activity_glass_results);
		mCardScrollView.setOnItemClickListener(this);

		mLocationHelper = new LocationHelper(this);

		final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GoogleGlassParking");
		acquireLock();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationHelper.startLocationSearch();
		updateUI(null);
		mHandler.postDelayed(mTimeout, GPS_TIMEOUT);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationHelper.stopLocationSearch();
		releaseLock();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIsDestroyed = true;
	}
	
	public void setErrorUI(){
		mIsError = true;
		
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
		if (!hasLocation) {
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
		if (foundParking || mFindingParking || mLocation == null) {
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
		if (parking instanceof GreenParking) {
			final GreenParking greenParking = (GreenParking) parking;
			GlassService.launchCard(this, greenParking);
			navigateTo(greenParking.getLocation(), greenParking.mAddress);
			finish();
		} else {
			final LawnParking lawnParking = (LawnParking) parking;
			GlassService.launchCard(this, lawnParking);
			navigateTo(lawnParking.getLocation(), lawnParking.mAddress);
			finish();
		}
	}

	public void navigateTo(final Location location, final String address) {
		if (location == null) {
			return;
		}
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		final double latitude = location.getLatitude();
		final double longitude = location.getLongitude();
		final String uri = "google.navigation:q=" + latitude + "," + longitude + "&mode=d&title=" + address.replaceAll("\\s", "+");
		Debug.log("uri: " + uri);
		intent.setData(Uri.parse(uri));
		startActivity(intent);
	}
}
