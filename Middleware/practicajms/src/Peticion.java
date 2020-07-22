import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Peticion implements Serializable {

    private String id_user;
    private int numFil;
    private String prop;
    private Date [] cad;

    // Constructor de peticion por tematica y palabra clave
    public Peticion (String id_user, int numFil, String prop) {
        this.id_user = id_user;
        this.numFil = numFil;
        this.prop = prop;
    }


    // Constructor de peticion por fecha
    public Peticion (String id_user, int numFil, Date [] cad) {
        this.id_user = id_user;
        this.numFil = numFil;
        this.cad = cad;
    }

    // Setters y Getters

    public Date[] getCad() {
        return cad;
    }

    public void setCad(Date[] cad) {
        this.cad = cad;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public int getNumFil() {
        return numFil;
    }

    public void setNumFil(int numFil) {
        this.numFil = numFil;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }





}
