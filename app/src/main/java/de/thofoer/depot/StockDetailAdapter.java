package de.thofoer.depot;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import de.thofoer.depot.data.FormatUtilities;
import de.thofoer.depot.data.Stock;

public class StockDetailAdapter extends ArrayAdapter<Stock>  {


    private static class ViewHolder {
        TextView stockName;
        TextView price;
        TextView diff;
        ImageView image;
        TextView total;
        TextView totalDiff;
    }

    public StockDetailAdapter(@NonNull Context context, int resource, @NonNull List<Stock> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return super.getCount()+1;
    }
    @Override
    public Stock getItem(int position) {
        return super.getItem(position-1);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StockDetailAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stock_detail_listrow, parent, false);
            viewHolder = new StockDetailAdapter.ViewHolder();
            viewHolder.stockName = (TextView) convertView.findViewById(R.id.textDetailStockName);
            viewHolder.price = (TextView) convertView.findViewById(R.id.textDetailQuote);
            viewHolder.diff = (TextView) convertView.findViewById(R.id.textDetailDiff);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.total = (TextView) convertView.findViewById(R.id.textDetailTotal);
            viewHolder.totalDiff = (TextView) convertView.findViewById(R.id.textDetailTotalDiff);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StockDetailAdapter.ViewHolder) convertView.getTag();
        }
        Typeface style = position==0
                ? Typeface.DEFAULT_BOLD
                : Typeface.DEFAULT;
        setTypeFace(viewHolder.stockName, style);
        setTypeFace(viewHolder.price, style);
        setTypeFace(viewHolder.diff, style);
        setTypeFace(viewHolder.total, style);
        setTypeFace(viewHolder.totalDiff, style);

        if (position==0) {
            viewHolder.stockName.setText("Name");
            viewHolder.price.setText("Kurs");
            viewHolder.diff.setText("Diff");
            viewHolder.total.setText("Gesamt");
            viewHolder.totalDiff.setText("Diff gesamt");
            viewHolder.diff.setTextColor(getContext().getResources().getColor(R.color.gray, getContext().getTheme()));
            viewHolder.totalDiff.setTextColor(getContext().getResources().getColor(R.color.gray, getContext().getTheme()));
            viewHolder.image.setImageDrawable(null);
            return convertView;
        }
        Stock stock = getItem(position);
        int color = R.color.gray;
        if (stock.price.value>0 && stock.price.diff > 0) {
            color = R.color.pos_text_color;
        }
        else if (stock.price.value>0 && stock.price.diff < 0) {
            color = R.color.neg_text_color;
        }

        viewHolder.diff.setTextColor(getContext().getResources().getColor(color, getContext().getTheme()));

        color = R.color.gray;
        if (stock.price.value>0 && stock.getTotalDiff() > 0) {
            color = R.color.pos_text_color;
        }
        else if (stock.price.value>0 && stock.getTotalDiff() < 0) {
            color = R.color.neg_text_color;
        }

        viewHolder.totalDiff.setTextColor(getContext().getResources().getColor(color, getContext().getTheme()));

        viewHolder.stockName.setText(stock.name);
        if (stock.price.value<0) {
            viewHolder.price.setText("");
            viewHolder.diff.setText("");
            viewHolder.total.setText("");
            viewHolder.totalDiff.setText("");
            viewHolder.image.setImageDrawable(null);
        }
        else {
            viewHolder.price.setText(FormatUtilities.formatValue(stock.price));
            viewHolder.diff.setText(FormatUtilities.formatDiff(stock.price));
            viewHolder.total.setText(FormatUtilities.formatValue(stock.getTotal()));
            viewHolder.totalDiff.setText(FormatUtilities.formatDiff(stock.getTotal(), stock.getTotalDiff()));
        }

        if (stock.price.value>=0) {
            if (stock.price.age <= 60) {
                viewHolder.image.setImageResource(R.drawable.kreisgruen);
            } else if (stock.price.age <= 300) {
                viewHolder.image.setImageResource(R.drawable.kreisgelb);
            } else {
                viewHolder.image.setImageResource(R.drawable.kreisrot);
            }
        }
        return convertView;
    }

    void setTypeFace(TextView textView, Typeface style) {
        textView.setTypeface(style);
    }


}
