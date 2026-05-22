package com.mundial2026.album.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.mundial2026.album.model.Lamina;
import com.mundial2026.album.model.Usuario;
import com.mundial2026.album.utils.DatosIniciales;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities  = {Lamina.class, Usuario.class},   // ← se agrega Usuario
    version   = 2,                                // ← versión sube a 2
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AlbumDatabase extends RoomDatabase {

    public abstract LaminaDao  laminaDao();
    public abstract UsuarioDao usuarioDao();    // ← nuevo DAO

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
                    .fallbackToDestructiveMigration()
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            dbExecutor.execute(() -> {
                                try {
                                    AlbumDatabase database = getDatabase(context);
                                    database.laminaDao()
                                            .insertAllLaminas(DatosIniciales.generarLaminas());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
