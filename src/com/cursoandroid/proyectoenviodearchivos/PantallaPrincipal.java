package com.cursoandroid.proyectoenviodearchivos;

import android.app.Activity;
import android.content.Context;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PantallaPrincipal extends Activity {

	// nº necesario para mandar en el intent, y que me respodnan con ese numero
	public final static int REQUEST_IMAGE_CAPTURE = 1555;
	private static int RESULT_LOAD_IMG = 15647;
	private String TAG = "INTENT_IMPLICITO";
	
	String imgDecodableString;

	TextView bienvenido;
	ImageButton boton_camara, boton_archivo, boton_imagen;
	ImageView imgView;
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
		imgView = (ImageView) findViewById(R.id.imagen_pantalla);
		boton_volver = (Button) findViewById(R.id.boton_volver);
		boton_siguiente = (Button) findViewById(R.id.boton_siguiente);

		boton_siguiente.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
				if(!image.equals(null)){
					imgView.buildDrawingCache();
					image = imgView.getDrawingCache();
					//Resize a la imagen
					image = scaleDownBitmap(image, 100, v.getContext());
					Bundle extras = new Bundle();
					extras.putParcelable("imageBitmap", image);
					
					Intent intent_conexiones = new Intent(v.getContext(), Conexiones.class);
					intent_conexiones.putExtras(extras);
					startActivity(intent_conexiones);
					}
				}catch(Exception e){
					Toast.makeText(v.getContext(), "No hay imagen", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		boton_volver.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	public void buscarImagen(View view){
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
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
	
	//Este metodo es porque la imagen puede ser muy grande, y tira error. Este metodo disminuye el tamaño de la imagen(resize)
	 public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

		 final float densityMultiplier = context.getResources().getDisplayMetrics().density;        

		 int h= (int) (newHeight*densityMultiplier);
		 int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

		 photo=Bitmap.createScaledBitmap(photo, w, h, true);

		 return photo;
		 }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
 
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
 
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
 
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));
 
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
    			Bundle extras = data.getExtras();
    			// obtengo los datos (imagen eneste caso) del bundle
    			image = (Bitmap) extras.get("data");
    			imgView.setImageBitmap(image);
    			// FragmentImagen fragment = new FragmentImagen();
    			//
    			// FragmentManager fm = getFragmentManager();
    			// FragmentTransaction fragmentTransaction = fm.beginTransaction();
    			// fragmentTransaction.replace(R.id.imagen, fragment);
    			// fragmentTransaction.commit();

    			// inserto la imagen en el ImageView

    		}
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
 
    
        
	}
	

}
