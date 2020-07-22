import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OperadorImpl extends UnicastRemoteObject implements OperadorInterfaz, Serializable, Runnable {

    // UID único del objeto remoto
    private static final long serialVersionUID = -6868667861505177483L;
    List<TiendaCallback> list = new ArrayList<TiendaCallback>();

    protected OperadorImpl() throws RemoteException {
        super();
    }

    /*
     * Devuelve el número de unidades de producto vendidas, deberá indicarse el ID
     * de la tienda
     */
    @Override
    public int TotalizarVentas(final int id) throws RemoteException {
        return Almacen.getTiendas().get(id);
    }

    /*
     * Registra una venta realizada, decrementa en 1 el inventario del producto se
     * debe indicar id de la tienda que realiza la venta. Devuelve si se ha
     * realizado correctamente la venta o no. En caso de que en el inventario haya 0
     * unidades de producto, el método devuelve la excepcion RoturaStock
     */
    @Override
    public boolean RealizarVenta(final int id) throws RoturaStock, RemoteException {
        if (Almacen.vacio())
            throw new RoturaStock("");
        Almacen.decrementar();
        Almacen.getTiendas().put(id, Almacen.getTiendas().get(id) + 1);
        return true;
    }

    /*
     * Registra la tienda designada por su id y la almacena en el HashMap de tiendas
     */
    @Override
    public boolean RegistrarTienda(final int id) throws RemoteException {
        Almacen.addTiendas(id);
        return Almacen.tiendaExiste(id);
    }

    /*
     * Devuelve ingresos de una tienda, indicando su id ingresos = numero de ventas
     * * 10 (siendo este número el precio de cada venta en euros)
     */
    @Override
    public int TotalizarIngresos(final int id) throws RemoteException {
        return Almacen.getTiendas().get(id) * 10;
    }

    /*
     * Realiza un pedido de productos a Almacen para incrementar el inventario en 10
     * unidades.
     */
    @Override
    public boolean RealizarPedido() throws RemoteException {
        boolean realizado = false;
        Almacen.setCantidad(Almacen.getCantidad() + 10);
        if (Almacen.getCantidad() > 5)
            realizado = true;
        return realizado;
    }

    /**
     * Incializamos el Thread para comprobar el stock y alertar a los usuarios.
     */
    public void start() {
        new Thread(this).start();
    }

    /*
     * Se añade la tienda para enviar información del estado del inventario.
     */
    public void connect(TiendaCallback da) throws RemoteException {
        System.out.println("Adding client " + da);
        list.add(da);
    }

    boolean done = false;

    public void run() {
        while (!done) {
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException unexpected) {
                System.out.println("Interrupted!");
                done = true;
            }
            Iterator<TiendaCallback> it = list.iterator();
            while (it.hasNext() && Almacen.bajo()) {
                try {
                    ((TiendaCallback) it.next()).alert();

                } catch (RemoteException |  NotBoundException re) {
                    System.out.println("Tienda eliminada");
                    System.out.println(re);
                    it.remove();
                }
            }

        }
    }



}
