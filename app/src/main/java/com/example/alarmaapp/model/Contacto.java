package com.example.alarmaapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(
        tableName = "contactos",
        foreignKeys = @ForeignKey(
                entity = Alarma.class,
                parentColumns = "id",
                childColumns = "alarmaId",
                onDelete = ForeignKey.CASCADE // Si se borra la alarma, se borran sus contactos
        ),
        // El indice acelera las queries por alarmaId y silencia el warning de Room
        indices = {@Index("alarmaId")}
)
public class Contacto {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long alarmaId;     // A que alarma pertenece este contacto
    @NotNull
    private String nombre;     // Nombre del contacto
    @NotNull
    private String telefono;   // Numero "+34666123456"
    @NotNull
    private String mensaje;    // Mensaje que se enviara al activar la alarma

    public Contacto(long alarmaId, String nombre, String telefono, String mensaje) {
        this.alarmaId = alarmaId;
        this.nombre   = nombre;
        this.telefono = telefono;
        this.mensaje  = mensaje;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getAlarmaId() { return alarmaId; }
    public void setAlarmaId(long alarmaId) { this.alarmaId = alarmaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}