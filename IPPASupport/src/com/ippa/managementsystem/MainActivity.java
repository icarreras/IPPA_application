package com.ippa.managementsystem;

import java.util.UUID;

import com.ippa.R;
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
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.*;


public class MainActivity extends Activity {

    private BluetoothService m_bluetoothService = null;
	private BluetoothAdapter m_bluetoothAdapter = null;
	private BluetoothSetup m_bluetoothSetup;
	private UUID m_foundUuid;
	
	private TextView textViewConnectionStatus;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize GUI objects
        final Button buttonVoiceCommand = (Button) findViewById(R.id.voice_command_button);
        final Button buttonTeachingMode = (Button) findViewById(R.id.teach_mode_button);
        final Button buttonConnect = (Button) findViewById(R.id.button1);
        final Button buttonSend = (Button) findViewById(R.id.button2);
        textViewConnectionStatus = (TextView) findViewById(R.id.connection_status);
        
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        
        
        // TODO: Maybe we will need to pass some info about Bluetooth through these intents
        buttonVoiceCommand.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, VoiceCommandActivity.class);

				startActivity(intent);
				
			}
		});
        
        buttonSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String test = "Ivette";
				m_bluetoothService.write(test.getBytes());
				
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
				
				// TODO: MOVE ALL OF THIS TO THE BLUETOOTHSETUPACTIVITY
				m_bluetoothSetup = new BluetoothSetup(MainActivity.this);
		        
		    	// Verify that the Bluetooth is enabled
		        if(m_bluetoothSetup.setup() == Constants.STATE_NONE)
		        {
		        	showCommNotPossibleDialog("This device does not have Bluetooth capabilities");
		        }
		        
		        // Find device to connect
		        boolean pairedDevice = m_bluetoothSetup.findIppaDevice();
		        if(pairedDevice)
		        {
		        	startBluetooth();
		        }
		        // Otherwise wait for the activity searching to finish and return the result
		        
			}
		});
        
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (m_bluetoothService != null) {
            m_bluetoothService.stop();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (m_bluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (m_bluetoothService.getState() == Constants.STATE_NONE) {
                // Start the Bluetooth chat services
                m_bluetoothService.start();
            }
        }
    }
    
    private void startBluetooth() { 
    	
    	// Handle the case where the device is not found
        if(!m_bluetoothSetup.isDevicePresent())
        {
        	// Show dialog that the device was not found
        	showCommNotPossibleDialog("The device is not found in the near area"); // TODO: CHANGE DIALOG
        }
        else
        {
        	m_bluetoothService = new BluetoothService(this);
            m_bluetoothService.setHandler(m_handler);
            
            // connect device
            String address = m_bluetoothSetup.getAddress();
            //String address = new String("C4:43:8F:01:EF:F5"); // nexus
            //address = new String("30:75:12:D5:AE:9C"); // xperia
            //String address = new String("46:73:6E:32:18:21"); // laptop
            // Get the BluetoothDevice object
            BluetoothDevice device = m_bluetoothAdapter.getRemoteDevice(address);
            
            // get supported uuid services
            m_foundUuid = Constants.MY_UUID_SECURE;
            /*if(device.fetchUuidsWithSdp())
            {
            	//Toast.makeText(MainActivity.this, "true", Toast.LENGTH_SHORT).show();
            	ParcelUuid[] uuids = device.getUuids();
            	m_foundUuid = uuids[0].getUuid();
            	
            }*/
            
            m_bluetoothService.connect(device, m_foundUuid);
        }
    	
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
			{
				if(resultCode == RESULT_CANCELED)
				{
					// The user didn't allow the Bluetooth to be enabled
					// Can't use our application
		        	// Pop up a message and the only button should say "quit"
					showCommNotPossibleDialog("The user did not allow Bluetooth Enabling");
				}
				break;
			}
			case Constants.REQUEST_DISCOVER_DEVICE:
			{
				String address = null;
				if(resultCode == RESULT_OK)
				{
					// Get device address
					address = data.getExtras()
			                .getString(DeviceDiscoveryActivity.EXTRA_DEVICE_ADDRESS);
				}
				
				m_bluetoothSetup.setAddress(address);
				
				// Start Bluetooth connection
				startBluetooth();
				break;
			}
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
    
    

    private void showCommNotPossibleDialog(String message)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(message)
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
