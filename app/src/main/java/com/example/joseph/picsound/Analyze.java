package com.example.joseph.picsound;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.joseph.picsound.Utils.AnalyzeImageTask;
import com.example.joseph.picsound.Utils.Audio;
import com.example.joseph.picsound.Utils.AudioMatcher;
import com.example.joseph.picsound.Utils.Tuple;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Category;
import com.microsoft.projectoxford.vision.contract.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Analyze extends AppCompatActivity {
    static final int CAMERA=1;
    static final int GALARY=2;

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
        if((int) bundle.get("Type")==CAMERA){
            byte[] byteArray = bundle.getByteArray("ByteArray");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            image.setImageBitmap(bmp);
            analyzeInBackground(bmp);
        }
        if((int) bundle.get("Type")==GALARY){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), (Uri) bundle.get("URI"));
                image.setImageBitmap(bitmap);
                analyzeInBackground(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        audio.stopSound();
        super.onPause();
    }

    private void analyzeInBackground(Bitmap bmp) {

        AnalyzeImageTask task = new AnalyzeImageTask(new AnalyzeImageTask.AnalysisCompleteCallback() {
            @Override
            public void onAnalysisComplete(AnalysisResult result) {
                if (result != null) {
                    List<Tuple<Integer, Float>> sounds = new ArrayList<>();
                    for (Tag tag : result.tags) {
                        int soundId = matcher.soundIdFromTag(tag);
                        if(soundId != 0) {
                            Log.v("TAG_ID", soundId + "");
                            sounds.add(new Tuple<>(soundId, 1.0f));
                        }

                        Log.v("TAG", tag.name + "(p=" + tag.confidence + ")");
                    }

                    audio.playSounds(sounds);

                    for (Category cat : result.categories) {
                        Log.v("CATEGORY", cat.name);
                    }
                }
            }
        }, bmp);

        task.execute();
    }
}
