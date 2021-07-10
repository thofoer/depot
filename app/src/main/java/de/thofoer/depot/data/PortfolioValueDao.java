package de.thofoer.depot.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface PortfolioValueDao {

    @Insert
    long insert(PortfolioValue portfolioValue);

    @Delete
    void delete(PortfolioValue portfolioValue);

    @Update
    void update(PortfolioValue portfolioValue);

    @Query("SELECT * FROM PortfolioValue")
    List<PortfolioValue> getAll();

    @Query("SELECT * FROM PortfolioValue WHERE date = :date")
    PortfolioValue getForDate(LocalDate date);

}
