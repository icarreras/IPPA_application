package com.ippa.managementsystem;

import java.util.UUID;

import com.ippa.R;
import com.ippa.bluetooth.BluetoothService;
import com.ippa.bluetooth.BluetoothSetup;
import com.ippa.bluetooth.Constants;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
	protected IppaApplication app;
	
	private TextView m_connectionStatusText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize GUI objects
        final Button buttonVoiceCommand = (Button) findViewById(R.id.voice_command_button);
        final Button buttonTeachingMode = (Button) findViewById(R.id.teach_mode_button);
        final Button buttonConnect = (Button) findViewById(R.id.button1);
        final Button buttonSend = (Button) findViewById(R.id.button2);
        m_connectionStatusText = (TextView) findViewById(R.id.connection_status);
        
        // Get reference to the global data (BT)
        app = (IppaApplication) getApplicationContext();
        
		m_bluetoothSetup = new BluetoothSetup(MainActivity.this);
        
    	// Verify that the Bluetooth is enabled
		m_bluetoothSetup.setup();
        
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
				String test = "IvetteIvetteIvetteee";
				// TODO: TESTING OF SHARED BLUETOOTH SERVICE
				app.sendViaBluetooth(test.getBytes());
				//m_bluetoothService.write(test.getBytes());
				
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
				if(m_bluetoothSetup.getState() == Constants.STATE_NONE)
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
    public void onResume() {
        super.onResume();
        
        // TODO: Determine behavior to reconnect 
        
        // TODO: => result: not needed since the service never dies in the application
        // this needs to be moved to the service object
    }
    
    private void startBluetooth() { 
    	
    	// Handle the case where the device is not found
        if(!m_bluetoothSetup.isDevicePresent())
        {
        	// Show dialog that the device was not found
        	showCommNotPossibleDialog("The device is not found in the near area");
        }
        else
        {
        	//m_bluetoothService = new BluetoothService(this);
        	app.setBTHandler(m_handler);
            //m_bluetoothService.setHandler(m_handler);
            
            app.connectToDevice(m_bluetoothSetup.getDevice());
            //m_bluetoothService.connect(m_bluetoothSetup.getDevice(), m_foundUuid);
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
				
				m_bluetoothSetup.setDevice(address);
				
				// Start Bluetooth connection
				startBluetooth();
				break;
			}
    	}
    }
    
    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence status, int color) 
    {
    	m_connectionStatusText.setText(status);
    	m_connectionStatusText.setTextColor(color);
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
                        	setStatus(getString(R.string.title_connected_to), Color.GREEN);
                        	break;
                        case Constants.STATE_CONNECTING:
                            setStatus(getString(R.string.title_connecting), Color.YELLOW);
                            break;
                        case Constants.STATE_NONE:
                            setStatus(getString(R.string.title_not_connected), Color.RED);
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
