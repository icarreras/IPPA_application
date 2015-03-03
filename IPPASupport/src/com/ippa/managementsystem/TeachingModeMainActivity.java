package com.ippa.managementsystem;

import com.ippa.R;
import com.ippa.bluetooth.Constants;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

public class TeachingModeMainActivity extends FragmentActivity implements ActionBar.TabListener, 
																		ViewPager.OnPageChangeListener{

	// Use custom PageAdapter to get correct Fragment based on position 
	GestureOptionsCollectionPageAdapter mPageAdapter;
	ViewPager mViewPager;
	
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_teaching_mode);
    	
    	final ActionBar actionBar = getActionBar();
    	
    	// ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
    	mPageAdapter = new GestureOptionsCollectionPageAdapter(getSupportFragmentManager());
    	
    	// Set the adapter of the view pager to the custom adapter 
    	mViewPager = (ViewPager)findViewById(R.id.pager);
    	mViewPager.setAdapter(mPageAdapter);
    	mViewPager.setOnPageChangeListener(this);
    	
    	// Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Add tabs
        for(int i=0; i< mPageAdapter.getCount(); i++)
        {
        	// Get tab text from the title of the fragment
        	actionBar.addTab(actionBar.newTab()
        			.setText(mPageAdapter.getPageTitle(i))
        			.setTabListener(this));
        }
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Nothing for now, possible refresh (recreation of the fragment)
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// When the tab is selected, switch to the
        // corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// not used
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		// When swiping between pages, select the
        // corresponding tab.
        getActionBar().setSelectedNavigationItem(position);	
	}
	/*
	/**
     * The Handler that gets information back from the BluetoothService
     
    private final Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Constants.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case Constants.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case Constants.STATE_LISTEN:
                        case Constants.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != UIActivity) {
                        Toast.makeText(UIActivity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != UIActivity) {
                        Toast.makeText(UIActivity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }; */

	// Custom Page Adapter Class
	public class GestureOptionsCollectionPageAdapter extends FragmentPagerAdapter {

		final int NUMBER_OF_GESTURE_PAGES = 2;
		
		public GestureOptionsCollectionPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position){
				case 0:
					return new  DemoGestureFragment();
				case 1:
					return new CreateGestureFragment();
				default:
					return null;
			}
		}

		@Override
		public int getCount() {
			return NUMBER_OF_GESTURE_PAGES;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			switch(position){
				case 0:
					return getString(R.string.title_fragment_demo_gesture);
				case 1:
					return getString(R.string.title_fragment_create_gesture) ;
				default:
					return null;
			}
		}

	} // end of class GestureOptionsCollectionPageAdapter 

} // end of class TeachingModeMainActivity


