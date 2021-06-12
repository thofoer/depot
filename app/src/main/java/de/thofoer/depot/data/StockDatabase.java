package de.thofoer.depot.data;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Stock.class},
          version = 2,
          autoMigrations = { @AutoMigration(from=1, to=2) })
public abstract class StockDatabase extends RoomDatabase {
    public abstract StockDao stockDao();

    private static volatile StockDatabase INSTANCE;

    public static StockDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (StockDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StockDatabase.class, "depot_stock.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
