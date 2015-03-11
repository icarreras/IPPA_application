package com.ippa.managementsystem;

import java.util.ArrayList;

import com.ippa.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class DemoGestureFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		
		final View demoGestureView = inflater.inflate(
				R.layout.fragment_demo_gesture, container, false);
		
		ArrayAdapter<String> gestureInMobileArrayAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.gesture_name);
		
		ArrayAdapter<String> gestureInArmArrayAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.gesture_name);
		
		ListView inMobileListView = (ListView) demoGestureView.findViewById(R.id.mobile_gestures);
		inMobileListView.setAdapter(gestureInMobileArrayAdapter);
		inMobileListView.setOnItemClickListener(m_gestureInMobileListener);

        ListView inArmListView = (ListView) demoGestureView.findViewById(R.id.arm_gestures);
        inArmListView.setAdapter(gestureInArmArrayAdapter);
        inArmListView.setOnItemClickListener(m_gestureInArmListener);
		
        ArrayList<Gesture> inMobile = ((TeachingModeMainActivity)this.getActivity()).getGesturesInMobile();
        ArrayList<Gesture> inArm = ((TeachingModeMainActivity)this.getActivity()).getGesturesInArm();
        
        // Add Gesture names to the appropriate list
        for(int i=0; i< inMobile.size(); i++)
        {
        	gestureInMobileArrayAdapter.add(inMobile.get(i).getGestureName());
        }
        
        for(int i=0; i< inArm.size(); i++)
        {
        	gestureInArmArrayAdapter.add(inArm.get(i).getGestureName());
        }
        
		return demoGestureView;
	}
	
	private AdapterView.OnItemClickListener m_gestureInMobileListener
    	= new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			// user position to determine what item was clicked on
			
		}
	
	};
	
	private AdapterView.OnItemClickListener m_gestureInArmListener
		= new AdapterView.OnItemClickListener() {

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		// TODO Auto-generated method stub
		
	}

};

}
