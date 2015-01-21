package com.infowaygroup.myweatherapp;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    public String readJSONFeed(String URL){
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {

            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }

    private  class ReadWeatherJSONFeedTask extends AsyncTask <String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result){
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONObject weatherObservationItems = new JSONObject(jsonObject.getString("current_observation"));
                String temp = weatherObservationItems.getString("temperature_string");
                TextView tv = (TextView) findViewById(R.id.textView);
                tv.setText("The current Temperature is " +temp);


                //Toast.makeText(getBaseContext(),
                  //      weatherObservationItems.getString("temperature_string"), Toast.LENGTH_LONG).show();

            }catch (Exception e){
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }

        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void weatherButton(View veiw) {
        EditText et1 = (EditText) findViewById(R.id.editText);
        String link = et1.getText().toString();
        link.replaceAll("\\w", "emp");

        new ReadWeatherJSONFeedTask().execute
                ("http://api.wunderground.com/api/8caab6ca634fc2c2/" +
                        "conditions/q/" + link + ".json");

    }
}
