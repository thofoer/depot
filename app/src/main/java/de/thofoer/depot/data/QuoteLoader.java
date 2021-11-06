package de.thofoer.depot.data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuoteLoader {

    private static final String SUFFIX_SHARE = "/times_and_sales?boerse_id=131";
    private static final String SUFFIX_FOND = SUFFIX_SHARE;
    private static final String SUFFIX_CERT = "";

    private static final String REGEX_SHARE = ".*<spanitemprop=\"price\"content=\".*\">(.*?)</span><span.*\">.*<tdclass=\"lastcol.*?\">(.*?)&nbsp;&euro;</td></tr></table><divclass=\"snapshotInforight\">([0-9:.]*).*?|.*";
    private static final String REGEX_FOND = REGEX_SHARE;
    private static final String REGEX_CERT = "<div class=\"value-lg\" .*?><span class=.*?>(.*?)€<!----></span></div> <div class=.*?><span class=.*?><span class=.*?>(.*?)<!----></span> <span class=.*?>.*?<!----></span></span> <span class=.*?>([0-9\\:\\.]*)<span.*";

    private static final String START_FRAGMENT_SHARE = "<spanitemprop=\"price\"";
    private static final String START_FRAGMENT_FOND = START_FRAGMENT_SHARE;
    private static final String START_FRAGMENT_CERT = "<divclass=\"value-lg\"";

    public void loadQuote(Stock stock) {
        String suffix;
        switch (stock.type) {
            case CERTIFICATE:
                suffix = SUFFIX_CERT;
                break;
            case FOND:
                suffix = SUFFIX_FOND;
                break;
            case SHARE:
                suffix = SUFFIX_SHARE;
                break;
            default:
                throw new IllegalArgumentException("unknown type " + stock.type);
        }
        String text = loadFileFromUrl(stock.uri, suffix);
        stock.price = parsePrice(stock, text);
    }

    private Price parsePrice(Stock stock, String text) {
        text = text.replaceAll("[ \t\n\r]", "");

        String start;
        String regex;
        switch (stock.type) {
            case CERTIFICATE:
                start = START_FRAGMENT_CERT;
                regex = REGEX_CERT;
                break;
            case FOND:
                start = START_FRAGMENT_FOND;
                regex = REGEX_FOND;
                break;
            case SHARE:
                start = START_FRAGMENT_SHARE;
                regex = REGEX_SHARE;
                break;
            default:
                throw new IllegalArgumentException("unknown type " + stock.type);
        }

        int idx = text.indexOf(start);
        text = text.substring(idx, idx + 500);

        regex = regex.replaceAll("[ \t\n\r]", "");

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String value = matcher.group(1);
            String diff = matcher.group(2);
            String time = matcher.group(3);
            int age = FormatUtilities.parseTimestamp(time);
            return new Price(value, diff, age);
        }
        return new Price("0,00", "0,00", 0);
    }

    private String loadFileFromUrl(String address, String suffix) {

        try {
            URL url = new URL(address + suffix);
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

