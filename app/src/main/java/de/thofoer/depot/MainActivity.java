package de.thofoer.depot;


import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.thofoer.depot.data.QuoteLoader;
import de.thofoer.depot.data.Stock;
import de.thofoer.depot.data.StockDatabase;

public class MainActivity extends AppCompatActivity {

    private static final int JOB_ID = 20210710;

    public static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private MenuItem refreshMenuItem;
    private MenuItem addMenuItem;
    private MenuItem depotDetailItem;

    private ArrayList<Stock> stockList;
    private StockAdapter itemsAdapter;

    private QuoteLoader quoteLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        quoteLoader = new QuoteLoader();
        executor.execute( () -> {
            stockList = new ArrayList<>(StockDatabase.getDatabase(getApplicationContext()).stockDao().getAll());

            itemsAdapter =
                    new StockAdapter(this, android.R.layout.activity_list_item, stockList);

            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(itemsAdapter);
        });
        ComponentName serviceEndpoint = new ComponentName(this, DepotRecordService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceEndpoint)
            .setPeriodic(30*60*1000)
            .setPersisted(true)
            .build();

        int x = getSystemService(JobScheduler.class).schedule(jobInfo);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == refreshMenuItem) {
            refresh();
        }
        else if (item == addMenuItem) {
            addStock();
        }
        else if (item == depotDetailItem) {
            depotDetail();
        }
        return true;
    }

    private void depotDetail() {
        Intent intent = new Intent(this, DepotDetailActivity.class);
        intent.putExtra("stocklist", stockList);
        startActivity(intent);
    }

    private void addStock() {
        Intent intent = new Intent(this, StockActivity.class);
        Stock value = new Stock();
        value.name = "Fraport";
        value.uri = "https://www.ariva.de/fraport-aktie";
        value.amount = 100;
        value.buyRate = 593200;
        value.isin = "DE0005773303";
        value.wkn  = "577330";

        intent.putExtra("stock", value);
        startActivityForResult(intent, 9999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9999) {
            if(resultCode == Activity.RESULT_OK){
                Stock stock = (Stock)data.getSerializableExtra("result");
                executor.execute(() -> {
                    StockDatabase.getDatabase(getApplicationContext()).stockDao().insert(stock);
                    runOnUiThread(() -> itemsAdapter.add(stock));
                });
            }
        }
    }


    private void refresh() {
        executor.execute( () -> {
            try {
                runOnUiThread(() -> refreshMenuItem.setEnabled(false));
                stockList.forEach(s -> s.price.value=-1);
                stockList.forEach(this::loadQuote);

            }
            finally {
                runOnUiThread(() -> {
                    itemsAdapter.notifyDataSetChanged();
                    refreshMenuItem.setEnabled(true);
                });
            }
        });
    }

    private void loadQuote(Stock stock) {
        quoteLoader.loadQuote(stock);
        runOnUiThread(() -> itemsAdapter.notifyDataSetChanged());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        refreshMenuItem = menu.findItem(R.id.menu_refresh);
        addMenuItem = menu.findItem(R.id.menu_add);
        depotDetailItem = menu.findItem(R.id.menu_details);
        return super.onCreateOptionsMenu(menu);
    }


}