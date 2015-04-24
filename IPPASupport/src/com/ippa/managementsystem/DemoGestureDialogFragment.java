package com.ippa.managementsystem;

import com.ippa.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

public class DemoGestureDialogFragment extends DialogFragment{ 
	
	// Mobile options
	private final int MOBILE_EDIT = 0;
	private final int MOBILE_MOVETOARM = 1;
	private final int MOBILE_DELETE = 2;
	private final int MOBILE_PLAY = 3;
	
	// In arm options
	private final int ARM_TRANSFER = 0;
	private final int ARM_TRIGGER = 1;
	private final int ARM_DELETE = 2;
	private final String TAG = "DemoDialog";
	
	private static final String PARCELKEY = "gesture";
	private static final String OPTIONSKEY = "optionsToDisplay";
	private static boolean s_dialogType; // true -> in Arm
	
	private Gesture m_gesture;
	private IppaApplication m_app;
	
	// Pass the integer that points to the array in the resource folder
	public static DemoGestureDialogFragment newInstance(Gesture gesture, int optionsToDisplay, int displayCode) {
		DemoGestureDialogFragment frag = new DemoGestureDialogFragment();
		// set the type of dialog for this instance
		s_dialogType = (displayCode == DemoGestureFragment.INMOBILE)? false : true;
        // set arguments
		Bundle args = new Bundle();
        args.putInt(OPTIONSKEY, optionsToDisplay);
        args.putParcelable(PARCELKEY, gesture);
        frag.setArguments(args);
        return frag;
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		OnClickListener listener = (s_dialogType)? m_inArmListener : m_inMobileListener;
		int optionsToDisplay = getArguments().getInt(OPTIONSKEY);
		m_gesture = getArguments().getParcelable(PARCELKEY);
		
		m_app = (IppaApplication) getActivity().getApplicationContext();
		
		Log.i(TAG, "This is the parced and recover gesture: " + m_gesture.toString());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	   
		// Add action buttons
	    builder.setTitle(R.string.dialog_title_options)
           .setItems(optionsToDisplay,  listener)
           .setNegativeButton("cancel", new DialogInterface.OnClickListener() 
           {
               public void onClick(DialogInterface dialog, int id) 
               {
            	   DemoGestureDialogFragment.this.getDialog().cancel();
               }
           });      
	    return builder.create();
    }
	
	OnClickListener m_inMobileListener = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which) 
        {
            // The 'which' argument contains the index position
            // of the selected item
     	   switch (which)
     	   {
     		   case MOBILE_EDIT:
     			   Log.i(TAG, "Edit is not implemented yet");
     			   // TODO: Switch to the create tab and pass the gesture as a parcel
     			   break;
     		   case MOBILE_DELETE:
     			   ((TeachingModeActivity)getActivity()).deleteGestureInMobile(which);
     			   break;
     		   case MOBILE_MOVETOARM:
     			   sendMessageToArm("C");
     			  ((TeachingModeActivity)getActivity()).addGestureToArm(m_gesture);
     			   break;
     		   case MOBILE_PLAY:
     			   sendMessageToArm("E");
     			   break;
     	   }
     		   
        }
	};
	
	OnClickListener m_inArmListener = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which) 
        {
            // The 'which' argument contains the index position
            // of the selected item
     	   switch (which)
     	   {
     		   case ARM_TRANSFER:
     			   Log.i(TAG, "Edit is not implemented yet");
     			   break;
     		   case ARM_TRIGGER:
     			   sendMessageToArm("A");
     			   break;
     		   case ARM_DELETE:
     			   sendMessageToArm("D");
     			   break;
     	   }
        }
	};
	
	private void sendMessageToArm(String type)
	{
		String message = "";
		if(type.equals("C"))
		{
			message = m_gesture.getPackageC();
		}
		else if(type.equals("E"))
		{
			message = m_gesture.getPackageE();
		}
		else if(type.equals("A"))
		{
			message = m_gesture.getPackageA();
		}
		else if(type.equals("D"))
		{
			message = m_gesture.getPackageD();
		}
		else 
		{
			Log.e(TAG, "This type of message has not been implemented");
		}
		m_app.sendViaBluetooth(message);
	}
}
