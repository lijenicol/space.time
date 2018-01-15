package com.example.littlerockplanet.spacesystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Random random = new Random();
    private int randomImageIndex;
    private static final int imagePool = 20;

    private ConstraintLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);

        //EXECUTE LOADING SCREEN WHILE DOWNLOADING BACKGROUND IMAGE
        loadingLayout = (ConstraintLayout)findViewById(R.id.loadingLayout);
        loadingLayout.setVisibility(View.VISIBLE);
        GetUnsplashedPictures pictures = new GetUnsplashedPictures();
        pictures.execute();

        //SET LISTENER ON BUTTON
        Button newsTrigger = (Button)findViewById(R.id.startTrigger);
        newsTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToNews();
            }
        });
    }

    private void transitionToNews(){
        Intent newsActivity = new Intent(this,NewsActivity.class);
        startActivity(newsActivity);
    }

    //SEARCH SPACE PHOTOS ON UNSPLASH AND RETURN URLS OF IMAGES
    private class GetUnsplashedPictures extends AsyncTask<Void,Void,String>{

        //THIS IS THE IMPORTANT PART
        protected String doInBackground(Void... urls){
            try{
                URL url = new URL("https://api.unsplash.com/search/photos?orientation=portrait&per_page="+imagePool+"&query=space&client_id=5a85a353d38877d469c6c462c7b7dab16b818ffd87e7541e57f2118f9b291d10");
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
            }
            catch (Exception e){
                return null;
            }
        }

        //ONCE THE JSON IS DOWNLOADED, SEARCH FOR THE URL
        protected void onPostExecute(String response) {
            String photoURL = "";
            String photoURLs[] = new String[imagePool];
            String creators[] = new String[imagePool];

            TextView creatorText = (TextView)findViewById(R.id.imageCreatorID);

            if(response == null){
                photoURL = "Dang it didnt work";
                return;
            }
            else {
                try {
                    JSONObject completeObject = new JSONObject(response);
                    JSONArray jsonArray = completeObject.getJSONArray("results");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        JSONObject picture = object.getJSONObject("urls");
                        JSONObject creator = object.getJSONObject("user");

                        //SET PHOTO URL
                        photoURL = picture.getString("regular");
                        photoURLs[i] = photoURL;

                        //SET CREATOR
                        if(creator.getString("last_name") != "null") {
                            creators[i] = creator.getString("first_name") + " " + creator.getString("last_name");
                        }
                        else{
                            creators[i] = creator.getString("first_name");
                        }
                    }
                } catch (JSONException e) {}
            }

            randomImageIndex = random.nextInt(imagePool);
            creatorText.setText("image by "+creators[randomImageIndex]);
            new DownloadImage().execute(photoURLs);
        }
    }

    private class DownloadImage extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[randomImageIndex];
            Bitmap image = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                image = BitmapFactory.decodeStream(is);
            }catch (Exception e){}
            return image;
        }

        protected void onPostExecute(Bitmap result){
            ImageView imageView = (ImageView)findViewById(R.id.backgroundImage);
            imageView.setImageBitmap(result);
            loadingLayout.setVisibility(View.INVISIBLE);
        }
    }
}