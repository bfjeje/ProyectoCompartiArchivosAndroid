package com.cursoandroid.proyectoenviodearchivos;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Conexiones extends Activity {

	public final static int REQUEST_ENABLE_BT = 6464;
	Bitmap bmp;
	BluetoothAdapter adaptadorBT;
	ListView conexioneslistView;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> arrayList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conexiones);
		conexioneslistView = (ListView)findViewById(R.id.conexioneslistView);
		arrayList = new ArrayList<String>();
		Bundle extras = getIntent().getExtras();
		bmp = (Bitmap) extras.getParcelable("imagebitmap");
		conexioneslistView.setAdapter(adapter);

		adaptadorBT = BluetoothAdapter.getDefaultAdapter();
		if (adaptadorBT == null) {
		    Toast.makeText(this, "Su dispositivo no tiene Bluetooth", Toast.LENGTH_SHORT);
		}else{
			if (!adaptadorBT.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
			reconocerDispositivos();
		}
		if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED){
		    Toast.makeText(this, "Bluetooth no pudo activarse", Toast.LENGTH_SHORT);
		}
	}
	
	private void reconocerDispositivos(){
		Set<BluetoothDevice> pairedDevices = adaptadorBT.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		        adapter.add(device.getName() + "\n" + device.getAddress());
		        adapter.notifyDataSetChanged();
		    }
		}
	}
}
