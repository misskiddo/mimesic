package GoogleAppEngine;

import com.google.appengine.api.datastore.Key;

import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class bbdd {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    public String numero;

       
    @Persistent
    public String pin;

    @Persistent
    private Date date;

    public bbdd(String n, String p, Date date) {
        this.numero = n;
    	
        this.pin = p;
        this.date = date;
    }

    public Key getKey() {
        return key;
    }

    public String getPin() {
        return pin;
    }

   

    public Date getDate() {
        return date;
    }

    public void setPin(String p) {
        this.pin = p;
    }

    

    public void setDate(Date date) {
        this.date = date;
    }
}