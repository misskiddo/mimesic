package mimesic.activities;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mimesic.baseDeDatos.BBDD;
import mimesic.criptografia.Entity;
import mimesic.criptografia.IBE;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
   
    private Button btnLogin;
    private Button btnLinkToRegister;
    private Button custom;
    private EditText telefono;
    private EditText password ;
    private String tlf;
	private ProgressDialog dialog;
    private String passCif;
    private TextView loginErrorMsg;
    private CheckBox cb;
    private BBDD db;

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
                 
        telefono = (EditText) findViewById(R.id.boxTlf);
        password = (EditText) findViewById(R.id.boxPass);
        cb = (CheckBox)findViewById(R.id.checkRecordar);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Iniciando sesión");
        dialog.setTitle("Por favor, espere");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
		
              
	    SharedPreferences pref = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
	    db = new BBDD(getApplicationContext());
   
		  
		String username = pref.getString("telefono", null);
		String pass = pref.getString("password", null);
		
		telefono.setText(username);
		password.setText(pass);
		
		        
		//////////////////////////////////  TITULO MIMESIC ///////////////////////////////////
        custom = (Button) findViewById(R.id.mimesic);
        Typeface font = Typeface.createFromAsset(getAssets(), "silent.ttf");
        custom.setTypeface(font);
        
        ////////////////////////////////SOLICITAR CLAVES ////////////////////////////////
      
        custom.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
	                Intent i = new Intent(getApplicationContext(),
	                        SolicitarClaves.class);
	                startActivity(i);

			}
		});
        
     
        ///////////////////////////////////////// REGISTRAR ///////////////////////////////////////////
        
       btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
                   
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
        	 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        Registro.class);
                startActivity(i);
               
            }
        });
        
       
    ////////////////////////////////////////////// LOGIN ////////////////////////////////////////////////
        
      btnLogin = (Button) findViewById(R.id.btnLogin);
      
       loginErrorMsg = (TextView) findViewById(R.id.login_error);
       btnLogin.setOnClickListener(new View.OnClickListener() {
	 
    	   public void onClick(View view) {
    		   
    		   tlf = telefono.getText().toString();
    		   String pass = password.getText().toString();
    		   try {
            	   passCif = sha1(pass);
            	}
            	catch(NoSuchAlgorithmException e){
            		e.printStackTrace();
            	}
    		 
    		  /* if (db.conectado(tlf)){
    			   Intent I = new Intent(getApplicationContext(), Main.class);
                   I.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(I);
                   finish();
    		   }
    		   else{
    			   db.iniciarSesion(tlf);
    			   new MiTarea().execute();
    			  
    		   }*/
    		   new MiTarea().execute();

    }
});
    

}
    
     
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {                              
            Intent i = new Intent();
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            android.os.Process.killProcess(android.os.Process.myPid());
           
        }
        return false;
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
	        	  if (db.comprobarPass(tlf, passCif)){
	        		  
	    			   Entity.quienSoy(telefono.getText().toString());
	                   
	                   SharedPreferences prefs =
	        				   getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
					    SharedPreferences.Editor editor = prefs.edit();

			                   if (cb.isChecked()){
			                   	   
			               				   editor.putString("telefono", telefono.getText().toString());
			               				   editor.putString("password", password.getText().toString());
			               				   editor.commit();
			               		   
			                   }
			                   else {
			                   	editor.clear();
			                   	editor.commit();
	                   	
			                   	}
	                   
	         /////////////////////////////////////// HIBE /////////////////////////////////////////
			                   try{
			                  
			               	   File ruta_sd = Environment.getExternalStorageDirectory();
			         		      
			        		       IBE hibe = new  IBE(ruta_sd.getAbsolutePath()+"/MIMESIC/ibe.txt");
			        		       Entity yo;
			        		       String tlf = telefono.getText().toString();   		     
			        		      
			        		       yo = new Entity(ruta_sd.getAbsolutePath()+"/MIMESIC/entity_"+tlf +".txt", hibe, passCif); 
			        		               		     
			          		       Entity.setObjeto(yo);
			          		       
			          		   
			          		       String x = Math.random()+"";
			          		      
			          		       
			          		       byte[] numCif = yo.encrypt(x.getBytes(),new String[]{tlf});
			          		       byte[] num = yo.decrypt(numCif);
			          		       String x2 = new String(num);
			          		    
			          		       if(!x.equals(x2)){
			          		    	  return false;
			              		  
			          		       }
			          		       
			          		       
			          		       
			          		     Intent I = new Intent(getApplicationContext(), Main.class);
			                     I.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			                     startActivity(I);
			                     finish();
			                 
			                   }
			                   catch(Exception e){
			                	   e.printStackTrace();
			                	  
			                	   Toast toast3 = Toast.makeText(getApplicationContext(),
			                     			 "Hay un error con las claves. Pruebe a descargarlas de nuevo", Toast.LENGTH_LONG);
			              		   toast3.show();
			              		 Intent I = new Intent(getApplicationContext(), Login.class);
			                     I.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			                     startActivity(I);
			                     finish();
			                   }
	                   		   
	                 
	                   return true;
	    		   }
	    		   else{
	    			   	    			  
	    			   return false;
	    			  
	    		   }
	        
	        	 
	          		 
	          		
	        	  
	          }
	          
	         
	       
	          protected void onPostExecute(Boolean res) {
	        	  if (res){
	        	  	 dialog.dismiss();
	        	  
	        	  }
	        	  else{
	        		  dialog.dismiss();
	        		  loginErrorMsg.setText("Número de telefono y/o password incorrectos");
	        	  }
	          }
	          
	 
	        
}


	        
     


    
}
    
 