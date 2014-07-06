package mimesic.activities;


import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import mimesic.baseDeDatos.BBDD;
import mimesic.conexion.AppEngine;
import mimesic.conexion.Hosting;
import mimesic.criptografia.Entity;
import mimesic.criptografia.IBE;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
 
public class Registro extends Activity {
	private Button btnRegister;
	private EditText telefono;
	private TextView registrate;
	private EditText password, passwordC;
	private TextView registerErrorMsg;
	private AppEngine comun;
	private Entity yo;
	private IBE ibe;
    private static String KEY_SUCCESS = "success";
    private ProgressDialog dialog;
    private String leersms="error";
    private String url;
    private String tlf;
    private String pass;
    private BBDD bd;
    private String passCif;

 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
 
   
        telefono = (EditText) findViewById(R.id.boxTlf2);
        password = (EditText) findViewById(R.id.boxPass2);
        passwordC = (EditText)findViewById(R.id.boxPassConfirmar);
        btnRegister = (Button) findViewById(R.id.btnRegistrar);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);
        comun = new AppEngine();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Recibiendo PIN...");
        dialog.setTitle("Por favor, espere");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        
        bd = new BBDD(getApplicationContext());

        registrate = (TextView) findViewById(R.id.lblRegistrate);
        Typeface font = Typeface.createFromAsset(getAssets(), "silent.ttf");
        registrate.setTypeface(font);
      

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	            	
                tlf = telefono.getText().toString();
                pass = password.getText().toString();
                String passC = passwordC.getText().toString();
               
                if (!pass.equals(passC)){
                    registerErrorMsg.setText("Las contraseñas no coinciden");
                }
                else{
                	new MiTarea().execute();
                }
             }

        });
        
        
    }
    
	   public void enviarPIN(){
		   url = "http://www.mimesicapp.appspot.com/recibirpin";
	         String res = comun.enviarPinyPass(url, tlf, leersms, passCif); // Enviamos el pin que hemos recibido
	    	         
	         if (res.contains("error")){
	        	 Toast toast1 =
	        			 Toast.makeText(getApplicationContext(),
	        			 "El codigo introducido es incorrecto", Toast.LENGTH_LONG);
	           			 toast1.show();
	         }
	         else if (res.contains("No se ha hecho ninguna peticion de clave")){
	        	 Toast toast2 =
	        			 Toast.makeText(getApplicationContext(),
	        			 "No se ha hecho ninguna solicitud de codigo", Toast.LENGTH_LONG);
	        	 		 toast2.show();
	        	 
	         }
	         else if(res.contains("expirado")){
	        	 Toast toast2 =
	        			 Toast.makeText(getApplicationContext(),
	        			 "El tiempo ha expirado. Pida de nuevo la clave", Toast.LENGTH_LONG);
	        			 toast2.show();
	        			 Intent i = new Intent(getApplicationContext(), Registro.class);
   		        	    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
   		                startActivity(i);
	         }
	         else { // Todo correcto
	        	 Toast toast3 =
	        			 Toast.makeText(getApplicationContext(),
	        			 "Su clave privada ha sido almacenada en su tarjeta sd con el nombre entity_" + tlf + ".txt", Toast.LENGTH_LONG);
	        			 toast3.show();
	        	  comun.guardarPrivada(res, "entity_"+tlf+".txt");
	        	  File ruta_sd = Environment.getExternalStorageDirectory();
	        	
	        	  yo = new Entity(ruta_sd.getAbsolutePath()+"/MIMESIC/entity_"+tlf +".txt", ibe, passCif); 
	        	  Entity.setObjeto(yo);
	        	 
	        	 Intent i = new Intent(getApplicationContext(), Main.class);
       	         i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(i);
	         }
	   }
	        
	   private void leerSMS() {
	    		
	    	    Uri allMessages = Uri.parse("content://sms/");
	    	    boolean encontrado = false;
	    	    Cursor cursor = this.getContentResolver().query(allMessages, null,
	    	            null, null, null);

	    	   
	    	    	 while (cursor.moveToNext() && !encontrado) {
	 	    	    	if (cursor.getString(2).equals("MIMESIC")){
	 	    	    		leersms =  cursor.getString(11);
	 	    	    		encontrado = true;
	 	    	    	}
	 	    	    }
	    	    	
	    	   	    	   
	    	  

	    	}  
	   		    
	   static String sha1(String input) throws NoSuchAlgorithmException {
		        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		        byte[] result = mDigest.digest(input.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < result.length; i++) {
		            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		        }
		         
		        return sb.toString();
		    }
	        
	   private class MiTarea extends AsyncTask<Long, Void, Boolean>{
 
 				protected void onPreExecute() {
	 	             	dialog.setProgress(0);
	 	             	dialog.setMax(100);
	 	                dialog.show(); //Mostramos el diálogo antes de comenzar
	 	          }	
	    		 
	     		 
		        protected Boolean doInBackground(Long... id) {
		      
		        	// Añadimos el usuario a la base de datos externa
	                	Hosting host = new Hosting();
	 	                JSONObject json = host.registerUser(tlf);
	 	   
	 	                try {
	 		                if (json.getString(KEY_SUCCESS) != null) {
	 		                        String res = json.getString(KEY_SUCCESS);
	 		                        if(Integer.parseInt(res) == 1){
	 	                
						                	try {
						                	   passCif = sha1(pass);
						                	}
						                	catch(NoSuchAlgorithmException e){
							                		e.printStackTrace();
							               	}
							               
						                	bd.addSesion(tlf, passCif);
						            	               
						                	Entity.quienSoy(tlf);
				              	
			 		                 }
			 		                  else{
			 		                	  return false; 
			 		                  }
	 		                    
	 		                    }
	 		                }catch (JSONException e) {
	 		  	                    e.printStackTrace();
	 		  	            }

		        	  
		        	  
				              ///////////////////////SOLICITUD CLAVE PUBLICA PKG ////////////////////////////
		             		    
		              		url = "http://www.mimesicapp.appspot.com/enviarhibe";
		      				String ibeS = comun.pedirHibe(url, "hibe", tlf);
		      				comun.guardarPublica(ibeS);
		      				File ruta_sd = Environment.getExternalStorageDirectory();
		            		     
		           		    ibe = new  IBE(ruta_sd.getAbsolutePath()+"/MIMESIC/ibe.txt");
		            		    
		                     /////////////////////SOLICITUD CLAVE PRIVADA ////////////////////////////////
		            		    
		           		    url = "http://www.mimesicapp.appspot.com/enviarpin";
		                    comun.solicitarPin(url, tlf); 
		        	 
				        	try {Thread.sleep(5000); }
				        	catch (InterruptedException e) {}
				            
				        	while (leersms.equals("error")){
				    				 leerSMS();
				    			
				    		}
				          		 
				          	return true;
				        	  
		          }
		         
		          protected void onPostExecute(Boolean res) {
		        	  if (res){
		        	  	 dialog.dismiss();
		        	  	enviarPIN();
		        	  	
		        	  }
		        	  else{
		                registerErrorMsg.setText("Hubo un error. Inténtelo de nuevo");
		              }
		          }
		                
	}
    
}