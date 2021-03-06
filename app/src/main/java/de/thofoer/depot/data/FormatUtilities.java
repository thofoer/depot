package de.thofoer.depot.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class FormatUtilities {

    public static int parseTimestamp(String timestamp) {
        DateFormat df;
        if (timestamp.length()==16) {
            df = new SimpleDateFormat("dd.MM.yyHH:mm:ss");
        }
        else if (timestamp.length()==10) {
            df = new SimpleDateFormat("dd.MM.yyyy");
        }
        else {
            timestamp = String.format("%1$td.%1$tm.%1$ty", new Date())+timestamp;
            df = new SimpleDateFormat("dd.MM.yyHH:mm:ss");
        }
        try {
            return (int) ((System.currentTimeMillis() -   df.parse(timestamp).getTime()) / 1000) ;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatValue(Price price) {
        return formatValue(price.value);
    }

    public static String formatValue(int value) {
        return strip(String.valueOf(  value/10000)+","+String.format("%04d", value%10000));
    }

    public static String formatDiff(Price price) {
        return formatDiff(price.value, price.diff);
    }

    public static String formatDiff(int value, int diff) {
        String neg = diff < 0 ? "-" : "+";
        int diffAbs = Math.abs(diff);
        String percent = calcPercent(value-diff, diffAbs);
        String s = neg + String.valueOf(diffAbs/10000) + "," +String.format("%02d", (diffAbs%10000)/100);
        return s + " (" + neg + percent + "%)";
    }

    public static String formatPercent(int value, int diff) {
        String neg = diff < 0 ? "-" : "+";
        int diffAbs = Math.abs(diff);
        String percent = calcPercent(value-diff, diffAbs);
        return neg + percent + "%";
    }

    private static String calcPercent(int value, int diff) {
        double percent = (double)diff / (double)value;
        return String.format("%.2f", 100*percent);
    }

    private static String strip(String s) {
        if (s.endsWith("00")) {
            return s.substring(0, s.length()-2) + "  ";
        }
        if (s.endsWith("0")) {
            return s.substring(0, s.length()-1) + " ";
        }
        return s;
    }

}
