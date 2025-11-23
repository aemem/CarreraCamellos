package carreraCamellos;
import mensajes.Mensaje;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
}