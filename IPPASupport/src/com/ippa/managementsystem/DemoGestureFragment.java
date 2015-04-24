package com.ippa.managementsystem;

import java.util.ArrayList;

import com.ippa.R;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class DemoGestureFragment extends Fragment{
	
	private final String TAG = "Demo Fragment";

	private ArrayList<Gesture> inMobileGestures;
	private ArrayList<Gesture> inArmGestures;
	private ArrayAdapter<String> m_gestureInMobileArrayAdapter;
	private ArrayAdapter<String> m_gestureInArmArrayAdapter;
	private final int m_optionsInMobile = R.array.dialog_demo_inmobile_options;
	private final int m_optionsInArm = R.array.dialog_demo_inarm_options;
	public static final int INMOBILE = 1;
	public static final int INARM = -1;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		
		final View demoGestureView = inflater.inflate(
				R.layout.fragment_demo_gesture, container, false);
		
		m_gestureInMobileArrayAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.gesture_name);
		
		m_gestureInArmArrayAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.gesture_name);
		
		ListView inMobileListView = (ListView) demoGestureView.findViewById(R.id.mobile_gestures);
		inMobileListView.setAdapter(m_gestureInMobileArrayAdapter);
		inMobileListView.setOnItemClickListener(m_gestureInMobileListener);

        ListView inArmListView = (ListView) demoGestureView.findViewById(R.id.arm_gestures);
        inArmListView.setAdapter(m_gestureInArmArrayAdapter);
        inArmListView.setOnItemClickListener(m_gestureInArmListener);
		
        inMobileGestures = ((TeachingModeActivity)this.getActivity()).getGesturesInMobile();
        inArmGestures = ((TeachingModeActivity)this.getActivity()).getGesturesInArm();
        
        // Add Gesture names to the appropriate list
        for(int i=0; i< inMobileGestures.size(); i++)
        {
        	m_gestureInMobileArrayAdapter.add(inMobileGestures.get(i).getGestureName());
        }
        
        for(int i=0; i< inArmGestures.size(); i++)
        {
        	m_gestureInArmArrayAdapter.add(inArmGestures.get(i).getGestureName());
        }
        
		return demoGestureView;
	}
	
	public void updateLists()
	{
		Log.i(TAG, "Updating the lists");
		inMobileGestures = ((TeachingModeActivity)this.getActivity()).getGesturesInMobile();
        inArmGestures = ((TeachingModeActivity)this.getActivity()).getGesturesInArm();
        
        // Add Gesture names to the appropriate list
        m_gestureInMobileArrayAdapter.clear();
        for(int i=0; i< inMobileGestures.size(); i++)
        {
        	m_gestureInMobileArrayAdapter.add(inMobileGestures.get(i).getGestureName());
        }
        
        m_gestureInArmArrayAdapter.clear();
        for(int i=0; i< inArmGestures.size(); i++)
        {
        	m_gestureInArmArrayAdapter.add(inArmGestures.get(i).getGestureName());
        }
	}
	
	private void showDialog(Gesture g, int option, int dialogType) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DemoGestureDialogFragment dialog = DemoGestureDialogFragment.newInstance(g, option, dialogType);
		dialog.show(fm, "DemoOptions");
        
    }
	
	
	private AdapterView.OnItemClickListener m_gestureInMobileListener
    	= new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			// user position to determine what item was clicked on
			Gesture gSelected = inMobileGestures.get(position);
			showDialog(gSelected, m_optionsInMobile, INMOBILE);
			
		}
	
	};
	
	private AdapterView.OnItemClickListener m_gestureInArmListener
		= new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
			// TODO Auto-generated method stub
			Gesture gSelected = inArmGestures.get(position);
			showDialog(gSelected, m_optionsInArm, INARM);
		}

	};

}
