package de.thofoer.depot.data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuoteLoader {

    private String suffix = "/times_and_sales?boerse_id=131";

    public void loadQuote(Stock stock) {
        String text = loadFileFromUrl(stock.uri);
        stock.price = parsePrice(text);
    }

    private Price parsePrice(String text) {
        text = text.replaceAll(" |\t|\n|\r", "");
        int idx = text.indexOf("<spanitemprop=\"price\"");
        text=text.substring( idx, idx+500 );

        String regex = ".*<span itemprop=\"price\" content=\".*\">(.*?)</span><span .*\">(.*?)&nbsp;&euro;</td></tr></table>.*".replaceAll(" |\t|\n|\r", "");

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String value = matcher.group(1);
            String diff = matcher.group(2);
            Price price = new Price(value, diff);
            return price;
        }
        return null;
    }

    private String loadFileFromUrl(String address) {

        try {
            URL url = new URL(address+suffix);
            StringBuilder buf = new StringBuilder();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(60000);

            try (InputStreamReader inReader = new InputStreamReader(conn.getInputStream());
                 BufferedReader bufferedReader = new BufferedReader(inReader)) {
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    buf.append(str);
                }
            }

            return buf.toString();
        } catch (Exception e) {
            Log.d("MyTag", e.toString());
            return null;
        }
    }

}
