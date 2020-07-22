import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/*
  Tienda tiene dos funcionalidades:
  -Programa principal para el cliente
  -Servidor para uso remoto de Tienda
*/

public class Tienda extends UnicastRemoteObject implements TiendaCallback {

    protected final static String host = "localhost";

    // Al ser un objeto remoto no requerimos de constructor
    public Tienda() throws RemoteException {
        super();
    }

    // Iniciamos el programa
    public static void main(String[] args)
            throws RemoteException, IOException, NotBoundException, InterruptedException, RoturaStock {
        new Tienda().iniciar();
    }

    public void iniciar() throws IOException, NotBoundException, InterruptedException, RoturaStock {

        // Se crea el scanner para insertar los datos
        Scanner input = new Scanner(System.in);
        // Variable usada para salir del bucle
        boolean exit = false;

        // Inicializamos Tienda para poder ser invocada de forma remota
        Naming.rebind("Tienda", this);

        // Buscamos Almacen
        OperadorInterfaz servicio = (OperadorInterfaz) Naming
                .lookup("rmi://" + host + "/" + OperadorInterfaz.LOOKUP_NAME);

        // Registramos la tienda
        System.out.println("Ingresar el id con la que se desea registrar la tienda: ");
        int id = input.nextInt();
        System.out.println(servicio.RegistrarTienda(id));

        // Nos conectamos para recibir avisos del inventario
        servicio.connect(this);

        // Bucle principal
        do {

            System.out.println("Seleciona una operacion a realizar:");
            System.out.println("1 - Venderproductos\n2 - Calcularventas\n3 - Calcularingresos\n4 - Salir");

            int opcion = input.nextInt();
            switch (opcion) {
            case 1:
                System.out.println("Seleccione el numero de ventas: ");
                int num = input.nextInt();
                if(num <= 0){
                    System.out.println("Venta no realizada");
                    break;
                }
                for (int i = 0; i < num; i++) {
                    servicio.RealizarVenta(id);
                }
                System.out.println("Venta realizada");
                break;
            case 2:
                // Obtenemos las ventas
                System.out.println("El número de ventas totales para la tienda es: ");
                System.out.println(servicio.TotalizarVentas(id));
                break;
            case 3:
                // Obtenemos los ingresos
                System.out.println("El número de ingresos totales para la tienda es: ");
                System.out.println(servicio.TotalizarIngresos(id));
                break;
            case 4:
                // Terminamos con la ejecucion del bucle
                System.out.println("Procediendo a salir...");
                exit = true;
                break;
            default:
                // Fallo en la entrada
                System.out.println("Fallo en la entrada vuelva a intentarlo");
                break;
            }

        } while (!exit);
        // Se cierra el Scanner
        input.close();
    }

    /*
     * Tienda Callback para informar de poco inventario y realizar pedido de inventario
     */
    @Override
    public void alert() throws RemoteException, NotBoundException {
        Registry registro = LocateRegistry.getRegistry();
        OperadorInterfaz servicio = (OperadorInterfaz)registro.lookup(OperadorInterfaz.LOOKUP_NAME);
        System.out.println("Inventario bajo, se piden 10 productos: " + servicio.RealizarPedido());        

    }
}
