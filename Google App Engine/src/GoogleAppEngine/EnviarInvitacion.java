package GoogleAppEngine;

import java.io.IOException;
import javax.servlet.http.*;
import com.oe.sdk.connection.SMSCConnection;
import com.oe.sdk.connection.SMSCConnectionFactory;
import com.oe.sdk.exceptions.SMSCException;
import com.oe.sdk.exceptions.SMSCRemoteException;
import com.oe.sdk.model.SMS;
import com.oe.sdk.model.SMSType;

@SuppressWarnings("serial")
public class EnviarInvitacion extends HttpServlet {
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		doPost(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	   
	        String numero = req.getParameter("numero");
	        String numero2 = req.getParameter("invitado");
	        
	        enviarSMS(numero, numero2);
	     
	       
	}
	
	 public void enviarSMS(String num1, String num2){
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
	    		String telefono = "+34" + num2;
	    		String mensaje = "Tus amigos ya utilizan MIMESIC. " + num1 +" te ha enviado un mensaje. Para leerlo, descargate MIMESIC del Play Store" ;
	    
	    		SMS sms = new SMS();
	    		sms.setSms_type(SMSType.GOLD_PLUS);
	    		sms.addSmsRecipient(telefono);
	    	 
	    		sms.setMessage(mensaje);
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
