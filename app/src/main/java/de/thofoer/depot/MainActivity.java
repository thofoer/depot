package de.thofoer.depot;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.thofoer.depot.data.Price;
import de.thofoer.depot.data.QuoteLoader;
import de.thofoer.depot.data.Stock;
import de.thofoer.depot.data.StockDatabase;

public class MainActivity extends AppCompatActivity {

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