package com.example.sandeepkumar.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView tempTextView,mainTextView,desTextView;
    EditText nameEditText;

    @SuppressLint("StaticFieldLeak")
    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();          //Save the link of URL
            URL url ;                                           //Store the HTML code of URL
            HttpURLConnection urlConnection;         //Browser to Use HTML Code
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result.append(current);             //Saving the char in result to make String
                    data = reader.read();               //Moving data to next char
                }
                return result.toString();               //Return the Data Collected from API
            } catch (Exception e) {
                e.printStackTrace();
                return "Falied";
            }
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try { //JSON Object are Multiple Array String
                JSONObject jsonObject = new JSONObject(result);
                //Extracting Weather String from Data Collected from Result String
                String weatherData = jsonObject.getString("weather");
                JSONArray array = new JSONArray(weatherData);
                for(int i = 0; i < array.length(); i++){//Extracting array Strings in Weather String
                    JSONObject jsonPart = array.getJSONObject(i);
                    mainTextView.setText(jsonPart.getString("main"));//Main String
                    desTextView.setText(jsonPart.getString("description"));
                }//Extracting Temperature Details String from Result String
                JSONObject tempData = new JSONObject(jsonObject.getString("main"));
                tempTextView.setText("Temperature(Kelvin):"+ tempData.getString("temp")
                                    +"\nTemperature Min:" + tempData.getString("temp_min")
                                    +"\nTemperature Max:" + tempData.getString("temp_max"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void showWeather(View view){
        String city = nameEditText.getText().toString();
        if (!city.isEmpty()) {//If user enter the City then Collecting the Data
            DownloadTask tab = new DownloadTask();
            try {
                tab.execute("https://api.openweathermap.org/data/2.5/weather?q="
                        + city + "&appid=d49eef2a97966066967c1fc018fb2ed2").get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else{//If user doesn't enter city A Toast Will Pop-up
            Toast.makeText(getApplicationContext(),"Enter City Name",Toast.LENGTH_SHORT).show();
        }
        //Hide Keyboard After Enter the City Name
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert manager != null;
        manager.hideSoftInputFromWindow(nameEditText.getWindowToken(),0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempTextView = findViewById(R.id.tempTextView);
        desTextView = findViewById(R.id.despTextView);
        mainTextView = findViewById(R.id.mainTextView);
        nameEditText = findViewById(R.id.nameEditText);
    }
}