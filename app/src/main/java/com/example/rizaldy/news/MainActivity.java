package com.example.rizaldy.news;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rizaldy.news.ListNewsDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView main_nav;
    private FrameLayout main_frame;

    private Fragment_ID home_fragment;
    private Fragment_AUS aus_fragment;
    private Fragment_ML ml_fragment;

    String API_KEY = "254caa9d34e84c9a98b9974c6f80d20e";
    String source_1 = "id";
    String source_2 = "au";
    String source_3 = "my";
    ListView listNews;
    ProgressBar loader;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
    static final String KEY_AUTHOR = "author";
    static final String KEY_TITTLE = "tittle";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
    static final String KEY_PUBLISHEDAT = "publishedAt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_frame = (FrameLayout) findViewById(R.id.main_frame);
        main_nav = (BottomNavigationView) findViewById(R.id.main_nav);

        home_fragment = new Fragment_ID();
        aus_fragment = new Fragment_AUS();
        ml_fragment = new Fragment_ML();
        loader = (ProgressBar) findViewById(R.id.load);
        listNews.setEmptyView(loader);

        if(fungsi.isNetworkAvailable(getApplicationContext()))
        {
            DownloadNews newsTask = new DownloadNews();
            newsTask.execute();
        }else{
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        main_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.indonesia :
                        main_nav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(home_fragment);
                        return true;

                    case R.id.australia :
                        main_nav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(aus_fragment);
                        return true;

                    case R.id.malaysia :
                        main_nav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        setFragment(ml_fragment);
                        return true;

                    default:
                        return false;
                }
            }

            private void setFragment(Fragment_ID fragment) {
                FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.main_frame, fragment);
                fragmenttransaction.commit();
            }

            private void setFragment(Fragment_AUS fragment) {
                FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.main_frame, fragment);
                fragmenttransaction.commit();
            }

            private void setFragment(Fragment_ML fragment) {
                FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.main_frame, fragment);
                fragmenttransaction.commit();
            }
        });
    }

    class DownloadNews extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String xml = "";

            String urlParameters = "";
            if(home_fragment.isResumed() == true) {
                xml = fungsi.excuteGet("https://newsapi.org/v2/top-headlines?country=" + source_1 + "&sortBy=top&apiKey=" + API_KEY, urlParameters);
                return xml;
            }else if (aus_fragment.isResumed() == true){
                xml = fungsi.excuteGet("https://newsapi.org/v2/top-headlines?country=" + source_2 + "&sortBy=top&apiKey=" + API_KEY, urlParameters);
                return xml;
            }else{
                xml = fungsi.excuteGet("https://newsapi.org/v2/top-headlines?country=" + source_3 + "&sortBy=top&apiKey=" + API_KEY, urlParameters);
                return xml;
            }
        }
        @Override
        protected void onPostExecute(String xml) {

            if(xml.length()>10){ // Just checking if not empty

                try {
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("articles");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_AUTHOR, jsonObject.optString(KEY_AUTHOR).toString());
                        map.put(KEY_TITTLE, jsonObject.optString(KEY_TITTLE).toString());
                        map.put(KEY_DESCRIPTION, jsonObject.optString(KEY_DESCRIPTION).toString());
                        map.put(KEY_URL, jsonObject.optString(KEY_URL).toString());
                        map.put(KEY_URLTOIMAGE, jsonObject.optString(KEY_URLTOIMAGE).toString());
                        map.put(KEY_PUBLISHEDAT, jsonObject.optString(KEY_PUBLISHEDAT).toString());
                        dataList.add(map);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
                }

                ListNewsDetail adapter_detail = new ListNewsDetail(MainActivity.this, dataList);
                listNews.setAdapter((ListAdapter) adapter_detail);

                listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent i = new Intent(MainActivity.this, detail_news.class);
                        i.putExtra("url", dataList.get(+position).get(KEY_URL));
                        startActivity(i);
                    }
                });

            }else{
                Toast.makeText(getApplicationContext(), "No news found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
