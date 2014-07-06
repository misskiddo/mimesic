package GoogleAppEngine;


import it.unisa.dia.gas.plaf.jpbc.pairing.CurveParams;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.jpbc.Pairing;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import it.unisa.dia.gas.jpbc.Element;

public class HIBE {

	private HIBEParams hibeParams;
	
	private String path;
	
	String getPath()
	{
		return path;
	}

	
	// path to hibe params
	public HIBE(String path)
	{
        try {
        	this.path = path;
    		FileInputStream fis = new FileInputStream(path);
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
    		
    		HIBEParams hibeParams = new HIBEParams(); 
    		byte[] a = new Base64().decode(bytes);
    		hibeParams.setFromBytes(a,0);
    		
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
	
	
	
	Entidad derive(Entidad ancestor, String[] identity)
	{
		// t = identity.length
		if(ancestor==null) 
            throw new IllegalArgumentException("Ancestor entity cannot be null");
		if(identity==null) 
            throw new IllegalArgumentException("identity array cannot be null");


		String id = "";
		int i=0;
		
		if(identity.length == 0)
		{
	         throw new IllegalArgumentException("Root is not derivable; use getRootEntity() or getEntity()" );

		}else
		{
			// make sure identity is a descendant of ancestor entity
			// and compute the id string
			for(; i< ancestor.getIdentity().length; i++)
			{
				if(!ancestor.getIdentity()[i].equals(identity[i]))
				{
		            throw new IllegalArgumentException("Specified identity is not a descendant of ancestor entity: " + ancestor.getIdentity()[i] + "!=" + identity[i]);
				}
				id += "/" + identity[i];
			}
		}
		
		Element Pt; // Et id mapped to G1
		Element St = ancestor.getSt(); // Et Secret Point
		
		Element[] Qis = null; // Q1 .. Qt-1
		

		Qis = new Element[identity.length];  
		
		int j=0;
		for(;j<ancestor.getQis().length;j++)
		{
			Qis[j] = ancestor.getQis()[j]; //Q1 ... Q(anscestor_level)
		}
		
		BigInteger st_1 = ancestor.get_st();
		
		// get the value of q to generate random integer in Z/qZ
		BigInteger q = hibeParams.getCurveParams().getBigInteger("q");
		
		for(;i<identity.length;i++)	 
		{
			id += "/" + identity[i];
			
			//1. computes Pt = H1(ID1; ... ; IDt) in G1;
			Pt = H1(id).getImmutable();

			//sets Ets secret point St to be St-1 + st-1 * Pt
			Element st_1_Pt = Pt.mul(st_1);
			St = St.add(st_1_Pt).getImmutable();


			//also give Et the values of Qi = si * P0 for 1 <= i <= t - 1.
			//if(i>=1) // Qis for level 0(root) and level 1 is empty
			Qis[j++] = hibeParams.getP0().mul(st_1).getImmutable(); // Q0-Qt-1
			
			// pick a random st-1 in Z/qZ
			st_1 = random(q);

		}
		
		return new Entidad(identity,st_1,St.getImmutable(),Qis, this);
	}
	
	
	

	public HIBEParams getHIBEParams()
	{
		return hibeParams;
	}
	
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
    		int curveParamsSize = Entidad.getIntegerFromBytes(source, offset); offset += 4;
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
    	
        	
    }

  
    public Entidad getEntity2(String[] identity)
    {
       
        return new Entidad("entity.txt",this);

    }
}