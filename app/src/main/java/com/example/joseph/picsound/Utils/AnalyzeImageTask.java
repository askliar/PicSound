package com.example.joseph.picsound.Utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Rafael Bankosegger on 18/11/2016.
 */

public class AnalyzeImageTask extends AsyncTask<String, String, String>  {

    // Rafael's secret Microsoft API key. Don't share!
    private final String SECRET_KEY = "a41c7f0b83ef4a3c85c2feeff50581a1";

    private Bitmap mImage;
    private Exception e = null;
    private AnalysisCompleteCallback mCallback;
    private VisionServiceClient mClient;


    public AnalyzeImageTask(AnalysisCompleteCallback callback, Bitmap image) {
        mClient = new VisionServiceRestClient(SECRET_KEY);
        mCallback = callback;
        mImage = image;
    }

    @Override
    protected String doInBackground(String... args) {
        try {
            return process();
        } catch (Exception e) {
            this.e = e;    // Store error
        }

        return null;
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();
        String[] features = {"Categories", "Tags", "Description"};
        String[] details = {};

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mImage.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.mClient.analyzeImage(inputStream, features, details);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        // Display based on error existence


        if (e != null) {
            mCallback.onAnalysisComplete(null);
            Log.e("ANALYZE","Connection not possible: \n" + e.getMessage());
            this.e = null;
        } else {
            Gson gson = new Gson();
            AnalysisResult result = gson.fromJson(data, AnalysisResult.class);
            mCallback.onAnalysisComplete(result);
        }
    }

    public interface AnalysisCompleteCallback {
        void onAnalysisComplete(AnalysisResult result);
    }
}
