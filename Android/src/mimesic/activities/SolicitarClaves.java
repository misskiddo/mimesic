package mimesic.activities;


import mimesic.conexion.AppEngine;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class SolicitarClaves extends Activity{

	private  String url;
	private  AppEngine comun;
	private  Button publica;
	private  Button privada;

	   @Override
	    public void onCreate(Bundle savedInstanceState) {
		 
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.solicitarclaves);
	        
	        publica = (Button) findViewById(R.id.btnClavePu);
	        privada = (Button) findViewById(R.id.btnClavePr);

	        
	        TextView custom = (TextView) findViewById(R.id.mimesic2);
	        Typeface font = Typeface.createFromAsset(getAssets(), "silent.ttf");
	        custom.setTypeface(font);
	        
	        comun = new AppEngine();
	        
	        publica.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					
					url = "http://www.mimesicapp.appspot.com/enviarhibe";
					String ibe = comun.pedirHibe(url, "hibe", "");
					String ok = comun.guardarPublica(ibe);
					if (ok.equals("ok")){
						Toast toast3 = Toast.makeText(getApplicationContext(),
		        			 "La clave publica del PKG ha sido almacenada en su tarjeta sd con el nombre ibe.txt", Toast.LENGTH_LONG);
					  
						toast3.show();
					 }
					else {
						Toast toast3 = Toast.makeText(getApplicationContext(),
			        			 "No se ha podido almacenar la clave pública. Compruebe de que tiene insertada la tarjeta sd", Toast.LENGTH_LONG);
						  
							toast3.show();
					}

				}
			});
	        
	        
	        privada.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					

	                Intent i = new Intent(getApplicationContext(),
	                		mimesic.activities.SolicitarPrivada.class);
	                startActivity(i);
							
						}
						
					});

	   }
	   

	

}

		
