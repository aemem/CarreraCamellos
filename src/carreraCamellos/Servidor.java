package carreraCamellos;

import mensajes.*;

import javax.swing.*;
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
    private int maxCamellos = 4; // maximo de camellos por grupo
    private List<Carrera> carreras = new ArrayList<>(); // array de carreras creadas
    private int idGrupo = 1;
    private final Semaphore semaforo = new Semaphore(1);

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
            System.out.println("Recibiendo mensaje solicitud");
            // comprobar si el mensaje es una solicitud o un error
            if(msjUni instanceof SolicitarJugar){
                System.out.println("Procesando solicitud de unirse a la carrera...");
                // unir al camello a un grupo
                int id = ((SolicitarJugar) msjUni).getIdCamello();
                System.out.println("Creando carrera...");
                Carrera carrera = asignarCamello(id);
                // enviar asignacion de grupo
                String ipMulti = carrera.getIpGrupo().getHostAddress();
                int puerto = carrera.getPuerto();
                AsignarGrupo ag = new AsignarGrupo(carrera.getIdCarrera(), ipMulti, puerto);
                tcp.enviar(ag);


                // Si la carrera está llena, empieza
                if(carrera.estaLlena()){
                    new Thread(() -> {
                        try{
                            controlarCarrera(carrera);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    },"Carrera-"+ carrera.getIdCarrera()).start();
                }

            }else if(msjUni instanceof MsjError){
                System.out.println("Error" + ((MsjError) msjUni).getTipoError());
                // manejar error
            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("No se encuentra el mensaje");
            e.printStackTrace();
        }
    }

    // 2. Añadir Camello a un grupo, cuando un grupo tenga 4 camellos se cierra
    // 3. Asignar al grupo una idGrupo (usar semáforo) y dir IP multicast (enviar msg AsignarGrupo)
    public Carrera asignarCamello(int idCamello) throws IOException, ClassNotFoundException {
        try {
            System.out.println("Intentando acceder a semáforo...");
            semaforo.acquire();

            for (Carrera c : carreras) {
                System.out.println("Buscando carreras con hueco...");
                if (!c.estaLlena()) {
                    c.agregarCamello(idCamello);
                    return c;
                }
            }
            System.out.println("No hay carreras con hueco, creando una nueva...");
            InetAddress ipGrupo = InetAddress.getByName(dirGrupo + idGrupo);
            int puerto = puertoUDP + idGrupo;

            Carrera nueva = new Carrera(idGrupo, ipGrupo, puerto, idCamello);
            nueva.agregarCamello(idCamello);
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

    public void controlarCarrera(Carrera carrera) throws IOException, ClassNotFoundException {

        try(MulticastSocket ms = new MulticastSocket(carrera.getPuerto())){
            ms.joinGroup(carrera.getIpGrupo());
            UDPmulticast udp = new UDPmulticast(carrera.getIpGrupo(), carrera.getPuerto());
            udp.socket = ms;

            udp.enviar(new EventoCarrera(idServidor,SALIDA));
            System.out.println("SALIDA enviada al grupo " + carrera.getIdCarrera());

            while (true){
                EventoCarrera ev = udp.recibir();
                if(ev.getTipoEvento() == META){
                    System.out.println("META recibida del camello " + ev.getIdEmisor());
                    udp.enviar(new EventoCarrera(1, FIN));
                    System.out.println("FIN de la carrera " + carrera.getIdCarrera());
                    break;
                }
            }
            carrera.setCarreraTerminada(true);
        }

    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try{
            servidor.iniciarServidor();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
