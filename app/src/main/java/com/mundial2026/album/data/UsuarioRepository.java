package com.mundial2026.album.data;

import android.app.Application;
import com.mundial2026.album.model.Usuario;
import com.mundial2026.album.utils.ShaUtils;

public class UsuarioRepository {

    private final UsuarioDao dao;

    public UsuarioRepository(Application application) {
        dao = AlbumDatabase.getDatabase(application).usuarioDao();
    }

    public interface RegistroCallback {
        void onExito(Usuario usuario);
        void onError(String mensaje);
    }

    public interface LoginCallback {
        void onExito(Usuario usuario);
        void onError(String mensaje);
    }

    /** Registra un nuevo usuario en background */
    public void registrar(String nombre, String correo, String clave,
                          RegistroCallback callback) {
        AlbumDatabase.dbExecutor.execute(() -> {
            // Validar que el correo no exista
            if (dao.existeCorreo(correo) > 0) {
                callback.onError("Este correo ya está registrado.");
                return;
            }
            // Hashear clave con SHA-256
            String hash = ShaUtils.sha256(clave);
            if (hash == null) {
                callback.onError("Error al procesar la clave.");
                return;
            }
            Usuario usuario = new Usuario(nombre, correo, hash);
            long id = dao.insertUsuario(usuario);
            usuario.setId((int) id);
            callback.onExito(usuario);
        });
    }

    /** Verifica credenciales en background */
    public void login(String correo, String clave, LoginCallback callback) {
        AlbumDatabase.dbExecutor.execute(() -> {
            Usuario usuario = dao.getByCorreo(correo);
            if (usuario == null) {
                callback.onError("Correo no registrado.");
                return;
            }
            if (!ShaUtils.verificar(clave, usuario.getClaveHash())) {
                callback.onError("Clave incorrecta.");
                return;
            }
            callback.onExito(usuario);
        });
    }
}
