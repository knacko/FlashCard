package com.SomeRobot.FlashCard;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class DelayedViewPager extends ViewPager {

	private long minTimeBetweenSwipes = 250; //in ms	
	private long timeOfSettle = 0;

	public DelayedViewPager(Context context) {
		super(context);
		setPageListener();
	}

	public DelayedViewPager(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
		setPageListener();
	}	
	
	public void setPageListener() {
		this.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
		    public void onPageScrollStateChanged(int state) {
		    	
		    	if (state == SCROLL_STATE_SETTLING ) {
		    		timeOfSettle = System.currentTimeMillis();
		    	}	    	
		    }

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
		
			}

			@Override
			public void onPageSelected(int position) {
			}
		   
		});
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		if (System.currentTimeMillis() - timeOfSettle > minTimeBetweenSwipes) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (System.currentTimeMillis() - timeOfSettle > minTimeBetweenSwipes) {
			return super.onTouchEvent(event);
		}

		return false;
	}
}