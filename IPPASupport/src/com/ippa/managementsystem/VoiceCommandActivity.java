package com.ippa.managementsystem;

import com.ippa.R;
import com.ippa.bluetooth.BluetoothService;
import com.ippa.bluetooth.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceCommandActivity extends Activity{
	
	BluetoothService m_bluetoothService;
	
	private TextView textViewBTStatus;
	private TextView textViewTranslated;

	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_command);

        textViewTranslated = (TextView)findViewById(R.id.translated_text);
        textViewTranslated.setText("Nothing has been translated");
        
        textViewBTStatus = (TextView)findViewById(R.id.voice_bt_status);
	}
	
	
	private final Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Constants.STATE_CONNECTED:
                        	textViewBTStatus.setText("Connected");
                            break;
                        case Constants.STATE_CONNECTING:
                        	textViewBTStatus.setText("Connecting");
                            break;
                        case Constants.STATE_LISTEN:
                        case Constants.STATE_NONE:
                        	textViewBTStatus.setText("NOT Connected");
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
    };
	
}
