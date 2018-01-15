package com.example.littlerockplanet.spacesystem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class NewsActivity extends AppCompatActivity {

    private RecyclerView articleListView;
    private ArrayList<ArticleObject> articleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_news);

        //GETS THE NEWS
        articleListView = (RecyclerView) findViewById(R.id.articleList);
        articleListView.setHasFixedSize(true);
        final LinearLayoutManager newsLayoutManager = new LinearLayoutManager(this);
        articleListView.setLayoutManager(newsLayoutManager);
        GetNews getNews = new GetNews();
        getNews.execute();

        //ADD FUNCTIONALITY TO BUTTONS
        TextView latestNewsTitleButton = (TextView)findViewById(R.id.newsTitle);
        latestNewsTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                articleListView.smoothScrollToPosition(0);
            }
        });
    }

    private class GetNews extends AsyncTask<Void, Void, String>{
        protected String doInBackground(Void... urls){
            try {
                URL url = new URL("https://newsapi.org/v2/everything?domains=spacenews.com,space.com&pageSize=50&apiKey=01879c4a1fd0441e9673ef62fd758fb1");
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                try{
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally {
                    urlConnection.disconnect();
                }
            }catch (Exception e){return null;}
        }

        protected void onPostExecute(String response){
            if(response == null){
                return;
            }
            else {
                try {

                    JSONObject completeObject = new JSONObject(response);
                    JSONArray jsonArray = completeObject.getJSONArray("articles");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject article = jsonArray.getJSONObject(i);
                        String headline = article.getString("title");
                        String content = article.getString("description");
                        String link = article.getString("url");
                        String publishedTime = article.getString("publishedAt");

                        articleList.add(new ArticleObject(headline,content,link, publishedTime));
                    }
                } catch (Exception e) {}

                //CREATE LIST VIEW AND LINKS
                articleListView.setAdapter(new NewsAdapter(getApplicationContext(),articleList));
            }
        }
    }
}