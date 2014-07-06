package GoogleAppEngine;

import java.io.IOException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.*;

import org.bouncycastle.util.encoders.Base64;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.Date;


@SuppressWarnings("serial")
public class RecibirPin extends HttpServlet {
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		doPost(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	   
	        String numero = req.getParameter("numero");
	        String pin = req.getParameter("pin");
	        String pass = req.getParameter("pass");
	        
	        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	        Query q = new Query("bbdd");
	        q.setFilter(new Query.FilterPredicate("numero",FilterOperator.EQUAL, numero));
	        q.addSort("numero", SortDirection.ASCENDING );
	        q.addSort("date", SortDirection.DESCENDING );
	        
	        PreparedQuery pq = ds.prepare(q);
	        
	        List<Entity> l = pq.asList(FetchOptions.Builder.withLimit(1));
	        if (!l.isEmpty()){
			        Entity e = l.get(0);
			       
			        String pin2 =  (String) e.getProperty("pin");
			        Date fecha = (Date) e.getProperty("date");
			        Date fechaActual = new Date();
			        
			        
			        if (pin.equals(pin2)){
			        	if ( (fechaActual.getTime() - fecha.getTime() ) > 900000 ){
			        		resp.setContentType("text/plain");
				    		resp.getWriter().println("expirado");
				        	
			        	}
			        	else {
			        	
				        	HIBE hibe = new HIBE("hibe");
				    		Entidad root = hibe.getEntity2(new String[0]);
				    		
				    		Entidad a = root.derive(new String[] {numero});
				    		String entity = a.guardarSave();
				    		
				    		byte [] cif = encryptAES(entity.getBytes(), pass.getBytes()); 
				        	byte[] aBase = Base64.encode(cif);
				        	
				        	String cifrado = new String(aBase);
				        	
				        	resp.setContentType("text/plain");
				    		resp.getWriter().println(cifrado);
			        	}
			        }
			        else{
			        	resp.setContentType("text/plain");
			    		resp.getWriter().println("error");
			        	
			        }
	        
	        }
	        else{
	        	resp.setContentType("text/plain");
	    		resp.getWriter().println("No ha solicitado clave");
	        	
	        }
	     
	       
	}
	
	public byte[] encryptAES(byte[] clave, byte[] key){
		byte[] cipherText = null;
		try{
			assert(key.length >= 16);
			
			SecretKeySpec aes_key = new SecretKeySpec(key, 0, 16, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, aes_key);
			
			cipherText = cipher.doFinal(clave);
		}
		catch(Exception e){
			System.err.println("Fallo al inicializar el cipher: " + e);
		}
		
		return cipherText;
	}
	
	
	
	
}
