package rtspsample.com.example.rtspsample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity
        implements
        OnClickListener,
        TextureView.SurfaceTextureListener {

    EditText rtspUrl;
    Button playButton;
    Button btnCapture;
    private MediaPlayer mMediaPlayer;
    private TextureView mPreview;
    private Surface surface;
    String TAG = "RTSP";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rtspUrl = (EditText) this.findViewById(R.id.editText);
        playButton = (Button) this.findViewById(R.id.playButton);
        playButton.setOnClickListener(this);

        btnCapture = (Button) this.findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(this);

        mPreview = (TextureView) findViewById(R.id.rtspVideo);
        mPreview.setSurfaceTextureListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playButton:
                RtspStream(rtspUrl.getEditableText().toString());
                break;
            case R.id.btnCapture:
                storeImage(getBitmap());
                break;
        }
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString());

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }


    private void RtspStream(String rtspUrl) {
        if (mPreview.isAvailable()){

            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(this, Uri.parse(rtspUrl));
                mMediaPlayer.setSurface(surface);
                mMediaPlayer.setLooping(true);

                // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
                // creating MediaPlayer
                mMediaPlayer.prepareAsync();
                // Play video when the media source is ready for playback.
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });

            } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    public Bitmap getBitmap() {
        return mPreview.getBitmap();
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        surface = new Surface(surfaceTexture);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            // Make sure we stop video and release resources when activity is destroyed.
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

}
