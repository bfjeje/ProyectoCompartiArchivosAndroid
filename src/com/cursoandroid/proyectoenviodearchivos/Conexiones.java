package com.cursoandroid.proyectoenviodearchivos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Conexiones extends Activity implements OnClickListener {


	private Button btnBluetooth;
	private BluetoothAdapter bAdapter;
	
	// Instanciamos un BroadcastReceiver que se encargara de detectar si el estado
			// del Bluetooth del dispositivo ha cambiado mediante su handler onReceive
			private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					final String action = intent.getAction();

					// Filtramos por la accion. Nos interesa detectar
					// BluetoothAdapter.ACTION_STATE_CHANGED
					if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
						// Solicitamos la informacion extra del intent etiquetada como
						// BluetoothAdapter.EXTRA_STATE
						// El segundo parametro indicara el valor por defecto que se
						// obtendra si el dato extra no existe
						final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
						switch (estado) {
						// Apagado
						case BluetoothAdapter.STATE_OFF: {
							btnBluetooth.setText(R.string.ActivarBluetooth);
							break;
						}

						// Encendido
						case BluetoothAdapter.STATE_ON: {
							// Cambiamos el texto del boton
							btnBluetooth.setText(R.string.DesactivarBluetooth);

							// Lanzamos un Intent de solicitud de visibilidad Bluetooth,
							// al que añadimos un par
							// clave-valor que indicara la duracion de este estado, en
							// este caso 120 segundos
							Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
							discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
							startActivity(discoverableIntent);

							break;
						}
						default:
							break;
						}

					}
				}
			};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conexiones);
		btnBluetooth= (Button) findViewById(R.id.btnBluetooth);
		
		// Obtenemos el adaptador Bluetooth. Si es NULL, significara que el
		// dispositivo no posee Bluetooth, por lo que deshabilitamos el boton
		// encargado de activar/desactivar esta caracteristica.
		bAdapter = BluetoothAdapter.getDefaultAdapter();
		if(bAdapter == null)
		{
		    btnBluetooth.setEnabled(false);
		    return;
		}
		
		// Comprobamos si el Bluetooth esta activo y cambiamos el texto del
		// boton dependiendo del estado.
		if(bAdapter.isEnabled())
		    btnBluetooth.setText(R.string.DesactivarBluetooth);
		else
		    btnBluetooth.setText(R.string.ActivarBluetooth);
		registrarEventosBluetooth();
		
	}

	
	
	private void registrarEventosBluetooth()
	{
	// Registramos el BroadcastReceiver que instanciamos previamente para
	// detectar los distintos eventos que queremos recibir
	IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
	this.registerReceiver(bReceiver, filtro);
	}
		
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	// Ademas de realizar la destruccion de la actividad, eliminamos el registro del
	// BroadcastReceiver.
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    this.unregisterReceiver(bReceiver);
	}

}
