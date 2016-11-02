package com.cursoandroid.proyectoenviodearchivos;

import android.app.Activity;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button enviar;
	private Button recibir;

//	SharedPreferences sharedpreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		enviar = (Button)findViewById(R.id.boton_enviar_imagen);
		recibir = (Button)findViewById(R.id.boton_recibir_imagen);
	}

	public void EnviarImagen(View v){
		Intent i = new Intent(this,PantallaPrincipal.class);
		startActivity(i);
	}
	
	public void RecibirImagen(View v){
		Intent i = new Intent(this,RecibirImagen.class);
		startActivity(i);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
