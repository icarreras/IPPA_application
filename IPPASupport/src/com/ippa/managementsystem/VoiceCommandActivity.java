package com.ippa.managementsystem;

import java.util.ArrayList;
import java.util.List;

import com.ippa.R;
import com.ippa.bluetooth.BluetoothService;
import com.ippa.bluetooth.Constants;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceCommandActivity extends Activity{
	
	private final int NUMBEROFRESULTS = 3; 
	private final int VOICE_RECOGNITION_REQUEST_CODE = 4232;
	private final String SPEECHHINT = "Say a command";
	
	private TextView m_textViewBTStatus;
	private TextView m_textViewTranslated;
	private Button m_buttonSpeech;

	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_command);

        m_textViewTranslated = (TextView)findViewById(R.id.translated_text);
        m_textViewBTStatus = (TextView)findViewById(R.id.voice_bt_status);
        m_buttonSpeech = (Button)findViewById(R.id.speech_button);
        
        m_textViewTranslated.setText("Nothing has been translated");
        
        checkVoiceRecognition();
        
        m_buttonSpeech.setOnClickListener(new View.OnClickListener() 
        {
			
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
				intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, NUMBEROFRESULTS);
				//Start the Voice recognizer activity for the result.
				startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

			}
		});        
	}
	
	public void checkVoiceRecognition() 
	{
		// Check if voice recognition is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		
		if (activities.size() == 0) {
			m_buttonSpeech.setEnabled(false);
			m_buttonSpeech.setText("Voice recognizer not present");
			showToastMessage("Voice recognizer not present");
		}
	}

	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)
		{
	    //If Voice recognition is successful then it returns RESULT_OK
	    if(resultCode == RESULT_OK) 
	    {
		    ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		    if (!textMatchList.isEmpty()) {
			    // display the match and send
		    	for(String text: textMatchList)
		    	{
		    		m_textViewTranslated.setText(text + " \n");
		    	}	
		    }
	     }

	   //Result code for various error.
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
	 }

	void showToastMessage(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	// TODO: make handler correct
	/*private final Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Constants.STATE_CONNECTED:
                        	m_textViewBTStatus.setText("Connected");
                            break;
                        case Constants.STATE_CONNECTING:
                        	m_textViewBTStatus.setText("Connecting");
                            break;
                        case Constants.STATE_LISTEN:
                        case Constants.STATE_NONE:
                        	m_textViewBTStatus.setText("NOT Connected");
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
                        Toast.makeText(VoiceCommandActivity.this, new String(Constants.DEVICE_NAME + " now connected"),
                        		Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                        Toast.makeText(VoiceCommandActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };*/
	
}
