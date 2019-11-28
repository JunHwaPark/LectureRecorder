package com.junhwa.lecturerecorder.player;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

import static com.junhwa.lecturerecorder.ui.fragment.ListFragment.*;

public class PlayerService extends Service {
    public final static int INITIALIZE_PLAYER = 0;
    public final static int PLAY = 1;
    public final static int PAUSE = 2;
    public final static int RESUME = 3;
    public final static int STOP = 4;
    public final static int SEEK_TO = 5;

    private MediaPlayer player = null;
    private File audio = null;
    private Timer timer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent == null)
            return Service.START_STICKY;

        int command = intent.getIntExtra("COMMAND", 0);
        if (command == INITIALIZE_PLAYER) {
            if (player != null) {
                player.stop();
                player.reset();
                player.release();
                player = null;
            }
            audio = new File(intent.getStringExtra("PATH"));
            initialize();
        } else if (command == PLAY) {
            startPlay();
        } else if (command == PAUSE) {
            player.pause();
        } else if (command == RESUME) {
            player.start();
            setTimer();
        } else if (command == STOP) {
            if (player != null) {
                player.stop();
                player.reset();
                player.release();
                player = null;
            }
        } else if (command == SEEK_TO) {
            player.seekTo(intent.getIntExtra("POSITION", 0));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (player.isPlaying())
            player.stop();
        player = null;

        super.onDestroy();
    }

    private void initialize() {
        Log.d("Initialize", "Initialize");

        try {
            FileInputStream fs = new FileInputStream(audio);
            FileDescriptor fd = fs.getFD();
            player = new MediaPlayer();
            player.setAudioAttributes(
                    new AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build());
            player.setDataSource(fd);
            player.prepare();

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Intent intent = new Intent("player");
                    intent.putExtra("COMMAND", STATUS);
                    intent.putExtra("STATUS", STOP);
                    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                    broadcastManager.sendBroadcast(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPlay() {
        try {
            if (!player.isPlaying()) {
                player.start();
                setTimer();
            } else {
                player.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent("player");
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

                if (player != null && player.isPlaying()) {
                    int position = player.getCurrentPosition();
                    intent.putExtra("COMMAND", POSITION);
                    intent.putExtra("POSITION", position);
                    broadcastManager.sendBroadcast(intent);
                } else {
                    timer.cancel();
                }
            }
        }, 500, 500);
    }
}
