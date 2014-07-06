package mimesic.activities;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import mimesic.baseDeDatos.BBDD;
import mimesic.conexion.AppEngine;
import mimesic.criptografia.Entity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CambiarPass extends Activity {
    
	private Button btnCambiar;
	private TextView registrate;
	private EditText pass, newpass, newpass2;
	private TextView registerErrorMsg;
    private String passCif;
    private BBDD db;
    private AppEngine comun;
    private ProgressDialog dialogCarga = null;
    private Handler handler = new Handler();
    private String telefono;
    private String newpassS;
    private String newpassCif;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambiarpass);
 
   
        pass = (EditText) findViewById(R.id.boxPassActual);
        newpass = (EditText) findViewById(R.id.boxNewPass);
        newpass2 = (EditText)findViewById(R.id.boxNewPass2);
        btnCambiar = (Button) findViewById(R.id.btnCambiarPass);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);
        db = new BBDD(getApplicationContext());
        comun = new AppEngine();
       

        registrate = (TextView) findViewById(R.id.lblRegistrate);
        Typeface font = Typeface.createFromAsset(getAssets(), "silent.ttf");
        registrate.setTypeface(font);
      

        btnCambiar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	            	
            	telefono = Entity.quienSoy();
                String passS = pass.getText().toString();
                newpassS = newpass.getText().toString();
                String newpass2S = newpass2.getText().toString();
                
                if (!newpassS .equals(newpass2S )){
                    registerErrorMsg.setText("Las contraseñas no coinciden");

                }
               else{
            	   try {
                	   passCif = comun.sha1(passS);
                	   newpassCif = comun.sha1(newpassS);
                	   
                	}
                	catch(NoSuchAlgorithmException e){
                		e.printStackTrace();
                	}
            	   if (db.comprobarPass(telefono, passCif)){
            		   
            		   lanzar();
            		   db.modificarPass(telefono, newpassCif);

	        			 Toast toast3 = Toast.makeText(getApplicationContext(),
			        			 "La contraseña se ha cambiado correctamente", Toast.LENGTH_LONG);
	        			 toast3.show();
  						 
            	   }
            	   else{
            		   registerErrorMsg.setText("La contraseña actual es incorrecta");
            	   }
            	   
	            
	 
                }
            }
        });
        
        
    }
    
    public void lanzar(){
    	dialogCarga = ProgressDialog.show(this, "No retire su tarjeta sd", "Su clave privada esta siendo modificada", true); 
	
    	 new Thread() {
    		 public void run() {
    		 try{
    			 comun.descifrarArchivo(passCif, newpassS, telefono);
    		 		
    		 } catch (Exception e) { }
    		 handler.sendEmptyMessage(0);
    		 dialogCarga.dismiss(); }
    		 }.start();
    	
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
}