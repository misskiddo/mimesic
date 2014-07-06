package mimesic.activities;

import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mimesic.baseDeDatos.BBDD;
import mimesic.conexion.Hosting;
import mimesic.criptografia.Entity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Main extends TabActivity {	
    static private TabHost mTabHost;
    private Resources mResources;	
    private Entity yoEnt;
    private String yo;
    private Timer myTimer;
	private static final int NOTIF_ALERTA_ID = 1;
	private BBDD db;
	int num=0;
	private Hosting host;
	
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
       mTabHost = getTabHost();       
       mResources = getResources();
      
      
       addTabDos();
       addTabUno();
       addTabTres();
     
      
       mTabHost.setCurrentTab(0);  
       
       yo = Entity.quienSoy();
       yoEnt = Entity.getObjeto();
       db = new BBDD(getApplicationContext());
		
	   myTimer = new Timer();
 			myTimer.schedule(new TimerTask() {			
 				@Override
 				public void run() {
 					TimerMethod();
 				}
 				
 			}, 0, 5000);

     
       
       
    }	
    private void addTabUno() {
       Intent intent = new Intent(this, Contactos.class);
       TabSpec spec = mTabHost.newTabSpec("Contactos");
       spec.setIndicator("", mResources
                         .getDrawable(R.drawable.pestana1));
       spec.setContent(intent);
       mTabHost.addTab(spec);
    }
    
   
    private void addTabDos() {
      Intent intent = new Intent(this, Conversaciones.class);
      TabSpec spec = mTabHost.newTabSpec("Conversaciones");
      spec.setIndicator("", mResources
                         .getDrawable(R.drawable.chat2));
      spec.setContent(intent);
      mTabHost.addTab(spec);
    }
    
    private void addTabTres() {
        Intent intent = new Intent(this, Configuracion.class);
        TabSpec spec = mTabHost.newTabSpec("Configuracion");
        spec.setIndicator("", mResources
                           .getDrawable(R.drawable.confi));
        spec.setContent(intent);
        mTabHost.addTab(spec);
      }
    
    /*
	   @Override
	    public void onBackPressed() {
	        finish();

	    	}
	   */
	   private void TimerMethod()
		{
	    
			this.runOnUiThread(Timer_Tick);
		}


		private Runnable Timer_Tick = new Runnable() {
			public void run() {
			try{
				obtenerMensajesHostinger();
			}
			catch(Exception e){
				
			}
			}
		};
	

  
	public void Notificar(int j){
		num +=j;
		  //Obtenemos una referencia al servicio de notificaciones
       String ns = Context.NOTIFICATION_SERVICE;
       NotificationManager notManager =  (NotificationManager) getSystemService(ns);
       
     //Configuramos la notificación
       int icono = R.drawable.logo;
       CharSequence textoEstado = "Mensaje nuevo!";
       long hora = System.currentTimeMillis();
       Notification notif =  new Notification(icono, textoEstado, hora);
       
     //Configuramos el Intent
       Context contexto = getApplicationContext();
       CharSequence titulo = "Tienes mensajes nuevos";
       CharSequence descripcion = num + " mensajes nuevos.";
       Intent notIntent = new Intent(contexto,Main.class);
       PendingIntent contIntent = PendingIntent.getActivity( contexto, 0, notIntent, 0);
       notif.setLatestEventInfo( contexto, titulo, descripcion, contIntent);
     
       notif.flags |= Notification.FLAG_AUTO_CANCEL;
       notif.defaults |= Notification.DEFAULT_SOUND;
	   notif.defaults |= Notification.DEFAULT_VIBRATE;
	   notif.defaults |= Notification.DEFAULT_LIGHTS;
		
	   notManager.notify(NOTIF_ALERTA_ID, notif);

	}

  public void obtenerMensajesHostinger(){
	
	   host = new Hosting();
	  
	   String ultid = db.getMaxId(yo);
	   
	  
      String json = host.leerTodos(yo, ultid);
   
                        
                try{
                   	
                   JSONArray jArray = new JSONArray(json);
                   JSONArray array = new JSONArray();
                      
                       JSONObject json_data = new JSONObject();
                                                                
                       if (jArray.length()==0){ // Esta vacio
                       	
                       }
                       else{
                       	Notificar(jArray.length());
                       }
                       for(int i=0;i<jArray.length();i++){
                       	   array = jArray.getJSONArray(i);
                       	   for (int j=0; j<array.length();j++){
                       		   json_data = array.getJSONObject(j);
                       		   
                       		    String id = json_data.getString("idmensajes");
                       		 
                         		String textoCifrado= json_data.getString("texto");
                         	                         			   	        
  		                   	    byte[] deBase = Base64.decode(textoCifrado);
  		                   	                 	  
  		                   	    String cuerpo = new String(yoEnt.decrypt(deBase));
  		                   	  
  		                   	    
  		                   	    StringTokenizer st = new StringTokenizer(cuerpo, "-->" ); 
  		                   	    String emisor = st.nextToken();
  		                   	    String fecha = st.nextToken();
  		                   	    String texto = st.nextToken();
  		                   	               	      
  		                     	db.addMensaje(emisor, yo, texto, fecha,id);	 
  		                     	if (id.compareTo(ultid)>0){
  		                     		db.setMaxId(yo, id);
  		                     		
  		                     	}
                       		
                       	   }
                       	
                       }

       	       
            } catch (JSONException e) {
           e.printStackTrace();
       }

  }
 
	 
    
  }
