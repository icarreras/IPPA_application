package com.ippa.managementsystem;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.ippa.R;
import com.ippa.bluetooth.Constants;
import com.ippa.bluetooth.IppaPackages;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class TeachingModeActivity extends FragmentActivity implements ActionBar.TabListener, 
																		ViewPager.OnPageChangeListener{

	private final String TAG = "Teaching Mode Activity";
	private final String MOBILEFILE = "inMobileGestures.dat";
	private final String ARMFILE = "inArmGestures.dat";
	
	// Use custom PageAdapter to get correct Fragment based on position 
	private GestureOptionsCollectionPageAdapter mPageAdapter;
	private ViewPager mViewPager;
	protected IppaApplication m_app;
	
	// List of loaded/modified gestures
	protected ArrayList<Gesture> m_inMobileGesture;
	protected ArrayList<Gesture> m_inArmGesture;
	
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_teaching_mode);
    	
    	final ActionBar actionBar = getActionBar();	
    	actionBar.setDisplayHomeAsUpEnabled(true);

    	m_app = (IppaApplication) getApplicationContext();
    	
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
    	g2.setGestureName("Grab 1");
    	g2.setGestureIdx(0);
    	Gesture g3 = new Gesture();
    	g3.setGestureName("Grab 2");
    	g3.setGestureIdx(1);
    	Gesture g4 = new Gesture();
    	g4.setGestureName("Grab 3");
    	g4.setGestureIdx(2);
    	
    	// Add them to the list
    	m_inMobileGesture.add(g1);
    	m_inArmGesture.add(g2);
    	m_inArmGesture.add(g3);
    	m_inArmGesture.add(g4);
    	
    	
        
	}
	
	 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            // only one item to be selected
            // call the dialog fragment
            case R.id.action_help:
            	// TODO: create the help activity
            	Intent intent = new Intent(TeachingModeActivity.this, HelpActivity.class);
				startActivity(intent);
            	return true;
            	
            case android.R.id.home:
            	onBackPressed();
            	return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onBackPressed() {
    	AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(TeachingModeActivity.this);
        confirmationDialog.setMessage(R.string.switch_to_auto_mode)
        .setTitle(R.string.confirm_mode_switch)
        .setCancelable(false)
        .setNegativeButton(R.string.button_cancel, null)
        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
           
        	public void onClick(DialogInterface dialog, int id) {
            	sendPackageToBluetooth(IppaPackages.getPackageF());
            	TeachingModeActivity.super.onBackPressed();
        	}
        })
        .create()
        .show();
    }

    private void sendPackageToBluetooth(String message)
    {
    	m_app.sendViaBluetooth(message);
    }
    
    /**
     * Saves the current gestures in mobile
     */
    public void saveGestures()
    {
        try{
            ObjectOutputStream objectOutputStreamMobile = new ObjectOutputStream(openFileOutput(MOBILEFILE,Context.MODE_PRIVATE));

            objectOutputStreamMobile.writeObject(m_inMobileGesture);
            objectOutputStreamMobile.close();
            
            ObjectOutputStream objectOutputStreamArm = new ObjectOutputStream(openFileOutput(ARMFILE,Context.MODE_PRIVATE));
            
            objectOutputStreamArm.writeObject(m_inArmGesture);
            objectOutputStreamArm.close();

        }catch (IOException e){
            Log.e(TAG, "Saving Gestures: " + e.getMessage());
        	e.printStackTrace();
        }
    }

    /**
     * Load a game based on whether or not loadFile is true, otherwise create new business object
     * @param loadFile  determines whether or not to load a file from memory
     */
    public void loadGestures()
    {
        File inMobileFile = getFileStreamPath(MOBILEFILE);
        File inArmFile = getFileStreamPath(ARMFILE);

        if (inMobileFile != null && inArmFile != null)
        {
            try
            {
                // try to create input stream
                ObjectInputStream objectInputStreamMobile = new ObjectInputStream(openFileInput(MOBILEFILE));
                ObjectInputStream objectInputStreamArm = new ObjectInputStream(openFileInput(MOBILEFILE));

                try 
                {
                    // get the lists of gestures
                    @SuppressWarnings("unchecked")
					ArrayList<Gesture> objMobile = (ArrayList<Gesture>)objectInputStreamMobile.readObject();                        
                    objectInputStreamMobile.close();

                    
                    @SuppressWarnings("unchecked")
					ArrayList<Gesture> objArm = (ArrayList<Gesture>)objectInputStreamArm.readObject();                        
                    objectInputStreamArm.close();
                    
                    // If no problems make the gestures available
                    m_inMobileGesture = objMobile;
                    m_inArmGesture = objArm;

                } catch (ClassNotFoundException e) 
                {
                    Log.e(TAG, "Error loading gestures: " + e.getMessage());
                }
            } catch (IOException e)
            {
            	Log.e(TAG, "Error loading gestures: " + e.getMessage());
            }
        }
        else
        {
        	Log.e(TAG, "Error getting the files with the gestures");
        }
    }
    
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// When the tab is selected, switch to the
        // corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

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
		sendPackageToBluetooth(IppaPackages.getPackageF());
		saveGestures();
		super.onDestroy();
		// inform system that teaching mode is over
		// do not disconnect
	}
	
     
    private final Handler m_Handler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Constants.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to), Color.GREEN);
                            break;
                        case Constants.STATE_CONNECTING:
                            //setStatus(getString(R.string.title_connecting), Color.YELLOW);
                            break;
                        case Constants.STATE_NONE:
                            //setStatus(getString(R.string.title_not_connected), Color.RED);
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
                    if (null != TeachingModeActivity.this) {
                        Toast.makeText(TeachingModeActivity.this, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != TeachingModeActivity.this) {
                        Toast.makeText(TeachingModeActivity.this, msg.getData().getString(Constants.TOAST),
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


