package com.ippa.managementsystem;

import java.util.ArrayList;

import junit.framework.Assert;

import com.ippa.R;
import com.ippa.bluetooth.IppaPackageInterface;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class CreateGestureFragment extends Fragment{
	
	private final int SEEKBARMIN = 0;
	
	// GUI elements
	private EditText m_textGestureName;
	private EditText m_textVoiceCommand;
	private CheckBox m_checkboxChangeDefault;
	private RadioGroup m_radiogroupPressure;
	private RadioButton m_radiobuttonMedium;
	private RadioButton m_radiobuttonHigh;
	private Button m_buttonSave;
	private Button m_buttonReset;
	
	// Finger starting position
	private SeekBar m_startPos_1;
	private SeekBar m_startPos_2;
	private SeekBar m_startPos_3;
	private SeekBar m_startPos_4;
	private SeekBar m_startPos_5;
	private TextView m_textStartPos_1;
	private TextView m_textStartPos_2;
	private TextView m_textStartPos_3;
	private TextView m_textStartPos_4;
	private TextView m_textStartPos_5;
	
	// Finger ending position
	private SeekBar m_endPos_1;
	private SeekBar m_endPos_2;
	private SeekBar m_endPos_3;
	private SeekBar m_endPos_4;
	private SeekBar m_endPos_5;
	
	
	// Logic elements
	private Gesture m_createdGesture;
	private IppaApplication m_app;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		
		View createGestureView = inflater.inflate(
				R.layout.fragment_create_gesture, container, false);
		
		m_app = (IppaApplication) getActivity().getApplicationContext();
		
		// set the rest of the UI components
		
		m_textGestureName = (EditText)createGestureView.findViewById(R.id.text_gesture_name);
		m_textVoiceCommand = (EditText)createGestureView.findViewById(R.id.text_gesture_voice_command);
		m_checkboxChangeDefault = (CheckBox)createGestureView.findViewById(R.id.checkbox_start_position);
		m_radiogroupPressure = (RadioGroup)createGestureView.findViewById(R.id.radio_group_pressure);
		m_radiobuttonMedium = (RadioButton)createGestureView.findViewById(R.id.radio_medium);
		m_radiobuttonHigh = (RadioButton)createGestureView.findViewById(R.id.radio_high);
		m_buttonSave = (Button)createGestureView.findViewById(R.id.btn_save);
		m_buttonReset = (Button)createGestureView.findViewById(R.id.btn_reset);
		
		// Start finger position
		m_startPos_1 = (SeekBar)createGestureView.findViewById(R.id.seekbar_start_finger1);
		m_startPos_2 = (SeekBar)createGestureView.findViewById(R.id.seekbar_start_finger2);
		m_startPos_3 = (SeekBar)createGestureView.findViewById(R.id.seekbar_start_finger3);
		m_startPos_4 = (SeekBar)createGestureView.findViewById(R.id.seekbar_start_finger4);
		m_startPos_5 = (SeekBar)createGestureView.findViewById(R.id.seekbar_start_finger5);
		
		m_textStartPos_1 = (TextView)createGestureView.findViewById(R.id.text_start_position_1);
		m_textStartPos_2 = (TextView)createGestureView.findViewById(R.id.text_start_position_2);
		m_textStartPos_3 = (TextView)createGestureView.findViewById(R.id.text_start_position_3);
		m_textStartPos_4 = (TextView)createGestureView.findViewById(R.id.text_start_position_4);
		m_textStartPos_5 = (TextView)createGestureView.findViewById(R.id.text_start_position_5);
		
		// End finger position
		m_endPos_1  = (SeekBar)createGestureView.findViewById(R.id.seekbar_end_finger1);
		m_endPos_2  = (SeekBar)createGestureView.findViewById(R.id.seekbar_end_finger2);
		m_endPos_3  = (SeekBar)createGestureView.findViewById(R.id.seekbar_end_finger3);
		m_endPos_4  = (SeekBar)createGestureView.findViewById(R.id.seekbar_end_finger4);
		m_endPos_5  = (SeekBar)createGestureView.findViewById(R.id.seekbar_end_finger5);
		
		m_createdGesture = new Gesture();
		
		
		
		m_buttonReset.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// Reset all UI components and gesture
				m_createdGesture = new Gesture();
				
				m_textGestureName.setText("");
				m_textVoiceCommand.setText("");
				//m_checkboxChangeDefault.setSelected(false);
				m_radiogroupPressure.clearCheck();
				
				if(m_checkboxChangeDefault.isChecked()){
					m_checkboxChangeDefault.toggle();
	            }
				
				showStartFingerSeekBars(false);
				resetAllSeekBars();
			}
			
		});
		
		m_buttonSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// Save current values of the elements in the gesture
				
				m_createdGesture.setGestureName(m_textGestureName.getText().toString());
				m_createdGesture.setVoiceCommand(m_textVoiceCommand.getText().toString());
				
				saveFingerPositions(IppaPackageInterface.FingerSelection.Start);
				saveFingerPositions(IppaPackageInterface.FingerSelection.End);
				
				int selectedPressureId = m_radiogroupPressure.getCheckedRadioButtonId();
				
				if(selectedPressureId == m_radiobuttonMedium.getId())
				{
					m_createdGesture.setPressure(IppaPackageInterface.Pressure.Medium);
				}
				if(selectedPressureId == m_radiobuttonHigh.getId())
				{
					m_createdGesture.setPressure(IppaPackageInterface.Pressure.High);
				}
				
				// Save the gesture in the phone
				saveGestureToPhone(m_createdGesture);
				
				showToastMessage("Gesture: " + m_createdGesture.getGestureName() + " has been saved");
				
				/*if(status)
				{
					showToastMessage(m_createdGesture.getGestureName()+ " was saved");
				}
				else
				{
					showToastMessage("Error: could not save the " + m_createdGesture.getGestureName()+ " gesture");
				}*/
			}
			
		});
	
		m_checkboxChangeDefault.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// display or not the start position for the fingers
				if(isChecked)
				{
					showStartFingerSeekBars(true);
				}
				else
				{
					showStartFingerSeekBars(false);
				}
			}
			
		});
		
		m_startPos_1.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean isFromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Send bluetooth package here
				saveFingerPositions(IppaPackageInterface.FingerSelection.Start);
				sendRealTimeFeedback(IppaPackageInterface.FingerSelection.Start);
			}
		
		});
		
		m_startPos_2.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean isFromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Send bluetooth package here
				saveFingerPositions(IppaPackageInterface.FingerSelection.Start);
				sendRealTimeFeedback(IppaPackageInterface.FingerSelection.Start);
			}
		
		});
		
		m_startPos_3.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean isFromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Send bluetooth package here
				saveFingerPositions(IppaPackageInterface.FingerSelection.Start);
				sendRealTimeFeedback(IppaPackageInterface.FingerSelection.Start);
			}
		
		});
		
		m_startPos_4.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean isFromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Send bluetooth package here
				saveFingerPositions(IppaPackageInterface.FingerSelection.Start);
				sendRealTimeFeedback(IppaPackageInterface.FingerSelection.Start);
			}
		
		});
		
		m_startPos_5.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean isFromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Send bluetooth package here
				saveFingerPositions(IppaPackageInterface.FingerSelection.Start);
				sendRealTimeFeedback(IppaPackageInterface.FingerSelection.Start);
			}
		
		});
		
		m_endPos_1.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean isFromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Send bluetooth package here
				saveFingerPositions(IppaPackageInterface.FingerSelection.End);
				sendRealTimeFeedback(IppaPackageInterface.FingerSelection.End);
			}
		
		});
		
		m_endPos_2.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean isFromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Send bluetooth package here
				saveFingerPositions(IppaPackageInterface.FingerSelection.End);
				sendRealTimeFeedback(IppaPackageInterface.FingerSelection.End);
			}
		
		});
		
		m_endPos_3.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean isFromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Send bluetooth package here
				saveFingerPositions(IppaPackageInterface.FingerSelection.End);
				sendRealTimeFeedback(IppaPackageInterface.FingerSelection.End);
			}
		
		});
		
		m_endPos_4.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean isFromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Send bluetooth package here
				saveFingerPositions(IppaPackageInterface.FingerSelection.End);
				sendRealTimeFeedback(IppaPackageInterface.FingerSelection.End);
			}
		
		});
		
		m_endPos_5.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean isFromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Send bluetooth package here
				saveFingerPositions(IppaPackageInterface.FingerSelection.End);
				sendRealTimeFeedback(IppaPackageInterface.FingerSelection.End);
			}
		
		});
 		
		return createGestureView;
	}

	private void sendRealTimeFeedback(IppaPackageInterface.FingerSelection selectedFingers)
	{
		String message;
		
		message = m_createdGesture.getPackageB(selectedFingers);
		
		m_app.sendViaBluetooth(message);
	}
	
	private void saveGestureToPhone(Gesture gesture)
	{
		boolean status = ((TeachingModeActivity)getActivity()).addGestureToMobile(gesture);
		Assert.assertEquals(true, status);

	}
	
	private void saveFingerPositions(IppaPackageInterface.FingerSelection selection)
	{
		int invertValue = 180;
		if(selection == IppaPackageInterface.FingerSelection.Start)
		{
			m_createdGesture.setStartPosition(1, invertValue - m_startPos_1.getProgress());
			m_createdGesture.setStartPosition(2, invertValue - m_startPos_2.getProgress());
			m_createdGesture.setStartPosition(3, invertValue - m_startPos_3.getProgress());
			m_createdGesture.setStartPosition(4, invertValue - m_startPos_4.getProgress());
			m_createdGesture.setStartPosition(5, invertValue - m_startPos_5.getProgress());
		}
		else if(selection == IppaPackageInterface.FingerSelection.End)
		{
			m_createdGesture.setEndPosition(1, invertValue - m_endPos_1.getProgress());
			m_createdGesture.setEndPosition(2, invertValue - m_endPos_2.getProgress());
			m_createdGesture.setEndPosition(3, invertValue - m_endPos_3.getProgress());
			m_createdGesture.setEndPosition(4, invertValue - m_endPos_4.getProgress());
			m_createdGesture.setEndPosition(5, invertValue - m_endPos_5.getProgress());
		}
	}
	
	private void resetAllSeekBars()
	{
		m_startPos_1.setProgress(SEEKBARMIN);
		m_startPos_2.setProgress(SEEKBARMIN);
		m_startPos_3.setProgress(SEEKBARMIN);
		m_startPos_4.setProgress(SEEKBARMIN);
		m_startPos_5.setProgress(SEEKBARMIN);
		
		m_endPos_1.setProgress(SEEKBARMIN);
		m_endPos_2.setProgress(SEEKBARMIN);
		m_endPos_3.setProgress(SEEKBARMIN);
		m_endPos_4.setProgress(SEEKBARMIN);
		m_endPos_5.setProgress(SEEKBARMIN);
	}
	
	private void showStartFingerSeekBars(Boolean show)
	{
		// Display the seekbars and text
		if(show)
		{
			m_startPos_1.setVisibility(SeekBar.VISIBLE);
			m_startPos_2.setVisibility(SeekBar.VISIBLE);
			m_startPos_3.setVisibility(SeekBar.VISIBLE);
			m_startPos_4.setVisibility(SeekBar.VISIBLE);
			m_startPos_5.setVisibility(SeekBar.VISIBLE);
			
			m_textStartPos_1.setVisibility(TextView.VISIBLE);
			m_textStartPos_2.setVisibility(TextView.VISIBLE);
			m_textStartPos_3.setVisibility(TextView.VISIBLE);
			m_textStartPos_4.setVisibility(TextView.VISIBLE);
			m_textStartPos_5.setVisibility(TextView.VISIBLE);
		}
		// Hide the seekbars
		else
		{
			m_startPos_1.setVisibility(SeekBar.GONE);
			m_startPos_2.setVisibility(SeekBar.GONE);
			m_startPos_3.setVisibility(SeekBar.GONE);
			m_startPos_4.setVisibility(SeekBar.GONE);
			m_startPos_5.setVisibility(SeekBar.GONE);
			m_textStartPos_1.setVisibility(TextView.GONE);
			m_textStartPos_2.setVisibility(TextView.GONE);
			m_textStartPos_3.setVisibility(TextView.GONE);
			m_textStartPos_4.setVisibility(TextView.GONE);
			m_textStartPos_5.setVisibility(TextView.GONE);
		}
	}
	
	void showToastMessage(String message)
	{
		Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
	}
}
