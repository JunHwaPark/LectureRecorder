package com.junhwa.lecturerecorder.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.junhwa.lecturerecorder.R;
import com.junhwa.lecturerecorder.player.PlayerService;
import com.junhwa.lecturerecorder.recyclerview.OnRecFileItemClickListener;
import com.junhwa.lecturerecorder.recyclerview.RecFile;
import com.junhwa.lecturerecorder.recyclerview.RecFileAdapter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

import static com.junhwa.lecturerecorder.player.PlayerService.*;

public class ListFragment extends Fragment {
    public final static int POSITION = 0;
    public final static int STATUS = 1;

    File[] files = null;
    File selected = null;
    RecFile audio = null;

    BroadcastReceiver receiver = null;
    ConstraintLayout playLayout;
    TextView txtSelected;
    SeekBar seekBar;

    Intent intent = null;

    int playerStatus = 0;
    //0 = stopped, 1 = playing, 2 = paused

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        final Button playButton = rootView.findViewById(R.id.button3);
        final Button exitButton = rootView.findViewById(R.id.button4);
        playLayout = rootView.findViewById(R.id.playLayout);
        txtSelected = rootView.findViewById(R.id.txtSelected);
        seekBar = rootView.findViewById(R.id.seekBar2);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final RecFileAdapter adapter = new RecFileAdapter();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int command = intent.getIntExtra("COMMAND", POSITION);
                if(command == POSITION) {
                    int position = intent.getIntExtra("POSITION", 0);
                    Log.d("receiver", position + "");
                    seekBar.setProgress(position);
                } else if(command == STATUS) {
                    int status = intent.getIntExtra("STATUS", 0);
                    if(status == STOP){
                        seekBar.setProgress(0);
                        playerStatus = 2;
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("player"));

        File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        files = dir.listFiles();

        for (File f : files) {
            try {
                FileInputStream fs = new FileInputStream(f);
                FileDescriptor fd = fs.getFD();
                long size = fs.getChannel().size();

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(fd);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                adapter.addAudio(new RecFile(f.getName(), Integer.parseInt(time), size));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnRecFileItemClickListener() {
            @Override
            public void onItemClick(RecFileAdapter.ViewHolder holder, View view, int position) {
                audio = adapter.getItem(position);
                selected = new File(getContext().
                        getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + File.separator + audio.getFileName());
                playLayout.setVisibility(View.VISIBLE);
                playButton.setText(getString(R.string.btn_play));
                txtSelected.setText(audio.getFileName());
                seekBar.setMax(audio.getIntDuration());
                seekBar.setProgress(0);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            intent = new Intent(getActivity(), PlayerService.class);
                            intent.putExtra("COMMAND", SEEK_TO);
                            intent.putExtra("POSITION", progress);
                            getActivity().startService(intent);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                playerStatus = 0;
                intent = new Intent(getActivity(), PlayerService.class);
                intent.putExtra("COMMAND", INITIALIZE_PLAYER);
                intent.putExtra("PATH", selected.getPath());
                getActivity().startService(intent);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerStatus == 0) {
                    intent = new Intent(getActivity(), PlayerService.class);
                    intent.putExtra("COMMAND", PLAY);
                    getActivity().startService(intent);
                    playerStatus = 1;
                    playButton.setText(getString(R.string.btn_pause));
                } else if(playerStatus == 1) {
                    intent = new Intent(getActivity(), PlayerService.class);
                    intent.putExtra("COMMAND", PAUSE);
                    getActivity().startService(intent);
                    playerStatus = 2;
                    playButton.setText(getString(R.string.btn_play));
                } else if(playerStatus == 2) {
                    intent = new Intent(getActivity(), PlayerService.class);
                    intent.putExtra("COMMAND", RESUME);
                    getActivity().startService(intent);
                    playerStatus = 1;
                    playButton.setText(getString(R.string.btn_pause));
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), PlayerService.class);
                intent.putExtra("COMMAND", STOP);
                getActivity().startService(intent);
                playerStatus = 0;

                playButton.setText(getString(R.string.btn_play));
                playLayout.setVisibility(View.GONE);
            }
        });

        return rootView;
    }
}
