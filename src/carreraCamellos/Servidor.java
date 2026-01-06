package carreraCamellos;

import jdk.jfr.Event;
import mensajes.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static mensajes.TipoEvento.*;


public class Servidor{

    // Atributos
    private int idServidor;
    private static final int PUERTO_TCP = 12345;
    private static int puertoUDP = 600; // se añadirá la idGrupo para que cambie en cada carrera
    private String dirGrupo = "232.0.0."; //raiz de la ip multicast a la que se añadirá la idGrupo como cuarto octeto para que cambie en cada carrera
    private List<InfoCarrera> carreras = new ArrayList<>(); // array de carreras creadas
    private int idGrupo = 1;
    private final Semaphore semaforo = new Semaphore(1);

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try{
            servidor.iniciarServidor();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void iniciarServidor(){
        // 1. Esperar a recibir msg SolicitarJugar de Camellos
        try(ServerSocket server = new ServerSocket(PUERTO_TCP)){
            System.out.println("Servidor iniciado, esperando solicitudes...");
            while(true){
                Socket camello =  server.accept();
                gestorTCP(camello);
            }
        } catch (IOException e) {
            System.out.println("Error al conectar el servidor");
            throw new RuntimeException(e);
        }
    }

    public void gestorTCP(Socket camello){
        try{
            TCPunicast tcp = new TCPunicast(camello);
            Mensaje msjUni = tcp.recibir();

            // comprobar si el mensaje es una solicitud o un error
            if(msjUni instanceof SolicitarJugar){
                System.out.println("Procesando solicitud de unirse a la carrera...");
                // unir al camello a un grupo
                int id = ((SolicitarJugar) msjUni).getIdCamello();

                InfoCarrera carrera = asignarCamello(id);
                // enviar asignacion de grupo
                String ipMulti = carrera.ipGrupo.getHostAddress();
                int puerto = carrera.puerto;
                AsignarGrupo ag = new AsignarGrupo(carrera.idCarrera, carrera.ipGrupo.getHostAddress(), carrera.puerto);
                tcp.enviar(ag);


                // Si la carrera está llena, empieza
                if(carrera.camellos.size() >= carrera.MAX_CAMELLOS && !carrera.controlComenzado){
                    carrera.controlComenzado = true;
                    new Thread(() -> {
                        try{
                            controlarCarrera(carrera);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    },"Carrera-"+ carrera.idCarrera).start();
                }

            }else if(msjUni instanceof MsjError){
                System.out.println("Error" + ((MsjError) msjUni).getTipoError());
            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("No se encuentra el mensaje");
            e.printStackTrace();
        }
    }

    // Buscar una carrera a la que añadir el camello
    public InfoCarrera asignarCamello(int idCamello) throws IOException, ClassNotFoundException {
        try {
            semaforo.acquire();
            // busca una carrera en la lista de carreras que no esté llena para añadir el camello
            for (InfoCarrera c : carreras) {
                System.out.println("Buscando carreras con hueco...");
                if (c.camellos.size() < InfoCarrera.MAX_CAMELLOS) {
                    c.camellos.add(idCamello);
                    return c;
                }
            }
            // si no hay, se crea una nueva
            System.out.println("No hay carreras con hueco, creando una nueva...");
            InetAddress ipGrupo = InetAddress.getByName(dirGrupo + idGrupo);
            int puerto = puertoUDP + idGrupo;

            InfoCarrera nueva = new InfoCarrera(idGrupo, ipGrupo, puerto);
            nueva.camellos.add(idCamello);
            idGrupo++;
            carreras.add(nueva);

            return nueva;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Error en semaforo", e);
        } finally {
            semaforo.release();
        }

    }

    //Enviar mensaje de SALIDA a la carrera y escuchar udp por si llega algun mensaje de META
    public void controlarCarrera(InfoCarrera carrera) throws IOException, ClassNotFoundException {

        try(MulticastSocket ms = new MulticastSocket(carrera.puerto)){
            ms.setReuseAddress(true);
            NetworkInterface netIf = UDPmulticast.encontrarInterfaz(carrera.ipGrupo);

            UDPmulticast udp = new UDPmulticast(carrera.ipGrupo, carrera.puerto);
            udp.socket = ms;
            SocketAddress sockaddr = new InetSocketAddress(carrera.ipGrupo, carrera.puerto);
            ms.joinGroup(sockaddr,netIf);
            Thread.sleep(500);
            EventoCarrera salida = new EventoCarrera(idServidor,SALIDA);
            salida.setListaCamellos(carrera.camellos);
            udp.enviar(salida);
            System.out.println("SALIDA enviada al grupo " + carrera.idCarrera);

            while (true){
                EventoCarrera ev = udp.recibir();
                if(ev.getTipoEvento() == META){
                    System.out.println("META recibida del camello " + ev.getIdEmisor());
                    udp.enviar(new EventoCarrera(1, FIN));
                    System.out.println("FIN de la carrera " + carrera.idCarrera);
                    break;
                }else if (ev.getTipoEvento() == CAIDA) {
                    // si un camello reporta caída, eliminarlo del registro del servidor
                    int idCaido = ev.getIdEmisor();
                    carrera.camellos.remove(Integer.valueOf(idCaido));
                    System.out.println("Servidor: Camello " + idCaido + " eliminado por CAIDA en carrera " + carrera.idCarrera);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    // Clase con informacion de las carreras generadas
    private static class InfoCarrera {
        static final int MAX_CAMELLOS = 4;
        int idCarrera;
        InetAddress ipGrupo;
        int puerto;
        List<Integer> camellos = new ArrayList<>();
        boolean controlComenzado = false;

        InfoCarrera(int idCarrera, InetAddress ipGrupo, int puerto) {
            this.idCarrera = idCarrera;
            this.ipGrupo = ipGrupo;
            this.puerto = puerto;
        }
    }

}