package com.ippa.managementsystem;

import java.util.ArrayList;

import com.ippa.R;
import com.ippa.bluetooth.Constants;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class TeachingModeMainActivity extends FragmentActivity implements ActionBar.TabListener, 
																		ViewPager.OnPageChangeListener{

	private final String TAG = "Teaching Mode Activity";
	
	// Use custom PageAdapter to get correct Fragment based on position 
	private GestureOptionsCollectionPageAdapter mPageAdapter;
	private ViewPager mViewPager;
	private TextView m_bluetoothStatus;
	
	// List of loaded/modified gestures
	protected ArrayList<Gesture> m_inMobileGesture;
	protected ArrayList<Gesture> m_inArmGesture;
	
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_teaching_mode);
    	
    	final ActionBar actionBar = getActionBar();	
    	m_bluetoothStatus = (TextView)findViewById(R.id.bt_connection_status_view);

    	
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
    	
        
        // GESTURE LOADING

    	m_inMobileGesture = new ArrayList<Gesture>();
    	m_inArmGesture = new ArrayList<Gesture>();
    	
    	// TODO: Load the gestures stored in the phone
    	// TODO: Load the gestures stored in the arm
    	Gesture g1 = new Gesture();
    	g1.setGestureName("Gesture1");
    	Gesture g2 = new Gesture();
    	g2.setGestureName("GestureInArm2");
    	m_inMobileGesture.add(g1);
    	m_inArmGesture.add(g2);
    	
    	
        
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
	
	@Override
	public void onPause()
	{
		super.onPause();
		// save changes that need to be made to the gesture file in the system
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// inform system that teaching mode is over
		// do not disconnect
	}
	
    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence status, int color) 
    {
    	m_bluetoothStatus.setText(status);
    	m_bluetoothStatus.setTextColor(color);
    }
     
    private final Handler m_Handler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Constants.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to), Color.GREEN);
                            break;
                        case Constants.STATE_CONNECTING:
                            setStatus(getString(R.string.title_connecting), Color.YELLOW);
                            break;
                        case Constants.STATE_NONE:
                            setStatus(getString(R.string.title_not_connected), Color.RED);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != TeachingModeMainActivity.this) {
                        Toast.makeText(TeachingModeMainActivity.this, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != TeachingModeMainActivity.this) {
                        Toast.makeText(TeachingModeMainActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }; 
	
	public ArrayList<Gesture> getGesturesInMobile()
	{
		return m_inMobileGesture;
	}
	
	public ArrayList<Gesture> getGesturesInArm()
	{
		return m_inArmGesture;
	}
	
	public void addGestureToMobile(Gesture gesture)
	{
		m_inMobileGesture.add(gesture);
	}
	
	public boolean deleteGestureInMobile(int position)
	{
		try
		{
			m_inMobileGesture.remove(position);
			return true;
		}
		catch(IndexOutOfBoundsException e)
		{
			Log.e(TAG,"The gesture index to delete is out of bounds. index = " + position);
			return false;
		}
	}
	
	public void addGestureToArm(Gesture gesture)
	{
		// TODO: must be sent to the arm
		m_inArmGesture.add(gesture);
	}
	
	public boolean deleteGestureInArm(int position)
	{
		try
		{
			// TODO: must send the request to the arm
			// Notes: use the position in the gesture -> built in the package interface
			m_inArmGesture.remove(position);
			return true;
		}
		catch(IndexOutOfBoundsException e)
		{
			Log.e(TAG,"The gesture index to delete is out of bounds. index = " + position);
			return false;
		}
	}

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


