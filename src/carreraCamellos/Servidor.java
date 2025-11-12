package carreraCamellos;

public class Servidor{

    /* Servidor multihilo
    * Funciones:
    * 1. Esperar a recibir msg SolicitarJugar de Camellos
    * 2. Añadir Camello a un grupo, cuando un grupo tenga 4 camellos se cierra
    * 3. Asignar al grupo una idGrupo (usar semáforo) y dir IP multicast (enviar msg AsignarGrupo)
    * 4. Enviar al grupo msg EventoCarrera - SALIDA
    * 5. Espera a recibir msg EventoCarrera - META
    * 6. Enviar msg FinCarrera al grupo
    * 7. Crear ranking y mostrarlo en pantalla
    * 8. Si recibe un mensaje de error ?
    * */

}
