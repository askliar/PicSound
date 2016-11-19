package com.example.joseph.picsound;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.joseph.picsound.Utils.Audio;
import com.example.joseph.picsound.Utils.AudioInformation;
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

public class Analyze extends AppCompatActivity implements View.OnClickListener {
    @Nullable
    private ClarifaiClient client;
    static final int CAMERA=1;
    static final int GALARY=2;
    static final int GALARYMULTIPLE=3;
    private Audio audio;
    private AudioMatcher matcher;
    private ProgressDialog loadingIndicator;

    private int galery_state = 0;
    private ArrayList<byte[]> byteArray;

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        audio = new Audio(getApplicationContext());
        matcher = new AudioMatcher(getApplicationContext());

        image = (ImageView) findViewById(R.id.Image);
        image.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        byteArray = new ArrayList<byte[]>();

        Log.d("bundle",String.valueOf(bundle.get("Type")));
        if( ((int) bundle.get("Type"))==CAMERA){
            byteArray.add(bundle.getByteArray("ByteArray"));
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray.get(0) , 0, byteArray.size());
            image.setImageBitmap(bitmap);
        }
        if( ((int) bundle.get("Type"))== GALARY){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), (Uri) bundle.get("URI"));
                image.setImageBitmap(bitmap);

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

                Bitmap bitmap2 = BitmapFactory.decodeByteArray(byteArray.get(galery_state % byteArray.size()) , 0, byteArray.get(galery_state % byteArray.size()).length);
                image.setImageBitmap(bitmap2);
            }
        }
        loadingIndicator = ProgressDialog.show(this, "Loading", "Please wait...");

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
                                          List<String> tags = new ArrayList<String>();
                                          for (Concept concept :
                                                  clarifaiOutput.data()) {
                                              tags.add(concept.name());
                                              Log.v("TAG", concept.name() + "(p=" + concept.value() + ")");
                                          }
                                          List<Integer> soundIds = matcher.soundIdsFromStrings(tags);
                                          List<AudioInformation> audioInformations = new ArrayList<AudioInformation>();
                                          for (Integer soundId:
                                               soundIds) {
                                              audioInformations.add(new AudioInformation(soundId, -1, 1.0f, 0, -1));
                                          }
                                          audio.playSounds(audioInformations);
                                      }
                                      loadingIndicator.dismiss();
                                  }

                                  @Override
                                  public void onClarifaiResponseUnsuccessful(int errorCode) {
                                      Log.d("request unsuccessful", "Error code is: " + errorCode);
                                      loadingIndicator.dismiss();
                                  }

                                  @Override
                                  public void onClarifaiResponseNetworkError(IOException e) {
                                      e.printStackTrace();
                                      loadingIndicator.dismiss();
                                  }
                              }

                );
    }

    @Override
    public void onClick(View v) {
        galery_state = (galery_state + 1);
        audio.stopAudio();


        if(galery_state >= byteArray.size()) {
            finish();
            return;
        }

        Bitmap bitmap2 = BitmapFactory.decodeByteArray(byteArray.get(galery_state % byteArray.size()) , 0, byteArray.get(galery_state % byteArray.size()).length);
        image.setImageBitmap(bitmap2);
        analyzeInBackground(byteArray.get(galery_state % byteArray.size()));
    }
}
