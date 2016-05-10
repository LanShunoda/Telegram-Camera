package com.plorial.telegramcamera;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.VideoView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.wnafee.vector.MorphButton;

import java.io.File;

/**
 * Created by plorial on 5/6/16.
 */
public class VideoPlayerFragment extends Fragment {

    private final static String TAG = VideoPlayerFragment.class.getSimpleName();

    private View view;
    private String videoPath;
    private VideoView videoView;
    private RoundCornerProgressBar progressBar;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.video_player_fragment, container, false);
        videoView = (VideoView) view.findViewById(R.id.videoView);
        videoPath = getArguments().getCharSequence(MainActivity.VIDEO_FILE_PATH).toString();
        videoView.setVideoPath(videoPath);
        handler = new Handler();
        final MorphButton playPauseButton = (MorphButton) view.findViewById(R.id.play_pause_button);
        playPauseButton.setBackgroundColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        playPauseButton.setForegroundColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        progressBar = (RoundCornerProgressBar) view.findViewById(R.id.progressBar);
        Log.d(TAG,"video duration " + videoView.getDuration() + " bar max " + progressBar.getMax());
        playPauseButton.setOnStateChangedListener(new MorphButton.OnStateChangedListener() {
            @Override
            public void onStateChanged(MorphButton.MorphState changedTo, boolean isAnimating) {
                playPause(changedTo);
            }
        });
        setClickListeners();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPauseButton.setState(MorphButton.MorphState.START);
            }
        });
        return view;
    }

    private void playPause(MorphButton.MorphState changedTo) {
        if(changedTo.equals(MorphButton.MorphState.START)){
            videoView.pause();
        }else {
            videoView.start();
            progressBar.setMax(videoView.getDuration());
            startPlayProgressUpdater();
        }
    }

    private void startPlayProgressUpdater() {
        progressBar.setProgress(videoView.getCurrentPosition());
        if (videoView.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,100);
        }else{
            videoView.pause();
        }
    }

    private void setClickListeners() {
        ImageButton bCancel = (ImageButton) view.findViewById(R.id.buttonCancelVideo);
        ImageButton bDone = (ImageButton) view.findViewById(R.id.buttonDoneVideo);
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
    }

    private void startCameraFragment() {
        CameraPreviewFragment fragment = new CameraPreviewFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
