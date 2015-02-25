package com.ippa.ippasupport;


import com.ippa.bluetooth.Constants;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class DeviceDiscoveryActivity extends Activity{
    /**
     * Tag for Log
     */
    private static final String TAG = "DeviceListActivity";

    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /**
     * Member fields
     */
    private BluetoothAdapter m_btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);
        
        
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(m_receiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(m_receiver, filter);

        // Get the local Bluetooth adapter
        m_btAdapter = BluetoothAdapter.getDefaultAdapter();
        
        
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	doDiscovery();
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (m_btAdapter != null) {
            m_btAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(m_receiver);
    }
    
    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        //TODO: Interesting: setTitle(R.string.scanning);
        setTitle("Scanning");

        // If we're already discovering, stop it
        if (m_btAdapter.isDiscovering()) {
            m_btAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        m_btAdapter.startDiscovery();
    }
    
    private void FoundDevice(BluetoothDevice btDevice)
    {
    	// Cancel discovery because it's costly and we're about to connect
        m_btAdapter.cancelDiscovery();

        String address;
        if(btDevice != null)
        {
        	address = btDevice.getAddress();
        }
        else
        {
        	address = null; // no device found
        }

        // Create the result Intent and include the MAC address
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    
    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver m_receiver = new BroadcastReceiver() 
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
                	Log.i("Reciever", "Device obtained: " + ((device.getName()== null)? "bad device" : "good device"));
                	// Check that the device found is the IPPA system
                	String name = device.getName() + "";
                	if(device.getName().contains(Constants.DEVICE_NAME))
                	{
                		FoundDevice(device);	
                	}
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) 
            {
                // TODO: pass the result as no devices found
            	FoundDevice(null);
            }
        }
    };
}
