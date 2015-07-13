package com.jimmylnsta.instagramclient;

import android.app.Activity;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends Activity {

    public  static  final  String CLIENT_ID="33469fd354744258aea3f758b74bc01d";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        //SEND OUT API REQUEST to POPULAR PHOTOS
        photos=new ArrayList<>();
        // 1. Create the adapter and linking it to the source
            aPhotos=new InstagramPhotosAdapter(this,photos);
        // 2. Find the listview from the layout
        ListView lvPhotos=(ListView) findViewById(R.id.lvPhotos);
        // 3. set the adapter binding it to the ListView
        lvPhotos.setAdapter(aPhotos);
        fetchPopularPhotos();
    }
    //Trigger API request
    public void fetchPopularPhotos(){
        /*
        -CLIENT ID : 33469fd354744258aea3f758b74bc01d
        - Popular : https://api.instagram.com/v1/media/{media-id}?access_token=ACCESS-TOKEN
        - Response
        - Type:{"data"=> [x] => "type"} ("image" or "video")
        - URL :{"data"=> [x] => "image" => "standard_resolution" => "url"}
        - Caption : {"data"=> [x] => "caption" => "text"}
        - Author name : {"data"=> [x] => "user" => "username"}
        */
        String url="https://api.instagram.com/v1/media/popular?client_id="+ CLIENT_ID;
        //create the network client
        AsyncHttpClient client=new AsyncHttpClient();
        //Trigger the GET Request
        client.get(url,null, new JsonHttpResponseHandler(){
            //onSuccess (Worked, 200)

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Expecting a JSON object
               //Iterate each of the photo items and decode the item into the Java object
                JSONArray photosJSON = null;
                try{
                    photosJSON = response.getJSONArray("data");//array of post
                    //Iterate array of post
                    for (int i=0;i<photosJSON.length();i++){
                        JSONObject photoJSON=photosJSON.getJSONObject(i);
                        //decode the attributes of the json into a data model
                        InstagramPhoto photo = new InstagramPhoto();

                        photo.username = photoJSON.getJSONObject("user").getString("username");
                        //get the JSONobject at that position
                        photo.caption = photoJSON.getJSONObject("caption").getString("text");

                        photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");

                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");

                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");

                        photos.add(photo);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                //Call back
                aPhotos.notifyDataSetChanged();
            }

            //on faillure

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
