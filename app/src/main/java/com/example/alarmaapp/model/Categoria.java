package com.example.alarmaapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categorias")
public class Categoria {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String nombre;  // Ej: "Trabajo", "Casa"
    private String color;   // Ej: "#1A73E8"

    public Categoria(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}