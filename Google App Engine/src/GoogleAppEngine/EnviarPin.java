package GoogleAppEngine;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import GoogleAppEngine.PMF;
import GoogleAppEngine.bbdd;

import com.oe.sdk.connection.SMSCConnection;
import com.oe.sdk.connection.SMSCConnectionFactory;
import com.oe.sdk.exceptions.SMSCException;
import com.oe.sdk.exceptions.SMSCRemoteException;
import com.oe.sdk.model.SMS;
import com.oe.sdk.model.SMSType;


public class EnviarPin extends HttpServlet {
  
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        
    	
    	String tlf = req.getParameter("numero");
      
    	Random rng = new Random();
        int dig5 = rng.nextInt(90000)+10000;
        String pin = dig5+"";
        Date date = new Date();
            
        bbdd greeting = new bbdd(tlf, pin, date);

        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(greeting);
        } finally {
            pm.close();
        }
        
       enviarSMS(pin, tlf);
       
    }
    
    public void enviarSMS(String PIN, String tlf){
    	SMSCConnection smsc_connection = null;
        try {
           smsc_connection = SMSCConnectionFactory.openConnection();
        } catch (SMSCException smsc_ex) {
        	System.err.println("error de conexion a SMStrend: "+
        			smsc_ex.getMessage());
        	return;
        }
        // operaciones de conexion
        // ...
        try {
        	smsc_connection.logout();
        } catch (SMSCRemoteException smscr_ex) {
        	System.err.println("error en cerrar la conexion a SMStrend: "+
        			smscr_ex.getMessage());
        }
    	
    	
    	try {
    		String telefono = "+34" + tlf;

    		SMS sms = new SMS();
    		sms.setSms_type(SMSType.GOLD_PLUS);
    		sms.addSmsRecipient(telefono);
    	 
    		sms.setMessage(PIN);
    	    sms.setSms_sender("MIMESIC");
    		sms.setImmediate(); 
    		
    		smsc_connection.sendSMS(sms);
		} catch (SMSCRemoteException smscre) {
			System.out.println("Exception from SMStrend server: "+smscre.getMessage());
		} catch (SMSCException smsce) {
			System.out.println("Exception creating message: "+smsce.getMessage());
		}


    	
    }
}