package com.plorial.telegramcamera;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Created by plorial on 5/5/16.
 */
public class CropFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.crop_fragment, container, false);
        CropImageView cropImageView = (CropImageView) view.findViewById(R.id.cropImageView);
        String filePath = getArguments().getCharSequence(MainActivity.PHOTO_FILE_PATH).toString();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        cropImageView.setImageBitmap(bitmap);
        return view;
    }
}
