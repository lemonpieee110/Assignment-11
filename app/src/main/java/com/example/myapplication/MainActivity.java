package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private Button startbtn, stopbtn, playbtn, stopplay;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int RESULT_LOAD_IMAGE = 999;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private static String URL =
            "https://www.baidu.com/img/flexible/logo/pc/result.png";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView2);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        PicFromInternet b = new PicFromInternet();

        b.execute();
        startbtn = (Button)findViewById(R.id.btnRecord);
        stopbtn = (Button)findViewById(R.id.btnStop);
        playbtn = (Button)findViewById(R.id.btnPlay);
        stopplay = (Button)findViewById(R.id.btnStopPlay);
        stopbtn.setEnabled(false);
        playbtn.setEnabled(false);
        stopplay.setEnabled(false);
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/AudioRecording.3gp";
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        Button albumButton = (Button) this.findViewById(R.id.button2);
        Button loadImage = (Button) this.findViewById(R.id.loadImage);

        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }

        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAsyncTask().execute(URL);
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestAudioPermission()) {
                    stopbtn.setEnabled(true);
                    startbtn.setEnabled(false);
                    playbtn.setEnabled(false);
                    stopplay.setEnabled(false);
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mRecorder.setOutputFile(mFileName);
                    try {
                        mRecorder.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mRecorder.start();
                }
            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(true);
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        });

        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(false);
                stopplay.setEnabled(true);
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(mFileName);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        stopplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.release();
                mPlayer = null;
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(false);
            }
        });


    }
    class MyAsyncTask extends AsyncTask<String,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bitmap = null;
            URLConnection connection;
            InputStream is;
            try {
                connection = new URL(url).openConnection();
                is = connection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                Thread.sleep(3000);
                bitmap = BitmapFactory.decodeStream(bis);
                is.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mImageView.setImageBitmap(bitmap);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else {
                //Error happen
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean requestAudioPermission(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
            return false;
        }
        return true;
    }}