package com.example.joseph.picsound;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.example.joseph.picsound.Utils.Audio;
import com.example.joseph.picsound.Utils.AudioMatcher;
import com.example.joseph.picsound.Utils.Tuple;
import com.microsoft.projectoxford.vision.contract.Category;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class Analyze extends AppCompatActivity {
    @Nullable
    private ClarifaiClient client;
    static final int CAMERA=1;
    static final int GALARY=2;
    static final int GALARYMULTIPLE=3;
    private Audio audio;
    private AudioMatcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        audio = new Audio(getApplicationContext());
        matcher = new AudioMatcher(getApplicationContext());

        ImageView image = (ImageView) findViewById(R.id.Image);
        Bundle bundle = getIntent().getExtras();
        ArrayList<byte[]> byteArray = new ArrayList<byte[]>();

        Log.d("bundle",String.valueOf(bundle.get("Type")));
        if( ((int) bundle.get("Type"))==CAMERA){
            byteArray.add(bundle.getByteArray("ByteArray"));
        }
        if( ((int) bundle.get("Type"))== GALARY){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), (Uri) bundle.get("URI"));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();
                byteArray.add(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if( ((int) bundle.get("Type"))== GALARYMULTIPLE){
            ArrayList<Uri> uriList= (ArrayList<Uri>) bundle.get("URIList");

            String[] filePathColumn = (String[]) bundle.get("FilePathColumn");
            for(int i=0;i<uriList.size();i++) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriList.get(i));
                    Log.d("null",String.valueOf(bitmap.toString()));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byteArray.add(stream.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        Future<ClarifaiClient> clientFuture = new ClarifaiBuilder(getString(R.string.clarify_app_id), getString(R.string.clarify_app_key)).build();
        try {
            client = clientFuture.get();
            analyzeInBackground(byteArray.get(0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause(){
        audio.stopAudio();
        super.onPause();
    }

    private void analyzeInBackground(byte[] bmp/*Bitmap bmp*/) {
        client.getDefaultModels().generalModel().predict()
                .withInputs(
                        ClarifaiInput.forImage(ClarifaiImage.of(bmp))
                )
                .executeAsync(new ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>>() {
                                  @Override
                                  public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {
                                      for (ClarifaiOutput<Concept> clarifaiOutput :
                                              clarifaiOutputs) {
                                          for (Concept concept :
                                                  clarifaiOutput.data()) {
                                              List<Tuple<Integer, Float>> sounds = new ArrayList<>();
                                              int soundId = matcher.soundIdFromTag(concept.name());
                                              if (soundId != 0) {
                                                  Log.v("TAG_ID", soundId + "");
                                                  sounds.add(new Tuple<>(soundId, 1.0f));
                                              }

                                              Log.v("TAG", concept.name() + "(p=" + concept.value() + ")");

                                              audio.playSounds(sounds);
                                          }
                                      }
                                  }

                                  @Override
                                  public void onClarifaiResponseUnsuccessful(int errorCode) {
                                      Log.d("request unsuccessful", "Error code is: " + errorCode);
                                  }

                                  @Override
                                  public void onClarifaiResponseNetworkError(IOException e) {
                                      e.printStackTrace();
                                  }
                              }

                );

//        AnalyzeImageTask task = new AnalyzeImageTask(new AnalyzeImageTask.AnalysisCompleteCallback() {
//            @Override
//            public void onAnalysisComplete(AnalysisResult result) {
//                if (result != null) {
//                    List<Tuple<Integer, Float>> sounds = new ArrayList<>();
//                    for (Tag tag : result.tags) {
//                        int soundId = matcher.soundIdFromTag(tag);
//                        if(soundId != 0) {
//                            Log.v("TAG_ID", soundId + "");
//                            sounds.add(new Tuple<>(soundId, 1.0f));
//                        }
//
//                        Log.v("TAG", tag.name + "(p=" + tag.confidence + ")");
//                    }
//
//                    audio.playSounds(sounds);
//                    for (Category cat : result.categories) {
//                        Log.v("CATEGORY", cat.name);
//                    }
//                }
//            }
//        }, bmp);
//
//        task.execute();
    }
}
