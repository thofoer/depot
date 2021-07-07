package de.thofoer.depot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.time.format.FormatStyle;
import java.util.List;

import de.thofoer.depot.data.FormatUtilities;
import de.thofoer.depot.data.Stock;
import de.thofoer.depot.data.StockDatabase;

public class StockAdapter extends ArrayAdapter<Stock> {

    private static class ViewHolder {
        TextView stockName;
        TextView price;
        TextView diff;
        TextView absDiff;
    }


    public StockAdapter(@NonNull Context context, int resource, @NonNull List<Stock> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Stock stock = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stock_listrow, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.stockName = (TextView) convertView.findViewById(R.id.textStockName);
            viewHolder.price = (TextView) convertView.findViewById(R.id.textPrice);
            viewHolder.diff = (TextView) convertView.findViewById(R.id.textDiff);
            viewHolder.absDiff = (TextView) convertView.findViewById(R.id.textAbsDiff);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

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

        viewHolder.absDiff.setTextColor(getContext().getResources().getColor(color, getContext().getTheme()));

        viewHolder.stockName.setText(stock.name);
        if (stock.price.value<0) {
            viewHolder.price.setText("");
            viewHolder.diff.setText("");
            viewHolder.absDiff.setText("");
        }
        else {
            viewHolder.price.setText(FormatUtilities.formatValue(stock.price));
            viewHolder.diff.setText(FormatUtilities.formatDiff(stock.price));
            viewHolder.absDiff.setText(FormatUtilities.formatPercent(stock.getTotal(), stock.getTotalDiff()));
        }


        viewHolder.stockName.setOnLongClickListener((view) -> openMenu(stock, viewHolder.stockName));
        viewHolder.price.setOnLongClickListener((view) -> openMenu(stock, viewHolder.stockName));
        viewHolder.diff.setOnLongClickListener((view) -> openMenu(stock, viewHolder.stockName));
        viewHolder.absDiff.setOnLongClickListener((view) -> openMenu(stock, viewHolder.stockName));
        return convertView;
    }

    private boolean openMenu(final Stock stock, View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.popupItemEdit) {
                editStock(stock);
            }
            else if (item.getItemId() == R.id.popupItemDelete) {
                deleteStock(stock);
            }
            else if (item.getItemId() == R.id.popupItemOpen) {
                openStock(stock);
            }
            return true;
        });
        popup.show();
        return true;
    }

    private void openStock(Stock stock) {
        String url = "https://www.comdirect.de/inf/aktien/"+stock.isin;
        getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void editStock(Stock stock) {

    }

    private void deleteStock(Stock stock) {
        MainActivity.executor.execute( () ->  {
            StockDatabase.getDatabase(getContext()).stockDao().delete(stock);
            StockAdapter.this.remove(stock);
        });
        notifyDataSetChanged();
    }

}

// https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView