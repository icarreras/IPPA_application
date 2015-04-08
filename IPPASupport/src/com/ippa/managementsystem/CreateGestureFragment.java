package com.ippa.managementsystem;

import com.ippa.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class CreateGestureFragment extends Fragment{
	
	private EditText m_text_gesture_name;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		
		View createGestureView = inflater.inflate(
				R.layout.fragment_create_gesture, container, false);
		
		// set the rest of the UI components
		
		m_text_gesture_name = (EditText)createGestureView.findViewById(R.id.voice_command_button);
		
		return createGestureView;
	}
	
	

}
