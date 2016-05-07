package com.plorial.telegramcamera;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import com.wnafee.vector.MorphButton;

import java.io.File;

/**
 * Created by plorial on 5/6/16.
 */
public class VideoPlayerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_player_fragment, container, false);
        final VideoView videoView = (VideoView) view.findViewById(R.id.videoView);
        final String videoPath = getArguments().getCharSequence(MainActivity.VIDEO_FILE_PATH).toString();
        videoView.setVideoPath(videoPath);
        final MorphButton playPauseButton = (MorphButton) view.findViewById(R.id.play_pause_button);
        playPauseButton.setOnStateChangedListener(new MorphButton.OnStateChangedListener() {
            @Override
            public void onStateChanged(MorphButton.MorphState changedTo, boolean isAnimating) {
               if(changedTo.equals(MorphButton.MorphState.START)){
                   videoView.pause();
               }else {
                   videoView.start();
               }
            }
        });
        playPauseButton.setBackgroundColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        playPauseButton.setForegroundColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

       Button bCancel = (Button) view.findViewById(R.id.buttonCancelVideo);
       Button bDone = (Button) view.findViewById(R.id.buttonDoneVideo);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(videoPath);
                file.delete();
                startCameraFragment();
            }
        });
        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraFragment();
            }
        });
        return view;
    }

    private void startCameraFragment() {
        CameraPreviewFragment fragment = new CameraPreviewFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
