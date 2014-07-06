package mimesic.conexion;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

 
public class Hosting {
 
    private JSON jsonParser;
   private static String URL = "http://misskiddo.webatu.com";
   // private static String URL =  "http://tonicabrera.16mb.com";

    private static String register_tag = "register";
    private static String pertenece_tag = "pertenece";

 
    public Hosting(){
        jsonParser = new JSON();
    }
 
       
    public JSONObject registerUser(String telefono){
       
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("telefono", telefono));
    
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
     
        return json;
    }
    
    public  JSONObject cambiarPass(String telefono, String pass, String newpass){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "cambiarPass"));
        params.add(new BasicNameValuePair("telefono", telefono));
        params.add(new BasicNameValuePair("pass", pass));
        params.add(new BasicNameValuePair("newPass", newpass));
 
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
     
        return json;
    }
 
 
      
 public JSONObject enviarMensaje(String destinatario, String texto){
    	
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "enviar"));
       
        params.add(new BasicNameValuePair("destinatario", destinatario));
        params.add(new BasicNameValuePair("texto", texto));
 
       
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
      
        return json;
    }
    
      

   
    public  String leerTodos(String emisor, String id){
    	
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "leerTodos"));
        params.add(new BasicNameValuePair("emisor", emisor));
        params.add(new BasicNameValuePair("id", id));
      

        String json = jsonParser.getJSONArrayFromUrl(URL, params);
        return json;
    	
    }
    
  
    public JSONObject perteneceNumero(String telefono){
   	 List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", pertenece_tag));
        params.add(new BasicNameValuePair("telefono", telefono));
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
      
        return json;
    }
  
   
 
}