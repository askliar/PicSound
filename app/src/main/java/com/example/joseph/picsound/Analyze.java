package com.example.joseph.picsound;

import android.content.Intent;
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
import com.example.joseph.picsound.Utils.SoundMatcher;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Category;
import com.microsoft.projectoxford.vision.contract.Tag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;

public class Analyze extends AppCompatActivity {
    static final int CAMERA=1;
    static final int GALARY=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
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

    private void analyzeInBackground(Bitmap bmp) {

        AnalyzeImageTask task = new AnalyzeImageTask(new AnalyzeImageTask.AnalysisCompleteCallback() {
            @Override
            public void onAnalysisComplete(AnalysisResult result) {
                SoundMatcher matcher = new SoundMatcher(getApplicationContext());

                if (result != null) {
                    for (Tag tag : result.tags) {
                        int soundId = matcher.soundIdFromTag(tag);
                        if(soundId != 0) {
                            Log.v("TAG_ID", soundId + "");
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), soundId);
                            mediaPlayer.start();
                        }

                        Log.v("TAG", tag.name + "(p=" + tag.confidence + ")");
                    }

                    for (Category cat : result.categories) {
                        Log.v("CATEGORY", cat.name);
                    }
                }
            }
        }, bmp);

        task.execute();
    }
}
