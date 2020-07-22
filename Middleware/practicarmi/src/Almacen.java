import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Scanner;

public class Almacen {
    /*
     * Atributos de almacen: -cantidad (int): -tiendas (HashMap <Integer, Integer>):
     * hashmap donde se almacenan nuestras tiendas con su id como key y su numero de
     * ventas como value.
     */
    private static int cantidad;
    private static HashMap<Integer, Integer> tiendas = new HashMap<Integer, Integer>();

    public Almacen(int cantidad, HashMap<Integer, Integer> tiendas) {
        Almacen.cantidad = cantidad;
        Almacen.tiendas = tiendas;
    }

    public static void main(String[] args) {
        Registry registro;
        OperadorImpl operadorImpl = null;
        try {
            operadorImpl = new OperadorImpl();
            registro = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            registro.rebind(OperadorInterfaz.LOOKUP_NAME, operadorImpl);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Scanner escaner = new Scanner(System.in);
        System.out.println(
                "Por favor, introduzca a continuacion la cantidad de producto " + "disponible en el inventario");
        int entrada = escaner.nextInt();
        System.out.printf("De acuerdo, se ha introducido la nueva cantidad (%d) " + "disponible en el inventario\n",
                entrada);
        setCantidad(entrada); // ANadimos la cantidad introducida
        operadorImpl.start();
        operadorImpl.run();
        escaner.close(); // Cerramos el escaner*/

    }

    // Getters y setters

    public static int getCantidad() {
        return cantidad;
    }

    public static void setCantidad(int cantidad) {
        Almacen.cantidad = cantidad;
    }

    public static HashMap<Integer, Integer> getTiendas() {
        return tiendas;
    }

    /*
     * Registramos una tienda con su id.
     */
    public static void addTiendas(int id) {
        tiendas.put(id, 0);
    }

    /*
     * Devuelve un booleano dependiendo si el inventario de productos es nulo o
     * no
     */
    public static boolean vacio() {
        if (cantidad == 0)
            return true;
        return false;
    }

    /*
     * Decrementa el inventario en una unidad
     */
    public static void decrementar() {
        cantidad--;
    }

    // Comprueba si existe una tienda de key id
    public static boolean tiendaExiste(int id) {
        return tiendas.containsKey(id);
    }

    /**
     * Comprobuba si el inventario es insuficiente, devolviendo true o false.
     */
    public static boolean bajo() {
        boolean bol = false;
        if (Almacen.getCantidad() <= 5)
            bol = true;
        return bol;
    }
}
