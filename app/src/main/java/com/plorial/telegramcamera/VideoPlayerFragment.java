package com.plorial.telegramcamera;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.wnafee.vector.MorphButton;

/**
 * Created by plorial on 5/6/16.
 */
public class VideoPlayerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_player_fragment, container, false);
        final VideoView videoView = (VideoView) view.findViewById(R.id.videoView);
        videoView.setVideoPath(getArguments().getCharSequence(MainActivity.VIDEO_FILE_PATH).toString());
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
        return view;
    }
}
