package com.sample.glass.glasssample;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.squareup.picasso.Picasso;

/**
 * Created by Emir Hasanbegovic on 2014-03-21.
 */
public class ProductsAdapter extends CardScrollAdapter {

	private final int mLayoutResourceId;
	private Context mContext;

	public ProductsAdapter(final Context context, final int layoutResourceId) {
		super();
		mLayoutResourceId = layoutResourceId;
		mPictures = getPictures();
		mContext = context;
	}

	public static List<Picture> getPictures() {
		final List<Picture> pictures = new ArrayList<Picture>();
		pictures.add(new Picture("https://i.imgur.com/8LOZwbE.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/EfLvmlO.png", "Cheetahs"));
		pictures.add(new Picture("https://i.imgur.com/VfH1siL.png", "Cloud Break"));
		pictures.add(new Picture("https://i.imgur.com/rmU8E53.png", "Lighthouse"));
		pictures.add(new Picture("https://i.imgur.com/qZCCdFW.png", "Uluru"));
		pictures.add(new Picture("https://i.imgur.com/zkt4IEl.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/xL9BSy3.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/lYQPhnY.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/5tM1vxy.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/lUbn6U6.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/fk4l1QY.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/wk49eIK.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/qwz8VUx.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/XR8Dc2D.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/lGBUYjK.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/8AgQWCh.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/YMwlUy3.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/MyWKsZf.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/gTghrcj.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/ePH4nP4.png", "National photo contest"));
		pictures.add(new Picture("https://i.imgur.com/KVJuX7O.png", "National photo contest"));
		return pictures;
	}

	private List<Picture> mPictures = new ArrayList<Picture>();

	@Override
	public int getCount() {
		return mPictures.size();
	}

	@Override
	public Object getItem(final int position) {
		return mPictures.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	private View getView(final Context context, final View convertView, final ViewGroup viewGroup) {
		if (convertView == null) {
			final LayoutInflater layoutInflater = LayoutInflater.from(context);
			final View view = layoutInflater.inflate(mLayoutResourceId, viewGroup, false);
			return view;
		}
		return convertView;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final View view = getView(mContext, convertView, parent);
		final Picture picture = (Picture) getItem(position);

		final ImageView imageView = (ImageView) view.findViewById(R.id.list_item_picture_image_view);
		imageView.setImageBitmap(null);
		Picasso.with(mContext).load(picture.mUrl).into(imageView);

		final TextView textView = (TextView) view.findViewById(R.id.list_item_picture_text_view);
		textView.setText(picture.mCaption);
		return view;
	}

	@Override
	public int getPosition(Object arg0) {
		for (int position = 0; position < mPictures.size(); position++) {
			if (mPictures.get(position) == arg0) {
				return position;
			}
		}
		return 0;
	}

}
