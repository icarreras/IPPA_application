package com.ippa.bluetooth;


import java.util.Set;





import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

public class BluetoothSetup {

    //private BluetoothService mChatService = null;
	private BluetoothAdapter m_bluetoothAdapter = null;
	private String m_address;
	private int m_state;
	private BluetoothDevice m_btDevice;
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
		m_state = Constants.STATE_CONNECTING;
		if(m_bluetoothAdapter == null)
        {
        	// This device does not support bluetooth communication
        	// Can't use our application
        	// Pop up a message and the only button should say "quit"
			m_state = Constants.STATE_NONE;
			return m_state;
        }
		
		if(!m_bluetoothAdapter.isEnabled())
        {
        	// Request permission to enable the Bluetooth
        	Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            UIActivity.startActivityForResult(enableBluetoothIntent, Constants.REQUEST_ENABLE_BT);
        }
		
		// call find device
		
		return m_state;
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
    				setDevice(m_address);
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
	
	public BluetoothDevice getDevice()
	{
		return m_btDevice;
	}
	
	public int getState()
	{
		return m_state;
	}
	
	public void setDevice(String a)
	{
		m_address = a; 
        // Get the BluetoothDevice object
		m_btDevice = m_bluetoothAdapter.getRemoteDevice(m_address);
	}
	
	
	public boolean isDevicePresent()
	{
		return (m_address == null)? false : true;
	}
	
}
