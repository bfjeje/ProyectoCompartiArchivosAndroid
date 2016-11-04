package com.cursoandroid.proyectoenviodearchivos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Conexiones extends Activity implements OnClickListener {

	private Button btnBuscarDispositivo;
	private ListView lvDispositivos;
	private Button btnBluetooth;
	private BluetoothAdapter bAdapter;
	private ArrayList<BluetoothDevice> arrayDevices;
	
	private final int REQUEST_READ_PHONE_STATE=15987;

	// Instanciamos un BroadcastReceiver que se encargara de detectar si el
	// estado
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
					// al que a�adimos un par
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
			// Cada vez que se descubra un nuevo dispositivo por Bluetooth, se
			// ejecutara
			// este fragmento de codigo
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Acciones a realizar al descubrir un nuevo dispositivo

				// Si el array no ha sido aun inicializado, lo instanciamos
				if (arrayDevices == null)
					arrayDevices = new ArrayList<BluetoothDevice>();

				// Extraemos el dispositivo del intent mediante la clave
				// BluetoothDevice.EXTRA_DEVICE
				BluetoothDevice dispositivo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				// A�adimos el dispositivo al array
				arrayDevices.add(dispositivo);

				// Le asignamos un nombre del estilo NombreDispositivo
				// [00:11:22:33:44]
				String descripcionDispositivo = dispositivo.getName() + " [" + dispositivo.getAddress() + "]";

				// Mostramos que hemos encontrado el dispositivo por el Toast
				Toast.makeText(getBaseContext(),
						getString(R.string.DetectadoDispositivo) + ": " + descripcionDispositivo, Toast.LENGTH_SHORT)
						.show();

			}

			// Codigo que se ejecutara cuando el Bluetooth finalice la busqueda
			// de dispositivos.
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				// Acciones a realizar al finalizar el proceso de descubrimiento
				// Instanciamos un nuevo adapter para el ListView mediante la
				// clase que acabamos de crear
				ArrayAdapter arrayAdapter = new BluetoothDeviceArrayAdapter(getBaseContext(),
						android.R.layout.simple_list_item_2, arrayDevices);

				lvDispositivos.setAdapter(arrayAdapter);
				Toast.makeText(getBaseContext(), "Fin de la b�squeda", Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conexiones);
		btnBluetooth = (Button) findViewById(R.id.btnBluetooth);
		btnBuscarDispositivo = (Button) findViewById(R.id.btnBuscarDispositivo);
		lvDispositivos = (ListView) findViewById(R.id.lvDispositivos);
		
		Bitmap miBitmap = getIntent().getExtras().getParcelable("imageBitmap");

		// Obtenemos el adaptador Bluetooth. Si es NULL, significara que el
		// dispositivo no posee Bluetooth, por lo que deshabilitamos el boton
		// encargado de activar/desactivar esta caracteristica.
		bAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bAdapter == null) {
			btnBluetooth.setEnabled(false);
			return;
		}

		// Comprobamos si el Bluetooth esta activo y cambiamos el texto del
		// boton dependiendo del estado.
		if (bAdapter.isEnabled())
			btnBluetooth.setText(R.string.DesactivarBluetooth);
		else
			btnBluetooth.setText(R.string.ActivarBluetooth);

		registrarEventosBluetooth();

		btnBuscarDispositivo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (R.id.btnBuscarDispositivo) {
				case R.id.btnBuscarDispositivo: {
					if (arrayDevices != null)
						arrayDevices.clear();

					// Comprobamos si existe un descubrimiento en curso. En caso
					// afirmativo, se cancela.
					if (bAdapter.isDiscovering())
						bAdapter.cancelDiscovery();

					// TODO puede que haya problema aca
					// Iniciamos la busqueda de dispositivos y mostramos el
					// mensaje de que el proceso ha comenzado
					if (bAdapter.startDiscovery())
						Toast.makeText(v.getContext(), "Iniciando b�squeda de dispositivos bluetooth",
								Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(v.getContext(), "Error al iniciar b�squeda de dispositivos bluetooth",
								Toast.LENGTH_SHORT).show();
					break;
				}
				}
			}
		});
		
		//Aca empieza lo de enviar el archivo al dispositivo
		lvDispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
				ConnectThread miConexion = new ConnectThread(arrayDevices.get(position));
				miConexion.run();
			}
		
		});
