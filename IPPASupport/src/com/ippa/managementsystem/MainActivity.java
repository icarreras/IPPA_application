package com.ippa.managementsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.ippa.R;
import com.ippa.bluetooth.BluetoothSetup;
import com.ippa.bluetooth.Constants;
import com.ippa.bluetooth.DeviceDiscoveryActivity;
import com.ippa.bluetooth.IppaPackages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


public class MainActivity extends Activity {

	private final int SPEECHNUMBEROFRESULTS = 3; 
	private final int VOICE_RECOGNITION_REQUEST_CODE = 4232;
	private final String SPEECHHINT = "Say a command";
	private final String TAG = "Main Activity";
	
	private BluetoothSetup m_bluetoothSetup;
	private ArrayList<String> m_voiceCommands;
	private int m_voiceCommandCount;
	private boolean m_waitingForEndPackage;
	private TextView m_textViewTranslated;
	private Button m_buttonVoiceCommand;
	private Button m_buttonTeachingMode;
	protected IppaApplication m_app;
	
	private TextView m_connectionStatusText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize GUI objects
        m_buttonVoiceCommand  = (Button) findViewById(R.id.voice_command_button);
        m_buttonTeachingMode = (Button) findViewById(R.id.teach_mode_button);
        final Button buttonConnect = (Button) findViewById(R.id.connect_button);
        
        m_textViewTranslated = (TextView)findViewById(R.id.translated_text);
        m_textViewTranslated.setText("Nothing has been translated");
        
        
        m_connectionStatusText = (TextView) findViewById(R.id.connection_status);
        
        // Get reference to the global data (BT)
        m_app = (IppaApplication) getApplicationContext();
        
        m_voiceCommands = new ArrayList<String>();
        m_voiceCommandCount = 0;
        m_waitingForEndPackage = false;
		m_bluetoothSetup = new BluetoothSetup(MainActivity.this);
        
    	// Verify that the Bluetooth is enabled
		m_bluetoothSetup.setup();
		
		checkVoiceRecognition();
        
