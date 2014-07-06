package mimesic.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import mimesic.baseDeDatos.BBDD;
import mimesic.conexion.AppEngine;
import mimesic.conexion.Hosting;
import mimesic.criptografia.Entity;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Chat extends ListActivity {

	 private Button enviar;
	 private Hosting host;
	 private Bundle extras;
	 private String  dest;
	 private String yo;
     private Handler handler = new Handler();
     boolean isPaused = false;
     private  Entity yoEnt;
     private  BBDD db;
     private  String[] from;
     private  int[] to;
     private  ListView lista;
     private static String KEY_SUCCESS = "success";
     private  TextView contacto;
     private  String tex;
     private Long id;
     private String textoACifrar;
     private AppEngine comun;
     private  boolean pertenece=true;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        extras = getIntent().getExtras();
        setTitle(extras.getString("nombre")); // Ponemos de titulo el nombre del contacto
        
        yoEnt = Entity.getObjeto(); // Recuperamos nuestro Entity
        yo = Entity.quienSoy(); // Recuperamos nuestro numero de telefono
        db = new BBDD(getApplicationContext());
        lista = (ListView) findViewById( android.R.id.list);
        host = new Hosting();
       
        dest = 	extras.getString("telefono"); // El telefono del destinatario
        lista.refreshDrawableState();
        contacto = (TextView) findViewById(R.id.LblContactoMimesic);
        
        JSONObject json = host.perteneceNumero(dest); // Comprobamos si el destinatario esta registrado en MIMESIC
		
		 try {
		      if (json.getString(KEY_SUCCESS) != null) {
		         
		          String res = json.getString(KEY_SUCCESS);
		           if(Integer.parseInt(res)!= 1){ // NO esta registrado
		          	 
		          	 contacto.setText("Este contacto no tiene cuenta MIMESIC");
		          	 pertenece=false;
		          
		   	       }
		      }
		  }catch (JSONException e) {
		      e.printStackTrace();
		  } 
         
       	
        handler.postDelayed(runnable, 5000); // Cada 5 segundos actualizara 
        
        leerMsg();
        enviarMsg();    
        
    }  
    
    private Runnable runnable = new Runnable() {
  
    	   public void run() {
    	
    			   leerMsg();
    			   enviarMsg(); 
    			   lista.refreshDrawableState();
    			   handler.postDelayed(this, 5000);
    
    	      
    	   }
    	};
    
    @Override
    public void onBackPressed() {
   
    	handler.removeCallbacks(runnable);
    	isPaused = true;
    	
        Intent i = new Intent(getApplicationContext(),
                Main.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    
    	return;
    	}
  
   
    
///////////////////////////// LEER MENSAJES ////////////////////////////
    
    public void leerMsg(){
    	  
        Cursor c = db.obtenerMensajes(yo, dest);
        from = new String[]{"texto", "fecha", "enviado"};
        to = new int[]{R.id.LblTitulo, R.id.LblSubTitulo, R.id.check};

        CustomCursorAdapter adapter = new CustomCursorAdapter(this, android.R.id.list, c, from, to);
        lista.setAdapter(adapter);
                                 
        registerForContextMenu(lista);
                       

    }
    
    
    /////////////////////////// ENVIAR MENSAJE ///////////////////////////////////////  
    public void enviarMsg(){
    	
    	  enviar = (Button) findViewById(R.id.btnEnviarMsg);
          enviar.setOnClickListener(new View.OnClickListener() {
          	public void onClick(View v) {	
          		 host = new Hosting();     
          	
          		 
          		EditText texto = (EditText) findViewById(R.id.boxMensaje);
         		tex = texto.getText().toString();
	           	           		
	          		DateFormat dateFormat = new SimpleDateFormat("dd/MMM HH:mm");
	          		Calendar cal = Calendar.getInstance();
	           		String fecha = dateFormat.format(cal.getTime());
	          		
	          			         
	     	        id = db.addMensaje(yo, dest, tex, fecha, "0");
	     	        textoACifrar = yo + "-->" + fecha +"-->"+ tex;
	     	          	        
	     	         new MiTarea().execute(id);
		 
          		 //////////////////////////////////////
          		 

  	             Intent i = new Intent(getApplicationContext(), Chat.class);
  	             i.putExtra("telefono", dest);
  	             i.putExtra("nombre", extras.getString("nombre"));
  	           	           
  	             startActivity(i);
  	             finish();
  		          				
  			}
  		});
    }
    
     
    @SuppressWarnings("unused")
    protected class CustomCursorAdapter extends SimpleCursorAdapter  {
       
		private int layout; 
        private LayoutInflater inflater;
      	private Context context;

        public CustomCursorAdapter (Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            this.layout = layout;
            this.context = context;
            inflater = LayoutInflater.from(context);

        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
        
            View v = inflater.inflate(R.layout.chatlista, parent, false);

            return v;
        }

        @Override
        public void bindView(View v, Context context, Cursor c) {
                   
            String emisor = c.getString(1);
        	String texto = c.getString(2);
            String fecha = c.getString(3);
         	String enviado = c.getString(4);
			
           
            TextView tex = (TextView) v.findViewById(R.id.LblTitulo);
            TextView fech = (TextView) v.findViewById(R.id.LblSubTitulo);
            ImageView imagen = (ImageView)v.findViewById(R.id.check);
         
    	  
            
            if (emisor.equals(yo)){
				tex.setGravity(Gravity.RIGHT); // Mensajes enviados los mostraos a la derecha
				fech.setGravity(Gravity.RIGHT);	
					
				if (enviado.equals("1")){
				
					imagen.setVisibility(View.VISIBLE); // Mostramos imagen del Check
				}
			}
            else{
            	tex.setGravity(Gravity.LEFT); // Mensajes recibidos los mostramos a la izquierda
				fech.setGravity(Gravity.LEFT);	
            }
            
            if (tex != null) {
                tex.setText(texto);
            }   
            if (fech != null) {
                fech.setText(fecha);
            }   
        }
    
    }
   
	
	
  
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = 	(AdapterContextMenuInfo) item.getMenuInfo();
    	switch (item.getItemId()) {
    	  	case R.id.CtxLstOpc1:
    	  		 
    	  		
    	  		
    	  		db.eliminarMensaje(info.id);
    	  		
    	  
    	  		Intent i = new Intent(getApplicationContext(),
                        Chat.class);
             i.putExtra("telefono", dest);
    
             i.putExtra("nombre", extras.getString("nombre"));
             startActivity(i);
             finish();
    	  		
     	default:
    	return super.onContextItemSelected(item);
    	}
    }
    
   
    @SuppressWarnings("unused")
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)  {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	   	  	    
	    if(v.getId() == R.id.LblTitulo)
	    	inflater.inflate(R.menu.menu, menu);
	    else if(v.getId() == android.R.id.list){
	    	AdapterView.AdapterContextMenuInfo info =
	    			(AdapterView.AdapterContextMenuInfo)menuInfo;
	    	menu.setHeaderTitle("Escoja una opcion");
	        inflater.inflate(R.menu.menu, menu);
	    }
    }
    
    
    // Clase para hacer tarea en segundo plano
    private class MiTarea extends AsyncTask<Long, Void, Boolean>{
    		 
    		     		 
    		          protected Boolean doInBackground(Long... id) {
    		            
    		        	     Hosting host = new Hosting();
    		          		 byte[] cifrado = yoEnt.encrypt(textoACifrar.getBytes(), new String[]{dest});
    		          		 byte[] aBase = Base64.encode(cifrado);
    		          		 String cifradoS = new String(aBase);
    		          	

    		          		 JSONObject json = host.enviarMensaje(dest, cifradoS);
    		          		
    		          		 if (!pertenece){ // Enviamos SMS al destinatario
    		          			String url = "http://www.mimesicapp.appspot.com/enviarinvitacion"; 
    		 		          	comun = new AppEngine();
    		 		          	comun.enviarInvitacion(url, yo, dest);
    		          		 }
    		          		 
    		          		 try {
    						      if (json.getString(KEY_SUCCESS) != null) {
    						         
    						          String res = json.getString(KEY_SUCCESS);
    						           if(Integer.parseInt(res) == 1){
    						          	 
    						          	  db.modificarMensaje(id[0]);
    						         
    						   	       }
    						           else{
    						        	   
    						           }
    						      }
    						  }catch (JSONException e) {
    						      e.printStackTrace();
    						  } 
    		          		 
    		          		 return true;
    		        	  
    		          }
    		 
    		        
    		    }
    
   
}
