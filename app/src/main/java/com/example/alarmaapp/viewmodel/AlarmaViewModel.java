package com.example.alarmaapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.alarmaapp.model.Alarma;
import com.example.alarmaapp.repository.AlarmaRepository;
import com.example.alarmaapp.repository.GeofenceManager;

import java.util.List;
import java.util.function.Consumer;

/* Clase intermediaria entre la UI y datos, la activity nunca toca ROOM ni el repository, siempre
* pasa por aqui, esta clase contiene toda la logica
 */
public class AlarmaViewModel extends AndroidViewModel {

    private AlarmaRepository repository;
    private LiveData<List<Alarma>> todasLasAlarmas;
    private GeofenceManager geofenceManager;


    public AlarmaViewModel(Application application) {
        super(application);
        repository = new AlarmaRepository(application);
        todasLasAlarmas = repository.getTodasLasAlarmas();
        geofenceManager = new GeofenceManager(application);
    }

    public LiveData<List<Alarma>> getTodasLasAlarmas() {
        return todasLasAlarmas;
    }

    // Cuando creamos una nueva alarma, la crearemos tambien en la bd y en el Geofence
    public void insertar(Alarma alarma) {
        /* Gracias a que hicimos el callback en ROOM, al insertar una alarma, nos devolvera un ID
        * que usaremos para registrar la alarma en Geofence con ese mismo ID para que sea mucho más facil
        * las ediciones y eliminaciones de alarmas
         */
        insertar(alarma, id -> { /* nada extra que hacer */ });
    }

    /* Sobrecarga que ademas devuelve el id al callback para que el llamador pueda guardar
     * cosas asociadas (por ejemplo la Repeticion). El registro en el Geofence se hace SIEMPRE
     * aqui dentro para que no dependa de quien use esta version del metodo.
     */
    public void insertar(Alarma alarma, Consumer<Long> callback) {
        repository.insertar(alarma, id -> {
            alarma.setId(id);
            if (alarma.isActiva()) {
                geofenceManager.registrarGeofence(alarma);
            }
            callback.accept(id);
        });
    }

    // Registrar/editar o borrar la alarma del Geofence según el switch, para que no monitoree conb el swicth en Off
    public void actualizar(Alarma alarma) {
        repository.actualizar(alarma);
        if (alarma.isActiva()) {
            geofenceManager.registrarGeofence(alarma);
        } else {
            geofenceManager.eliminarGeofence(alarma);
        }
    }

    // Eliminamos la alarma de la bd y del Geofence
    public void eliminar(Alarma alarma) {
        repository.eliminar(alarma);
        geofenceManager.eliminarGeofence(alarma);
    }

    public LiveData<List<Alarma>> getAlarmasPorCategoria(long categoriaId) {
        return repository.getAlarmasPorCategoria(categoriaId);
    }
}
