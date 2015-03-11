package com.ippa.managementsystem;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.util.Log;

import com.ippa.bluetooth.IppaPackageInterface;

public class Gesture implements IppaPackageInterface{
	
	private final int DEFAULT = 0;
	private final int FINGERCOUNT = 5;
	public final int STARTPOSITION = 1;
	public final int ENDPOSITION = -1;
	private final String SEPARATOR = ".";
	private final String TAG = "GestureObject";
	
	public enum Pressure {Light, Medium, High }
	
	// Position of each finger at the beginning of the gesture
	private ArrayList<Integer> m_startPosition;
	
	// Position of each finger at the end of the gesture
	private ArrayList<Integer> m_endPosition;
	
	private int m_idx; // location in the arm
	private String m_command;
	private boolean m_storedInArm;
	private Pressure m_pressureAllowed;
	private String m_name;
	
	// Used for the set the finger positions the user is changing during
	// the gesture creation. 
	private int m_activeFingers;
	
	
	public Gesture()
	{
		m_startPosition = new ArrayList<Integer>(FINGERCOUNT);
		m_endPosition = new ArrayList<Integer>(FINGERCOUNT);
		
		for(int index=0; index< FINGERCOUNT; index++)
		{
			m_startPosition.add(index, DEFAULT);
			m_endPosition.add(index, DEFAULT);
		}
		
		m_storedInArm = false;
		m_idx = -1;
		m_command = "";
		m_pressureAllowed = Pressure.Medium;
		m_activeFingers = 0;
		m_name = "Default" + m_command;
	}
	
	// This constructor will be used when loading gestures from a file
	public Gesture(String gestureInformation)
	{
		ArrayList<Integer> startPosition = new ArrayList<Integer>(FINGERCOUNT);
		ArrayList<Integer> endPosition = new ArrayList<Integer>(FINGERCOUNT);
		
		StringTokenizer parser = new StringTokenizer(gestureInformation, SEPARATOR);
		
		//int count = parser.countTokens(); // Debugging
		try
		{
			m_idx = Integer.parseInt(parser.nextToken());
			
			for(int index=0; index < FINGERCOUNT; index++)
			{
				startPosition.add(index, Integer.parseInt(parser.nextToken()));
			}
			for(int index=0; index < FINGERCOUNT; index++)
			{
				endPosition.add(index, Integer.parseInt(parser.nextToken()));
			}
			
			m_command = parser.nextToken();
			m_pressureAllowed = Pressure.valueOf(parser.nextToken());
			m_storedInArm = Boolean.parseBoolean(parser.nextToken());
			
			m_startPosition = startPosition;
			m_endPosition = endPosition;
		} catch(NullPointerException e)
		{
			Log.e(TAG, "Problem parsing inputed string");
		}		
	}

	@Override
	public String getPackageA() 
	{
		if(m_idx != -1)
		{
			return (PackageType.A + SEPARATOR +  Integer.toString(m_idx));
		}
		return null;
	}

	@Override
	public String getPackageB() 
	{
		String tmp = PackageType.B.toString();
		if(m_activeFingers == STARTPOSITION) // Starting position setting
		{
			tmp = tmp + SEPARATOR +  fingerPositionToString(STARTPOSITION);
		}
		else if(m_activeFingers == ENDPOSITION) // Ending position setting
		{
			tmp = tmp + SEPARATOR +  fingerPositionToString(ENDPOSITION);
		}
		else
		{
			Log.e(TAG, "Package B creation, no focus on start/end specified");
		}
		return tmp;
	}

	@Override
	public String getPackageC() 
	{
		String tmp = PackageType.C.toString();
		tmp = tmp + SEPARATOR +  fullGestureInfo();
		
		return tmp;
	}

	@Override
	public String getPackageD() 
	{
		if(m_idx != -1)
		{
			return (PackageType.D + SEPARATOR +  Integer.toString(m_idx));
		}
		return null;
	}

	@Override
	public String getPackageE() 
	{
		String tmp = PackageType.E.toString();
		tmp = tmp + SEPARATOR +  fullGestureInfo();
		
		return null;
	}

	
	private String fullGestureInfo()
	{
		String tmp = "";
		tmp = tmp + SEPARATOR +  Integer.toString(m_idx);
		tmp = tmp + SEPARATOR +  fingerPositionToString(STARTPOSITION);
		tmp = tmp + SEPARATOR +  fingerPositionToString(ENDPOSITION);
		tmp = tmp + SEPARATOR +  m_command + SEPARATOR +  m_pressureAllowed.toString();
		return tmp;
	}
	
	private String fingerPositionToString(int whichFingers)
	{
		String tmp = "";
		if(whichFingers == STARTPOSITION) // start position
		{
			for(int index=0; index < FINGERCOUNT; index++)
			{
				tmp = tmp + SEPARATOR +  Integer.toString(m_startPosition.get(index));
			}
		}
		else if(whichFingers == ENDPOSITION) // end position
		{
			for(int index=0; index < FINGERCOUNT; index++)
			{
				tmp = tmp + SEPARATOR +  Integer.toString(m_endPosition.get(index));
			}
		}
		return tmp;
	}
	
	@Override
	public String toString()
	{
		String tmp = fullGestureInfo();
		tmp = tmp + SEPARATOR +  Boolean.toString(m_storedInArm);
		tmp = tmp + SEPARATOR + m_name;
		
		return tmp;
	}
	
	public void setStartPosition(int finger, int position)
	{
		m_startPosition.add(finger, position);
	}
	
	public void setEndPosition(int finger, int position)
	{
		m_endPosition.add(finger, position);
	}
	
	public void setCurrentFingerFocus(int focus)
	{
		m_activeFingers = focus;
	}
	
	public void setVoiceCommand(String command){
		m_command = command;
	}
	
	public void setGestureIdx(int index)
	{
		m_idx = index;
	}
	
	public void setPressure(Pressure p)
	{
		m_pressureAllowed = p;
	}
	
	public void gestureStoredInArm(boolean status)
	{
		m_storedInArm = status;
	}
	
	public void setGestureName(String n)
	{
		m_name = n;
	}
	
	public String getGestureName()
	{
		return m_name;
	}

}
