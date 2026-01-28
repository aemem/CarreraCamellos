package red;

import java.net.*;
import java.util.Enumeration;

public class UtilsRed {
    public static NetworkInterface encontrarInterfaz(InetAddress grupo){
        try {

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while(interfaces.hasMoreElements()){
                NetworkInterface interfaz = interfaces.nextElement();

                if (!interfaz.isUp() || interfaz.isLoopback()) continue;

                for (InterfaceAddress iadd : interfaz.getInterfaceAddresses()){
                    InetAddress direccion = iadd.getAddress();
                    if (direccion instanceof Inet4Address) {
                        byte[] dir = direccion.getAddress();
                        if((dir[0] & 0xFF) == 192 || (dir[0] & 0xFF) == 10){
                            return interfaz;
                        }
                    }

                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Direccion NO encontrada");
        return null;
    }
}
