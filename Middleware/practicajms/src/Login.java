import java.io.Serializable;

public class Login implements Serializable {

     private String idUser;
     private String suscripcion;

    // Constructor de Login
    public Login(String idUser, String suscripcion) {
        this.idUser = idUser;
        this.suscripcion=suscripcion;
    }


    // getters y setters
    public String getSuscripcion() {
        return suscripcion;
    }


    public void setSuscripcion(String suscripcion) {
        this.suscripcion = suscripcion;
    }


    public String getIdUser() {
        return idUser;
    }



    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

}