//		(new AdapterView.OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				
//				//Enviar el archivo al dispositivo con la posicion
//				ConnectThread miConexion = new ConnectThread(arrayDevices.get(position));
//				miConexion.run();
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			
//		});
//
//	}
	}
	private void registrarEventosBluetooth() {
		// Registramos el BroadcastReceiver que instanciamos previamente para
		// detectar los distintos eventos que queremos recibir
		IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		this.registerReceiver(bReceiver, filtro);

		// Registramos el BroadcastReceiver que instanciamos previamente para
		// detectar las distintos acciones que queremos recibir
		IntentFilter filtro2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filtro2.addAction(BluetoothDevice.ACTION_FOUND);

		this.registerReceiver(bReceiver, filtro2);

	}

	@Override
	public void onClick(View v) {

	}

	// Ademas de realizar la destruccion de la actividad, eliminamos el registro
	// del
	// BroadcastReceiver.
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(bReceiver);
	}

	public class BluetoothDeviceArrayAdapter extends ArrayAdapter {

		private List<BluetoothDevice> deviceList; // Contendra el listado de
													// dispositivos
		private Context context; // Contexto activo

		public BluetoothDeviceArrayAdapter(Context context, int textViewResourceId, List<BluetoothDevice> objects) {
			// Invocamos el constructor base
			super(context, textViewResourceId, objects);

			// Asignamos los parametros a los atributos
			this.deviceList = objects;
			this.context = context;
		}

		@Override
		public int getCount() {
			if (deviceList != null)
				return deviceList.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int position) {
			return (deviceList == null ? null : deviceList.get(position));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if ((deviceList == null) || (context == null))
				return null;
			// Usamos un LayoutInflater para crear las vistas
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// Creamos una vista a partir de simple_list_item_2, que contiene
			// dos TextView.
			// El primero (text1) lo usaremos para el nombre, mientras que el
			// segundo (text2)
			// lo utilizaremos para la direccion del dispositivo.
			View elemento = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);

			// Referenciamos los TextView
			TextView tvNombre = (TextView) elemento.findViewById(android.R.id.text1);
			TextView tvDireccion = (TextView) elemento.findViewById(android.R.id.text2);
			// Obtenemos el dispositivo del array y obtenemos su nombre y
			// direccion, asociandosela
			// a los dos TextView del elemento
			BluetoothDevice dispositivo = (BluetoothDevice) getItem(position);
			if (dispositivo != null) {
				tvNombre.setText(dispositivo.getName());
				tvDireccion.setText(dispositivo.getAddress());
			} else {
				tvNombre.setText("ERROR");
			}

			// Devolvemos el elemento con los dos TextView cumplimentados
			return elemento;
		}
	}
	
	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;

	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;

	        
	        try{
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);

	        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
	        	//TODO
	            ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
	        } else {
	        	try {
		        	TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			        String uuid = tManager.getDeviceId();
		            // MY_UUID is the app's UUID string, also used by the server code
		            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
		        } catch (Exception e) {
		        	System.out.println(e.toString());
		        }
	        }
	        }
	        catch(Exception e){
	        	System.out.println(e.toString());

	        }
	        mmSocket = tmp;

	        
	    }

	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        bAdapter.cancelDiscovery();

	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }

	        // Do work to manage the connection (in a separate thread)
	        ConnectedThread con = new ConnectedThread(mmSocket);
	        con.run();
	    }

	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
	    switch (requestCode) {
	        case REQUEST_READ_PHONE_STATE:
	            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
	                //TODO
	            }
	            break;

	        default:
	            break;
	    }
	}
	
	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;

	    //TODO Poner otro parametro que sea la imagen a enviar
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;

	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }

	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }

	    public void run() {
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes; // bytes returned from read()

	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);
	                // Send the obtained bytes to the UI activity
//	                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//	                        .sendToTarget();
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }

	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }

	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
}
