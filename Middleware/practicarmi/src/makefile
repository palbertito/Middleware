sourcefiles = \
Almacen.java \
OperadorInterfaz.java \
OperadorImpl.java \
RoturaStock.java \
Tienda.java \
TiendaCallback.java

classfiles  = $(sourcefiles:.java=.class)
#classfiles = Simple3.class Simple2.class Simple1.class

all: $(classfiles)

%.class: %.java
	javac -d ../clases -classpath . $<

clean:
	rm -f *.class
