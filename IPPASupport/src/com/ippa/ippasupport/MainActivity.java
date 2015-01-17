package com.ippa.ippasupport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize GUI objects
        final Button buttonVoiceCommand = (Button) findViewById(R.id.voice_command_button);
        final Button buttonTeachingMode = (Button) findViewById(R.id.teach_mode_button);
        final TextView textViewConnectionStatus = (TextView) findViewById(R.id.connection_status);

        String connectionStatus = "Not Connected";

        // Set status
        textViewConnectionStatus.setText(connectionStatus);
        
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
    }

   
}
