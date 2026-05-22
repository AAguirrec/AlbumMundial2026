package com.mundial2026.album.data;

import androidx.room.*;
import com.mundial2026.album.model.Usuario;

@Dao
public interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertUsuario(Usuario usuario);   // Retorna el id generado

    /** Busca usuario por correo para hacer login */
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    Usuario getByCorreo(String correo);

    /** Verifica si un correo ya está registrado */
    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo")
    int existeCorreo(String correo);
}
