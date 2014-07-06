package mimesic.baseDeDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class BBDD extends SQLiteOpenHelper {
 
    private static final String DATABASE_NAME = "BBDDMimesic";
	    
  
    public BBDD(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
 
  
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE mensajes (id INTEGER PRIMARY KEY AUTOINCREMENT, emisor TEXT, destinatario TEXT, texto TEXT, fecha TEXT, enviado TEXT, idmensajes TEXT)";
        db.execSQL(sql);    
        sql = "CREATE TABLE contactos (id INTEGER, telefono TEXT , nombre TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE login (id INTEGER, telefono TEXT , pinE TEXT, maxId TEXT)";
        db.execSQL(sql);
        
    }
    
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS mensajes");

        onCreate(db);
    }
    
  
    public long addMensaje(String e, String d, String t, String f, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
    
        values.put("emisor", e); 
        values.put("destinatario", d); 
        values.put("texto", t); 
        values.put("fecha", f); 
        values.put("enviado", "0");
        values.put("idmensajes", id);
     
        long ident = db.insert("mensajes", null, values);
        db.close();
        
        return ident;
    }
    
    public String obtenerUltimoId(String yo){
    	SQLiteDatabase db = this.getReadableDatabase();
    	
        	Cursor c = db.rawQuery("Select id, idmensajes from mensajes", null);
           	if (c.getCount() == 0){
        		db.close();
        		return "0";
       
        	}
        	else {
        		c.moveToLast();
            	String x = c.getString(1);
            	db.close();
            	return x;
        	}
	
    }
   
 
    public void resetMensajes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("mensajes", null, null);
        db.close();
        
        
    }
    public void eliminarMensaje(Long id){
    	 SQLiteDatabase db = this.getWritableDatabase();
    	 db.execSQL("delete from mensajes where id = " + id);
    	 db.close();
    	
    }
    
    public void eliminarMensajes(String usuario){
   	 SQLiteDatabase db = this.getWritableDatabase();
   	 db.execSQL("delete from mensajes where emisor = '" + usuario + "' or destinatario = '" + usuario+ "'");
   	 db.close();
   	
   }
    
    public Cursor leerConvers(String emisor){
    
    	SQLiteDatabase db1 = this.getWritableDatabase();
    	SQLiteDatabase db = this.getReadableDatabase();
    	db1.execSQL("create temp view temporal as Select emisor emi, id from mensajes where destinatario = '" + emisor + "' union Select destinatario emi, id from mensajes where emisor = '" + emisor + "'");
    	db1.execSQL("create temp view temporal2 as select emi, id from temporal order by id DESC");
 
    	Cursor c = db.rawQuery("Select distinct emi from temporal2", null);
    	c.moveToFirst();

    	
    	db.close();
    	
    	return c;
    	
    }

    
    public Cursor obtenerMensajes(String emisor, String destinatario){
  	  SQLiteDatabase db = this.getReadableDatabase();
  	Cursor c = db.rawQuery("SELECT id _id, emisor, texto, fecha, enviado from mensajes where( (emisor = '" + destinatario + "' and destinatario = '"+ emisor + "') or (emisor = '" + emisor + "' and destinatario = '"+ destinatario + "') )", null);
  	c.moveToFirst();
 
  	
  	db.close();
  	return c;
    	
    }
    
    public void modificarMensaje(Long id){
    	 SQLiteDatabase db = this.getWritableDatabase();
    	 db.execSQL("update mensajes set enviado='1' where id ="+id);
    	 
    }
    
      
    
    /////////////////////// login////////////////////////////
    public void addSesion(String telefono, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        String cero = "0";
        
        db.execSQL("INSERT INTO login (telefono, pinE, maxId) " +
        		 "VALUES ('" + telefono +"','"+ pin+ "', '"+ cero +"')");    

        db.close(); 
    }
    
          
 public void modificarPass(String tlf, String pass){
	  SQLiteDatabase db = this.getReadableDatabase();
	  db.execSQL("update login set pinE='"+pass +"' where telefono ='"+tlf+"'");
	 
 }
 
 public boolean comprobarPass(String tlf, String pass){
	  SQLiteDatabase db = this.getReadableDatabase();
	  Cursor c = db.rawQuery("SELECT * from login where telefono = '"+ tlf +"' and pinE = '" + pass + "'" , null);
	
	  if (c.getCount() == 0){ // Si el cursor esta vacio, es porque la consulta no ha devuelto nada
		  db.close();
		  return false;
	  }
	  else{
		  db.close();
		  return true;
	  }
	  
	 
 }
 
 public void setMaxId(String yo, String max){
	 SQLiteDatabase db = this.getWritableDatabase();
   	 db.execSQL("update login set maxId='"+max +"' where telefono ='"+yo+"'");
	
 }
 
 public String getMaxId(String yo){
	  SQLiteDatabase db = this.getReadableDatabase();
	  String id;
 	Cursor c = db.rawQuery("SELECT maxId from login where telefono = '"+ yo +"'", null);
 	c.moveToFirst();
 	 if (c.getCount() == 0){ // Si el cursor esta vacio, es porque la consulta no ha devuelto nada
		  db.close();
		  return "0";
	  }
 	 else {
 		 id = c.getString(0);
 		 db.close();
 		 return id;
 	 }
 	
 		
	 
 }

    
    
    
    ////////////////////// contactos /////////////////////////////
    
 public void addContacto(String id, String telefono, String nombre) {
     SQLiteDatabase db = this.getWritableDatabase();
    
     
     db.execSQL("INSERT INTO contactos (id, telefono, nombre) " +
     		 "VALUES ('" + id + "', '" + telefono +"',' "+ nombre+ "')");    

     db.close(); 
 }

 

 public void eliminarContactos() {
	  SQLiteDatabase db = this.getWritableDatabase();
      db.delete("contactos", null, null);
      db.close();
 }
 


public String obtenerNombre(String telefono){
	  SQLiteDatabase db = this.getReadableDatabase();
	  String user;
 	Cursor c = db.rawQuery("SELECT nombre from contactos where telefono = '"+ telefono +"'", null);
 	c.moveToFirst();
 	if(c.moveToFirst()){
 		user = c.getString(0);
 		
 	}
 	else{
 		user = telefono;
 		
 	}

 	db.close();
 			    	
 	return user;
	 
}
    
}