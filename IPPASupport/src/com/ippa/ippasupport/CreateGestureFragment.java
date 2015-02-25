package com.ippa.ippasupport;

import com.ippa.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CreateGestureFragment extends Fragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		
		View createGestureView = inflater.inflate(
				R.layout.fragment_create_gesture, container, false);
		
		// set the rest of the UI components
		
		return createGestureView;
	}
	
	

}
