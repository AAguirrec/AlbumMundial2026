package com.mundial2026.album.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.mundial2026.album.data.LaminaRepository;
import com.mundial2026.album.model.Lamina;
import java.util.List;

public class AlbumViewModel extends AndroidViewModel {

    private final LaminaRepository repository;

    public final LiveData<List<Lamina>> todasLasLaminas;
    public final LiveData<List<String>> secciones;
    public final LiveData<Integer>      totalLaminas;
    public final LiveData<Integer>      totalTengo;
    public final LiveData<Integer>      totalFaltan;
    public final LiveData<Integer>      totalRepetidas;
    public final LiveData<Integer>      sobrantes;

    private final MutableLiveData<String> seccionActiva;
    public  final LiveData<List<Lamina>>  laminasFiltradas;

    public AlbumViewModel(@NonNull Application application) {
        super(application);

        repository      = new LaminaRepository(application);
        todasLasLaminas = repository.todasLasLaminas;
        secciones       = repository.secciones;
        totalLaminas    = repository.totalLaminas;
        totalTengo      = repository.totalTengo;
        totalFaltan     = repository.totalFaltan;
        totalRepetidas  = repository.totalRepetidas;
        sobrantes       = repository.sobrantes;

        seccionActiva    = new MutableLiveData<>(null);
        laminasFiltradas = Transformations.switchMap(seccionActiva, seccion -> {
            if (seccion == null) return todasLasLaminas;
            return repository.getLaminasPorSeccion(seccion);
        });
    }

    public void setSeccion(String seccion)  { seccionActiva.setValue(seccion); }
    public void marcarTengo(int numero)     { repository.marcarTengo(numero); }
    public void marcarFalta(int numero)     { repository.marcarFalta(numero); }
    public void updateLamina(Lamina lamina) { repository.updateLamina(lamina); }

    /** Recarga láminas si la BD está vacía y ejecuta callback al terminar */
    public void recargarSiVacia(Runnable onCompleto) {
        repository.cargarLaminasSiVacia(onCompleto);
    }
}
