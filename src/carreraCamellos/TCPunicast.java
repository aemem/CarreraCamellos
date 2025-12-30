package carreraCamellos;
import mensajes.Mensaje;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Enumeration;
import java.util.List;

public class TCPunicast{

    // metodos para la comunicacion TCP unicast entre el servidor y UN camello , el resto, METODO EVIAR Y RECIVIR
    public Socket socket;
    public ObjectOutputStream oos;
    public ObjectInputStream ois;

    public TCPunicast(Socket socket) throws IOException {
        this.socket = socket;
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
    }

    public void enviar(Mensaje msj) throws IOException {
        oos.writeObject(msj);
        System.out.println("Mensaje tcp enviado");
    }

    public Mensaje recibir() throws IOException, ClassNotFoundException {
        Mensaje msj = (Mensaje) ois.readObject();
        System.out.println("Mensaje tcp recibido");
        return msj;
    }

    public void cerrar() throws IOException {
        oos.close();
        ois.close();
    }

    public static InetAddress encontrarDireccionLocal(String host){
        try {
            InetAddress hostAddress = InetAddress.getByName(host);

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while(interfaces.hasMoreElements()){
                NetworkInterface interfaz = interfaces.nextElement();

                if (!interfaz.isUp() || interfaz.isLoopback()) continue;

                for (InterfaceAddress iadd : interfaz.getInterfaceAddresses()){
                    InetAddress direccion = iadd.getAddress();
                    if (!(direccion instanceof Inet4Address)) continue;

                    short prefijo = iadd.getNetworkPrefixLength();
                    int mascara = getMascara(prefijo);
                    int intHost = ip4toInt(hostAddress);
                    int intLocal = ip4toInt(direccion);

                    if((intLocal & mascara) == (intHost & mascara)){
                        return direccion;
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