package com.junhwa.lecturerecorder.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.junhwa.lecturerecorder.R;
import com.junhwa.lecturerecorder.recorder.NotificationGenerator;
import com.junhwa.lecturerecorder.recorder.RecordService;
import com.junhwa.lecturerecorder.recyclerview.lecture_list.Lecture;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.junhwa.lecturerecorder.recorder.RecordService.*;

public class RecordFragment extends Fragment {
    private ProgressBar progressBar;
    private TextView txtTimer, txtSelectedLecture, txtMode, txtQuality;

    private Intent intent = null;

    private int channel = 2;
    private int bitRate = 192000;
    private String directory = "";

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
        final Button lectureButton = rootView.findViewById(R.id.btn_select_lecture);
        progressBar = rootView.findViewById(R.id.progressBar);
        txtTimer = rootView.findViewById(R.id.textView);
        txtSelectedLecture = rootView.findViewById(R.id.txt_selected_lecture);
        txtMode = rootView.findViewById(R.id.txt_mode);
        txtQuality = rootView.findViewById(R.id.txt_quality);
        txtSelectedLecture.setText("Select Lecture");

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordingState == 0) {
                    Date now = new Date();
                    intent = new Intent(getActivity(), RecordService.class);
                    intent.putExtra("COMMAND", INITIALIZE_RECORDER);
                    intent.putExtra("CHANNEL", channel);
                    intent.putExtra("BITRATE", bitRate);
                    if (directory.length() > 0)
                        directory += "/";
                    intent.putExtra("FILENAME", directory
                            + getString(R.string.Prefix_filename) + dateFormat.format(now));
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
                    txtTimer.setText("00:00:00");
                    recordingState = 0;
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recordingState == 1) {
                    intent = new Intent(getActivity(), RecordService.class);
                    intent.putExtra("COMMAND", PAUSE);
                    getActivity().startService(intent);
                    recordingState = 2;
                    timer.cancel();
                    timerThread = null;
                    pauseButton.setText(getString(R.string.resume));;
                } else {
                    intent = new Intent(getActivity(), RecordService.class);
                    intent.putExtra("COMMAND", RESUME);
                    getActivity().startService(intent);
                    recordingState = 1;


                    timerThread = new Thread(new Runnable() {
                        DecimalFormat formatter = new DecimalFormat("00");
                        int hour = Integer.parseInt(txtTimer.getText().toString().substring(0, 2));
                        int min = Integer.parseInt(txtTimer.getText().toString().substring(3, 5));
                        int sec = Integer.parseInt(txtTimer.getText().toString().substring(6, 8));

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
                    pauseButton.setText(getString(R.string.btn_pause));
                }
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
                                if (which == 0) {
                                    channel = 2;
                                    txtMode.setText("Stereo");
                                } else {
                                    channel = 1;
                                    txtMode.setText("Mono");
                                }
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
                                        txtQuality.setText("256000kbps");
                                        break;
                                    case 1:
                                        bitRate = 192000;
                                        txtQuality.setText("192000kbps");
                                        break;
                                    case 2:
                                        bitRate = 128000;
                                        txtQuality.setText("128000kbps");
                                        break;
                                }
                            }
                        }).show();
            }
        });

        lectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath());
                File[] files = dir.listFiles();
                ArrayList<String> arrayList = new ArrayList<>();
                for (File f : files) {
                    if (f.isDirectory()) {
                        try {
                            String[] name = f.getName().split("-");
                            arrayList.add(name[0] + "-" + name[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                final String[] array = new String[arrayList.size()];
                arrayList.toArray(array);

                AlertDialog.Builder qualityDialog = new AlertDialog.Builder(getActivity());
                qualityDialog.setTitle(getString(R.string.hint_lecture))
                        .setItems(array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                directory = array[which];
                                txtSelectedLecture.setText(array[which]);
                            }
                        }).show();
            }
        });

        return rootView;
    }
}
