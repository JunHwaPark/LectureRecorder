package com.junhwa.lecturerecorder.recorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.ProgressBar;

import java.io.File;

public class RecordService extends Service {
    public final static int INITIALIZE_RECORDER = 0;
    public final static int STOP_RECORD = 1;
    public final static int PAUSE = 2;

    private MediaRecorder recorder;
    private Context context;
    Boolean isRecording = false;
    ProgressBar progressBar;
    int channel;
    int bitRate;

    public RecordService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return START_STICKY;

        int command = intent.getIntExtra("COMMAND", INITIALIZE_RECORDER);
        if (command == INITIALIZE_RECORDER) {
            int channel = intent.getIntExtra("CHANNEL", 2);
            int bitRate = intent.getIntExtra("BITRATE", 192000);
            String fileName = intent.getStringExtra("FILENAME");

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            recorder.setAudioChannels(channel);
            recorder.setAudioSamplingRate(44100);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(bitRate);
            recorder.setOutputFile(getApplication().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + File.separator + fileName + ".aac");

            try {
                recorder.prepare();
                recorder.start();
                isRecording = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (command == PAUSE && recorder != null) {
            if (Build.VERSION.SDK_INT > 23)
                recorder.pause();
        } else if (command == STOP_RECORD && recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            isRecording = false;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
