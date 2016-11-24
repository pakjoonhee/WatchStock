package com.sam_chordas.android.stockhawk.ui;

/**
 * Created by joonheepak on 11/18/16.
 */

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sam_chordas.android.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class GraphFragment extends Fragment implements OnChartValueSelectedListener {
    public static final String SYMBOL = "SYMBOL";
    public static final String CURRENT_PRICE = "CURRENT_PRICE";
    private String theSymbol;
    private String bidPrice;
    String yesterdayDate = getYesterdayDateString();
    String threeMonthsDate = getThreeMonthsDateString();
    private String BASE_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20";
    private String END_URL = "%20and%20startDate%20%3D%20\"" + threeMonthsDate + "\"%20and%20endDate%20%3D%20\"" + yesterdayDate + "\"&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    private ArrayList<String> stockHistory = new ArrayList<>();
    private ArrayList<String> theStockDate = new ArrayList<>();
    private ArrayList<String> theStockPrice = new ArrayList<>();
    private ArrayList<String> reformedDates = new ArrayList<>();
    private String[] dateSplit;
    private LineChart mpAndroidChart;
    private TextView stockPrice;
    private TextView stockDate;
    private TextView currentPrice;
    private String [] dateValues;
    View rootView;
    private String monthString;

    public static GraphFragment newInstance(String symbol, String currentPrice) {
        Bundle args = new Bundle();
        args.putString(SYMBOL, symbol);
        args.putString(CURRENT_PRICE, currentPrice);
        GraphFragment fragment = new GraphFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theSymbol = getArguments().getString(SYMBOL);
        bidPrice = getArguments().getString(CURRENT_PRICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.graph_fragment, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        mpAndroidChart = (LineChart) rootView.findViewById(R.id.mpandroidchart);
        List<Entry> lineData = new ArrayList<Entry>();
        String historyUrl = BASE_URL + "\"" + theSymbol + "\"" + END_URL;
        stockPrice = (TextView) rootView.findViewById(R.id.stock_price);
        stockDate = (TextView) rootView.findViewById(R.id.stock_date);
        currentPrice = (TextView) rootView.findViewById(R.id.current_price);


        try {
            theStockPrice = new AsyncHttpTask().execute(historyUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < theStockPrice.size(); i++) {
            theStockDate.add(theStockPrice.get(i));
            theStockPrice.remove(i);
        }

        float [] floatValues = new float[theStockPrice.size()];
        for (int i = 0; i < theStockPrice.size(); i++) {
            floatValues[i] = Float.parseFloat(theStockPrice.get(i));
            Entry theEntry = new Entry((float)i, floatValues[i]);
            lineData.add(theEntry);

        }

        XAxis xAxis = mpAndroidChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = mpAndroidChart.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawZeroLine(true);
        mpAndroidChart.getAxisRight().setEnabled(false);

        LineDataSet theLine = new LineDataSet(lineData, "Company 1");
        theLine.setDrawCircles(false);
        theLine.setLineWidth(2f);
        theLine.setHighLightColor(Color.RED);
        theLine.setColor(getResources().getColor(R.color.color_primary, null));
        theLine.setDrawHorizontalHighlightIndicator(true);

//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(mpAndroidChart);
//        mpAndroidChart.setMarker(mv);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(theLine);

        LineData data = new LineData(dataSets);
        data.setDrawValues(false);
        mpAndroidChart.setDescription(null);
        mpAndroidChart.setData(data);
        mpAndroidChart.setDrawGridBackground(true);
        mpAndroidChart.setViewPortOffsets(15f, 200f, 15f, 200f);
        mpAndroidChart.setGridBackgroundColor(getResources().getColor(R.color.white, null));
        mpAndroidChart.setScaleEnabled(false);
        mpAndroidChart.setOnChartValueSelectedListener(this);
        mpAndroidChart.getLegend().setEnabled(false);
        mpAndroidChart.invalidate();

        return rootView;
    }


    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    private String getThreeMonthsDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -90);
        return dateFormat.format(cal.getTime());
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        stockPrice.setText("$" + e.getY());
        stockDate.setText(theStockDate.get((int)e.getX()));
        currentPrice.setText("$" + bidPrice);

    }

    @Override
    public void onNothingSelected() {

    }

    public class AsyncHttpTask extends AsyncTask<String, Void, ArrayList<String>> {
        HttpURLConnection connection = null;

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> result = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                String response = streamToString(stream);
                result = parseStockClose(response);
            } catch (Exception e) {
                Log.d("FAIL", e.getLocalizedMessage());
            }
            return result;
        }

        private ArrayList<String> parseStockClose(String result) {
            JSONArray resultsArray = null;
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(result);
                resultsArray = jsonObject.getJSONObject("query").getJSONObject("results").getJSONArray("quote");
                for (int i = 0; i < resultsArray.length(); i++) {
                    jsonObject = resultsArray.optJSONObject(i);
                    String stockDate = jsonObject.optString("Date");
                    stockHistory.add(stockDate);
                    String stockPrice = jsonObject.optString("Close");
                    stockHistory.add(stockPrice);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return stockHistory;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);

        }
        public String streamToString(InputStream stream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            if (null != stream) {
                stream.close();
            }
            return result;
        }


    }

}


