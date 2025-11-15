package carreraCamellos;

import mensajes.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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

    public void enviar(EventoCarrera evento) throws IOException {
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