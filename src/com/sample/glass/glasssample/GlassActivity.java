package com.sample.glass.glasssample;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.glass.widget.CardScrollView;

public class GlassActivity extends Activity {

	private CardScrollView mCardScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCardScrollView = new CardScrollView(this);
		mCardScrollView.setAdapter(new ProductsAdapter(getApplicationContext(), R.layout.list_item_picture));
		mCardScrollView.activate();
		setContentView(mCardScrollView);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCardScrollView = null;
	}
}
