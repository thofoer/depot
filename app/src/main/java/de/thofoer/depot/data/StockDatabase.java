package de.thofoer.depot.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Stock.class}, version = 1)
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
