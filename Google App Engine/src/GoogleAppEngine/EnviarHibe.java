package GoogleAppEngine;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class EnviarHibe extends HttpServlet {
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		doPost(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	
		
	        FileInputStream fis = new FileInputStream("hibe");
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		byte[] buf = new byte[1024];
    		try{
    			for (int readNum; (readNum= fis.read(buf)) !=-1;){
    				bos.write(buf, 0, readNum);
    				
    			}
    			
    		}
    		catch (IOException ex) 
    		{}
    		byte[] bytes = bos.toByteArray();
    		
	        String hibe = new String(bytes);
	        
	       
		
		
		resp.setContentType("text/plain");
		resp.getWriter().println(hibe);
		
		
	}
	
	
	
	
}
