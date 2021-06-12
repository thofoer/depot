package de.thofoer.depot.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Stock implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String wkn;
    public String isin;
    public String uri;
    public int buyRate;
    public int amount;
    public AssetType type;

    @Ignore
    public Price price = new Price();

    public int getTotal() {
        return amount * price.value;
    }

    public int getBuyTotal() {
        return amount * buyRate;
    }

    public int getTotalDiff() {
        return amount * (price.value - buyRate);
    }

}
