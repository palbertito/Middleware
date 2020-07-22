import java.rmi.*;

/** Client -- the interface for the client callback */
public interface TiendaCallback extends Remote {
	public void alert() throws RemoteException, NotBoundException;
}
// END main
