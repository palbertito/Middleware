# PRACTICA RMI
Es una practica de la asignatura Middleware que trata sobre RMI

En el directorio src/ encontraremos las 6 clases que forman este proyecto para cumplir sus correspondientes requisitos:

-> La clase Almacen donde encontramos la implementacion del almacen central explicado en la practica, con sus correspondientes getters, setters y metodos para manejar la información de las tiendas y su implementación del objeto remoto. En sus parámetros encontramos un entero correspondiente a la cantidad de productos en el inventario y un hashmap correspondiente a la lista de tiendas donde se relaciona su id (key) con su numero de ventas (value). Esta clase ofrece una interfaz grafica por comandos, donde al inicio de la ejecucion de esta elegiremos el numeros de productos total en el inventario.

-> La clase OperadorInterfaz.java donde se encuentra la interfaz remota que se dara a conocer publicamente para su correspondiente uso. Encontramos todos los metodos asociados al realizamiento de la practica (TotalizarVentas, RealizarVenta...)

-> La clase OperadorImpl.java donde encontramos la implementacion local de la interfaz OperadorInterfaz.java.

-> La clase Tienda.java donde se define el objeto remoto usado en la practica (Tienda), donde se implementa esta caracteristica y diferentes caracteristicas de la funcionalidad extra. Esta clase ofrece una interfaz grafica por terminal, donde al inicio se dara un id a la tienda en cuestion y tras esto podremos realizar las operaciones definidas por la practica, como Venderproductos, CalcularIngresos y CalcularVentas.

-> La clase TiendaCallback.java en la que se implementa una parte de la funcionalidad extra.

-> La clase RoturaStock.java donde encontramos la implementacion de la excepcion que debe ocurrir si en algun momento vendemos mas productos de los que hay en inventario.

EJECUCION DEL PROYECTO:

1) Antes de nada, es necesario tener JDK instalado para permitir el funcionamiento de RMI.

2) Se realiza la compilación de Almacen con "javac Almacen.java" y su posterior ejecución con "java Gestor" introducir el numero de productos en inventario e inicializar el almacen central

> javac Almacen.java
> java Almacen

3) Se compila la clase Tienda con "javac Tienda.java" y tras esto ejecutamos el programa tantas veces como tiendas queramos, a través de la ejecución de "java Tienda" en tantas terminales como Tiendas queramos.

> javac Tienda.java
> java Tienda

A) Alternativamente a este metodo para ejecutar el proyecto, hemos creado un Makefile en la carpeta src/ que si es ejecutado ahí realiza las tareas anteriormente descritas a través de estos comandos.

> cd src/
> make

COMPROBACION DE CONCURRENCIA:

1 TIENDA: OK
2 TIENDAS: OK
3 TIENDAS: OK
4 TIENDAS: OK
5 TIENDAS: OK
6 TIENDAS: OK
