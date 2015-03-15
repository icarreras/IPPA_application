package com.ippa.bluetooth;

import java.util.UUID;

public class Constants {
	
	public static final String CONNECTION_NAME = "IPPA Mobile";
	public static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	
	// Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "Xperia";
    public static final String TOAST = "toast";
    
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
	// Intent request codes
    public static final int REQUEST_DISCOVER_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 3;
    
    
}
