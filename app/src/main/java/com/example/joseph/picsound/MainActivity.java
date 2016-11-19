package com.example.joseph.picsound;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.Picture;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int GALARY_RETURN = 2;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button CameraOpen = (Button) findViewById(R.id.CameraOpen);
        Button GalaryOpen = (Button) findViewById(R.id.GalaryOpening);
        GalaryOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, GALARY_RETURN);

            }
        });

        CameraOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("error",String.valueOf(requestCode));
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent = new Intent(MainActivity.this,Analyze.class);
            intent.putExtra("ByteArray",byteArray);
            intent.putExtra("Type",1);
            startActivity(intent);
        }
        if (requestCode == GALARY_RETURN && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            Intent intent =new Intent(MainActivity.this,Analyze.class);
            intent.putExtra("URI",  selectedImage);
            intent.putExtra("Type",2);
            startActivity(intent);


        }
    }
}

