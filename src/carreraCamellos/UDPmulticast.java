package carreraCamellos;

import mensajes.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.Buffer;

public class UDPmulticast{
    // metodos para la comunicacion entre el servidor y TODOS los camellos
    //solo eventos de carrera
    public InetAddress ipMulticast;
    public int puerto;
    public MulticastSocket socket;

    public UDPmulticast(InetAddress ipMulticast, int puerto) throws IOException {
        this.ipMulticast = ipMulticast;
        this.puerto = puerto;
        socket.joinGroup(ipMulticast);   // MUY IMPORTANTE
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

        // 1. Preparar buffer para recibir datagrama
        byte[] recibidos = new byte[2048];
        DatagramPacket paquete = new DatagramPacket(recibidos, recibidos.length);

        // 2. Recibir paquete multicast
        socket.receive(paquete);

        // 3. Convertir bytes → objeto EventoCarrera
        ByteArrayInputStream bais = new ByteArrayInputStream(recibidos);
        ObjectInputStream ois = new ObjectInputStream(bais);

        EventoCarrera evento = (EventoCarrera) ois.readObject();

        ois.close();
        bais.close();

        return evento;
    }
}