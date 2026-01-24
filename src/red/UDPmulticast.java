package red;

import mensajes.*;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class UDPmulticast{
    // metodos para la comunicacion entre el servidor y TODOS los camellos
    //solo eventos de carrera
    public InetAddress ipMulticast;
    public int puerto;
    public MulticastSocket socket;

    public UDPmulticast(InetAddress ipMulticast, int puerto) throws IOException {
        this.ipMulticast = ipMulticast;
        this.puerto = puerto;
    }

    public UDPmulticast(){}

    public void enviar(EventoCarrera evento) throws IOException {
        if (socket == null) System.out.println("No se ha encontrado socket");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(evento);
        oos.flush();

        byte[] datos = baos.toByteArray();

        DatagramPacket paquete = new DatagramPacket(
                datos,
                datos.length,
                ipMulticast,
                puerto
        );

        socket.send(paquete);

        oos.close();
        baos.close();
    }

    public EventoCarrera recibir() throws IOException, ClassNotFoundException {
        if (socket == null) System.out.println("No se ha encontrado socket");

        byte[] recibidos = new byte[2048];
        DatagramPacket paquete = new DatagramPacket(recibidos, recibidos.length);

        socket.receive(paquete);

        ByteArrayInputStream bais = new ByteArrayInputStream(recibidos);
        ObjectInputStream ois = new ObjectInputStream(bais);

        EventoCarrera evento = (EventoCarrera) ois.readObject();

        ois.close();
        bais.close();

        return evento;
    }


}