//Implementacion de la excepcion que debe ocurrir si en algun momento vendemos mas productos de los que hay en inventario.
public class RoturaStock extends Exception {
    private static final long serialVersionUID = -464196277362659008L;
    public RoturaStock(String errorMessage){
        super(errorMessage);
    }
}
