package com.ippa.bluetooth;

import java.util.Set;

import com.ippa.ippasupport.MainActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Window;
import android.widget.Toast;

public class BluetoothSetup {

    //private BluetoothService mChatService = null;
	private BluetoothAdapter m_bluetoothAdapter = null;
	private BluetoothDevice m_ippaDevice;
	private Activity UIActivity; // used for the status updates
	
	public BluetoothSetup(Activity parentActivity)
	{
		m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		UIActivity = parentActivity;
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
		
		// Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        UIActivity.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        UIActivity.registerReceiver(mReceiver, filter);
		
		return Constants.STATE_CONNECTING;
	}
	
	private void endDiscovery()
	{
		// Make sure we're not doing discovery anymore
        if (m_bluetoothAdapter != null) {
        	m_bluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        UIActivity.unregisterReceiver(mReceiver);
	}
	
	public void findIppaDevice()
    {
    	boolean status = false;
    	
		// Show spinning circle on the top bar
       // UIActivity.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    	
    	// Search paired devices first
    	Set<BluetoothDevice> pairedDevices = m_bluetoothAdapter.getBondedDevices();
    	
    	if(pairedDevices.size() > 0)
    	{
    		for(BluetoothDevice bDevice : pairedDevices)
    		{
    			if(bDevice.getName().equals(Constants.DEVICE_NAME))
    			{
    				m_ippaDevice = bDevice;
    				status = true;
    			}
    		}
    	}
    	// Otherwise, search discoverable devices nearby
    	// This should only happen once. After the device has been paired once
    	// there is no need to re-discover the device
    	if(!status)
    	{
    		if(m_bluetoothAdapter.isDiscovering())
    		{
    			// if running the device discover stop it and start again
    			m_bluetoothAdapter.cancelDiscovery();
    		}
    		m_bluetoothAdapter.startDiscovery();
    	}
    	
    }
	
	public String getAddress()
	{
		return m_ippaDevice.getAddress();
	}
	
	public boolean isDevicePresent()
	{
		return (m_ippaDevice == null)? false : true;
	}
	
	/**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() 
    {
        @Override
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) 
            {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) 
                {
                	// Check that the device found is the IPPA system
                	if(device.getName().equals(Constants.DEVICE_NAME))
                	{
                		m_ippaDevice = device;
                		// Cancel discovery
                		endDiscovery();
                	}
                	// TODO: for debugging
                	else
                	{
                		Toast.makeText(UIActivity, new String("device" + device.getName()),Toast.LENGTH_SHORT).show();
                	}
                    
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) 
            {
                endDiscovery();
            }
        }
    };
	
	
}
