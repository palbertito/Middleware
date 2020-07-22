import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OperadorInterfaz extends Remote {

    // Interfaz del objeto remoto

    public static final String LOOKUP_NAME = "Sensor_Service";

    /*
     * Devuelve el número de unidades de producto vendidas, deberá indicarse el ID
     * de la tienda
     */
    public int TotalizarVentas(int id) throws RemoteException;

    /*
     * Registra una venta realizada, decrementa en 1 el inventario del producto se
     * debe indicar id de la tienda que realiza la venta. Devuelve si se ha
     * realizado correctamente la venta o no. En caso de que en el inventario haya 0
     * unidades de producto, el método devuelve la excepcion RoturaStock
     */
    public boolean RealizarVenta(int id) throws RoturaStock, RemoteException;

    /*
     * Devuelve los ingresos de una tienda determinada por su id (en euros)
     */
    public int TotalizarIngresos(int id) throws RemoteException;

    /*
     * Registra tienda el almacen central para que este tenga constancia de la
     * tienda. Devuelve si la operación se ha hecho correctamente o no
     */
    public boolean RegistrarTienda(int id) throws RemoteException;

    /*
     * Realiza un pedido de productos a Almacen para incrementar el inventario
     * en 10 unidades.
     */
    public boolean RealizarPedido() throws RemoteException;

    /*
     * Se añade la tienda para enviar información del estado del inventario.
     */
    public void connect(TiendaCallback d) throws RemoteException;

}
