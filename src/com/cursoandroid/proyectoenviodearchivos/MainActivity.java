package com.cursoandroid.proyectoenviodearchivos;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	public EditText _nombre;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences sharedPref = this.getSharedPreferences(
		        getString(R.string.preference_file_key), this.MODE_PRIVATE);
		Fragment main = new MainFragment();
		 //main.setArguments(args);
         FragmentManager fragmentManager = getFragmentManager();
         fragmentManager.beginTransaction().replace(R.id.content_frame, main).commit();
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
	
	public void OnAceptar(View arg)
	{
		_nombre = (EditText)findViewById(R.id.etNombre);
		SharedPreferences sharedPref = getPreferences(this.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.preference_file_key), _nombre.getText().toString());
		editor.commit();
	}
}
