package com.paad.wifi4us;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.VideoView;

public class MyVideoView extends VideoView{
	private Activity currentActivity;
	private DisplayMetrics metrics;
	public void SetCurrentState(Activity activity, DisplayMetrics met){
		currentActivity = activity;
		metrics = met;
	}
	
	
	public MyVideoView(Context context) {  
		super(context);    
	}

    public MyVideoView(Context context, AttributeSet attrs) {
    	super(context, attrs, 0);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		currentActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		
		setMeasuredDimension(width,height);
	}
}
