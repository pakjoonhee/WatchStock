package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
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
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class LineGraphActivity extends Activity {
    String yesterdayDate = getYesterdayDateString();
    String threeMonthsDate = getThreeMonthsDateString();
    private String BASE_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20";
    private String END_URL = "%20and%20startDate%20%3D%20\"" + threeMonthsDate + "\"%20and%20endDate%20%3D%20\"" + yesterdayDate + "\"&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    private ArrayList<String> stockHistory = new ArrayList<>();
    private ArrayList<String> dateStock = new ArrayList<>();
    private ArrayList<String> retrievedStockHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        Bundle bundle = getIntent().getExtras();
        String symbol = bundle.getString("symbol");


        String historyUrl = BASE_URL + "\"" + symbol + "\"" + END_URL;

        try {
            retrievedStockHistory = new AsyncHttpTask().execute(historyUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < retrievedStockHistory.size(); i++) {
            dateStock.add(retrievedStockHistory.get(i));
            retrievedStockHistory.remove(i);
        }
        float [] floatValues = new float[retrievedStockHistory.size()];
        for (int i = 0; i < retrievedStockHistory.size(); i++) {
            floatValues[i] = Float.parseFloat(retrievedStockHistory.get(i));
        }

        float sum = 0;
        for(int i = 0; i < floatValues.length; i++) {
            sum += floatValues[i];
        }
        float average = sum / floatValues.length;
        String averageRounded = String.format("%.2f", average);
        TextView averagePrice = (TextView)findViewById(R.id.chart_price);
        averagePrice.setText(String.valueOf(averageRounded));


        //int listSize = retrievedStockHistory.size();

        //for (int i = 0; i<listSize; i++){
         //   Log.i("the date: ", retrievedStockHistory.get(i));
        //}

        String[] dateArr = new String[dateStock.size()];
        dateArr = dateStock.toArray(dateArr);


        LineChartView lineChartView = (LineChartView) findViewById(R.id.linechart);

        LineSet dataset = new LineSet(dateArr, floatValues);
        dataset.setColor(Color.parseColor("#53c1bd"))
                .setSmooth(true)
                .setFill(Color.parseColor("#3d6c73"))
                .setGradientFill(new int[]{Color.parseColor("#364d5a"), Color.parseColor("#3f7178")}, null);
        lineChartView.addData(dataset);

        lineChartView.setBorderSpacing(10)
                .setStep(50)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXAxis(false)
                .setYAxis(true)
                .setLabelsColor(-1)
                .setFontSize(40)
                .setBorderSpacing(Tools.fromDpToPx(0));


        Animation anim = new Animation();
        lineChartView.addData(dataset);
        lineChartView.show();


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
