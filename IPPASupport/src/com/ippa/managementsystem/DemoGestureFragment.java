package com.ippa.managementsystem;

import com.ippa.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class DemoGestureFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		
		View demoGestureView = inflater.inflate(
				R.layout.fragment_demo_gesture, container, false);
		
		// set the rest of the UI components
		
		return demoGestureView;
	}

}
