package mimesic.criptografia;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import it.unisa.dia.gas.jpbc.Element;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Base64;

public class Entity {

	private String[] identity;	// hierarchical identity of the entity, empty array means root
	private Element St; 		// secret
	private BigInteger st; 		//Lower-level Setup
	private Element[] Qis; 		// Q1 - Qt-1 // t=identity.length
	private IBE hibe; 			// a reference to the corresponding HIBE setup
	
	 private static Entity objeto = null;
	 private static String yo;
	   
	   public static void setObjeto(Entity newObjeto) {
	      objeto = newObjeto;
	   }
	   
	   public static Entity getObjeto() {
	      return objeto;
	   }
	   
	   public static void quienSoy(String y){
		   yo = y;
	   }
	   
	   public static String quienSoy(){
		   return yo;
	   }
	   

	// not public so only HIBE can create an entity object
	Entity(String[] identity,BigInteger st, Element St, Element[] Qis, IBE hibe)
	{
		this.identity = identity;
		this.St = St;
		this.st = st;
		this.Qis = Qis;
		this.hibe = hibe;
	}
	
	Entity(byte[] entityBytes, IBE hibe)
	{
		this.hibe = hibe;
		setFromBytes(entityBytes,0);
	}
	
	@SuppressWarnings("static-access")
	public Entity(String path, IBE hibe, String passCif)
	{
		this.hibe = hibe;
        try {
          	        
    	        BufferedReader fin = new BufferedReader(new InputStreamReader(	new FileInputStream(path)));
		    	String texto = fin.readLine();
		    	 byte[] deBase = Base64.decode(texto);
		    	 byte[] des = decryptAES(deBase, passCif.getBytes());
    	     
    	    
    		 
    	        setFromBytes( new Base64().decode(des)  ,0);
    	        fin.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        finally
        {
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
	

	String[] getIdentity()
	{
		return identity;
	}
	
	Element getSt()
	{
		return St;
	}
	
	BigInteger get_st()
	{
		return st;
	}
	
	Element[] getQis()
	{
		return Qis;
	}
	
	// n bytes id (4 bytes) , id , st, St ,Qis
	public void setFromBytes(byte[] bytes, int offset)
	{

		int idBytesLen = getIntegerFromBytes(bytes,offset); offset += 4;

		String id = null;
		try
		{
			id = new String(bytes,offset,idBytesLen, "UTF-8");
			offset += idBytesLen;
		} catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		}
		// remove initial "/" and then split on "/"
		if(id.equals("/")) 
			identity = new String[0];
		else
			identity = id.substring(1).split("/");
   
		int stBytesLen = getIntegerFromBytes(bytes,offset); offset += 4;

		byte[] stBytes = new byte[stBytesLen];
		for(int i = 0; i < stBytesLen; i++)
			stBytes[i] = bytes[offset++];
		st = new BigInteger(stBytes);

		St = hibe.getPairing().getG1().newElement();
		offset += St.setFromBytes(bytes, offset);
		St = St.getImmutable();
		ArrayList<Element> QisArrayList = new ArrayList<Element>();
		
		while(offset<bytes.length)
		{
			Element Qi = hibe.getPairing().getG1().newElement();
			offset += Qi.setFromBytes(bytes, offset);
			Qi = Qi.getImmutable();
			QisArrayList.add(Qi);
		}
		
		Qis = new Element[QisArrayList.size()];
		QisArrayList.toArray(Qis);
	}
	
	
	
	public byte[] encrypt(byte[] message, String[] identity)
	{
		return hibe.encrypt(message, identity);
	}
	
	
	public byte[] decrypt(byte[] ciphertext)
	{
		return hibe.decrypt(this, ciphertext);
	}
	
	
	
	
	byte[] toBytes()
	{
		byte[] bytes = null;
		byte[][] QiBytes = new byte[Qis.length][];
		int QiBytesLen = 0;
		for(int j=0; j <Qis.length ; j++)
		{
			QiBytes[j] = Qis[j].toBytes();
			QiBytesLen += QiBytes[j].length;
		}
		
		String id = "";
		if(identity == null || identity.length == 0)
		{
			id = "/";
		}
		{
			for(String i:identity)
			{
				id += "/" +  i;
			}
		}

		try {
			byte[] idBytes = id.getBytes("UTF-8");
			byte[] stBytes = st.toByteArray();

			byte[] StBytes = St.toBytes();

			// n bytes id (4 bytes) , id , st len (4 bytes), st, St ,Qis
			bytes = new byte[4 + idBytes.length + 4 + stBytes.length +  QiBytesLen + StBytes.length    ];
			int idBytesLen = idBytes.length;
			int offset =0 ;
			
			getIntegerBytes(idBytesLen,bytes,offset);offset+=4;
			
			System.arraycopy(idBytes, 0, bytes, offset, idBytes.length);
			offset += idBytes.length;
			
			// st
			getIntegerBytes(stBytes.length,bytes,offset); offset += 4;
			
			for(int i=0; i<stBytes.length; i++)
				bytes[offset++] = stBytes[i];

			// St
			System.arraycopy(StBytes,0,bytes,offset,StBytes.length); offset += StBytes.length;
			
			for( int j =0; j< Qis.length; j++)
			{
				System.arraycopy(QiBytes[j],0,bytes,offset,QiBytes[j].length);
				offset += QiBytes[j].length;
			}
			

			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return bytes;
	}
	
	
	
	
	
	// extracts i bytes and put them in bytes starting at offset and ending at offset+4
	static void getIntegerBytes(int integer, byte[] bytes, int offset)
	{
		int i = 0;
		for ( ; i < 4 ; i++ ) 
			bytes[ offset++ ] = (byte) ( ( integer >> ( i * 8 ) ) & 0x000000FF );
	}
	

	
	// extract the integer stored at offset - offset+4
	static int getIntegerFromBytes(byte[] bytes, int offset)
	{
		int integer = 0;
		for ( int i =0; i < 4 ; i++ ) 
		{
			int byteInt =  (bytes[ offset ] << ( i * 8 ) );
			if(byteInt < 0) byteInt += 256;
			integer += byteInt ;
			offset++;
		}
		return integer;
	}
	
	
}
