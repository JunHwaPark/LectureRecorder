package com.junhwa.lecturerecorder.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.junhwa.lecturerecorder.R;
import com.junhwa.lecturerecorder.recyclerview.lecture_list.Lecture;
import com.junhwa.lecturerecorder.recyclerview.lecture_list.LectureAdapter;
import com.junhwa.lecturerecorder.recyclerview.lecture_list.OnLectureItemClickListener;

import java.io.File;

public class LectureManagementActivity extends AppCompatActivity {
    LectureAdapter lectureAdapter;
    RecyclerView recyclerView = null;
    File[] files;
    File dir;

    Button button;
    EditText editText, editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_management);

        button = findViewById(R.id.button5);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);

        recyclerView = findViewById(R.id.lecture_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        check();

        recyclerView.setAdapter(lectureAdapter);
        lectureAdapter.setOnItemClickListener(new OnLectureItemClickListener() {
            @Override
            public void onItemClick(LectureAdapter.ViewHolder holder, View view, int position) {
                showDialog(lectureAdapter.getItem(position), position);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().length() > 0 &&
                editText2.getText().toString().length() > 0) {
                    new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() +
                            "/" + editText.getText().toString() + "-" + editText2.getText().toString()).mkdir();
                    lectureAdapter.addLecture(new Lecture(editText.getText().toString(), editText2.getText().toString()));
                    recyclerView.setAdapter(lectureAdapter);
                }
            }
        });
    }

    void showDialog(final Lecture lecture, int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_lecture) + " : " + lecture.getLecture() + "-" + lecture.getProfessor());
        builder.setMessage(getString(R.string.d_lecture_text));
        builder.setPositiveButton("Y",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath()
                        +"/"+lecture.getLecture()+"-"+lecture.getProfessor()).delete();
                        check();
                        recyclerView.setAdapter(lectureAdapter);
                    }
                });
        builder.setNegativeButton("N",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    void check(){
        lectureAdapter = new LectureAdapter();
        dir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()){
                try {
                    Log.d("Directory", f.getName());
                    String[] name = f.getName().split("-");
                    lectureAdapter.addLecture(new Lecture(name[0], name[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
