package com.mundial2026.album.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.mundial2026.album.model.Lamina;
import com.mundial2026.album.utils.DatosIniciales;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Lamina.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AlbumDatabase extends RoomDatabase {

    public abstract LaminaDao laminaDao();

    // Pool de hilos para operaciones en background
    public static final ExecutorService dbExecutor =
            Executors.newFixedThreadPool(4);

    private static volatile AlbumDatabase INSTANCE;

    public static AlbumDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AlbumDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AlbumDatabase.class,
                            "album_mundial_2026.db"
                    )
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Poblar BD la primera vez que se instala la app
                            dbExecutor.execute(() -> {
                                AlbumDatabase database = getDatabase(context);
                                database.laminaDao()
                                        .insertAllLaminas(DatosIniciales.generarLaminas());
                            });
                        }
                    })
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
