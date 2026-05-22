package com.mundial2026.album.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gestiona la sesión del usuario usando SharedPreferences.
 * Guarda si hay un usuario logueado para no pedir login cada vez.
 */
public class SessionManager {

    private static final String PREFS_NAME  = "album_session";
    private static final String KEY_LOGGED  = "esta_logueado";
    private static final String KEY_USER_ID = "usuario_id";
    private static final String KEY_NOMBRE  = "usuario_nombre";
    private static final String KEY_CORREO  = "usuario_correo";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Guarda la sesión al hacer login exitoso */
    public void guardarSesion(int id, String nombre, String correo) {
        prefs.edit()
             .putBoolean(KEY_LOGGED,  true)
             .putInt    (KEY_USER_ID, id)
             .putString (KEY_NOMBRE,  nombre)
             .putString (KEY_CORREO,  correo)
             .apply();
    }

    /** Cierra la sesión (logout) */
    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }

    public boolean estaLogueado() { return prefs.getBoolean(KEY_LOGGED, false); }
    public int     getUserId()    { return prefs.getInt(KEY_USER_ID, -1); }
    public String  getNombre()    { return prefs.getString(KEY_NOMBRE, ""); }
    public String  getCorreo()    { return prefs.getString(KEY_CORREO, ""); }
}
