package com.example.alarmaapp.model;

/**
 * No es una entidad de Room, es como un saco para guardar el resultado
 * de la query "alarma más activada" con su nombre y el numero de veces.
 */
public class AlarmaMasActivada {
    public String nombreAlarma;
    public int veces;
}