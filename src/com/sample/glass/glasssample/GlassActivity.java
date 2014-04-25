package com.sample.glass.glasssample;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.media.CameraManager;
import com.google.android.glass.widget.CardScrollView;

public class GlassActivity extends Activity implements OnItemClickListener {

	private static final int SPEECH_REQUEST = 0;
	private static final int TAKE_PICTURE_REQUEST = 1;

	private CardScrollView mCardScrollView;
	private View mSelectedView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glass);
		mCardScrollView = (CardScrollView) findViewById(R.id.activity_glass_card_scroll_view);
		mCardScrollView.setAdapter(new ProductsAdapter(getApplicationContext(), R.layout.list_item_picture));
		mCardScrollView.setOnItemClickListener(this);	
	}

	@Override
	protected void onStart() {
		super.onStart();
		mCardScrollView.activate();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mCardScrollView.deactivate();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCardScrollView = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("Glass", "click id: " + id);
		mSelectedView = view;
		takePicture();
	}

	private void displaySpeechRecognizer() {
		final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		Log.d("GlassTest", "displaySpeechRecognizer");
		startActivityForResult(intent, SPEECH_REQUEST);
	}

	private void takePicture() {
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Log.d("GlassTest", "takePicture");
		startActivityForResult(intent, TAKE_PICTURE_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("GlassTest", "onActivityResult");
		if (resultCode == RESULT_OK) {
			if (requestCode == SPEECH_REQUEST) {
				final List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				if (results != null && !results.isEmpty() && mSelectedView != null) {
					Log.d("GlassTest", "speechRequest");
					final TextView textView = (TextView) mSelectedView.findViewById(R.id.list_item_picture_text_view);
					textView.setText(results.get(0));
				}
			} else if (requestCode == TAKE_PICTURE_REQUEST) {
				if (data != null && mSelectedView != null) {
					Log.d("GlassTest", "pictureRequest");
					final String picturePath = data.getStringExtra(CameraManager.EXTRA_THUMBNAIL_FILE_PATH);
					final ImageView imageView = (ImageView) mSelectedView.findViewById(R.id.list_item_picture_image_view);
					processPictureWhenReady(picturePath, imageView);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void processPictureWhenReady(final String picturePath, final ImageView imageView) {
		Log.d("GlassTest", "picturePath: " + picturePath);
	    final File pictureFile = new File(picturePath);
	    Log.d("GlassTest", "processPictureWhenReady");
	    if (pictureFile.exists()) {
	    	Log.d("GlassTest", "fileExists");
	    	Bitmap myBitmap = BitmapFactory.decodeFile(picturePath);
	    	imageView.setImageBitmap(myBitmap);
	    	displaySpeechRecognizer();
	    } else {
	    	Log.d("GlassTest", "startedWatching");
	        final File parentDirectory = pictureFile.getParentFile();
	        FileObserver observer = new FileObserver(parentDirectory.getPath()) {
	            private boolean isFileWritten = false;

	            @Override
	            public void onEvent(int event, String path) {
	                if (!isFileWritten) {
	                	Log.d("GlassTest", "onEvent: " + event + ", path: " + path);
	                    File affectedFile = new File(parentDirectory, path);
	                    Log.d("GlassTest", "affectedFile: " + affectedFile + ", pictureFile: " + pictureFile);
	                    isFileWritten = (event == FileObserver.CLOSE_WRITE && affectedFile.equals(pictureFile));

	                    if (isFileWritten) {
	                    	Log.d("GlassTest", "isFileWritten");
	                        stopWatching();
	                        runOnUiThread(new Runnable() {
	                            @Override
	                            public void run() {
	                            	Log.d("GlassTest", "runOnUiThread");
	                                processPictureWhenReady(picturePath, imageView);
	                            }
	                        });
	                    }
	                }
	            }
	        };
	        observer.startWatching();
	    }
	}
}
