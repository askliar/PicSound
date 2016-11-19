package com.example.joseph.picsound.Utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 19-11-2016.
 */

public class synonymsRequest{
    private final  String KEY  = "0d788376f7d9b86637e05475a9a7b20b";
    HttpURLConnection connection = null;
    private String url;
    private ArrayList<String> syns =new ArrayList<>();

    public synonymsRequest(String word){
        url = new String("http://words.bighugelabs.com/api/2/"  +  KEY +"/"+ word +"/json");
        new DownloadTask().execute();
    }

    private class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params){
            String content = null;
            try {
                Log.d("empty",syns.toString());
                content = downloadContent(url);
                JSONObject json = new JSONObject(content);
                JSONObject jsonObject = json.getJSONObject("noun");
                JSONArray jsonArray = jsonObject.getJSONArray("syn");

                Log.d("jsonString",jsonArray.toString());

                for(int i=0;i<jsonArray.length();i++){
                   syns.add(jsonArray.getString(i));
                }
                Log.d("sysn",syns.get(1).toString());

            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return content;

        }
        private String downloadContent(String myurl) throws IOException {
            InputStream is = null;
            int length = 100000;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("TAG", "The response is: " + response);

                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = convertInputStreamToString(is, length);
                return contentAsString;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[length];
            reader.read(buffer);
            return new String(buffer);
        }
    }

    public ArrayList<String> getSyns(){
        return syns;
    }

    public interface OnComple {

    }
}
