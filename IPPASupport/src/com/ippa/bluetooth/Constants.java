package com.ippa.bluetooth;

import java.util.UUID;

public class Constants {
	
	public static final String CONNECTION_NAME = "IPPA Mobile";
	public static final UUID MY_UUID_SECURE = UUID.fromString("8ecad208-b3be-11e4-a71e-12e3f512a338");
	//public static final UUID MY_UUID_INSECURE = UUID.fromString("434908f8-b3c4-11e4-a71e-12e3f512a338");
	
	
	// Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "Nexus 4";
    public static final String TOAST = "toast";
    
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
	// Intent request codes
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 3;
    
}
