package com.example.joseph.picsound.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.joseph.picsound.R;
import com.microsoft.projectoxford.vision.contract.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by Rafael Bankosegger on 19/11/2016.
 */

public class AudioMatcher {

    private Resources mResources;

    public AudioMatcher(Context c) {
        mResources = c.getResources();
    }

    class fTuple {
        public final String name;
        //        public final int id;
        public final float priority;
        public final String[] nameParts;

        public fTuple(String name, float priority, String[] nameParts) {
            this.name = name;
//            this.id = id;
            this.priority = priority;
            this.nameParts = nameParts;
        }
    }

    public List<Integer> soundIdsFromTags(List<Tag> tags) {
        List<String> tagStrings = new ArrayList<>();
        for(Tag tag : tags) {
            tagStrings.add(tag.name);
        }
        return soundIdsFromStrings(tagStrings);
    }

    public List<Integer> soundIdsFromStrings(List<String> tagStrings) {

        //TODO: Include similarwords

        String[] files = mResources.getStringArray(R.array.sound_names);

        List<fTuple> sums = new ArrayList<>();

        for (String file : files) {
            String[] file_parts = file.split("_");
            float sum = 0;
            for (String part : file_parts) {
                if(tagStrings.contains(part)) {
                    sum += 1;
                    Log.v("PART", part);
                }
            }
            sums.add(new fTuple(file, sum, file_parts));
        }

        Collections.sort(sums, new Comparator<fTuple>() {
            @Override
            public int compare(fTuple lhs, fTuple rhs) {
                float p1 = lhs.priority;
                float p2 = rhs.priority;
                if (p1 < p2) {
                    return 1;
                } else if (p1 == p2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        for (fTuple t : sums) {
            Log.v("PQUEUE", t.name + "(p=" + t.priority + ")");
        }

        ArrayList<fTuple> filesToPlay = new ArrayList<>(sums);

        ListIterator<fTuple> it = sums.listIterator();
        while (it.hasNext()) {
            fTuple current = it.next();
            ListIterator<fTuple> it2 = sums.listIterator(it.nextIndex());
            while(it2.hasNext()) {
                fTuple next = it2.next();

                List<String> list = new ArrayList<String>(Arrays.asList(current.nameParts));
                List<String> list2 = new ArrayList<String>(Arrays.asList(next.nameParts));

                list.retainAll(list2);
                if(list.size() > 0){
                    filesToPlay.remove(next);
                }
            }

            if(current.priority <= 0) {
                filesToPlay.remove(current);
            }
        }

        List<Integer> soundIds = new ArrayList<>();

        for (fTuple t : filesToPlay) {
            Log.v("TUPLE", t.name + ", " + t.priority);

            int soundId = getResId(t.name);

            if(soundId != 0) {
                soundIds.add(soundId);
            }
        }

        return soundIds;
    }

    private List<String> getAlternativeTags(String tag){
        return new ArrayList<>();
    }

    public int getResId(String resName) {
        return mResources.getIdentifier(resName, "raw", "com.example.joseph.picsound");
    }
}
