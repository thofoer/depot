package de.thofoer.depot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.thofoer.depot.data.QuoteLoader;
import de.thofoer.depot.data.Stock;

public class DepotDetailActivity extends AppCompatActivity {

    private TextView totalTextView;
    private TextView percentTextView;

    private TextView diffYesterdayTextView;
    private TextView percentYesterdayTextView;


    private MenuItem detailRefreshMenuItem;

    private List<Stock> stockList;
    private QuoteLoader quoteLoader;
    private StockDetailAdapter itemsAdapter;

    ExecutorService executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot_detail);
        quoteLoader = new QuoteLoader();
        stockList = (List<Stock>) getIntent().getSerializableExtra("stocklist");

        totalTextView = findViewById(R.id.textViewTotalAbsolut);
        percentTextView = findViewById(R.id.textViewTotalPercent);
        percentYesterdayTextView = findViewById(R.id.textViewDiffPercent);
        diffYesterdayTextView = findViewById(R.id.textViewDiffAbsolut);

        itemsAdapter =
                new StockDetailAdapter(this, android.R.layout.activity_list_item, stockList);

        ListView listView = (ListView) findViewById(R.id.detailListView);
        listView.setAdapter(itemsAdapter);

        updateTotal();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        detailRefreshMenuItem = menu.findItem(R.id.menu_detail_refresh);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == detailRefreshMenuItem) {
            refresh();
        }
        return true;
    }

    private void refresh() {
        executor.execute( () -> {
            try {
                runOnUiThread(() -> detailRefreshMenuItem.setEnabled(false));
                stockList.forEach(s -> s.price.value=-1);
                stockList.forEach(this::loadQuote);

            }
            finally {
                runOnUiThread(() -> {
                    itemsAdapter.notifyDataSetChanged();
                    detailRefreshMenuItem.setEnabled(true);
                    updateTotal();
                });
            }
        });
    }

    private void loadQuote(Stock stock) {
        quoteLoader.loadQuote(stock);
        runOnUiThread(() -> itemsAdapter.notifyDataSetChanged());
    }

    private void updateTotal() {
        int currentTotal = stockList.stream().map( s-> s.price.value * s.amount).mapToInt(Integer::intValue).sum();
        int currentDelta = stockList.stream().map( s-> s.price.diff * s.amount).mapToInt(Integer::intValue).sum();
        int buyingTotal = stockList.stream().map( s-> s.buyRate * s.amount).mapToInt(Integer::intValue).sum();
        double percentTotal = 100 * ( ((double)currentTotal-buyingTotal) / (double)buyingTotal);
        double percentDelta = 100 * ( ((double)currentDelta) / (double)(currentTotal-currentDelta));

        String deltaSign = currentDelta < 0 ? "-" : "+";
        String total =  String.valueOf(currentTotal/10000)+","+String.format("%02d", (currentTotal%10000)/100);
        String delta =  deltaSign + String.valueOf(Math.abs(currentDelta)/10000)+","+String.format("%02d", (Math.abs(currentDelta)%10000)/100);
        if (currentTotal<0) {
            return;
        }
        totalTextView.setText(total);
        percentTextView.setText(String.format("(%+.2f%%)", percentTotal));
        int textColor = R.color.black;
        if (percentTotal<0) {
            textColor = R.color.neg_text_color;
        }
        else if (percentTotal>0) {
            textColor = R.color.pos_text_color;
        }
        percentTextView.setTextColor(getResources().getColor(textColor, getTheme()));

        diffYesterdayTextView.setText(delta);
        percentYesterdayTextView.setText(String.format("(%+.2f%%)", percentDelta));

        textColor = R.color.black;
        if (percentDelta<0) {
            textColor = R.color.neg_text_color;
        }
        else if (percentDelta>0) {
            textColor = R.color.pos_text_color;
        }
        percentYesterdayTextView.setTextColor(getResources().getColor(textColor, getTheme()));
        diffYesterdayTextView.setTextColor(getResources().getColor(textColor, getTheme()));
    }



}