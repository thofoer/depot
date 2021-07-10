package de.thofoer.depot.data;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.LocalDate;

@Database(entities = {Stock.class, PortfolioValue.class},
          version = 3,
          autoMigrations = { @AutoMigration(from=1, to=2),
                             @AutoMigration(from=2, to=3)})
@TypeConverters({StockDatabase.LocalDateConverter.class})
public abstract class StockDatabase extends RoomDatabase {

    public abstract StockDao stockDao();
    public abstract PortfolioValueDao portfolioValueDao();

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

    static class LocalDateConverter {

        @TypeConverter
        public static LocalDate toDate(String dateString) {
            if (dateString == null) {
                return null;
            } else {
                return LocalDate.parse(dateString);
            }
        }

        @TypeConverter
        public static String toDateString(LocalDate date) {
            if (date == null) {
                return null;
            } else {
                return date.toString();
            }
        }
    }

}
