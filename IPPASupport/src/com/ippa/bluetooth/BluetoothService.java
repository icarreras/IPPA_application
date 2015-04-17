package com.ippa.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

// This class will be singleton class
public class BluetoothService{
	
    // Member fields
	private final String TAG = "BT_Service";
	private final int MAXPACKAGELENGTH = 13;
    private final BluetoothAdapter mAdapter;
    private Handler m_handler;
   // private AcceptThread mSecureAcceptThread; // no need for server side
    private ConnectThread m_ConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;


    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param context The UI Activity Context
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BluetoothService(Context context) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = Constants.STATE_NONE;
        m_handler = null;
    }
    
    public void setHandler(Handler handler)
    {
    	m_handler = handler;
    }
    
    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        m_handler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }
    

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device) {
        // Cancel any thread attempting to make a connection
    	
        if (mState == Constants.STATE_CONNECTING) {
            if (m_ConnectThread != null) {
                m_ConnectThread.cancel();
                m_ConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        m_ConnectThread = new ConnectThread(device);
        m_ConnectThread.start();
        setState(Constants.STATE_CONNECTING);
    }


    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {

    	
        // Cancel the thread that completed the connection
        if (m_ConnectThread != null) {
            m_ConnectThread.cancel();
            m_ConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = m_handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        m_handler.sendMessage(msg);

        setState(Constants.STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {

        if (m_ConnectThread != null) {
            m_ConnectThread.cancel();
            m_ConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(Constants.STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * call to send output
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(String out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != Constants.STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Divide each package into 13 bytes -> bluetooth device requirement
        int loopCount = (out.length()/MAXPACKAGELENGTH);
        String subpackage = "";
        for(int i=0; i <= loopCount; i++)
        {	
        	try
        	{
        		 subpackage = out.substring(i*MAXPACKAGELENGTH, i*MAXPACKAGELENGTH + MAXPACKAGELENGTH);
        	}
        	catch(IndexOutOfBoundsException e)
        	{
        		subpackage = out.substring(i*MAXPACKAGELENGTH);
        	}
        	// Perform the write unsynchronized
            r.write(subpackage.getBytes());	
            
            Log.i(TAG, "Sending: " +subpackage);
            try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.getMessage());
			}
        }
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = m_handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        m_handler.sendMessage(msg);
        setState(Constants.STATE_NONE);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = m_handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        m_handler.sendMessage(msg);
        setState(Constants.STATE_NONE);
    }

    
    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     * Client side: a remote device opened the server socket, here we just get it
     * It must use the same UUID the server sets
     */
     
    private class ConnectThread extends Thread {
        private BluetoothSocket m_socket; // Not final to provide fall back in case the first socket fails
        private final BluetoothDevice mmDevice;
        private String m_socketType;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(Constants.MY_UUID_SECURE);
			} catch (IOException e) {
				Log.e(TAG, "Error creating socket");
				Log.e(TAG, e.toString());
			}

            m_socket = tmp;
            Log.v(TAG, "Socket was created");
        }

        public void run() {

            setName("ConnectThread" + m_socketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
            	Log.i(TAG, "Connecting to socket ...");
                m_socket.connect();
            } catch (IOException e) {
            	Log.e(TAG, e.toString());
				try {
					Log.e(TAG, "Try fallback for socket creation");
					m_socket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
					m_socket.connect();
					Log.i(TAG, "Connected");
				}catch (Exception e2) {
					Log.e(TAG, "Couldn't establish connection");
					try{
						m_socket.close();
					}catch(IOException e3){
						Log.e(TAG, "Unable to close socket: "+ m_socketType);
						e.printStackTrace();
					}  
	                connectionFailed();
	                e.printStackTrace();
	                return;
				}
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                m_ConnectThread = null;
            }

            // Start the connected thread
            connected(m_socket, mmDevice, m_socketType);
        }

        public void cancel() {
            try {
                m_socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     * Constantly reading, must call a separate method to write
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket m_socket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
    	private String m_tempPackage;
    	private boolean m_waitingForEndPackage;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            m_socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            m_tempPackage = null;
            m_waitingForEndPackage = false;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                Log.i(TAG, "Got input/output Stream");
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                	
                    Log.i(TAG, "Message received: " + new String(buffer, 0, 100));
                    String readMessage = new String(buffer, 0, bytes);
                    
                    if(m_waitingForEndPackage)
                	{
                    	m_tempPackage = m_tempPackage.concat(readMessage);
                    	Log.i(TAG, "temp package: " + m_tempPackage);
                    	if(readMessage.contains(IppaPackages.ENDOFPACKAGE))
                        {	
                    		
                    		// Send the obtained bytes to the UI Activity
                    		m_handler.obtainMessage(Constants.MESSAGE_READ, m_tempPackage.length(), -1, m_tempPackage.getBytes())
                            .sendToTarget();
                        }
                    	else
                    	{
                    		m_waitingForEndPackage = true;
                    	}
                	}
                	else 
                	{
                		// New package (whole or partial)
                		m_tempPackage = readMessage;
                		Log.i(TAG, "temp package: " + m_tempPackage);
                		if(readMessage.contains(IppaPackages.ENDOFPACKAGE))
                		{
                			// Send the obtained bytes to the UI Activity
                			m_handler.obtainMessage(Constants.MESSAGE_READ, m_tempPackage.length(), -1, m_tempPackage.getBytes())
                            .sendToTarget();
                		}
                		else
                    	{
                    		m_waitingForEndPackage = true;
                    	}
                	}       

                    // Send the obtained bytes to the UI Activity
                    
                } catch (IOException e) {
                	Log.e(TAG, "Couldn't read from in stream: Connection Lost");
                	Log.e(TAG, e.toString());
                	e.printStackTrace();
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                m_handler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                m_socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