		m_buttonVoiceCommand.setEnabled(false);
		m_buttonVoiceCommand.setOnClickListener(new View.OnClickListener() {
			
        	@Override
			public void onClick(View v) {
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				
				// Specify the calling package to identify your application
				intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				    .getPackage().getName());

				// Display an hint to the user about what he should say.
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, SPEECHHINT);

				// Given an hint to the recognizer about what the user is going to say
				//There are two form of language model available
			    //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
				//2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
			
			
				// Specify how many results you want to receive. The results will be
				// sorted where the first result is the one with higher confidence.
				// TODO: go through the results in case the first is not the correct match
				intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, SPEECHNUMBEROFRESULTS);
				//Start the Voice recognizer activity for the result.
				startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

			}
		});
        
		m_buttonTeachingMode.setEnabled(false);
        m_buttonTeachingMode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(MainActivity.this);
		        confirmationDialog.setMessage(R.string.switch_to_teaching_mode)
		        .setTitle(R.string.confirm_mode_switch)
		        .setCancelable(false)
		        .setNegativeButton(R.string.button_cancel, null)
		        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
		           
		        	public void onClick(DialogInterface dialog, int id) {
		        		Intent intent = new Intent(MainActivity.this, TeachingModeActivity.class);
						startActivity(intent);
		        	}
		        })
		        .create()
		        .show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            // only one item to be selected
            // call the dialog fragment
            case R.id.action_help:
            	// TODO: create the help activity
            	Intent intent = new Intent(MainActivity.this, HelpActivity.class);
				startActivity(intent);
            	return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
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
        	m_app.setBTHandler(m_handler);
            //m_bluetoothService.setHandler(m_handler);
            
        	m_app.connectToDevice(m_bluetoothSetup.getDevice());
            //m_bluetoothService.connect(m_bluetoothSetup.getDevice(), m_foundUuid);
        }
    	
        // Request Voice commands
        m_app.sendViaBluetooth(IppaPackages.getPackageF());
    }  
    
    
    
    public void checkVoiceRecognition() 
	{
		// Check if voice recognition is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		
		if (activities.size() == 0) {
			m_buttonVoiceCommand.setEnabled(false);
			m_buttonVoiceCommand.setText("Voice recognizer not present");
			showToastMessage("Voice recognizer not present");
		}
	}
    
    
    /*
     * This method will receive 
     * - all the Bluetooth connection request from the Bluetooth Setup class
     * - the speech translation from the Google API
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
			case VOICE_RECOGNITION_REQUEST_CODE:
			{
		    //If Voice recognition is successful then it returns RESULT_OK
			    if(resultCode == RESULT_OK) 
			    {
				    ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				    if (!textMatchList.isEmpty()) 
				    {
					    // display the match and send
				    	String result = null;
				    	for(String text: textMatchList)
				    	{
				    		// verify the translated text matches a known command
				    		if(m_voiceCommands.contains(text))
				    		{
				    			// send the command
				    			m_app.sendViaBluetooth(IppaPackages.getPackageA(m_voiceCommands.indexOf(text)));
				    			result = text;
				    		}
				    	}
				    	if(result == null)
				    	{
				    		// The translated text didn't match the voice commands
				    		Log.i(TAG, "The translated text didn't match the voice commands");
				    		showToastMessage("No matching command found");
				    	}
				    	else
				    	{
				    		m_textViewTranslated.setText(result);
				    	}
				    }
			    }else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
				    showToastMessage("Audio Error");
			    }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
			    	showToastMessage("Client Error");
			    }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
			    	showToastMessage("Network Error");
			    }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
			    	showToastMessage("No Match");
			    }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
			    	showToastMessage("Server Error");
			    }
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
                        	m_buttonVoiceCommand.setEnabled(true);
                        	m_buttonTeachingMode.setEnabled(true);
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
                    processMessageFromBluetooth(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // display connected toast
                        showToastMessage(Constants.DEVICE_NAME + " now connected");
                    break;
                case Constants.MESSAGE_TOAST:
                        showToastMessage(msg.getData().getString(Constants.TOAST));
                    break;
            }
        }
    };
    
    private void processMessageFromBluetooth(String bluetoothMessage)
    {
    	StringTokenizer parser = new StringTokenizer(bluetoothMessage, IppaPackages.SEPARATOR);
    	Log.i(TAG, "message received from bluetooth: " + bluetoothMessage);
    	
    	if(m_waitingForEndPackage)
    	{
    		// add the first token to the end of the existing command
    		String secondPart = parser.nextToken().replace(IppaPackages.ENDOFPACKAGE, "");
    		String firstPart = m_voiceCommands.get(m_voiceCommandCount);
    		m_voiceCommands.add(m_voiceCommandCount, firstPart.concat(secondPart));
    		++m_voiceCommandCount;
    		processPackageH(parser);
    	}
    	else
    	{
    		String subPart = parser.nextToken();
    		int count = Integer.parseInt(parser.nextToken());
        	
        	// first package of the message
    		if(subPart.equals("H"))
    		{
    			processPackageH(parser);
    		}
    		// TODO : POSSIBLE ADDITION OF PACKAGES
    		/*else if(subPart.equals("I"))
    		{
    			
    		}*/
    	}
    	
    	m_waitingForEndPackage = !(bluetoothMessage.contains(IppaPackages.ENDOFPACKAGE));
    }
    
    private void processPackageH(StringTokenizer parser)
    {
    	while(parser.hasMoreTokens())
    	{
    		String temp = parser.nextToken();
    		if(parser.hasMoreTokens())
    		{
    			// the available token is a whole command
    			m_voiceCommands.add(m_voiceCommandCount, temp);
    			++m_voiceCommandCount;
    		}
    		else
    		{
    			// the command was broken into two parts
    			// remove the end of package
    			m_voiceCommands.add(m_voiceCommandCount, temp.substring(0, temp.length()-1));
    		}
    	}
    }
    
    void showToastMessage(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

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
