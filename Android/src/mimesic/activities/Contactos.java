package mimesic.activities;

import mimesic.baseDeDatos.BBDD;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.ToggleButton;

	 
	public class Contactos extends ListActivity {

	       private ListView lstLista;
		   private BBDD bbdd;
		   private SQLiteDatabase db;
		   private SimpleCursorAdapter adapter;
		   private Cursor c;
		   private   Cursor mCursor;
		   ToggleButton toggle;
		
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.contactos);
	        setTitle("Elige a un contacto");
	     	  
	        toggle = (ToggleButton)findViewById(R.id.ToggleContacto);
	        
	        
       //////////////////////////////////////////OBTENER USUARIOS BBDD ///////////////////////////
	
	        
	        lstLista = (ListView)findViewById(android.R.id.list);
	       
	        bbdd = new BBDD(getApplicationContext());
	        db = bbdd.getWritableDatabase();
	       
	       cargarLista2();
	        
	        toggle.setOnClickListener(new View.OnClickListener() {
	        	@Override
	        	public void onClick(View arg0)   	{
		        	if(toggle.isChecked())
		        		 cargarLista();
		        	else
		        		 cargarLista2();	
	        	}
	        });
	        
	        
	    	        
	    }
	    
	    
	    @Override
		    protected void onListItemClick(ListView l, View v, int position, long id) {
	    	
		        Cursor c = (Cursor) getListAdapter().getItem(position);
		     		        
		        String nombre = c.getString(1);
		        String phone = c.getString(2);
		             		 
		    		   	    
		        Intent i = new Intent(getApplicationContext(), Chat.class);
		        i.putExtra("telefono", phone);
		        i.putExtra("nombre", nombre);
		   
		      
		        startActivity(i);
		        
		    }
	    
	    private void cargarLista()
	    {
	       db = bbdd.getReadableDatabase();
	       if (db != null)
	        {
	          c = db.rawQuery("SELECT id _id, telefono, nombre FROM contactos", null);
	        
	          startManagingCursor(c);
	          adapter = new SimpleCursorAdapter(this, 
		                android.R.layout.simple_list_item_2, 
		                c, 
		                new String[] { "nombre", "telefono" }, 
		                                                                   
		                new int[] { android.R.id.text1, android.R.id.text2 } 
		                                                                       
		        );
		        setListAdapter(adapter);
		        
		        lstLista.setFastScrollEnabled(true);
		        lstLista.setTextFilterEnabled(true);
		        
		        EditText etext=(EditText)findViewById(R.id.search_box);
		        etext.addTextChangedListener(new TextWatcher() {
		            public void onTextChanged(CharSequence s, int start, int before, int count) {
		            }

		            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		            }

		            public void afterTextChanged(Editable s) {
		                ListView av = (ListView)findViewById(android.R.id.list);
		                SimpleCursorAdapter filterAdapter = (SimpleCursorAdapter)av.getAdapter();
		                filterAdapter.getFilter().filter(s.toString());
		            }
		        });
		        
		        adapter.setFilterQueryProvider(new FilterQueryProvider() {
		            public Cursor runQuery(CharSequence constraint) {
		            	  Cursor cursor ;
		            	  String value = "'%"+constraint.toString()+"%'";
		            			 
		            			  if (constraint == null  ||  constraint.length () == 0)  {
		            				  cursor = db.rawQuery("SELECT id _id, telefono, nombre FROM contactos", null);
		            				 
		            			  }
		            			  else {
		            				  cursor = db.rawQuery("SELECT id _id, telefono, nombre FROM contactos where nombre like " + value, null);
		            				 
		            			  }
		            			  

		      	        return cursor;
		                
		            }
		        });
		    
	       }

	    }
	    

	    private void cargarLista2()
	    {
	         mCursor = getContentResolver().query(
	                Data.CONTENT_URI,
	                new String[] { Data._ID, Data.DISPLAY_NAME, Phone.NUMBER,
	                        Phone.TYPE },
	                Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "' AND "
	                        + Phone.NUMBER + " IS NOT NULL", null,
	                Data.DISPLAY_NAME + " ASC");
	 
	        startManagingCursor(mCursor);
	 
	       
	         adapter = new SimpleCursorAdapter(this,
	                android.R.layout.simple_list_item_2, 
	                mCursor, 
	                new String[] { Data.DISPLAY_NAME, Phone.NUMBER }, 
	                new int[] { android.R.id.text1, android.R.id.text2 } 
	                                                                        
	        );
	        setListAdapter(adapter);
	        lstLista.setFastScrollEnabled(true);
	        lstLista.setTextFilterEnabled(true);
	        
	        EditText etext=(EditText)findViewById(R.id.search_box);
	        etext.addTextChangedListener(new TextWatcher() {
	            public void onTextChanged(CharSequence s, int start, int before, int count) {
	            }

	            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	            }

	            public void afterTextChanged(Editable s) {
	                ListView av = (ListView)findViewById(android.R.id.list);
	                SimpleCursorAdapter filterAdapter = (SimpleCursorAdapter)av.getAdapter();
	                filterAdapter.getFilter().filter(s.toString());
	            }
	        });
	        
	        adapter.setFilterQueryProvider(new FilterQueryProvider() {
	            public Cursor runQuery(CharSequence constraint) {
	            	  Cursor cursor ;
	            	  String value = "'%"+constraint.toString()+"%'";
	            			 
	            			  if (constraint == null  ||  constraint.length () == 0)  {
	            				  cursor =  getContentResolver().query(
	            			                Data.CONTENT_URI,
	            			                new String[] { Data._ID, Data.DISPLAY_NAME, Phone.NUMBER,
	            			                        Phone.TYPE },
	            			                Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "' AND "
	            			                        + Phone.NUMBER + " IS NOT NULL", null,
	            			                Data.DISPLAY_NAME + " ASC");
	            				  
	            			  }
	            			  else {
	            				  cursor = getContentResolver().query( Data.CONTENT_URI,
	          	      	                new String[] { Data._ID, Data.DISPLAY_NAME, Phone.NUMBER, Phone.TYPE },
	        	      	                Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "' AND "
	        	      	                        + Data.DISPLAY_NAME + " like " + value , null,
	        	      	                Data.DISPLAY_NAME + " ASC");
	            			  }
	            			  
	      	               
	      	 
	      	        
	      	        
	      	        return cursor;
	                
	            }
	        });


	    }
	 
	   
}