import java.util.ArrayList;
import java.util.Comparator;
import java.io.Serializable;
import java.util.Date;



public class Noticia implements Serializable{

    private String titulo;
    private Date fecha;
    private ArrayList<String> palClave;
    private String contenido;


    // Constructor de la clase Noticia
    public Noticia(String titulo, Date fecha, ArrayList<String> palClave, String contenido) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.palClave = palClave;
        this.contenido = contenido;

    }

    // setters
    public void setTitulo(String title) {
        titulo = title;

    }

    public void  setFecha(Date date) {
        fecha = date;

    }

    public void setPalClave(ArrayList<String> lista) {
        palClave = lista;
    }

    public void  setContenido( String content) {
        contenido = content;

    }

    // getters
    public String getTitulo() {
        return titulo;
    }

    public Date getFecha() {
        return fecha;
    }

    public ArrayList<String> getPalClave() {
        return palClave;

    }

    public String getContenido() {
        return contenido;

    }

    // Funcion que imprime todos los atributos de la clase Noticia
    public void printNoticia(){
        System.out.println("Titulo de la noticia: " + titulo);
        System.out.println("Fecha de la noticia: " + fecha);
        System.out.println("Tematica de la noticia: " + palClave.get(0));
        System.out.println("Palabras clave de la noticia: ");
        for (int i = 1; i < palClave.size(); i++) {
            System.out.print(palClave.get(i) + " ");
        }
        System.out.println("Contendido de la noticia: " + contenido);
    }

    // Comparador por fecha
    public static Comparator<Noticia> NoticiaFechaComparator = new Comparator<Noticia>() {
        public int compare(Noticia n1, Noticia n2) {
            Date Fecha1=n1.getFecha();
            Date Fecha2=n2.getFecha();
            return Fecha1.compareTo(Fecha2);
        }
    };



}
