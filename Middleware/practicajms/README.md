# PracticaJMS

Proyecto para la asignatura Middleware.

En este proyecto se implementa un Servicio de Noticias, donde los clientes podrán consultar las noticias y los gestores insertar las noticias para que sean distribuidas.

En el directorio raíz encontraremos las cinco clases que forman este proyecto para cumplir sus correspondientes requisitos:

-> La clase Gestor que ofrecerá una interfaz de usuario para insertar noticias individualmente mediante pantalla o varias por fichero.

-> La clase Cliente que proporciona una interfaz de usuario para loguearse con un nombre de usuario y un tipo de suscripción y posteriormente la consulta de noticias mediante temática, palabra clave o fecha.

-> La clase Noticia se corresponde al objeto Noticia que crearemos al insertar las noticias o que consultaremos desde Cliente.

-> La clase Petición que sirve para enviar las peticiones de consulta de noticias desde Cliente a Gestor.

-> La clase Login que se corresponde al objeto Login que envía Cliente a Gestor para que este compruebe que el usuario está registrado y por tanto, si puede consultar noticias.


EJECUCIÓN DEL PROYECTO:

1) Se debe realizar la descarga de OpenMq y su descompresión en el equipo y arrancar el servidor.

2) Se debe añadir en el CLASSPATH del directorio donde se vaya a ejecutar el programa todas las librerias correspondientes a JMS.

3) Se realiza la compilación de Gestión con "javac Gestor.java" y su posterior ejecución con "java Gestor" para insertar las noticias.

4) Se compila la clase Cliente con "javac Cliente.java" y su posterior ejecución con "java Cliente" para loguearse en el servicio de noticias y realizar las consultas.
