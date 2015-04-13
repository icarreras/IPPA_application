package com.ippa.managementsystem;

import com.ippa.R;
import com.ippa.managementsystem.Gesture.Pressure;

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
	
	// Finger ending position
	private SeekBar m_endPos_1;
	private SeekBar m_endPos_2;
	private SeekBar m_endPos_3;
	private SeekBar m_endPos_4;
	private SeekBar m_endPos_5;
	
	
	// Logic elements
	private Gesture m_createdGesture;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		
		View createGestureView = inflater.inflate(
				R.layout.fragment_create_gesture, container, false);
		
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
				// TODO Reset the values for the Gesture
				//and all the view elements
				
				m_createdGesture = new Gesture();
				
				m_textGestureName.setText("");
				m_textVoiceCommand.setText("");
				m_checkboxChangeDefault.setSelected(false);
				m_radiogroupPressure.clearCheck();
				
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
				
				saveFingerPositions();
				
				int selectedPressureId = m_radiogroupPressure.getCheckedRadioButtonId();
				
				if(selectedPressureId == m_radiobuttonMedium.getId())
				{
					m_createdGesture.setPressure(Pressure.Medium);
				}
				if(selectedPressureId == m_radiobuttonHigh.getId())
				{
					m_createdGesture.setPressure(Pressure.High);
				}
				
				// Save the gesture in the phone
				boolean status = saveGestureToPhone(m_createdGesture.toString());
				
				if(status)
				{
					showToastMessage(m_createdGesture.getGestureName()+ " was saved");
				}
				else
				{
					showToastMessage("Error: could not save the " + m_createdGesture.getGestureName()+ " gesture");
				}
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
		
		// TODO: add the implementation for the seekbar changes and bluetooth
		
		return createGestureView;
	}
	
	private boolean saveGestureToPhone(String gesture)
	{
		// TODO implement
		
		return false;
	}
	
	private void saveFingerPositions()
	{
		m_createdGesture.setStartPosition(1, m_startPos_1.getProgress());
		m_createdGesture.setStartPosition(2, m_startPos_2.getProgress());
		m_createdGesture.setStartPosition(3, m_startPos_3.getProgress());
		m_createdGesture.setStartPosition(4, m_startPos_4.getProgress());
		m_createdGesture.setStartPosition(5, m_startPos_5.getProgress());
		
		m_createdGesture.setEndPosition(1, m_endPos_1.getProgress());
		m_createdGesture.setEndPosition(2, m_endPos_2.getProgress());
		m_createdGesture.setEndPosition(3, m_endPos_3.getProgress());
		m_createdGesture.setEndPosition(4, m_endPos_4.getProgress());
		m_createdGesture.setEndPosition(5, m_endPos_5.getProgress());
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
		// Display the seekbars
		
		// TODO: add the visibility to the text that goes with this
		if(show)
		{
			m_startPos_1.setVisibility(SeekBar.VISIBLE);
			m_startPos_2.setVisibility(SeekBar.VISIBLE);
			m_startPos_3.setVisibility(SeekBar.VISIBLE);
			m_startPos_4.setVisibility(SeekBar.VISIBLE);
			m_startPos_5.setVisibility(SeekBar.VISIBLE);
		}
		// Hide the seekbars
		else
		{
			m_startPos_1.setVisibility(SeekBar.GONE);
			m_startPos_2.setVisibility(SeekBar.GONE);
			m_startPos_3.setVisibility(SeekBar.GONE);
			m_startPos_4.setVisibility(SeekBar.GONE);
			m_startPos_5.setVisibility(SeekBar.GONE);
		}
	}
	
	void showToastMessage(String message)
	{
		Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
	}
}
