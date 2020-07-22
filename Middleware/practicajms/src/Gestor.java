//Step 1:
//Import the JMS API classes.
import javax.jms.Queue;
import javax.jms.*;
//Import the classes to use JNDI.
import javax.naming.*;
import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Gestor {

    public static Connection myConn; 
    public static Session mySess;   
    private static ArrayList<Noticia> listaNoticias = new ArrayList<Noticia>();// donce se almacenan las noticias
    private static ArrayList<Login> Clientes = new ArrayList<Login>();// donde se almacenan los clientes
    private static Scanner scan = new Scanner(System.in);

    static class TextListener implements MessageListener {

        private boolean done = false;

        public void onMessage(Message message) {
            if (message instanceof ObjectMessage) {
                ObjectMessage objetoRec = (ObjectMessage) message;
                try {
                    // si es login
                    if (objetoRec.getObject() instanceof Login) {
                        Login login =(Login) objetoRec.getObject();
                        actualizarCliente(login);

                    } else if (objetoRec.getObject() instanceof Peticion) {
                        // si es peticion
                        Peticion peticion = (Peticion) objetoRec.getObject();
                        if(comprobarCliente(peticion)){
                        ArrayList<Noticia> noticiasAEnv = null;
                        switch (peticion.getNumFil()) {
                        case 1:
                            System.out.println("filtro tematica");
                            noticiasAEnv = filtrarTematica(peticion.getProp());
                            break;
                        case 2:
                            System.out.println("filtro fecha");
                            noticiasAEnv = filtrarFecha(peticion.getCad()[0], peticion.getCad()[1]);
                            break;
                        case 3:
                            System.out.println("filtro palabras claves");
                            noticiasAEnv = filtrarPalClave(peticion.getProp());
                            break;
                        default:
                            System.out.println("Filtro no reconocido");
                        }

                        // Enviar respuesta al mismo cliente que envio la peticion
                        for(int i=0;i<noticiasAEnv.size();i++){
                        Destination replyDestination = message.getJMSReplyTo();
                        MessageProducer replyProducer = mySess.createProducer(replyDestination);
                        ObjectMessage respuestaACliente = mySess.createObjectMessage();
                        respuestaACliente.setObject(noticiasAEnv.get(i));
                        respuestaACliente.setJMSCorrelationID(message.getJMSMessageID());
                        replyProducer.send(respuestaACliente);
                        }
                    }
                        //Envio de mensaje para indicar fin de comunicacion (no quedan mas noticias por enviar)
                        Destination replyDestination = message.getJMSReplyTo();
                        MessageProducer replyProducer = mySess.createProducer(replyDestination);
                        ObjectMessage respuestaACliente = mySess.createObjectMessage();
                        respuestaACliente.setObject(new Noticia("Fin comunicacion",null,null,null));
                        respuestaACliente.setJMSCorrelationID(message.getJMSMessageID());
                        replyProducer.send(respuestaACliente);
                    }

                } catch (Exception e) {
                    System.out.println("Exception in onMessage(): " + e.toString());
                }
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
            //Estructura tipica de jms
            ConnectionFactory myConnFactory;
            Topic myTopic;

            myConnFactory = new com.sun.messaging.ConnectionFactory();

            myConn = myConnFactory.createConnection();

            mySess = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //donde se reciben los mensajes
            myTopic = new com.sun.messaging.Topic("world");
            
            //creamos consumidor y ponemos a escuchar
            MessageConsumer myMsgConsumer = mySess.createConsumer(myTopic);
            TextListener textListener = new TextListener();
            myMsgConsumer.setMessageListener(textListener);

            myConn.start();

            System.out.println("Waiting for Message ... ");

            boolean val = false;
            String inputString;
            String inputaux;

            // interfaz para introducir noticia
            while (!textListener.done) {

                val = false;
                System.out.println("Por favor, escriba que metodo prefiere para" + " introducir las noticias");
                System.out.println("1->Mediante archivo 2->Mediante pantalla");

                while (!val) {
                    inputString = scan.nextLine();
                    switch (inputString) {
                        case "1":
                            System.out.println("¿Cual es la direccion de su archivo?");
                            System.out.println("Por favor, escriba la direccion absoluta");
                            System.out.println("Recuerde que el formato es el siguiente:");
                            System.out.println("Titulo|Fecha|PalabrasClave(separadas por espacios)|" + "CuerpoDeLaNoticia");
                            System.out.println("Cada noticia en una linea independiente y todo en un .txt");
                            // inputaux recibe la ruta del archivo
                            inputaux = scan.nextLine();
                            insNotArchivo(inputaux);
                            val = true;
                            break;
                        case "2":
                            insertarPantalla();
                            val = true;
                            break;
                        default:
                            System.out.println("Por favor vuelva a escribir su seleccion");
                    }
                }
            }

            // se cierra la sesion conexion y scanner
            scan.close();
            mySess.close();
            myConn.close();

        } catch (Exception jmse) {
            System.out.println("Exception occurred : " + jmse.toString());
            jmse.printStackTrace();
        }

    }

    /**
     * Metodo que comprueba si un cliente (apartir de su peticion) esta en la lista de clientes de gestor,
     * devolviendo false o true
     */
    private static boolean comprobarCliente(Peticion cliente) {
        String id = cliente.getId_user();
        boolean val = false;
            for (Login i : Clientes){
                if (i.getIdUser().equals(id)) {
                 val = true;
                }
            }
         return val;
        }

    /**
     * Metodo que actualiza la lista de clientes
     * recive un objeto login
     */
    private static void actualizarCliente(Login login) {
        boolean encontrado = false;
        for (int i = 0; !encontrado && i < Clientes.size(); i++) {
            if (login.getIdUser().equals(Clientes.get(i).getIdUser())) {
                encontrado = true;
                Clientes.set(i, login);
            }
        }
        if (!encontrado)
            Clientes.add(login);
    }

    /**
     * Metodo que lee el fichero e inserta las noticias contenidas en este en la lista listaNoticias
     * Recive la ruta al fichero como String
    */
    private static void insNotArchivo(String nombreFichero) {
        String csvSplit = "|"; // Separador de campo
        String notSplit = " "; // Separador de palabras clave
        String line = "";
        Date fecha = null;
        ArrayList<String> palCl = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreFichero))) {
            while ((line = br.readLine()) != null) {
                String[] noticia = line.split(csvSplit);
                String[] palclave = noticia[3].split(notSplit);
                if (formatoValido(noticia[2]))
                    fecha = obtenerDate(noticia[2]);
                for (int i = 0; i < palclave.length; i++){
                    palCl.add(palclave[i]);
                }
                listaNoticias.add(new Noticia(noticia[1], fecha, palCl, noticia[4]));
            }
            System.out.println("Se ha anadido correctamente el fichero a la lista de noticias");
        } catch (IOException e) {
            System.err
                    .println("Se ha producido un error a la hora de anadir las noticias desde" + "el archivo indicado");
            e.printStackTrace();
        }

    }

    /**
     * Metodo que inserta las notricias por pantalla
     * no recive ni devuelve nada
     */
    private static void insertarPantalla() {
        boolean val = false;
        Date fecha = null;
        String fechas = null;
        String titulo = null;
        String palabras = null;
        String contenido = null;
        ArrayList<String> palCl = new ArrayList<>();

        // inserta el titulo de la noticia
        while (titulo == null) {
            System.out.println("Escribir titulo noticia:");
            titulo = scan.nextLine();
        }

        // insertar la tematica de la noticia
        // la tematica se guarda en el primer elemento de la lista palCl
        while (!val) {
            System.out.println("Seleccione una tematica :");
            System.out.println("1 -> Politica | 2 -> Economia | 3 -> Deportes");
            contenido = scan.nextLine();
            switch (contenido) {
            case "1":
                palCl.add("Politica");
                val = true;
                break;
            case "2":
                palCl.add("Ecomomia");
                val = true;
                break;
            case "3":
                palCl.add("Deportes");
                val = true;
                break;
            default:
                System.out.println("tematica no valida vuelva a intentarlo");
                val = false;
            }
        }

        // insertar las palabras clave en la lista palCl
        while (val) {
            System.out.println("Escribir palabras clave: \n"+"Para terminar escriba -> quit <-");
            palabras = scan.nextLine();

            if (palabras.equals("quit"))
                val = false;

            else palCl.add(palabras);
        }

        // insertar la fecha
        while (fecha == null) {
            System.out.println("Escribir fecha de publicacion con el formato dd/mm/yyyy:");
            fechas = scan.nextLine();
            if (!formatoValido(fechas))
                fechas = null;
            else
                fecha = obtenerDate(fechas);
        }

        // insertar contenido de la noticia
        System.out.println("Escriba el contenido :");
        contenido = scan.nextLine();

        // insertar la noticia en la lista listaNoticias
        listaNoticias.add(new Noticia(titulo, fecha, palCl, contenido));
        System.out.println("Noticia agregada con exito!");
    }

    /**
     * Metodo que comprueba si el formato del String pasado es correcto o no,
     * devolviendo false o true
     */
    private static boolean formatoValido(String Date) {
        Date fecha = null;
        fecha = obtenerDate(Date); // llama al metodo obtenerDate() para comprobar que se puede parsear
        boolean val = false;
        if (!(fecha == null))
            val = true;
        return val;
    }

    /**
     * Metodo que recive un String con la fecha y devuelve un objeto Date de ese Sting
     */
    private static Date obtenerDate(String Date) {
        Date fecha = null;
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");     // crea un nuevo formato con el especificado
            fecha = formato.parse(Date);                                       // crea Date con el String
            if (!Date.equals(formato.format(fecha))) {
                fecha = null;
            }
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return fecha;
    }


    /**
     * Metodo que filtra por tematica recive la tematica como argumento devuelve un
     * arraylist con la lista de noticias que contienen como primer argumento de la
     * lista PalClave la tematica
     */
    private static ArrayList<Noticia> filtrarTematica(String tematica) {
        ArrayList<Noticia> noticias = new ArrayList<>();
        for (Noticia i : listaNoticias) {                                      // recorre la lista listaNoticias
            if (i.getPalClave().get(0).equals(tematica))                       // comprueba si clave esta dentro de la lista PalClave
                noticias.add(i);
        }
        return noticias;
    }

    /**
     * Metodo que filtra por palabras clave recive la palabra clave como argumento
     * devuelve un arraylist con la lista de noticias que contienen la palabra clave
     * en en la lista PalClave
     */
    private static ArrayList<Noticia> filtrarPalClave(String palCl) {
        ArrayList<Noticia> noticias = new ArrayList<>();
        for (Noticia i : listaNoticias) { // recorre la lista listaNoticias
                if (i.getPalClave().contains(palCl)) { // comprueba si clave esta dentro de la lista PalClave
                    noticias.add(i);
                }
        }
        return noticias;
    }

    /**
     * Metodo que filtra por fecha recive la fecha de inicio y la fecha de fin
     * devuelve un arraylist con la lista de noticias que esten dentro del marco de
     * tiempo de initdate y enddate
     */
    private static ArrayList<Noticia> filtrarFecha(Date initdate, Date enddate) {
        ArrayList<Noticia> noticias = new ArrayList<>();
        for (Noticia i : listaNoticias) {
            if (enddate == null && i.getFecha().after(initdate))
                noticias.add(i);
            else if (initdate == null && i.getFecha().before(enddate))
                noticias.add(i);
            else if (i.getFecha().after(initdate) && i.getFecha().before(enddate)) {      // comprueba si estan dentro del
                                                                                          // marco
                noticias.add(i);
            }
        }
        return noticias;
    }
}
