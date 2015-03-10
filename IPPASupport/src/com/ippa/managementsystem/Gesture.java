package com.ippa.managementsystem;

import java.util.ArrayList;

import android.util.Log;

import com.ippa.bluetooth.IppaPackageInterface;

public class Gesture implements IppaPackageInterface{
	
	private final int DEFAULT = 0;
	private final int FINGERCOUNT = 5;
	private final int STARTPOSITION = 1;
	private final int ENDPOSITION = -1;
	private final String TAG = "GestureObject";
	
	public enum Pressure {Light, Medium, High }
	
	// Position of each finger at the beginning of the gesture
	private ArrayList<Integer> m_startPosition;
	
	// Position of each finger at the end of the gesture
	private ArrayList<Integer> m_endPosition;
	
	private int m_id; // location in the arm
	private String m_command;
	private boolean m_storedInArm;
	private Pressure m_pressureAllowed;
	
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
		m_id = -1;
		m_command = "";
		m_pressureAllowed = Pressure.Medium;
		m_activeFingers = 0;
	}

	@Override
	public String getPackageA() 
	{
		if(m_id != -1)
		{
			return (PackageType.A + Integer.toString(m_id));
		}
		return null;
	}

	@Override
	public String getPackageB() 
	{
		String tmp = PackageType.B.toString();
		if(m_activeFingers == STARTPOSITION) // Starting position setting
		{
			tmp = tmp + fingerPositionToString(STARTPOSITION);
		}
		else if(m_activeFingers == ENDPOSITION) // Ending position setting
		{
			tmp = tmp + fingerPositionToString(ENDPOSITION);
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
		tmp = tmp + fullGestureInfo();
		
		return tmp;
	}

	@Override
	public String getPackageD() 
	{
		if(m_id != -1)
		{
			return (PackageType.D + Integer.toString(m_id));
		}
		return null;
	}

	@Override
	public String getPackageE() 
	{
		String tmp = PackageType.E.toString();
		tmp = tmp + fullGestureInfo();
		
		return null;
	}

	
	private String fullGestureInfo()
	{
		String tmp = "";
		tmp = tmp + Integer.toString(m_id);
		tmp = tmp + fingerPositionToString(STARTPOSITION);
		tmp = tmp + fingerPositionToString(ENDPOSITION);
		tmp = tmp + m_command + m_pressureAllowed.toString();
		return tmp;
	}
	
	private String fingerPositionToString(int whichFingers)
	{
		String tmp = "";
		if(whichFingers == STARTPOSITION) // start position
		{
			for(int index=0; index < FINGERCOUNT; index++)
			{
				tmp = tmp + Integer.toString(m_startPosition.get(index));
			}
		}
		else if(whichFingers == ENDPOSITION) // end position
		{
			for(int index=0; index < FINGERCOUNT; index++)
			{
				tmp = tmp + Integer.toString(m_endPosition.get(index));
			}
		}
		return tmp;
	}
	
	@Override
	public String toString()
	{
		String tmp = fullGestureInfo();
		tmp = tmp + Boolean.toString(m_storedInArm);
		
		
		return tmp;
	}

}
