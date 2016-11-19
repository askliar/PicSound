package com.example.joseph.picsound.Utils;

import android.content.Context;
import android.content.res.Resources;

import com.microsoft.projectoxford.vision.contract.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rafael Bankosegger on 19/11/2016.
 */

public class AudioMatcher {

    private Resources mResources;

    public AudioMatcher(Context c) {
        mResources = c.getResources();
    }

    public List<Integer> soundIdsFromTags(List<String> tags) {
        List<Integer> soundIds = new ArrayList<>();

        for (String tag : tags) {
            int newSoundId = soundIdFromTag(tag);
            if(newSoundId != 0)
                soundIds.add(newSoundId);
        }

        return soundIds;
    }

    public int soundIdFromTag(String tag) {

        int soundId = getResId(tag);

        if(soundId == 0) {
            for (String tag2 : getAlternativeTags(tag)) {
                soundId = getResId(tag2);
                if(soundId != 0) {
                    break;
                }
            }
        }
        return soundId;
    }

    private List<String> getAlternativeTags(String tag){
        return new ArrayList<>();
    }

    public int getResId(String resName) {
        return mResources.getIdentifier(resName, "raw", "com.example.joseph.picsound");
    }
}
