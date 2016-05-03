package com.plorial.telegramcamera;

import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by plorial on 5/3/16.
 */
public class PictureCallback implements Camera.PictureCallback, Runnable {

    private static final String TAG = PictureCallback.class.getSimpleName();
    private byte[] data;
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
      this.data = data;
        new Thread(this).start();
    }

    @Override
    public void run() {
        File pictureFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Date date = new Date();
        File photo = new File(pictureFile,"img_" + date +".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(photo);
            fos.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            assert fos != null;
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
