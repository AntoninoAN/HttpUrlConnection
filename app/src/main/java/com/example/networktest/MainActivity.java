package com.example.networktest;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    static String BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    static String QUERY_Q = "q";
    static String PARAM_MAX_RESULTS = "maxResults";
    static String PARAM_PRINT_TYPE = "printType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_Q, "Game of Thrones")
                .appendQueryParameter(PARAM_MAX_RESULTS, "10")
                .appendQueryParameter(PARAM_PRINT_TYPE, "books")
                .build();

        new CustomAsyncTask(uri).execute();
    }

    class CustomAsyncTask extends AsyncTask<String,
            Void, ArrayList<String>> {
        Uri uri;

        public CustomAsyncTask(Uri uri) {
            this.uri = uri;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(uri.toString());
                HttpURLConnection connection = (HttpURLConnection)
                        url.openConnection();
                connection.setReadTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                int requestResponseCode = connection.getResponseCode();

                InputStream inputStream = connection.getInputStream();

                //todo parsing method
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream)
                );
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }
                if (builder.length() == 0) {
                    //todo failed reader statement
                }
                //assign to specific variable

                getStringFromJson(builder);


                connection.disconnect();
                inputStream.close();
            } catch (MalformedURLException err) {
                err.printStackTrace();
            } catch (IOException err) {
                err.printStackTrace();
            } catch (JSONException err) {
                err.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);

        }
    }

    private void getStringFromJson(StringBuilder builder) throws JSONException {
        JSONObject jsonObject = new JSONObject(builder.toString());
        JSONArray array = jsonObject.getJSONArray("items");
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            String title = object.getString("title");
            String author = object.getString("author");
            String publishDate = object.getString("publishDate");
        }
    }

}
