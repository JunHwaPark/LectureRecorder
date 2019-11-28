package com.junhwa.lecturerecorder.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.junhwa.lecturerecorder.R;
import com.junhwa.lecturerecorder.recorder.RecordService;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.junhwa.lecturerecorder.recorder.RecordService.*;

public class RecordFragment extends Fragment{
    private ProgressBar progressBar;
    private TextView txtTimer;

    private Intent intent = null;

    private int channel = 2;
    private int bitRate = 192000;

    private int recordingState = 0;
    //0 = initialized, 1 = recording, 2 = paused

    final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd_hh:mm:ss");

    private Thread timerThread = null;
    Timer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_record, container, false);

        final Button recordButton = rootView.findViewById(R.id.btn_record);
        final Button modeButton = rootView.findViewById(R.id.btn_mode);
        final Button qualityButton = rootView.findViewById(R.id.btn_quality);
        final Button pauseButton = rootView.findViewById(R.id.btn_pause);
        progressBar = rootView.findViewById(R.id.progressBar);
        txtTimer = rootView.findViewById(R.id.textView);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordingState == 0) {
                    Date now = new Date();
                    intent = new Intent(getActivity(), RecordService.class);
                    intent.putExtra("COMMAND", INITIALIZE_RECORDER);
                    intent.putExtra("CHANNEL", channel);
                    intent.putExtra("BITRATE", bitRate);
                    intent.putExtra("FILENAME", getString(R.string.Prefix_filename) + dateFormat.format(now));
                    getActivity().startService(intent);

                    recordButton.setText("Stop");

                    timerThread = new Thread(new Runnable() {
                        DecimalFormat formatter = new DecimalFormat("00");
                        int hour = 0, min = 0, sec = 0;

                        @Override
                        public void run() {
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if ((++sec) >= 60) {
                                        sec = 0;
                                        if (++min >= 60) {
                                            min = 0;
                                            ++hour;
                                        }
                                    }
                                    txtTimer.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            txtTimer.setText(formatter.format(hour) + ":" + formatter.format(min) + ":" + formatter.format(sec));
                                        }
                                    });
                                }
                            }, 1000, 1000);
                        }
                    });
                    timerThread.start();
                    if (Build.VERSION.SDK_INT > 23)
                        pauseButton.setVisibility(View.VISIBLE);
                    recordingState = 1;
                } else {
                    intent = new Intent(getActivity(), RecordService.class);
                    intent.putExtra("COMMAND", STOP_RECORD);
                    getActivity().startService(intent);

                    recordButton.setText("Start Record");
                    pauseButton.setVisibility(View.GONE);

                    timer.cancel();
                    timerThread = null;
                    recordingState = 0;
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), RecordService.class);
                intent.putExtra("COMMAND", PAUSE);
                getActivity().startService(intent);
                recordingState = 2;
            }
        });

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder modeDialog = new AlertDialog.Builder(getActivity());
                modeDialog.setTitle(getString(R.string.mode_dialog))
                        .setItems(R.array.mode, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0)
                                    channel = 2;
                                else
                                    channel = 1;
                            }
                        }).show();
            }
        });

        qualityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder qualityDialog = new AlertDialog.Builder(getActivity());
                qualityDialog.setTitle(getString(R.string.quality_dialog))
                        .setItems(R.array.quality, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        bitRate = 256000;
                                        break;
                                    case 1:
                                        bitRate = 192000;
                                        break;
                                    case 2:
                                        bitRate = 128000;
                                        break;
                                }
                            }
                        }).show();
            }
        });
        return rootView;
    }
}
