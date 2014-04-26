package com.sample.glass.glasssample;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.sample.glass.glasssample.utilities.Debug;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class GlassService extends Service {
	private static final String LIVE_CARD_ID = "parking";
	private LiveCard mLiveCard;
	
    @Override
    public void onCreate()
    {
    	Debug.setIsDebug(true);
        super.onCreate();
    }
    
	@Override
    public IBinder onBind(Intent intent)
    {
		publishCard(getApplicationContext());
        return null;
    }
	
	 @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	 publishCard(getApplicationContext());

        return START_STICKY;
    }
	 
	 private void publishCard(Context context)
    {
        if (mLiveCard == null) {
        	RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
        		      R.layout.activity_glass);
	    	mLiveCard = new LiveCard(this, LIVE_CARD_ID);
	    	mLiveCard.setViews(remoteViews);
            Intent intent = new Intent(context, GlassActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(context, 0, intent, 0));
            mLiveCard.publish(LiveCard.PublishMode.REVEAL);
        } else {
            // Card is already published.
            mLiveCard.unpublish();
            return;
        }
    }
}
