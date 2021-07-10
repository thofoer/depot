package de.thofoer.depot.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity
public class PortfolioValue {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long value;

    public LocalDate date;

}
