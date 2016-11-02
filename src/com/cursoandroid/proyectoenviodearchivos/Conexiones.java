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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Conexiones extends Activity implements OnClickListener {

	private Button btnBuscarDispositivo;
	private ListView lvDispositivos;
	private Button btnBluetooth;
	private BluetoothAdapter bAdapter;
	private ArrayList<BluetoothDevice> arrayDevices;

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

				// Añadimos el dispositivo al array
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
				// Instanciamos un nuevo adapter para el ListView mediante la clase que acabamos de crear
			    ArrayAdapter arrayAdapter = new BluetoothDeviceArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_2, arrayDevices);
			 
			    lvDispositivos.setAdapter(arrayAdapter);
			    Toast.makeText(getBaseContext(), "Fin de la búsqueda", Toast.LENGTH_SHORT).show();
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
				switch(R.id.btnBuscarDispositivo){
			case R.id.btnBuscarDispositivo:
			{
			    if(arrayDevices != null)
			        arrayDevices.clear();
			 
			    // Comprobamos si existe un descubrimiento en curso. En caso afirmativo, se cancela.
			    if(bAdapter.isDiscovering())
			        bAdapter.cancelDiscovery();
			 
			    //TODO puede que haya problema aca
			    // Iniciamos la busqueda de dispositivos y mostramos el mensaje de que el proceso ha comenzado
			    if(bAdapter.startDiscovery())
			        Toast.makeText(v.getContext(), "Iniciando búsqueda de dispositivos bluetooth", Toast.LENGTH_SHORT).show();
			    else
			        Toast.makeText(v.getContext(), "Error al iniciar búsqueda de dispositivos bluetooth", Toast.LENGTH_SHORT).show();
			    break;
			}
				}
			}
		});

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
		 
		    this.registerReceiver(bReceiver, filtro);
		    
		    
		
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

	public class BluetoothDeviceArrayAdapter extends ArrayAdapter
	{
	 
	    private List<BluetoothDevice> deviceList; // Contendra el listado de dispositivos
	    private Context context;                    // Contexto activo
	 
	    public BluetoothDeviceArrayAdapter(Context context, int textViewResourceId,
	                                        List<BluetoothDevice> objects) {
	        // Invocamos el constructor base
	        super(context, textViewResourceId, objects);
	 
	        // Asignamos los parametros a los atributos
	        this.deviceList = objects;
	        this.context = context;
	    }
	    @Override
	    public int getCount()
	    {
	        if(deviceList != null)
	            return deviceList.size();
	        else
	            return 0;
	    }
	    @Override
	    public Object getItem(int position)
	    {
	        return (deviceList == null ? null : deviceList.get(position));
	    }
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent)
	    {
	    	if((deviceList == null) || (context == null))
	    	    return null;
	    	// Usamos un LayoutInflater para crear las vistas
	    	LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	// Creamos una vista a partir de simple_list_item_2, que contiene dos TextView.
	    	// El primero (text1) lo usaremos para el nombre, mientras que el segundo (text2)
	    	// lo utilizaremos para la direccion del dispositivo.
	    	View elemento = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
	    	 
	    	// Referenciamos los TextView
	    	TextView tvNombre = (TextView)elemento.findViewById(android.R.id.text1);
	    	TextView tvDireccion = (TextView)elemento.findViewById(android.R.id.text2);
	    	// Obtenemos el dispositivo del array y obtenemos su nombre y direccion, asociandosela
	    	// a los dos TextView del elemento
	    	BluetoothDevice dispositivo = (BluetoothDevice)getItem(position);
	    	if(dispositivo != null)
	    	{
	    	    tvNombre.setText(dispositivo.getName());
	    	    tvDireccion.setText(dispositivo.getAddress());
	    	}
	    	else
	    	{
	    	    tvNombre.setText("ERROR");
	    	}
	    	 
	    	// Devolvemos el elemento con los dos TextView cumplimentados
	    	return elemento;
	    }
	}
}
