package com.ippa.ippasupport;

import com.ippa.bluetooth.BluetoothService;
import com.ippa.bluetooth.BluetoothSetup;
import com.ippa.bluetooth.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;


public class MainActivity extends Activity {

    private BluetoothService m_ChatService = null;
	private BluetoothAdapter m_bluetoothAdapter = null;
	private BluetoothSetup m_bluetoothSetup;
	
	private TextView textViewConnectionStatus;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize GUI objects
        final Button buttonVoiceCommand = (Button) findViewById(R.id.voice_command_button);
        final Button buttonTeachingMode = (Button) findViewById(R.id.teach_mode_button);
        final Button buttonConnect = (Button) findViewById(R.id.button1);
        textViewConnectionStatus = (TextView) findViewById(R.id.connection_status);
        
        
        // TODO: Maybe we will need to pass some info about Bluetooth through these intents
        buttonVoiceCommand.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, VoiceCommandActivity.class);
				startActivity(intent);
				
			}
		});
        
        buttonTeachingMode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, TeachingModeMainActivity.class);
				startActivity(intent);
				
			}
		});
        
        buttonConnect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_bluetoothSetup = new BluetoothSetup(MainActivity.this);
		        
		    	// Verify that the Bluetooth is enabled
		        if(m_bluetoothSetup.setup() == Constants.STATE_NONE)
		        {
		        	showCommNotPossibleDialog();
		        }
		        
		        // Find device to connect
		        m_bluetoothSetup.findIppaDevice();
		        
		        // Handle the case where the device is not found
		        if(!m_bluetoothSetup.isDevicePresent())
		        {
		        	// Show dialog that the device was not found
		        	showCommNotPossibleDialog(); // TODO: CHANGE DIALOG
		        }
		        else
		        {
			        // Continue to connect
			        startBluetooth();
		        }
			}
		});
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (m_ChatService != null) {
            m_ChatService.stop();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (m_ChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (m_ChatService.getState() == Constants.STATE_NONE) {
                // Start the Bluetooth chat services
                m_ChatService.start();
            }
        }
    }
    
    private void startBluetooth() {
        // Initialize the BluetoothService to perform bluetooth connections
        m_ChatService = new BluetoothService(this);
        m_ChatService.setHandler(m_handler);
        
        // connect device
        String address = m_bluetoothSetup.getAddress(); 
        // Get the BluetoothDevice object
        BluetoothDevice device = m_bluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        m_ChatService.connect(device);
    }  
    
    /*
     * This method will receive all the Bluetooth connection request
     * from the Bluetooth Setup class
     * 
     */
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	switch(requestCode)
    	{
			case Constants.REQUEST_ENABLE_BT:
				if(resultCode == RESULT_CANCELED)
				{
					// The user didn't allow the Bluetooth to be enabled
					// Can't use our application
		        	// Pop up a message and the only button should say "quit"
					showCommNotPossibleDialog();
				}
				break;
    	}
    	
    
    }
    
    
    /**
     * The Handler that gets information back from the BluetoothService
     */
    private final Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Constants.STATE_CONNECTED:
                        	textViewConnectionStatus.setText("Connected");
                            break;
                        case Constants.STATE_CONNECTING:
                        	textViewConnectionStatus.setText("Connecting");
                            break;
                        case Constants.STATE_LISTEN:
                        case Constants.STATE_NONE:
                        	textViewConnectionStatus.setText("NOT Connected");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    // TODO: once a message is written
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    // TODO: once a message is READ
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // display connected toast
                        Toast.makeText(MainActivity.this, new String(Constants.DEVICE_NAME + " now connected"),
                        		Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                        Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    
    

    private void showCommNotPossibleDialog()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(R.string.bt_setup_enable_failed_message)
				.setTitle(R.string.bt_setup_failed_title);
		builder.setNegativeButton(R.string.bt_setup_quit, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// End application
				MainActivity.this.finish();
			}
		});
		
		
		AlertDialog dialog = builder.create();
		dialog.show();
    }
   
}
