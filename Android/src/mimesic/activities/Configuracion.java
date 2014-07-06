package mimesic.activities;


import org.json.JSONException;
import org.json.JSONObject;
import mimesic.baseDeDatos.BBDD;
import mimesic.conexion.Hosting;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
	 
	public class Configuracion extends ListActivity{
			 
		    private static String KEY_SUCCESS = "success";
		    private String nombre;
		    private String telefono;
		    private String idC;
		    private ListView lstOpciones;
		    private ProgressDialog dialogCarga = null;
		    private Handler handler = new Handler();
		    private Hosting host;
	
		    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.configuracion);
	     
	        
	        final String[] datos =
	        		new String[]{"Actualizar Contactos MIMESIC","Eliminar todos los mensajes", "Cambiar Contraseña","Acerca de MIMESIC", "Cerrar Sesión"};
	        
	        ArrayAdapter<String> adaptador =
	        		new ArrayAdapter<String>(this,
	        		android.R.layout.simple_list_item_1, datos);
	        		lstOpciones = (ListView)findViewById(android.R.id.list);
	        		lstOpciones.setAdapter(adaptador);
	        		
	        		lstOpciones.setOnItemClickListener(new OnItemClickListener() {
	        			
	        			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	        				
	        				String opcion = (String) lstOpciones.getItemAtPosition(position);
	        				
	        				if (opcion.equals(datos[0])){ // Refrescar Contactos
	        					lanzar();
	        					
	        				}
	        				
	        				else if(opcion.equals(datos[1])){ // reset tabla mensajes
	        					 BBDD m = new BBDD(getApplicationContext());
	        					 m.resetMensajes();
	     	        			
	     	        			 Toast toast3 = Toast.makeText(getApplicationContext(),
					        			 "Se han eliminado todos los mensajes", Toast.LENGTH_LONG);
	        						 
	        					 toast3.show();
	     	        			
	        				}
	        				else if(opcion.equals(datos[2])){ // Cambiar Contraseña
	        					Intent i = new Intent(getApplicationContext(),
        		                        CambiarPass.class);
        		                startActivity(i);
	        					
	        				}
	        				else if(opcion.equals(datos[3])){ // Acerca de MIMESIC 
	        					 Intent i = new Intent(getApplicationContext(),
	        		                        Mimesic.class);
	        		                startActivity(i);
	        					
	        				}
	        				else if (opcion.equals(datos[4])){ // Cerrar Sesion
	        					
	        					 Intent login = new Intent(getApplicationContext(), Login.class);
	        	                   login.setAction(Intent.ACTION_MAIN);
	        	                   login.addCategory(Intent.CATEGORY_HOME);
	        	                   login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	                   startActivity(login);
	        	                   android.os.Process.killProcess(android.os.Process.myPid());
	        		         	
	        				}
	        				

	        			}
	        		});
	        		
	        
	        
	    }
	    
	   
	    public void lanzar(){
	    	dialogCarga = ProgressDialog.show(this, "Por favor espere", "Cargando contactos", true); 
    	
	    	 new Thread() {
	    		 public void run() {
	    		 try{
	    		
	    		 		refrescarContactos();
	    		 } catch (Exception e) { }
	    		 handler.sendEmptyMessage(0);
	    		 dialogCarga.dismiss(); }
	    		 }.start();
	    	
	    }
	    
	    public void refrescarContactos(){
	    	  	    	
	        BBDD bbdd = new BBDD(getApplicationContext());
	        		bbdd.eliminarContactos();
	      		
        		         		
			 Cursor mCursor = getContentResolver().query(
		           Data.CONTENT_URI,
		           new String[] { Data._ID, Data.DISPLAY_NAME, Phone.NUMBER,
		                   Phone.TYPE },
		           Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "' AND "
		                   + Phone.NUMBER + " IS NOT NULL", null,
		           Data.DISPLAY_NAME + " ASC");
		      	        		
			        						
				   mCursor.moveToFirst();
						
						int numeroColumnaNombre = mCursor.getColumnIndex(Data.DISPLAY_NAME);
						int id = mCursor.getColumnIndex(Data._ID);       
						int numeroColumnaTelefono = mCursor.getColumnIndex(Phone.NUMBER);
				
				
				do{
					  	         
					  nombre = mCursor.getString(numeroColumnaNombre);
					  idC = mCursor.getString(id);
					  telefono = mCursor.getString(numeroColumnaTelefono);
					 
					  host = new Hosting();
		                JSONObject json = host.perteneceNumero(telefono);
		                try {
		                    if (json.getString(KEY_SUCCESS) != null) {
		                       
		                        String res = json.getString(KEY_SUCCESS);
		                         if(Integer.parseInt(res) == 1){
		                        	 
		                        	 bbdd.addContacto(idC, telefono, nombre);
			             	                          }
		                    }
		                }catch (JSONException e) {
		                    e.printStackTrace();
		                } 
					
					
					               
				} while(mCursor.moveToNext());
					
	    	
	    }
	    
	    
	
}