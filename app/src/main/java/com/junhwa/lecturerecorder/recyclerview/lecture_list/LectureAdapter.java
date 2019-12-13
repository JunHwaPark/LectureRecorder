package com.junhwa.lecturerecorder.recyclerview.lecture_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.junhwa.lecturerecorder.R;
import com.junhwa.lecturerecorder.recyclerview.recFile.OnRecFileItemClickListener;
import com.junhwa.lecturerecorder.recyclerview.recFile.RecFileAdapter;

import java.util.ArrayList;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.ViewHolder> implements OnLectureItemClickListener {
    ArrayList<Lecture> items = new ArrayList<>();
    OnLectureItemClickListener listener;

    static public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView2;

        public ViewHolder(@NonNull View itemView, final OnLectureItemClickListener listener) {
            super(itemView);

            textView = itemView.findViewById(R.id.txt_lecture);
            textView2 = itemView.findViewById(R.id.txt_professor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null)
                        listener.onItemClick(ViewHolder.this, v, position);
                }
            });
        }

        public void setLecture(Lecture lecture) {
            textView.setText(lecture.getLecture());
            textView2.setText(lecture.getProfessor());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.lecture_list, parent, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lecture item = items.get(position);
        holder.setLecture(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemClick(LectureAdapter.ViewHolder holder, View view, int position) {
        if(listener != null)
            listener.onItemClick(holder, view, position);
    }

    public void setOnItemClickListener(OnLectureItemClickListener listener) {
        this.listener = listener;
    }

    public void addLecture(Lecture lecture) {
        items.add(lecture);
    }

    public Lecture getItem(int position) {
        return items.get(position);
    }
}
