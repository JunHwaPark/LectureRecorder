package com.junhwa.lecturerecorder.recyclerview.recFile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.junhwa.lecturerecorder.R;
import com.junhwa.lecturerecorder.recyclerview.lecture_list.LectureAdapter;

import java.util.ArrayList;

public class RecFileAdapter extends RecyclerView.Adapter<RecFileAdapter.ViewHolder> implements OnRecFileItemClickListener {
    ArrayList<RecFile> items = new ArrayList<>();
    OnRecFileItemClickListener listener;

    static public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView2, textView3;

        public ViewHolder(@NonNull View itemView, final OnRecFileItemClickListener listener) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView2);
            textView2 = itemView.findViewById(R.id.textView3);
            textView3 = itemView.findViewById(R.id.textView4);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null)
                        listener.onItemClick(ViewHolder.this, v, position);
                }
            });
        }

        public void setRecFile(RecFile recFile) {
            textView.setText(recFile.getFileName());
            textView2.setText(recFile.getDuration());
            textView3.setText(recFile.getSize()+"byte");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.audio_item, parent, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecFile item = items.get(position);
        holder.setRecFile(item);
    }

    public void setOnItemClickListener(OnRecFileItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(listener != null)
            listener.onItemClick(holder, view, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addAudio(RecFile audio) {
        items.add(audio);
    }

    public void setItems(ArrayList<RecFile> items) {
        this.items = items;
    }

    public void setItem(int position, RecFile audio) {
        items.set(position, audio);
    }

    public RecFile getItem(int position) {
        return items.get(position);
    }
}
