package mimesic.criptografia;


import android.annotation.SuppressLint;
import it.unisa.dia.gas.plaf.jpbc.pairing.CurveParams;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.jpbc.Pairing;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Base64;
import it.unisa.dia.gas.jpbc.Element;

public class IBE {

	private HIBEParams hibeParams;
	
	private BigInteger secret;
	
	private String path;
	
	String getPath()
	{
		return path;
	}
	

	
	@SuppressWarnings("static-access")
	public IBE(String path)
	{
        try {
        	this.path = path;
    		FileInputStream fis = new FileInputStream(path);
    		 ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		 byte[] buf = new byte[1024];
    	        try {
    	            for (int readNum; (readNum = fis.read(buf)) != -1;) {
    	                bos.write(buf, 0, readNum); //no doubt here is 0
    	                //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
    	               
    	            }
    	        } catch (IOException ex) {
    	        	System.out.println("Error al abrir ibe.txt");
    	        }
    	        byte[] bytes = bos.toByteArray();
    		 
    		HIBEParams hibeParams = new HIBEParams(); 
    	
    		hibeParams.setFromBytes( new Base64().decode(bytes)  ,0);

    		
    		this.hibeParams = hibeParams;
    		hibeParams.pairing = PairingFactory.getPairing(hibeParams.getCurveParams());

        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        finally
        {
        }
	}
	
	
	
	
	Pairing getPairing()
	{
		return hibeParams.pairing;
	}
	public byte[] decrypt(Entity entity, byte[] ciphertext) //Ciphertext ciphertext)
	{
		Ciphertext ct = new Ciphertext(ciphertext,this);
		
		Element[] Uis = ct.get_rPis(); // U0, U2, U3, ...Ut (t items)
		Element U0 = Uis[0];

		Element St = entity.getSt();

		Element[] Qis = entity.getQis(); // Q1 - Qt-1 (t-1 items)
		
		//dividend = ˆe(U0, St) in G2
		Element dividend = hibeParams.pairing.pairing(U0, St); 
		
		
		//Pi(2..t) ˆe(Qi-1,Ui)
		Element divisor = hibeParams.pairing.getGT().newOneElement();
		for(int i = 0; i < Qis.length; i++)
		{
			divisor.mul(hibeParams.pairing.pairing(Qis[i],Uis[i+1]));
		}
		
		Element quotient = dividend.div(divisor);
		
		byte[] key = H2(quotient); 
		

		return decryptAES(ct.get_ciphertext(), key);
	}
	
	public byte[] encrypt(byte[] message, String[] identity)
	{

		// pick a random st-1 in Z/qZ
		BigInteger q = hibeParams.getCurveParams().getBigInteger("q");
		BigInteger r = random(q);

		Element[] rPis = new Element[identity.length+1]; // rP0, rP2 ... rPt, t = identity.length
		
		Element P0 = hibeParams.getP0();
		rPis[0] = P0.mul(r); // compute rP0

		Element P1 = H1("/").getImmutable();
		String ID  = "";

		for( int i =0; i< identity.length; i++)
		{
			ID += "/" + identity[i];
			rPis[i+1] = H1(ID).mul(r).getImmutable();  // rPi = r * Pi // i = 2 - t
		}
		
		//g = ˆe(Q0, P1) in G2
		Element Q0 = hibeParams.getQ0();

		Element g = hibeParams.pairing.pairing(Q0, P1); 

		Element g2r = g.pow(r);  // g^r
		
		byte[] key = H2(g2r);
		
		return (new Ciphertext(rPis,encryptAES(message, key))).toBytes();
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
	
	
	private byte[] encryptAES(byte[] message, byte[] key)
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
		    System.err.println("failed to initialize cipher: " + e);
		}

		return cipherText;
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
	
	
	//H2 : G2 -> {0,1}n
	private byte[] H2(Element element)
	{
		// Generate an hash from string (48-bit hash)
		MessageDigest md = null;
		try
		{
		    md = MessageDigest.getInstance("SHA");	
		    md.update(element.toBytes());
			return md.digest();
		}catch (NoSuchAlgorithmException e) {
			return null;
		}

	}
	
	//H1 : {0,1}* -> G1
	private Element H1(String string)
	{
		// Generate an hash from string (48-bit hash)
		try {
		    MessageDigest md = MessageDigest.getInstance("SHA");
		    byte[] bytes = null;
			try {
				bytes = string.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		    md.update(bytes);
		    byte[] hash = md.digest();
			Element h = hibeParams.pairing.getG1().newElement().setFromHash(hash, 0, hash.length);

		    return h;
		}
		catch (NoSuchAlgorithmException e) {
		    System.err.println("no such hash algorithm");
		    return null;
		}
		
	}
	
	public String toString()
	{
		String output = "";
		if(hibeParams != null)
			output += hibeParams.toString();
		if(secret != null)
			output += "\nSecret = " + secret;
		return output;
	}

	// can be called only HIBE is generated and the secret is set otherwise returns null
	public Entity getRootEntity()
	{
		if(secret == null) // it is not root
			return null;
		
		BigInteger st_1 = secret;
		
		// get the value of q to generate random integer in Z/qZ
		BigInteger q = hibeParams.getCurveParams().getBigInteger("q");
		
		String id = "/";
			
		//1. computes Pt = H1(ID1) in G1;
		Element Pt = H1(id).getImmutable();

		//sets Et’s secret point St to be St-1 + st-1 * Pt
		Element st_1_Pt = Pt.mul(st_1);
		Element St = hibeParams.pairing.getG1().newZeroElement().add(st_1_Pt).getImmutable();

		// pick a random st-1 in Z/qZ
		st_1 = random(q);

		Entity root = 	new Entity(new String[0] //  empty id array -> root  
								  ,st_1 // master secret, for root
								  ,St // S0 = identity element of G1  
								  ,new Element[0] // Q1 - Qt-1 , since for root t =0 this array is empty 
								  ,this);

		return root;
	}
	
	
	
	
	

	public HIBEParams getHIBEParams()
	{
		return hibeParams;
	}
	
  @SuppressLint("UseValueOf")
static private BigInteger random(BigInteger limit)
  {
      Random random = new Random(System.currentTimeMillis());
		BigInteger n = BigInteger.ONE;
		do {
		    String rand = (new Integer(random.nextInt(2147483647))).toString();
		    n = n.multiply(new BigInteger(rand));
		} while (n.compareTo(limit) < 0);
		return n.mod(limit);
  }

  static class HIBEParams {
  	
  	private CurveParams curveParams ;
  	private Element P0;
  	private Element Q0;

  	private Pairing pairing;

  	public void setFromBytes(byte[] source, int offset)
  	{
  		int curveParamsSize = Entity.getIntegerFromBytes(source, offset); offset += 4;
  		curveParams = new CurveParams();
  		curveParams.load(new ByteArrayInputStream(source,offset,curveParamsSize)); offset += curveParamsSize;
  		pairing = PairingFactory.getPairing(curveParams);
  		P0 = pairing.getG1().newElement();
  		int i = P0.setFromBytes(source,offset); offset += i;
  		P0 = P0.getImmutable();
  		Q0 = pairing.getG1().newElement();
  		Q0.setFromBytes(source, offset);
  		Q0 = Q0.getImmutable();
  	}
  	
  	// (curveParams len, 4 bytes) (params, P0, Q0)
  	public byte[] toBytes()
  	{
  		byte[] bytes = null;
  		try {
  			
  			byte[] curveParamBytes = curveParams.toString().getBytes("UTF-8"); //360 may vary
  			byte[] P0Bytes = P0.toBytes();    			
  			byte[] Q0Bytes = Q0.toBytes();

  			bytes = new byte[4 + curveParamBytes.length + P0Bytes.length + Q0Bytes.length ];

  			int offset = 0;
  			Entity.getIntegerBytes(curveParamBytes.length, bytes, offset);  offset += 4;
  			
  			System.arraycopy(curveParamBytes,0,bytes,offset, curveParamBytes.length  ); offset += curveParamBytes.length;
  			System.arraycopy(P0Bytes,0, bytes,offset,P0Bytes.length); offset += P0Bytes.length;
  			System.arraycopy(Q0Bytes,0, bytes,offset ,Q0Bytes.length);

  		} catch (UnsupportedEncodingException e) {
  			e.printStackTrace();
  		}

  		return bytes;
  	}
  	
  	public CurveParams getCurveParams()
  	{
  		return curveParams;
  	}
  	
  	public void setCurveParams(CurveParams curveParams)
  	{
  		this.curveParams = curveParams;
  	}
  	
  	public void setP0(Element P0)
  	{
  		this.P0 = P0;
  	}
  	
  	public Element getP0()
  	{
  		return P0;
  	}
  	
  	
  	public void setQ0(Element Q0)
  	{
  		this.Q0 = Q0;
  	}
  	
  	public Element getQ0()
  	{
  		return Q0;
  	}
  	
  	public String toString()
  	{
  		String output = "";
  		output += curveParams;
  		output += "P0 = " + P0 + "\n";
  		output += "Q0 = " + Q0;
  		return output;
  	}
  	
  }
  
  class Ciphertext {
  	Element[] rPis;
  	byte[] enc;
  	
  	Element[] get_rPis()
  	{
  		return rPis;
  	}
  	
  	byte[] get_ciphertext()
  	{
  		return enc;
  	}

  	Ciphertext(Element[] rPis,byte[] enc)
  	{
  		this.rPis = rPis;
  		this.enc = enc;
  	}
  	
  	// (int - number of rPis, rPis, bytes)
  	public Ciphertext(byte[] ciphertext, IBE hibe)
  	{
  		int offset = 0;
  		int rPisLen = Entity.getIntegerFromBytes(ciphertext, offset); offset += 4;
  		rPis = new Element[rPisLen];
  		for(int i=0; i<rPisLen ; i++)
  		{
  			rPis[i] = hibe.hibeParams.pairing.getG1().newElement();
  			offset += rPis[i].setFromBytes(ciphertext, offset);
  		}
  		
  		enc = new byte[ciphertext.length - offset];
  		System.arraycopy(ciphertext, offset, enc, 0, ciphertext.length - offset);
  		
  	}
  	
  	byte[] toBytes()
  	{
  		byte[] bytes = null;
  		
  		byte[][] rPiBytes = new byte[rPis.length][];
  		
  		int rPiBytesLen = 0;
  		for(int i = 0; i<rPis.length; i++)
  		{
  			rPiBytes[i] = rPis[i].toBytes();
  			rPiBytesLen += rPiBytes[i].length;
  		}
  		
  		bytes = new byte[4 + rPiBytesLen + enc.length];
  		
  		int offset =0;
      	// (int - number of rPis, rPis, bytes)

  		Entity.getIntegerBytes(rPis.length, bytes, offset); offset += 4;
  		
  		for(int i = 0; i<rPis.length; i++)
  		{
  			System.arraycopy(rPiBytes[i], 0, bytes, offset, rPiBytes[i].length); offset += rPiBytes[i].length ;
  		}
  		
  		System.arraycopy(enc, 0, bytes, offset, enc.length); offset += enc.length;

  		return bytes;
  	}
  }
  
 

}
