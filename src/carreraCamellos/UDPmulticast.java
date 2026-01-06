package carreraCamellos;

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

    // Busca la interfaz de red con la direccion multicast del grupo
    public NetworkInterface encontrarDireccionLocal(InetAddress grupoAddress){
        try {

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while(interfaces.hasMoreElements()){
                NetworkInterface interfaz = interfaces.nextElement();

                if (!interfaz.isUp() || interfaz.isLoopback()) continue;

                for (InterfaceAddress iadd : interfaz.getInterfaceAddresses()){
                    InetAddress direccion = iadd.getAddress();
                    if (!(direccion instanceof Inet4Address)) continue;

                    short prefijo = iadd.getNetworkPrefixLength();
                    int mascara = getMascara(prefijo);
                    int intHost = ip4toInt(grupoAddress);
                    int intLocal = ip4toInt(direccion);

                    if((intLocal & mascara) == (intHost & mascara)){
                        return interfaz;
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Direccion NO encontrada");
        return null;
    }

    public static int getMascara (short pre){
        return Integer.rotateLeft(0xFFFFFFFF, 32 - pre);
    }

    public static int ip4toInt(InetAddress direccion){
        byte[] bs = direccion.getAddress();
        return ((bs[0] & 0xFF) << 24)
                | ((bs[1] & 0xFF) << 16)
                | ((bs[2] & 0xFF) << 8 )
                |  (bs[3] & 0xFF);
    }
}