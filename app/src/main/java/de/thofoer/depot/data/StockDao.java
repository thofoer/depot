package de.thofoer.depot.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StockDao {

    @Insert
    long insert(Stock stock);

    @Delete
    void delete(Stock stock);

    @Update
    void update(Stock stock);

    @Query("SELECT * FROM stock")
    List<Stock> getAll();
}
