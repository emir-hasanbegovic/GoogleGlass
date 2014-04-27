package com.sample.glass.glasssample.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.sample.glass.glasssample.R;
import com.sample.glass.glasssample.model.GreenParking;
import com.sample.glass.glasssample.model.LawnParking;
import com.sample.glass.glasssample.model.Parking;

/**
 * Created by Emir Hasanbegovic on 2014-03-21.
 */
public class ParkingAdapter extends CardScrollAdapter {

	private enum Types {
		greenParking, lawnParking
	}

	private Context mContext;

	public ParkingAdapter(final Context context) {
		super();
		mContext = context;
	}

	public void setParkingList(final ArrayList<Parking> parkingList) {
		mParkingList = parkingList;
	}

	@Override
	public int getViewTypeCount() {
		return Types.values().length;
	}

	@Override
	public int getItemViewType(int position) {
		final Parking parking = (Parking) getItem(position);
		if (parking instanceof GreenParking) {
			return Types.greenParking.ordinal();
		}
		return Types.lawnParking.ordinal();
	}

	private List<Parking> mParkingList = new ArrayList<Parking>();

	@Override
	public int getCount() {
		if (mParkingList == null) {
			return 0;
		}
		return mParkingList.size();
	}

	@Override
	public Object getItem(final int position) {
		return mParkingList.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	private View getView(final Context context, final Types type, final View convertView, final ViewGroup viewGroup) {
		if (convertView == null) {
			final LayoutInflater layoutInflater = LayoutInflater.from(context);
			final int resourceId = getResourceId(type);
			final View view = layoutInflater.inflate(resourceId, viewGroup, false);
			return view;
		}
		return convertView;
	}

	private int getResourceId(final Types type) {
		switch (type) {
		case greenParking:
			return R.layout.list_item_green_parking;
		case lawnParking:
		default:
			return R.layout.list_item_lawn_parking;
		}
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final int itemViewType = getItemViewType(position);
		final Types type = Types.values()[itemViewType];
		final View view = getView(mContext, type, convertView, parent);
		final Parking parking = (Parking) getItem(position);

		return bind(view, type, parking);

	}

	private View bind(final View view, final Types type, final Parking parking) {
		switch (type) {
		case greenParking: {
			final GreenParking greenParking = (GreenParking) parking;

			final TextView addressTextView = (TextView) view.findViewById(R.id.list_item_green_parking_address);
			addressTextView.setText(greenParking.mAddress);

			final TextView distanceTextView = (TextView) view.findViewById(R.id.list_item_green_parking_distance);
			final float distanceInKm = getDistanceInKm(greenParking.mDistance);
			final String distance = String.format(Parking.DISTANCE, distanceInKm);
			distanceTextView.setText(distance);

			final TextView priceTextView = (TextView) view.findViewById(R.id.list_item_green_parking_price);
			final String price = String.format(Parking.PRICE, greenParking.mRateHalfHour);
			priceTextView.setText(price);
			break;
		}
		case lawnParking:
		default:
			final LawnParking lawnParking = (LawnParking) parking;
			final TextView addressTextView = (TextView) view.findViewById(R.id.list_item_lawn_parking_address);
			addressTextView.setText(lawnParking.mAddress);

			final TextView distanceTextView = (TextView) view.findViewById(R.id.list_item_lawn_parking_distance);
			final float distanceInKm = getDistanceInKm(lawnParking.mDistance);
			final String distance = String.format(Parking.DISTANCE, distanceInKm);
			distanceTextView.setText(distance);
		}
		return view;

	}

	private float getDistanceInKm(final float distanceInM) {
		return distanceInM / 1000f;
	}

	@Override
	public int getPosition(Object arg0) {
		for (int position = 0; position < mParkingList.size(); position++) {
			if (mParkingList.get(position) == arg0) {
				return position;
			}
		}
		return 0;
	}

}