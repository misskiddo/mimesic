package mimesic.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mimesic.baseDeDatos.BBDD;
import mimesic.criptografia.Entity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
	 
	public class Conversaciones extends ListActivity {
	

		private List <String> emi;
		private Timer myTimer;
		private BBDD db;
		private String yo;
		private ListView convers;
	
		
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        onStart();
	        setContentView(R.layout.conversaciones);
	        setTitle("Mensajes");
	        db = new BBDD(getApplicationContext());
	        		
	     
	        myTimer = new Timer();
			myTimer.schedule(new TimerTask() {			
				@Override
				public void run() {
					TimerMethod();
				}
				
			}, 0, 5000);
	        
	   
	        leerConversaciones();
	
   
	    }	
	    
	   
	    public void leerConversaciones(){
	    	
	          yo = Entity.quienSoy();
	          final List<String> datos = new ArrayList<String>();
	          emi = new ArrayList<String>();
	      
	    	 		
	          Cursor c =   db.leerConvers(yo);
	          c.moveToFirst();
	          for (int j=0; j<c.getCount() ;j++){
	    		  int col = c.getColumnIndex("emi");
	    		  String numero = c.getString(col);
	    		  String nombre = db.obtenerNombre(numero);
	              datos.add(nombre);
	              emi.add(numero);
	              c.moveToNext();
	          }   
	          
	               
	         
	                ArrayAdapter<String> adaptador =
	    	        		new ArrayAdapter<String>(this,
	    	        		android.R.layout.simple_list_item_1, datos);
	    	        		convers = (ListView)findViewById(android.R.id.list);
	    	        		convers.setAdapter(adaptador);
	    	        		
	    	        		if (datos.isEmpty()){
	    	        			
	    	        		}
	    	        		
	    	        		registerForContextMenu(convers);
	  	      
	    	        		
	    	        		convers.setOnItemClickListener(new OnItemClickListener() {
	    	        			
	    	        			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	    	        				
	    	        				String nombre = (String) convers.getItemAtPosition(position);
	    	        				String numero = (String) emi.get(position); 
	    	        				
	    	        				Intent i = new Intent(getApplicationContext(),
        			                        Chat.class);
	    	        					    	        				 
	    	        			        i.putExtra("telefono", numero);
	    	        			        i.putExtra("nombre",nombre );
	    	        			        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 
	    	        			           startActivity(i);

	    	        			}
	    	        		});		
	    	        		
	    	        	
	  	}
	
	

		    private void TimerMethod()
			{
		    	
				this.runOnUiThread(Timer_Tick);
			}


			private Runnable Timer_Tick = new Runnable() {
				public void run() {
					
					leerConversaciones();
				
				}
			};
		
	 		
		   
		   @Override
		    public boolean onContextItemSelected(MenuItem item) {
		    	AdapterContextMenuInfo info = 	(AdapterContextMenuInfo) item.getMenuInfo();
		    	switch (item.getItemId()) {
		    	  	case R.id.CtxLstOpc1:
	
		    	  	  String numero = (String) emi.get(info.position); 
  		
		    	  		db.eliminarMensajes(numero);

		    	  		Intent i = new Intent(getApplicationContext(),
		                        Main.class);
		           
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
			
			    
			    if(v.getId() == android.R.id.list){
			    	AdapterView.AdapterContextMenuInfo info =
			    			(AdapterView.AdapterContextMenuInfo)menuInfo;
			    	menu.setHeaderTitle("Escoja una opcion");
			        inflater.inflate(R.menu.menu, menu);
			    }
		    }
		    
		
		        	    
}