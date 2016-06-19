package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.db.chart.view.ChartView;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class LineGraphActivity extends Activity {
    private String BASE_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20";
    private String END_URL = "%20and%20startDate%20%3D%20\"2009-09-11\"%20and%20endDate%20%3D%20\"2010-03-10\"&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    private ArrayList<String> stockHistory = new ArrayList<>();
    private ArrayList<String> retrievedStockHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_line_graph);
        Bundle bundle = getIntent().getExtras();
        String symbol = bundle.getString("symbol");

        //StringBuilder urlStringBuilder = new StringBuilder();
        //try {
            //urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            //urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol " + "= ", "UTF-8"));
        // } catch (UnsupportedEncodingException e) {
           // e.printStackTrace();
        //}
        String historyUrl = BASE_URL + "\"" + symbol + "\"" + END_URL;

        try {
            retrievedStockHistory = new AsyncHttpTask().execute(historyUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ChartView chartView = (ChartView)findViewById(R.id.linechart);


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
                result = parseReview(response);
            } catch (Exception e) {
                Log.d("FAIL", e.getLocalizedMessage());
            }
            return result;
        }

        private ArrayList<String> parseReview(String result) {
            JSONArray resultsArray = null;
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(result);
                resultsArray = jsonObject.getJSONObject("query").getJSONObject("results").getJSONArray("quote");
                for (int i = 0; i < resultsArray.length(); i++) {
                    jsonObject = resultsArray.optJSONObject(i);
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
