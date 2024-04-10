package com.zigg.soundboard;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SoundPool soundPool;
    ArrayList<Integer> soundList = new ArrayList<>();

    Field[] rawFields=R.raw.class.getFields(); //creates array of raw folder contents

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // preparing soundpool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(1) // total number of sounds allowed to play at once
                .setAudioAttributes(audioAttributes)
                .build();

        this.soundLoader(soundPool);
        this.buttonGenerator();
    }

    protected void soundLoader(SoundPool poolarg){
        for (Field rawField : rawFields) {
            int soundName = getResources().getIdentifier(rawField.getName(), "raw", getPackageName());
            int playID = poolarg.load(this, soundName, 1);
            soundList.add(playID);
        }
    }


    protected void buttonGenerator(){
        int count = 0;

        for (Field rawField: rawFields) {
            String soundName = rawField.getName().toUpperCase();
            String newString = soundName.replace("_", " ");

            DisplayMetrics displaySize = Resources.getSystem().getDisplayMetrics();
            int maxWidth = 1200;
            int maxHeight = 2200;

            int buttonWidth = displaySize.widthPixels >= maxWidth ? displaySize.widthPixels/3 : displaySize.widthPixels/2;
            int buttonHeight = displaySize.heightPixels >= maxHeight ? displaySize.heightPixels/9 : displaySize.heightPixels/5;
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(buttonWidth, buttonHeight);

            TextView mButton = new TextView(this);
            mButton.setLayoutParams(layoutParams);
            mButton.setBackgroundResource(R.drawable.button_selector);
            mButton.setGravity(Gravity.CENTER); // position for text inside button
            mButton.setText(newString);
            mButton.setTextColor(ContextCompat.getColorStateList(this, R.color.black));
            mButton.performClick();

            int finalCount = count;
            mButton.setOnClickListener(view -> {
                soundPool.play(soundList.get(finalCount), 1, 1, 0, 0, 1);
            });

            GridLayout gridLayout = findViewById(R.id.buttoncontainer);
            gridLayout.addView(mButton);

            count += 1;
        }
    }


    // memory safety function - app won't crash when spamming buttons
    @Override
    protected void onDestroy(){
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}