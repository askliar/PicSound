package com.example.joseph.picsound.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.example.joseph.picsound.Utils.Tuple;
import java.util.ArrayList;
import java.util.List;

public class Audio implements SoundPool.OnLoadCompleteListener {
    SoundPool soundPool;
    Context context;
    int numLoaded;
    boolean loaded;
    List<AudioInformation> fileInfos;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Audio(Context context) {
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());
        builder.setMaxStreams(10);
        this.soundPool = builder.build();
        this.context = context;
        numLoaded = 0;
        loaded = true;
        soundPool.setOnLoadCompleteListener(this);
    }

    private void playAfterDelay(long delayMillis, final int resId) {
        soundPool.stop(resId);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                soundPool.resume(resId);
            }
        }, delayMillis);
    }
    public void playSounds(List<AudioInformation> fileJsonInfos) {
        fileInfos = fileJsonInfos;
        for (AudioInformation info: fileInfos) {
            int resId = info.getResourceId();
            float volume = info.getVolume();
            int streamId = soundPool.load(context, resId, 1);
            info.setStreamId(streamId);
        }
        while (numLoaded <= fileInfos.size()) {
            if (!loaded){
                Log.d("ERROR", "playSounds: some of sounds coudn\'t be loaded");
                break;
            }
            if (numLoaded == fileInfos.size() && loaded) {
                for (AudioInformation info: fileInfos) {
                    soundPool.play(info.getStreamId(), info.getVolume(), info.getVolume(), 1, info.getLoopNumber(), 1.0f);
                }
            }
        }
    }

    public void stopSoundPool(){
        stopAudio();
        for (AudioInformation info: fileInfos) {
            soundPool.unload(info.getStreamId());
        }
    }

    public void stopAudio(){
        for (AudioInformation info: fileInfos) {
            soundPool.stop(info.getStreamId());
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
        numLoaded++;
        loaded = loaded && (i1 != 0);

    }
}