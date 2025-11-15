package carreraCamellos;

import mensajes.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Servidor{

    // Atributos
    private static final int PUERTO_TCP = 12345;
    private static int puertoUDP = 600; // se añadirá la idGrupo para que cambie en cada carrera
    private String dirGrupo = "230.0.0."; //raiz de la ip multicast a la que se añadirá la idGrupo como cuarto octeto para que cambie en cada carrera
    private int maxCamellos = 4; // maximo de camellos por grupo
    private List<Carrera> carreras = new ArrayList<>(); // array de carreras creadas
    private int idGrupo = 1;
    private final Semaphore semaforo = new Semaphore(1);

    public void iniciarServidor(){
        // 1. Esperar a recibir msg SolicitarJugar de Camellos
        System.out.println("Servidor iniciado, esperando solicitudes...");

        try(ServerSocket server = new ServerSocket(PUERTO_TCP)){
            while(true){
                Socket camello =  server.accept();
                try{
                    TCPunicast tcp = new TCPunicast(camello); // el constructor de TCPunicast debe recibir el socket como parametro
                    Mensaje msjUni = tcp.recibir();

                    // comprobar si el mensaje es una solicitud o un error
                    if(msjUni instanceof SolicitarJugar){
                        System.out.println("Procesando solicitud de unirse a la carrera...");
                        // unir al camello a un grupo
                        asignarCamello(((SolicitarJugar) msjUni).getIdCamello());
                        // enviar asignacion de grupo
                        String ipMulti = dirGrupo + String.valueOf(idGrupo);
                        int puerto = puertoUDP + idGrupo;
                        AsignarGrupo ag = new AsignarGrupo(idGrupo, ipMulti, puerto);
                        tcp.enviar(ag);
                        idGrupo++;
                    }else if(msjUni instanceof MsjError){
                        System.out.println("Error" + ((MsjError) msjUni).getTipoError());
                        // segun el tipo de error ya veré como manejarlo
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("No se encuentra el mensaje");
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al conectar el servidor");
            throw new RuntimeException(e);
        }
    }

    // 2. Añadir Camello a un grupo, cuando un grupo tenga 4 camellos se cierra
    // 3. Asignar al grupo una idGrupo (usar semáforo) y dir IP multicast (enviar msg AsignarGrupo)
    public void asignarCamello(int idCamello) throws IOException, ClassNotFoundException {
        // busca una carrera con hueco
        for (Carrera c : carreras){
            if(!c.llena){
                c.agregarCamello(idCamello);
            }
        }

        // si no hay crea una nueva
        InetAddress ipGrupo = InetAddress.getByName(dirGrupo + String.valueOf(idGrupo));
        int puerto = puertoUDP + idGrupo;
        Carrera nuevaCarrera = new Carrera(idGrupo, ipGrupo, puerto);
        nuevo.agregarCamello(idCamello);
        carreras.add(nuevaCarrera);
        // iniciar hilo aqui?
    }

    public void controlarCarrera(){
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
    }

    public void crearRanking(){

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

    }

}
