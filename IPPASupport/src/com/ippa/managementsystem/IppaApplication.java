package com.ippa.managementsystem;

import com.ippa.bluetooth.BluetoothService;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

public class IppaApplication extends Application{
	
	private BluetoothService m_btService; 
	
	@Override
	public void onCreate()
	{
	    super.onCreate();
	     
	    initializeBT();
	}
	 
	protected void initializeBT()
	{
	    // Initialize the instance of singleton
	    m_btService = new BluetoothService(this);
	}
	  
    public void setBTHandler(Handler handler)
	{
		m_btService.setHandler(handler);
	}
    
    public void sendViaBluetooth(byte[] message)
    {
    	m_btService.write(message);
    }
    
    public void connectToDevice(BluetoothDevice device)
    {
    	m_btService.connect(device);
    }
    		
    public void terminateBluetoothService()
    {
    	if (m_btService != null) {
    		m_btService.stop();
        }
    }
    
 
}
