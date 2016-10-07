package com.cursoandroid.proyectoenviodearchivos;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

public class PantallaPrincipal extends Activity {

	
	TextView bienvenido;
	ImageButton boton_camara;
	ImageButton boton_imagen;
	ImageButton boton_archivo;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pantalla_principal);
		
	}

}
