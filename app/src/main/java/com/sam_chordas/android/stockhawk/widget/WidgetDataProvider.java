package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Bundle;
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

    Context mContext = null;
    Intent mIntent = null;
    String symbol;
    String price;
    String current;
    String change;
    Cursor initQueryCursor;
    String id;
    RemoteViews view;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @Override
    public void onCreate() {
        initData();

    }

    @Override
    public void onDataSetChanged() {
        final long token = Binder.clearCallingIdentity();
        try {
            initData();
        } finally {
            Binder.restoreCallingIdentity(token);
        }

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
        if (initQueryCursor.moveToPosition(position)) {
            id = initQueryCursor.getString(initQueryCursor.getColumnIndex("_id"));
            symbol = initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol"));
            change = initQueryCursor.getString(initQueryCursor.getColumnIndex("change"));
            price = initQueryCursor.getString(initQueryCursor.getColumnIndex("bid_price"));
            current = initQueryCursor.getString(initQueryCursor.getColumnIndex("is_current"));
            view = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_layout);
            view.setTextViewText(R.id.symbol, symbol);
            view.setTextViewText(R.id.price, price);
            view.setTextViewText(R.id.change, change);
        }
        Intent newIntent = new Intent();
        newIntent.putExtra("symbol", symbol);
        view.setOnClickFillInIntent(R.id.rootview, newIntent);
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

    public void initData() {
        initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[] {QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE, QuoteColumns.ISCURRENT, QuoteColumns.CHANGE}, "is_current='1' ",
                null, null);
    }


}
