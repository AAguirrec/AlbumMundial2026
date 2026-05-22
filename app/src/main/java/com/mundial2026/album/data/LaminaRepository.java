package com.mundial2026.album.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.mundial2026.album.model.EstadoLamina;
import com.mundial2026.album.model.Lamina;
import com.mundial2026.album.utils.DatosIniciales;
import java.util.List;

public class LaminaRepository {

    private final LaminaDao dao;

    public final LiveData<List<Lamina>> todasLasLaminas;
    public final LiveData<List<String>> secciones;
    public final LiveData<Integer>      totalLaminas;
    public final LiveData<Integer>      totalTengo;
    public final LiveData<Integer>      totalFaltan;
    public final LiveData<Integer>      totalRepetidas;
    public final LiveData<Integer>      sobrantes;

    public LaminaRepository(Application application) {
        AlbumDatabase db = AlbumDatabase.getDatabase(application);
        dao = db.laminaDao();

        todasLasLaminas = dao.getAllLaminas();
        secciones       = dao.getSecciones();
        totalLaminas    = dao.getTotalLaminas();
        totalTengo      = dao.getTotalTengo();
        totalFaltan     = dao.getTotalFaltan();
        totalRepetidas  = dao.getTotalRepetidas();
        sobrantes       = dao.getTotalSobrantes();

        // Si la BD está vacía (primera vez o datos borrados), carga las láminas
        AlbumDatabase.dbExecutor.execute(() -> {
            if (dao.contarLaminasSync() == 0) {
                dao.insertAllLaminas(DatosIniciales.generarLaminas());
            }
        });
    }

    public LiveData<List<Lamina>> getLaminasPorSeccion(String seccion) {
        return dao.getLaminasPorSeccion(seccion);
    }

    /** Recarga forzada de láminas (útil desde el botón en MainActivity) */
    public void cargarLaminasSiVacia(Runnable onCompleto) {
        AlbumDatabase.dbExecutor.execute(() -> {
            if (dao.contarLaminasSync() == 0) {
                dao.insertAllLaminas(DatosIniciales.generarLaminas());
            }
            if (onCompleto != null) onCompleto.run();
        });
    }

    public void marcarTengo(int numero) {
        AlbumDatabase.dbExecutor.execute(() -> {
            Lamina lamina = dao.getLaminaByNumero(numero);
            if (lamina == null) return;
            if (lamina.getEstado() == EstadoLamina.FALTA) {
                dao.updateEstado(numero, EstadoLamina.TIENE, 1);
            } else {
                dao.updateEstado(numero, EstadoLamina.REPETIDA, lamina.getCantidad() + 1);
            }
        });
    }

    public void marcarFalta(int numero) {
        AlbumDatabase.dbExecutor.execute(() -> {
            Lamina lamina = dao.getLaminaByNumero(numero);
            if (lamina == null) return;
            int nuevaCantidad = Math.max(lamina.getCantidad() - 1, 0);
            EstadoLamina nuevoEstado;
            if      (nuevaCantidad == 0) nuevoEstado = EstadoLamina.FALTA;
            else if (nuevaCantidad == 1) nuevoEstado = EstadoLamina.TIENE;
            else                         nuevoEstado = EstadoLamina.REPETIDA;
            dao.updateEstado(numero, nuevoEstado, nuevaCantidad);
        });
    }

    public void updateLamina(Lamina lamina) {
        AlbumDatabase.dbExecutor.execute(() -> dao.updateLamina(lamina));
    }
}
