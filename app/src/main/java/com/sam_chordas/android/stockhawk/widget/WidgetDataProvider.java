package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteDatabase;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joonheepak on 6/23/16.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String[] FORECAST_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.ISCURRENT
    };
    private static final String TAG = "WidgetDataProvider";

    List<String> mCollection = new ArrayList<>();
    Context mContext = null;
    String symbol;
    String price;
    String current;
    Cursor initQueryCursor;

    static final int INDEX_ID = 0;
    static final int INDEX_SYMBOL = 1;
    static final int INDEX_BIDPRICE = 2;
    static final int INDEX_ISCURRENT = 3;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[] {QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE, QuoteColumns.ISCURRENT}, "is_current='1' ",
                null, null);

        if(initQueryCursor != null && initQueryCursor.moveToFirst() ){
            symbol = initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol"));
            price = initQueryCursor.getString(initQueryCursor.getColumnIndex("bid_price"));
            current = initQueryCursor.getString(initQueryCursor.getColumnIndex("is_current"));
            initQueryCursor.close();
        }

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return initQueryCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_layout);
        view.setTextViewText(R.id.symbol, symbol);
        view.setTextViewText(R.id.price, price);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        mCollection.clear();
        for (int i = 1; i <= 10; i++) {
            mCollection.add("ListView item " + i);
        }
    }

}
