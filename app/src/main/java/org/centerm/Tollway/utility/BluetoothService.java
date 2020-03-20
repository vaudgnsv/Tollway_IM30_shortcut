package org.centerm.Tollway.utility;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothService {
	// Debugging
	private static final String TAG = "BluetoothService";

	// Intent request code
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// RFCOMM Protocol
	private static final UUID MY_UUID = UUID
			.fromString("4d414445-2d54-4841-4956-414e344d5254");

	private BluetoothAdapter btAdapter;

	private Activity mActivity;

	// Constructors
	public BluetoothService(Activity ac) {
		mActivity = ac;

		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}


	public void enableBluetooth() {
		Log.i(TAG, "Check the enabled Bluetooth");

		if (btAdapter.isEnabled()) {
			Log.d(TAG, "Bluetooth Enable Now");
			scanDevice();
		} else {
			Log.d(TAG, "Bluetooth Enable Request");

			Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
		}
	}

	public void scanDevice() {
		Log.d(TAG, "Scan Device");

		Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
		mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}


	public BluetoothSocket getDeviceSocket(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		// BluetoothDevice device = btAdapter.getRemoteDevice(address);
		BluetoothDevice device = btAdapter.getRemoteDevice(address);

		Log.d(TAG, "Get Device Info \n" + "address : " + address);

		BluetoothSocket tmp = null;

		try {
			tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			Log.e(TAG, "create() failed", e);
		}

		btAdapter.cancelDiscovery();

		try {
			tmp.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tmp;
	}

}