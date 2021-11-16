package de.thofoer.depot;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import de.thofoer.depot.data.PortfolioValue;
import de.thofoer.depot.data.QuoteLoader;
import de.thofoer.depot.data.Stock;
import de.thofoer.depot.data.StockDatabase;

public class DepotRecordWorker extends Worker {

    public DepotRecordWorker(
        @NonNull Context context,
        @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        record();
        return Result.success();
    }

    private void record() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        DayOfWeek weekday = now.getDayOfWeek();
        if (hour < 20 || weekday==DayOfWeek.SUNDAY || weekday==DayOfWeek.SATURDAY) {
            return;
        }

        StockDatabase database = StockDatabase.getDatabase(getApplicationContext());
        LocalDate today = LocalDate.now();
        if (database.portfolioValueDao().getForDate(today)!=null) {
            return;
        }

        List<Stock> stockList = new ArrayList<>(database.stockDao().getAll());
        QuoteLoader quoteLoader = new QuoteLoader();
        stockList.forEach(quoteLoader::loadQuote);
        long value = stockList.stream().mapToLong( s -> s.amount * s.price.value).sum();
        PortfolioValue newPortfolioValue = new PortfolioValue();
        newPortfolioValue.date = today;
        newPortfolioValue.value = value;
        database.portfolioValueDao().insert(newPortfolioValue);
    }
}
