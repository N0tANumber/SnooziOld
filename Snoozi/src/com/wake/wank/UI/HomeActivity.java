package com.wake.wank.UI;


import com.wake.wank.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

public class HomeActivity extends FragmentActivity {
	
	/**
     * The number of pages to show
     */
    private static int NUM_PAGES = 3;
    private static final int ALARM_POSITION = 0;
    private static final int FEED_POSITION = 1;
    private static final int GALLERY_POSITION = 2;
     private static final int VIDEO_POSITION = 3;
     
     

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
	private boolean mShowingDetail;
	
	private FragmentClock mAlarmView = null;
	private FragmentFeed mFeedView = null;
	private FragmentGallery mGalleryView = null;
	private FragmentDetail mDetailView = null;
	//private ActionBar actionBar = null;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
       // requestWindowFeature(Window.FEATURE_ACTION_BAR);
       // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_home);
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);
        
		
	    
	    
	    
	    
        // Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());
        //mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				// When swiping between pages, select the
                // corresponding tab.
				if(position < getActionBar().getTabCount())
					getActionBar().setSelectedNavigationItem(position);
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				if(state == ViewPager.SCROLL_STATE_IDLE)
				{
					if(mPager.getCurrentItem() < VIDEO_POSITION)
					{
						if(mShowingDetail)
			        	{
							mShowingDetail = false;
							mPager.removeView(mDetailView.getView());
							mPagerAdapter.notifyDataSetChanged();
							
						}
					}
				}
				
			}
		});
        
        
        
        
        final ActionBar actionBar = getActionBar();
		// Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    
	    // Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
	      
	    };

	    // Add 3 tabs, specifying the tab's text and TabListener
	    for (int i = 0; i < 3; i++) {
	        actionBar.addTab(
	                actionBar.newTab()
	                        .setText(getResources().getStringArray(R.array.hometabs)[i])
	                        .setTabListener(tabListener));
	    }
	    
	    
	    
    }
    

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else 
         {   	 
            // Otherwise, select the previous step.
        	mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            
        }
    }
    
    
    
    public void showDetailView()
    {
    	mShowingDetail = true;
    	mPager.setCurrentItem(VIDEO_POSITION);
    }
    

    
    
    /**
     * A simple pager adapter that represents 3 Fragments objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	Fragment theResult = null;
        	switch (position) {
        	case ALARM_POSITION:
        		if(mAlarmView == null)
        		  mAlarmView = new FragmentClock();
        		theResult = mAlarmView;
        		break;
        	case FEED_POSITION:
        		if(mFeedView == null)
        			mFeedView = new FragmentFeed();
          		theResult = mFeedView;
          		break;
        	case GALLERY_POSITION:
        		if(mGalleryView == null)
        			mGalleryView = new FragmentGallery();
          		theResult = mGalleryView;
          		break;
        	case VIDEO_POSITION:
        		if(mDetailView == null)
        			mDetailView = new FragmentDetail();
          		theResult = mDetailView;
          		break;
			
			}
        	
           return theResult;
        }

        @Override
        public int getCount() {
        	if(mShowingDetail)
        		return NUM_PAGES+1;
        	else
        		return NUM_PAGES;
        }
        @Override
        public int getItemPosition(Object object){
        	if(object == mAlarmView)
        		return PagerAdapter.POSITION_UNCHANGED;
        	else if(object == mFeedView)
        		return PagerAdapter.POSITION_UNCHANGED;
        	else if(object == mGalleryView)
        		return PagerAdapter.POSITION_UNCHANGED;
        	else
        		return PagerAdapter.POSITION_NONE;
            	
        		
        }
    }
    
    
    
    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
    
    
    
    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                        (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }

		
    }
}
