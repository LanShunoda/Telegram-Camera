package com.plorial.telegramcamera;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by plorial on 5/5/16.
 */
public class CropFragment extends Fragment {

    public static final String TAG = CropFragment.class.getSimpleName();

    private View view;
    private CropImageView cropImageView;
    private String filePath;
    private FrameLayout degrees;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.crop_fragment, container, false);

        cropImageView = (CropImageView) view.findViewById(R.id.cropImageView);
        degrees = (FrameLayout) view.findViewById(R.id.degreesView);
        filePath = getArguments().getCharSequence(MainActivity.PHOTO_FILE_PATH).toString();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        cropImageView.setImageBitmap(bitmap);
        DegreesView degreesView = new DegreesView(getActivity());
        degrees.addView(degreesView);
        final TextView tvDegree = (TextView) view.findViewById(R.id.tvDegree);
        degreesView.setDegreeChangedCallback(new DegreesView.DegreeChangedCallback() {
            float previousDegree = 0;
            @Override
            public void onDegreeChanged(float degree) {
                cropImageView.rotateImage((int)(degree-previousDegree));
                previousDegree = degree;
                DecimalFormat df=new DecimalFormat("0.00");
                String formate = df.format(((-1) *degree));
                tvDegree.setText((formate) + "°");
            }
        });
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
                cropImageView.rotateImage(270);
            }
        });
        bDone.setOnClickListener(new View.OnClickListener() {
            Bitmap cropped;
            @Override
            public void onClick(View v) {
                try {
                    cropped = cropImageView.getCroppedImage();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            bitmapToFile(cropped);
                        }
                    }).start();
                    startCameraFragment();
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"Crop borders must be inside photo", Toast.LENGTH_LONG).show();
                }
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
