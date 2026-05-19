package com.mundial2026.album.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.mundial2026.album.model.EstadoLamina;
import com.mundial2026.album.model.Lamina;
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
    }

    public LiveData<List<Lamina>> getLaminasPorSeccion(String seccion) {
        return dao.getLaminasPorSeccion(seccion);
    }

    /** Suma 1 a la lámina: FALTA→TIENE, TIENE/REPETIDA→REPETIDA con +1 cantidad */
    public void marcarTengo(int numero) {
        AlbumDatabase.dbExecutor.execute(() -> {
            Lamina lamina = dao.getLaminaByNumero(numero);
            if (lamina == null) return;

            if (lamina.getEstado() == EstadoLamina.FALTA) {
                dao.updateEstado(numero, EstadoLamina.TIENE, 1);
            } else {
                int nuevaCantidad = lamina.getCantidad() + 1;
                dao.updateEstado(numero, EstadoLamina.REPETIDA, nuevaCantidad);
            }
        });
    }

    /** Resta 1 a la lámina: REPETIDA→TIENE/FALTA según cantidad restante */
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
