package com.ippa.bluetooth;

import java.util.ArrayList;

public class IppaPackages {
	
	public static final String SEPARATOR = ".";
	public static final String ENDOFPACKAGE = "/";

	public static String getPackageF()
	{
		return "F" + ENDOFPACKAGE;
	}
	
	public static String getPackageG()
	{
		return "G" + ENDOFPACKAGE;
	}
	
	// This is used for known gesture position but no gesture information
	public static String getPackageA(int position)
	{
		return "A" + SEPARATOR + position + ENDOFPACKAGE;
	}
	
	public static ArrayList<String> fromPackageH(String pack)
	{
		ArrayList<String> voiceCommands = new ArrayList<String>();
		// TODO: figure how to have the complete transmission then how is it passed to this ? like a string ?
		/*The data is sent from the IPPA to the mobile device. 
		The first piece of data must be an integer, representing 
		the number of gestures in the arm. The rest it’s a list of 
		strings: the voice command set for each gesture stored in 
		the arm. This will be used for the voice command triggering.*/
		
		return voiceCommands;
	}	
	
}
