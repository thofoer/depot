package de.thofoer.depot;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import de.thofoer.depot.data.PortfolioValue;
import de.thofoer.depot.data.QuoteLoader;
import de.thofoer.depot.data.Stock;
import de.thofoer.depot.data.StockDatabase;

public class DepotRecordService extends JobService {
    private static final String TAG = DepotRecordService.class.getCanonicalName();

    @Override
    public boolean onStartJob(JobParameters params) {
        new Thread(()->record()).start();
        return true;
    }

    private void record() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        DayOfWeek weekday = now.getDayOfWeek();
        if (hour != 23 || weekday==DayOfWeek.SUNDAY || weekday==DayOfWeek.SATURDAY) {
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

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
