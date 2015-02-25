package com.ippa.bluetooth;


import java.util.Set;



import com.ippa.ippasupport.DeviceDiscoveryActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

public class BluetoothSetup {

    //private BluetoothService mChatService = null;
	private BluetoothAdapter m_bluetoothAdapter = null;
	private String m_address;
	private Activity UIActivity; // used for the status updates
	
	public BluetoothSetup(Activity parentActivity)
	{
		m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		UIActivity = parentActivity;
		m_address = null;
	}
	
	/* 
	 * Tries to setup the connection with the IPPA device.
	 * Returns a constant value referring to the state of the connection
	 */
	public int setup()
	{
		if(m_bluetoothAdapter == null)
        {
        	// This device does not support bluetooth communication
        	// Can't use our application
        	// Pop up a message and the only button should say "quit"
        	return Constants.STATE_NONE;
        }
		
		if(!m_bluetoothAdapter.isEnabled())
        {
        	// Request permission to enable the Bluetooth
        	Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            UIActivity.startActivityForResult(enableBluetoothIntent, Constants.REQUEST_ENABLE_BT);
        }
		
		// call find device
		
		return Constants.STATE_CONNECTING;
	}
	
	public boolean findIppaDevice()
    {
    	boolean status = false;
    	
		// TODO: Show spinning circle on the top bar
       // UIActivity.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    	
    	// Search paired devices first
    	Set<BluetoothDevice> pairedDevices = m_bluetoothAdapter.getBondedDevices();
    	
    	if(pairedDevices.size() > 0)
    	{
    		for(BluetoothDevice bDevice : pairedDevices)
    		{
    			if(bDevice.getName().contains(Constants.DEVICE_NAME))
    			{
    				m_address = bDevice.getAddress();
    				return true;
    			}
    		}
    	}
    	// Otherwise, search discoverable devices nearby
    	// This should only happen once. After the device has been paired once
    	// there is no need to re-discover the device
		Intent intent = new Intent(UIActivity, DeviceDiscoveryActivity.class);
        UIActivity.startActivityForResult(intent, Constants.REQUEST_DISCOVER_DEVICE);
    	return false;
    }
	
	public String getAddress()
	{
		return m_address;
	}
	
	public void setAddress(String a)
	{
		m_address = a; 
	}
	
	
	public boolean isDevicePresent()
	{
		return (m_address == null)? false : true;
	}
	
}
