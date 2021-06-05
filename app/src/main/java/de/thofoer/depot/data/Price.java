package de.thofoer.depot.data;

import java.io.Serializable;

public class Price implements Serializable {
    public int value;
    public int diff;
    public int age;

    public Price() {
        value = -1;
        diff = -1;
    }

    public Price(String value, String diff, int age) {
        this.value = parse(value);
        this.diff = parse(diff);
        this.age = age;
    }

    private int parse(String v) {
        v = v.trim().replace('.', ',');
        int neg = v.charAt(0)=='-' ? -1 : 1;
        if (neg==-1) {
            v=v.substring(1);
        }
        int idx = v.indexOf(',');
        String number = v.substring(0, idx);
        String frac = (v.substring(idx+1)+"0000").substring(0, 4);
        return neg * (Integer.valueOf(number)*10000+Integer.valueOf(frac));
    }


    public String toString() {
        return FormatUtilities.formatValue(this)+" ("+FormatUtilities.formatDiff(this)+")";
    }
}
