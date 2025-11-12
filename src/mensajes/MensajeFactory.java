package mensajes;

public class MensajeFactory {

    public static Mensaje crearMensaje(TipoMsg tipoMsg) {
        switch (tipoMsg) {
            case ASIGNAR_GRUPO:
                return new AsignarGrupo();
            case ERROR:
                return new Error();
            case EVENTO_CARRERA:
                return new EventoCarrera();
            case FIN_CARRERA:
                return new FinCarrera();
            case SOLICITAR_JUGAR:
                return new SolicitarJugar();
            default:
                throw new IllegalArgumentException("Tipo de mensaje desconocido: " + tipoMsg);
        }
    }
}

