package com.mundial2026.album.data;

import androidx.room.TypeConverter;
import com.mundial2026.album.model.EstadoLamina;

/**
 * Convierte el enum EstadoLamina a String para guardarlo en SQLite.
 */
public class Converters {

    @TypeConverter
    public static String fromEstado(EstadoLamina estado) {
        return estado == null ? null : estado.name();
    }

    @TypeConverter
    public static EstadoLamina toEstado(String nombre) {
        return nombre == null ? null : EstadoLamina.valueOf(nombre);
    }
}
