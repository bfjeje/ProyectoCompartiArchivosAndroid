package com.cursoandroid.proyectoenviodearchivos;

import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PantallaPrincipal extends Activity {

	// nº necesario para mandar en el intent, y que me respodnan con ese numero
	public final static int REQUEST_IMAGE_CAPTURE = 1555;
	private String TAG = "INTENT_IMPLICITO";

	TextView bienvenido;
	ImageButton boton_camara, boton_archivo, boton_imagen;
	ImageView imagen_pantalla;
	Button boton_volver, boton_siguiente;
	Bitmap image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pantalla_principal);

		bienvenido = (TextView) findViewById(R.id.bienvenida_text_view);
		boton_camara = (ImageButton) findViewById(R.id.boton_camara);
		boton_imagen = (ImageButton) findViewById(R.id.boton_imagen);
		boton_archivo = (ImageButton) findViewById(R.id.boton_archivo);
		imagen_pantalla = (ImageView) findViewById(R.id.imagen_pantalla);
		boton_volver = (Button) findViewById(R.id.boton_volver);
		boton_siguiente = (Button) findViewById(R.id.boton_siguiente);

		boton_siguiente.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent_conexiones = new Intent(v.getContext(), Conexiones.class);
				intent_conexiones.putExtra("imagen", image);
				startActivity(intent_conexiones);
				
			}
		});
		
		boton_volver.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void tomarFoto(View v) {
		// Creo un intent de toma de imagen
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// esto es para verificar que exista una app que responda al intent
		if (intent.resolveActivity(getPackageManager()) != null) {
			// Inicio el intent
			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "imagen recibida");

		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			// obtengo los datos (imagen eneste caso) del bundle
			image = (Bitmap) extras.get("data");
			imagen_pantalla.setImageBitmap(image);
			// FragmentImagen fragment = new FragmentImagen();
			//
			// FragmentManager fm = getFragmentManager();
			// FragmentTransaction fragmentTransaction = fm.beginTransaction();
			// fragmentTransaction.replace(R.id.imagen, fragment);
			// fragmentTransaction.commit();

			// inserto la imagen en el ImageView

		}
	}
	
//	private void metodoVolver(View v){
//		this.finish();
//	}
	
//	private void metodoSiguiente(View v){
//		Intent intent_conexiones = new Intent(getApplicationContext(), Conexiones.class);
//		intent_conexiones.putExtra("imagen", image);
//		startActivity(intent_conexiones);
//	}

}
