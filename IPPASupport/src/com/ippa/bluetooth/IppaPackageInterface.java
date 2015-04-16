package com.ippa.bluetooth;

import java.util.ArrayList;

public interface IppaPackageInterface {
	/*
	 * A - Trigger a gesture => gesture id
	 * B - Update the position of each finger => 5 position values
	 * C - Arm a new gesture to the arm => full gesture info
	 * D - Delete existing gesture => gesture id
	 * E - Temporary store of a gesture (demo) and trigger => full gesture info 
	 * F - Request command strings stored in the arm => list of strings (received)
	 */
	public enum PackageType { A, B, C, D, E, F}
	public enum Pressure {Medium, High };
	public enum FingerSelection{Start, End};
	
	public String getPackageA();
	public String getPackageB(FingerSelection activeFingers);
	public String getPackageC();
	public String getPackageD();
	public String getPackageE();
}
