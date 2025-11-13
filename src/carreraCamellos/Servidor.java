package carreraCamellos;

import mensajes.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Servidor{

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int puertoTCP = 12345;
        int puertoUDP = 6000;
        InetAddress dirIP = InetAddress.getByName("230.33.0.3");
        List<Camello> listaEspera = null;

        // 1. Esperar a recibir msg SolicitarJugar de Camellos
        ServerSocket server = new ServerSocket(puertoTCP);
        Socket camello = server.accept();
        System.out.println("Esperando a que se conecten Camellos...");

        TCPunicast tcp = new TCPunicast(); // el constructor de TCPunicast debe recibir el socket como parametro

        Mensaje msgUni = tcp.recibir();

        // 2. Añadir Camello a un grupo, cuando un grupo tenga 4 camellos se cierra
        // 3. Asignar al grupo una idGrupo (usar semáforo) y dir IP multicast (enviar msg AsignarGrupo)
        if(msgUni instanceof SolicitarJugar){
            while(listaEspera.size() < 4){
                // añadir camello a listaEspera
            }
            // crear carrera
            listaEspera.clear();
        } else if(msgUni instanceof MsgError){
            // ver tipo de error etc
        }else{
            System.out.println("El mensaje no se puede leer");
        }
        // 4. Enviar al grupo msg EventoCarrera - SALIDA

        UDPmulticast udpEnvio = new UDPmulticast(); // necesita recibir como parametros InetAddress y puerto
        EventoCarrera ecSalida = new EventoCarrera(); // necesita recibir como parámetro el tipo de evento
        udpEnvio.enviar(ecSalida);

        // 5. Espera a recibir msg EventoCarrera - META
        UDPmulticast udpRecibir = new UDPmulticast(); // necesita recibir como parámetro InetAddress, puerto remoto, puerto local
        Mensaje msgMulti = (EventoCarrera) udpRecibir.recibir();
//        if(msgMulti.getTipo() = META){
//            // 6. Enviar msg FinCarrera al grupo
//            EventoCarrera ecFin = new EventoCarrera(); // necesita recibir como parámetro el tipo de evento
//            udpEnvio.enviar(ecFin);
//        }

        // 7. Crear ranking y mostrarlo en pantalla
















    }




}
