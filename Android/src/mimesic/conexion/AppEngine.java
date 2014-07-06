package mimesic.conexion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.util.encoders.Base64;

import android.os.Environment;
import android.util.Log;

public class AppEngine {
	
	public AppEngine(){
		
	}
	
	public String solicitarPin(String url, String numero) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);
	    httppost.setHeader("User-Agent", "mimesic company");
	    String output = null;
	
	    try {
	        
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	        nameValuePairs.add(new BasicNameValuePair("numero", numero));
	     	     
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        HttpEntity httpEntity = response.getEntity();
            output = EntityUtils.toString(httpEntity);
        
	   	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	    return output;
	   	   
	}
	
	public String enviarPinyPass(String url, String numero, String pin, String pass ) {

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);
	    httppost.setHeader("User-Agent", "mimesic company");
	    String output = null;
	
	    try {
	        
	    	
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	        nameValuePairs.add(new BasicNameValuePair("numero", numero));
	        nameValuePairs.add(new BasicNameValuePair("pin", pin));
	        nameValuePairs.add(new BasicNameValuePair("pass", pass));
	     
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        HttpEntity httpEntity = response.getEntity();
            output = EntityUtils.toString(httpEntity);
        
	   	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	    return output;
	   	   
	}
	
	
	
	
	// Hace un post al app engine, enviandole dos parametros. El primero siempre sera el telefono
		// El segundo sera o el email, o el pin
		public String enviarInvitacion(String url, String numero, String numero2) {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(url);
		    httppost.setHeader("User-Agent", "mimesic company");
		    String output = null;
		
		    try {
		        
		    	
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("numero", numero));
		        nameValuePairs.add(new BasicNameValuePair("invitado", numero2));
		     
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        
		        HttpEntity httpEntity = response.getEntity();
	            output = EntityUtils.toString(httpEntity);
	        
		   	        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }
		    return output;
		   	   
		}
	
	
	
	// Solicitamos la clava publica del PKG
	
	 public String pedirHibe(String url, String clave, String valor){
    	 // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);
	    httppost.setHeader("User-Agent", "mimesic company");
	    String output = null;
	
	    try {
	        
	    	
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	        nameValuePairs.add(new BasicNameValuePair(clave, valor));
	     
	     
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        HttpEntity httpEntity = response.getEntity();
            output = EntityUtils.toString(httpEntity);
            
            
	       
	   	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	    return output;
    	
    }
	
	
	// Guarda la clave en el fichero indicado
	
	  public String guardarPublica(String clave){
				  
		  String ok="ok";
		  try {        	
			    File ruta_sd = Environment.getExternalStorageDirectory();
			   
		        File f = new File(ruta_sd.getAbsolutePath()+"/MIMESIC/");
		        f.mkdir();
		        
		        File fich = new File(f, "ibe.txt");
		    
		        OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(fich));
	
		    	 fout.write(clave);
				 fout.close();
		
		
			return ok;

	        }
		   catch(FileNotFoundException e){
			   e.printStackTrace();
			   return "error";
			   
		   }
	        catch (IOException ex){
	            ex.printStackTrace();
	            
	        }
		  
	      
		   return ok;
		   
	   }
	 
	  public String guardarPrivada(String clave, String nombreFi){
		  String ok="ok";
		  try {        	
			  
			    
			    File ruta_sd = Environment.getExternalStorageDirectory();
			   
		        File f = new File(ruta_sd.getAbsolutePath()+"/MIMESIC/");
		        f.mkdir();
		        
		        File fich = new File(f, nombreFi);
		    
		        OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(fich));
	
		    	 fout.write(clave);
				 fout.close();
		
		
			return ok;

	        }
		 		  
		   catch(FileNotFoundException e){
			   e.printStackTrace();
			   return "error";
			   
		   }
	        catch (IOException ex){
	            ex.printStackTrace();
	            
	        }
		   return ok;
	  }
	  
	  public void descifrarArchivo(String pass, String newpass, String tlf){
	    	try
	    	{
		    	File ruta_sd = Environment.getExternalStorageDirectory();
		    	File f = new File(ruta_sd.getAbsolutePath(), "/MIMESIC/entity_"+tlf +".txt");
		    	BufferedReader fin = new BufferedReader(new InputStreamReader(	new FileInputStream(f)));
		    	String texto = fin.readLine();
	    	    byte[] deBase = Base64.decode(texto);
	    	    byte[] des = decryptAES(deBase, pass.getBytes());
	    	    String newpassCif = sha1(newpass);

			 
				    byte[] cif = encryptAES(des, newpassCif.getBytes());
				    byte[] aBase = Base64.encode(cif);
	         		
				    
				    String cifrado = new String(aBase);
		    	 
		    	guardarPrivada(cifrado,  "entity_"+tlf +".txt");
		    	
		    	
		    	fin.close();
	    	}
	    	catch (Exception ex)
	    	{
	    	    Log.e("Ficheros", "Error al leer fichero desde tarjeta SD");
	    	    ex.printStackTrace();
	    	}
	    	
	    }
	    
	    private byte[] decryptAES(byte[] ciphertext, byte[] key)
		{
		   
			byte[] plaintext = null;
			try {
			    assert(key.length >= 16);
			   
			    SecretKeySpec aes_key = new SecretKeySpec(key, 0, 16, "AES");
			
			    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			    cipher.init(Cipher.DECRYPT_MODE, aes_key);
			    
			    plaintext = cipher.doFinal(ciphertext);
			}
			catch (Exception e) {
			    System.err.println("failed to initialize cipher: " + e);
			}
			return plaintext;
		}
	  
	  
	 public String sha1(String input) throws NoSuchAlgorithmException {
	        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
	        byte[] result = mDigest.digest(input.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < result.length; i++) {
	            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	        }
	         
	        return sb.toString();
	    }
	   
	  static byte[] encryptAES(byte[] message, byte[] key)
		{
			byte[] cipherText = null;
			try {
			    assert(key.length >= 16);
			 
			    SecretKeySpec aes_key = new SecretKeySpec(key, 0, 16, "AES");
			    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			    cipher.init(Cipher.ENCRYPT_MODE, aes_key);
			 
			     cipherText = cipher.doFinal(message);
			  
			}
			catch (Exception e) {
			    System.err.println("Error al inicializar el cipher: " + e);
			}

			return cipherText;
		}
	    
	   
	
	

}
