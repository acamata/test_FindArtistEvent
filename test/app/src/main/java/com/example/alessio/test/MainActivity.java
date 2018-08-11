package com.example.alessio.test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {
    private ListView listView;
    private Button button;
    private Button search;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewsById();
        //al click del pulsante search trova l'artista
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String artist = text.getText().toString();
                getSupportActionBar().setTitle(artist + " Tour Dates");
                new ArtistDataDownloadTask().execute("http://api.bandsintown.com/artists/" + artist + "/" +
                        "events.json?api_version=2.0&app_id=SelectiveTask");


            }
        });


        button.setOnClickListener(this);
    }

    private void updateList(JSONArray artists) {
        CustomAdapter adapter = new CustomAdapter(this, artists);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

    }

    //scarica i dati tramite richiesta rest dal sito bandsintown
    private class ArtistDataDownloadTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... urls) {
            try {
                return downloadAndParseData(urls[0]);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray res) {
            if(res != null) updateList(res);
            else {
                Toast.makeText(MainActivity.this, "Sorry. Cannot load the artist"
                        , Toast.LENGTH_LONG).show();
            }
        }

        private JSONArray downloadAndParseData(String requestdUrl) throws IOException, JSONException {
            InputStream is = null;

            try {
                URL url = new URL(requestdUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();

                return parseInputStreamToJSON(is);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        private JSONArray parseInputStreamToJSON(InputStream is) throws IOException, JSONException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder res = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                res.append(line);
            }

            Log.e("downloader_result", res.toString());
            return new JSONArray(res.toString());
        }
    }


    //collegamento pulsanti java-xml
    private void findViewsById() {
        listView = (ListView) findViewById(R.id.list);
        button = (Button) findViewById(R.id.button);
        search = (Button) findViewById(R.id.search);
        text = (EditText) findViewById(R.id.mioEditText);
    }

    private List<JSONObject> getSelectedItems() {
        List<JSONObject> result = new ArrayList<>();
        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();

        for (int i = 0; i < checkedItems.size(); i++) {
            if (checkedItems.valueAt(i))
                result.add((JSONObject) listView.getItemAtPosition(checkedItems.keyAt(i)));

        }

        return result;
    }




}
