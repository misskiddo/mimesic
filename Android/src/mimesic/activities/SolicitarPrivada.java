package mimesic.activities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mimesic.baseDeDatos.BBDD;
import mimesic.conexion.AppEngine;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SolicitarPrivada extends Activity {
   

	private  String url;
	private EditText tlf;
	private EditText pass;
	private AppEngine comun;
	private  String leersms="error";
	private  String telefono;
	private String password;
	private String passCif;
	private ProgressDialog dialog;
	private BBDD bd;
	    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.solicitarprivada);
        
        tlf = (EditText)findViewById(R.id.boxTlfPrivada);
        pass = (EditText)findViewById(R.id.boxPassPrivada);
        TextView custom = (TextView) findViewById(R.id.mimesic2);
        Typeface font = Typeface.createFromAsset(getAssets(), "silent.ttf");
        custom.setTypeface(font);

        bd = new BBDD(getApplicationContext());
		Button btn = (Button)findViewById(R.id.btnSolicitarTlfPrivada);
		comun = new AppEngine();
		dialog = new ProgressDialog(this);
        dialog.setMessage("Recibiendo PIN...");
        dialog.setTitle("Por favor, espere");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        
        btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				telefono = tlf.getText().toString();
				password = pass.getText().toString();
				try{
					passCif = sha1(password);
				}
				catch(NoSuchAlgorithmException e){
            		e.printStackTrace();
            	}
				
				 if (bd.comprobarPass(telefono, passCif)){
					 url = "http://www.mimesicapp.appspot.com/enviarpin";
					 new MiTarea().execute();
					 
				 }
				 else{
					 Toast toast3 = Toast.makeText(getApplicationContext(),
                 			 "Password incorrecta", Toast.LENGTH_LONG);
          		   toast3.show();
				 }
				
				
        
			}
		});
     }
	    
     public void enviarPIN(){
		   url = "http://www.mimesicapp.appspot.com/recibirpin";
	         String res = comun.enviarPinyPass(url, telefono, leersms, passCif); // Enviamos el pin que hemos recibido
	         System.out.println("res: "+ res);
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
	        			 Intent i = new Intent(getApplicationContext(), SolicitarClaves.class);
   		        	    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
   		                startActivity(i);
	         }
	         else { // Todo correcto
	        	 Toast toast3 =
	        			 Toast.makeText(getApplicationContext(),
	        			 "Su clave privada ha sido almacenada en su tarjeta sd con el nombre entity_" + telefono + ".txt", Toast.LENGTH_LONG);
	        			 toast3.show();
	        		
	        	 comun.guardarPrivada(res, "entity_"+telefono+".txt");
	        	 Intent i = new Intent(getApplicationContext(), Login.class);
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
	    	    		System.out.println("leersms1: " +leersms);
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
	    		        
	    		        	  comun.solicitarPin(url, telefono); 
	    		        	  
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
	    		          }
	    		          
	    		 
	    		        
	}
	    
	   	
}
