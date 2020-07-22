
//Step 1:
//Import the JMS API classes.
import javax.jms.Queue;
import javax.jms.*;
//Import the classes to use JNDI.
import javax.naming.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Cliente {

	static class TextListener implements MessageListener {

		public boolean done = false;

		public void onMessage(Message message) { 
			if (message instanceof ObjectMessage) {
				ObjectMessage objetoRec = (ObjectMessage) message;
				try { 
					if(objetoRec.getObject() instanceof Noticia){ //si lo que recibimos es noticia actuamos
					
					Noticia noticia = (Noticia) objetoRec.getObject();
					if(noticia.getTitulo().equals("Fin comunicacion")){done=true;} //caso donde se marca la ultima noticia que hay que imprimir
					else{
						noticia.printNoticia(); //imprimimos noticias que van llegando
					}
	  			}
				} catch (Exception e) { // System.out.println("Exception in onMessage(): " + e.toString()); 
					e.printStackTrace(); } 
			}
	  
		}
	}
	

	/**
	 * Main method.
	 *
	 * @param args not used
	 *
	 */
	public static void main(String[] args) {

		try {
			//Estructura tipica jms
			ConnectionFactory myConnFactory;
			Topic myTopic;
			Queue replyQueue;

			myConnFactory = new com.sun.messaging.ConnectionFactory();

			Connection myConn = myConnFactory.createConnection();

			Session mySess = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);

			myTopic = new com.sun.messaging.Topic("world");

			//Estructura para el recibo de informacion de gestor a cliente
			replyQueue = new com.sun.messaging.Queue("replyQueue");

            //Donde se envian las peticiones/login		
			MessageProducer myMsgProducer = mySess.createProducer(myTopic);
			
			//Donde se reciben las noticias
			MessageConsumer myMsgConsumer = mySess.createConsumer(replyQueue);
            		TextListener textListener = new TextListener();
           		myMsgConsumer.setMessageListener(textListener);

            		myConn.start();
			
			//Se inicializa en el escaner para realizar el login

			Scanner scan = new Scanner(System.in);  
			String inputString = "";
			
			//Se preguntan los datos del usuario
			System.out.println("Nombre de usuario:"); 
			inputString = scan.nextLine();
			String idcliente = inputString;	
			inputString = "";
			//Creacion mensaje a enviar
			ObjectMessage objectMessage = mySess.createObjectMessage(); 
			//Establecer la estructura por donde se van a recibir las noticias
			objectMessage.setJMSReplyTo(replyQueue);	
			Login login;
			
			//Se pregunta el tipo de suscripcion al usuario
			while (inputString == "") {		
				System.out.println("¿Es usted un usuario free o premium?");
				inputString = scan.nextLine();
				switch (inputString) {
				case "free":
					login = new Login(idcliente, "free");
					objectMessage.setObject(login);
					break;
				case "premium":
					login = new Login(idcliente, "premium");
					objectMessage.setObject(login);
					break;
				default:
					System.out.println("Por favor, vuelva a escribir la respuesta");
					inputString = "";
				}
			}
			//Envio del mensaje
			myMsgProducer.send(objectMessage);			
			System.out.println("Se ha logueado con el nombre de: " + idcliente);	
			ObjectMessage objectMessage2 = mySess.createObjectMessage();
			objectMessage2.setJMSReplyTo(replyQueue);


			//Se inicializa la peticion para realizar una consulta y enviarlas a Gestor
			Peticion peticion=null;
			inputString = "";

			while (inputString == "") {
				System.out.println("¿Cómo quiere buscar las noticias?");
				System.out.println("1->Tematica \n 2->Fechas dd/MM/yyyy \n 3->Palabras Clave");
				inputString = scan.nextLine();
				boolean val=false;
				switch (inputString) {  
				case "1": // por tematica
				while (!val) { 
					System.out.println("Seleccione una tematica :");
					System.out.println("1 -> Politica | 2 -> Economia | 3 -> Deportes");
					inputString = scan.nextLine();
					switch (inputString) {	                                //Se selecciona un tipo de tematica y
					case "1":												//se crea una nueva peticion con el tipo de consulta
						peticion = new Peticion(idcliente, 1, "Politica");  
						val = true;
						break;
					case "2":
						peticion = new Peticion(idcliente, 1, "Ecomomia");
						val = true;
						break;
					case "3":
						peticion = new Peticion(idcliente, 1, "Deportes");
						val = true;
						break;
					default:
						System.out.println("tematica no valida vuelva a intentarlo");
						val = false;
					}
				}
					objectMessage2.setObject(peticion);			
					break;


				case "2": // por fecha
					String origen, fin;
					Date[] dates = new Date[2];
					System.out.println("Primera fecha (si no quiere pulse enter)");		
					origen = scan.nextLine();			
					if (origen == ""){dates[0] = obtenerDate("01/01/0000");}//si es vacia una fecha muy alta para simular 0
					else {dates[0]=obtenerDate(origen);}
					System.out.println("Segunda fecha (si no quiere pulse enter)");
					fin = scan.nextLine();
					if (fin == ""){dates[1] = obtenerDate("31/12/9999");}//si es vacia una fecha muy alta para simular infinito
					else {dates[1]=obtenerDate(fin);}
					peticion = new Peticion(idcliente, 2, dates);  
					objectMessage2.setObject(peticion);
					break;


				case "3": // por palabras claves
					inputString = "";
					System.out.println("Introduce palabra clave");  
					inputString = scan.nextLine();				
					peticion = new Peticion(idcliente,3,inputString);
					objectMessage2.setObject(peticion);
					break;


				default: //Si no se selecciona ninguna de las opciones se vuelve a preguntar
					System.out.println("Por favor, vuelva a escribir la respuesta");
					inputString = "";
				}
			}
			myMsgProducer.send(objectMessage2);

			while(!textListener.done){
				Thread.sleep(1000);
		            }
			
			mySess.close();
			myConn.close();
			scan.close();

		} catch (Exception jmse) {
			System.out.println("Exception occurred : " + jmse.toString());
			jmse.printStackTrace();
		}
	}

	 /**
     * Metodo que recive un String con la fecha y devuelve un objeto Date de ese
     * String
     */
	private static Date obtenerDate(String Date) {
		Date fecha = null;
		try {
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); //Se establece un nuevo formato 
			fecha = formato.parse(Date);								   //Se convierte obtiene el objeto Date a partir del String
			if (!Date.equals(formato.format(fecha))) {					
				fecha = null;
			}
		} catch (ParseException e) {
			e.printStackTrace();

		}
		return fecha;
	}
}
