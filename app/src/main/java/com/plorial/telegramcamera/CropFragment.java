package com.plorial.telegramcamera;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by plorial on 5/5/16.
 */
public class CropFragment extends Fragment {

    private View view;
    private CropImageView cropImageView;
    private String filePath;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.crop_fragment, container, false);

        cropImageView = (CropImageView) view.findViewById(R.id.cropImageView);
        filePath = getArguments().getCharSequence(MainActivity.PHOTO_FILE_PATH).toString();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        cropImageView.setImageBitmap(bitmap);

        buttonsSetClickListeners();

        return view;
    }

    private void buttonsSetClickListeners(){
        Button bCancel = (Button) view.findViewById(R.id.bCancel);
        Button bReset = (Button) view.findViewById(R.id.bReset);
        Button bDone = (Button) view.findViewById(R.id.bDone);
        ImageButton bRotate = (ImageButton) view.findViewById(R.id.bRotate);

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraFragment();
            }
        });

        bReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.resetCropRect();
            }
        });

        bRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(90);
            }
        });
        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bitmap cropped = cropImageView.getCroppedImage();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bitmapToFile(cropped);
                    }
                }).start();
                startCameraFragment();
            }
        });
    }

    private void bitmapToFile(Bitmap cropped) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            cropped.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraFragment() {
        CameraPreviewFragment fragment = new CameraPreviewFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
