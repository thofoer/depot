package de.thofoer.depot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import de.thofoer.depot.data.Stock;
import de.thofoer.depot.data.StockDatabase;

public class StockActivity extends AppCompatActivity {


    private EditText inputName;
    private EditText inputWkn;
    private EditText inputIsin;
    private EditText inputAmount;
    private EditText inputBuyingRate;
    private EditText inputUrl;

    private Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        findViewById(R.id.buttonSave).setOnClickListener((view)->save());
        findViewById(R.id.buttonCancel).setOnClickListener((view)->cancel());

        inputName = findViewById(R.id.editTextName);
        inputWkn = findViewById(R.id.editTextWkn);
        inputIsin = findViewById(R.id.editTextIsin);
        inputAmount = findViewById(R.id.editTextAmount);
        inputBuyingRate = findViewById(R.id.editTextRate);
        inputUrl = findViewById(R.id.editTextUrl);

        stock = (Stock)getIntent().getSerializableExtra("stock");

        inputName.setText(stock.name);
        inputWkn.setText(stock.wkn);
        inputIsin.setText(stock.isin);
        inputAmount.setText(String.valueOf(stock.amount));
        inputBuyingRate.setText(String.valueOf(stock.buyRate));
        inputUrl.setText(stock.uri);
    }

    private void cancel() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void save() {
        stock.name = inputName.getText().toString();
        stock.wkn = inputWkn.getText().toString();
        stock.isin = inputIsin.getText().toString();
        stock.amount = Integer.parseInt( inputAmount.getText().toString());
        stock.buyRate = Integer.parseInt(inputBuyingRate.getText().toString());
        stock.uri = inputUrl.getText().toString();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", stock);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }
}