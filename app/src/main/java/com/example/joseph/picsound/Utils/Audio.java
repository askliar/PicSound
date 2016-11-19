package com.example.joseph.picsound.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import com.example.joseph.picsound.Utils.Tuple;
import java.util.ArrayList;
import java.util.List;

public class Audio {
    SoundPool soundPool;
    Context context;
    // first argument - id of audio thread, second - it's volume;
    List<Tuple<Integer, Float>> soundInfos;

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
        soundInfos = new ArrayList<>();
    }

    /***
     * @param fileInfos - first argument is resource id, second - volume that it will be played at
     */
    //TODO: Stage the audios
    public void playSounds(List<Tuple<Integer, Float>> fileInfos) {
        for (Tuple<Integer, Float> tuple:
             fileInfos) {
            int resId = tuple.getFirst();
            float volume = tuple.getSecond();
            soundInfos.add(new Tuple<Integer, Float>(soundPool.load(context, resId, 1), volume));
        }
        for (Tuple<Integer, Float> soundInfo:
             soundInfos) {
            soundPool.play(soundInfo.getFirst(), soundInfo.getSecond(), soundInfo.getSecond(), 1, -1, 1.0f);
        }
    }

    public void stopSound(){
        for (Tuple<Integer, Float> soundInfo:
             soundInfos) {
            soundPool.stop(soundInfo.getFirst());
        }
    }
}